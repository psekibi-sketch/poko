package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

enum class SpeechState {
    IDLE,
    READY,
    LISTENING,
    PROCESSING,
    SUCCESS,
    TRY_AGAIN,
    ERROR
}

data class SpeechEvaluation(
    val spokenText: String,
    val targetText: String,
    val accuracy: Int, // 0 - 100
    val isPassed: Boolean,
    val stars: Int, // 1 to 3
    val feedbackMessage: String
)

class SpeechRecognitionService(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null

    private val _speechState = MutableStateFlow(SpeechState.IDLE)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _soundRmsDb = MutableStateFlow(0f)
    val soundRmsDb: StateFlow<Float> = _soundRmsDb.asStateFlow()

    private val _lastEvaluation = MutableStateFlow<SpeechEvaluation?>(null)
    val lastEvaluation: StateFlow<SpeechEvaluation?> = _lastEvaluation.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentTargetText: String = ""
    private var onEvaluationResult: ((SpeechEvaluation) -> Unit)? = null

    val isRecognitionAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        mainHandler.post {
            try {
                if (SpeechRecognizer.isRecognitionAvailable(context)) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                        setRecognitionListener(createListener())
                    }
                } else {
                    Log.w("SpeechService", "Speech recognition service is not available on this device.")
                }
            } catch (e: Exception) {
                Log.e("SpeechService", "Failed to initialize SpeechRecognizer", e)
            }
        }
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _speechState.value = SpeechState.LISTENING
                _errorMessage.value = null
            }

            override fun onBeginningOfSpeech() {
                _speechState.value = SpeechState.LISTENING
            }

            override fun onRmsChanged(rmsdB: Float) {
                // rmsdB ranges usually between -2 to 10
                _soundRmsDb.value = rmsdB.coerceIn(0f, 10f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _speechState.value = SpeechState.PROCESSING
                _soundRmsDb.value = 0f
            }

            override fun onError(error: Int) {
                _soundRmsDb.value = 0f
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Ses kaydedilemedi. Lütfen tekrar dene."
                    SpeechRecognizer.ERROR_CLIENT -> "Mikrofon bağlantısı kurulamadı."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mikrofon izni gerekli."
                    SpeechRecognizer.ERROR_NETWORK -> "İnternet bağlantısı gerekiyor."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Bağlantı zaman aşımına uğradı."
                    SpeechRecognizer.ERROR_NO_MATCH -> "Ses tam anlaşılamadı. Bir kez daha söyleyelim!"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Mikrofon meşgul, tekrar deneniyor..."
                    SpeechRecognizer.ERROR_SERVER -> "Sunucu yanıt vermedi."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Ses duyulamadı. Tekrar mikrofon butonuna bas!"
                    else -> "Ses algılanamadı, lütfen tekrar dene."
                }
                Log.w("SpeechService", "SpeechRecognizer error: $error ($message)")
                _errorMessage.value = message

                val errorEval = SpeechEvaluation(
                    spokenText = "",
                    targetText = currentTargetText,
                    accuracy = 0,
                    isPassed = false,
                    stars = 0,
                    feedbackMessage = message
                )
                _lastEvaluation.value = errorEval
                _speechState.value = SpeechState.TRY_AGAIN
                onEvaluationResult?.invoke(errorEval)
            }

            override fun onResults(results: Bundle?) {
                _speechState.value = SpeechState.PROCESSING
                _soundRmsDb.value = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                val topSpoken = matches.firstOrNull() ?: ""
                _recognizedText.value = topSpoken
                evaluateSpeechCandidates(matches)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                val partial = matches.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    _recognizedText.value = partial
                    // Early fast-track if partial match already meets criteria
                    val bestEval = findBestEvaluation(matches, currentTargetText)
                    if (bestEval != null && bestEval.isPassed) {
                        _lastEvaluation.value = bestEval
                        _speechState.value = SpeechState.SUCCESS
                        onEvaluationResult?.invoke(bestEval)
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    fun startListening(
        target: String,
        onResult: ((SpeechEvaluation) -> Unit)? = null
    ) {
        currentTargetText = target
        onEvaluationResult = onResult
        _recognizedText.value = ""
        _lastEvaluation.value = null
        _errorMessage.value = null
        _speechState.value = SpeechState.LISTENING // Immediately activate listening UI with zero delay!

        mainHandler.post {
            try {
                if (speechRecognizer == null) {
                    initRecognizer()
                } else {
                    speechRecognizer?.cancel()
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }

                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e("SpeechService", "Error starting listening", e)
                _errorMessage.value = "Mikrofon başlatılamadı: ${e.localizedMessage}"
                _speechState.value = SpeechState.ERROR
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e("SpeechService", "Error stopping listening", e)
            }
        }
    }

    fun cancelListening() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
                _speechState.value = SpeechState.IDLE
                _soundRmsDb.value = 0f
                _recognizedText.value = ""
                _lastEvaluation.value = null
            } catch (e: Exception) {
                Log.e("SpeechService", "Error cancelling listening", e)
            }
        }
    }

    /**
     * Test / Simulation helper for emulators or manual speech testing.
     */
    fun simulateRecognition(spokenText: String, target: String = currentTargetText) {
        currentTargetText = target
        _recognizedText.value = spokenText
        evaluateSpeechCandidates(listOf(spokenText))
    }

    private fun evaluateSpeechCandidates(candidates: List<String>) {
        val target = currentTargetText
        if (candidates.isEmpty() || target.isBlank()) {
            val emptyEval = SpeechEvaluation(
                spokenText = "",
                targetText = target,
                accuracy = 0,
                isPassed = false,
                stars = 0,
                feedbackMessage = "Ses algılanamadı, lütfen tekrar dene."
            )
            _lastEvaluation.value = emptyEval
            _speechState.value = SpeechState.TRY_AGAIN
            onEvaluationResult?.invoke(emptyEval)
            return
        }

        val bestEvaluation = findBestEvaluation(candidates, target)
            ?: evaluateSingle(candidates.first(), target)

        _lastEvaluation.value = bestEvaluation
        _speechState.value = if (bestEvaluation.isPassed) SpeechState.SUCCESS else SpeechState.TRY_AGAIN
        onEvaluationResult?.invoke(bestEvaluation)
    }

    private fun findBestEvaluation(candidates: List<String>, target: String): SpeechEvaluation? {
        var topEval: SpeechEvaluation? = null
        for (cand in candidates) {
            if (cand.isBlank()) continue
            val eval = evaluateSingle(cand, target)
            if (eval.isPassed) {
                return eval
            }
            if (topEval == null || eval.accuracy > topEval.accuracy) {
                topEval = eval
            }
        }
        return topEval
    }

    private fun evaluateSingle(spokenRaw: String, targetRaw: String): SpeechEvaluation {
        val normalizedSpoken = normalizeText(spokenRaw)
        val normalizedTarget = normalizeText(targetRaw)

        val isNumMatch = isNumberOrDigitMatch(normalizedSpoken, normalizedTarget)
        val isKidMatch = isKidPhoneticMatch(normalizedSpoken, normalizedTarget)
        val containsMatch = containsTargetWord(normalizedSpoken, normalizedTarget)
        val rawAccuracy = calculateAccuracy(normalizedSpoken, normalizedTarget)

        val isPassed = isNumMatch || isKidMatch || containsMatch || rawAccuracy >= 55

        val accuracy = when {
            isNumMatch -> 100
            isKidMatch -> 95
            containsMatch -> max(85, rawAccuracy)
            isPassed -> max(75, (rawAccuracy * 1.25).toInt().coerceIn(75, 100))
            else -> rawAccuracy
        }

        val stars = when {
            accuracy >= 85 -> 3
            accuracy >= 70 -> 2
            isPassed -> 1
            else -> 0
        }

        val feedback = when {
            accuracy >= 90 -> "Mükemmel telaffuz! Harikasın! 🌟"
            accuracy >= 75 -> "Çok güzel söyledin! Aferin! 👏"
            isPassed -> "Harika bir deneme! Tebrikler! 🎉"
            accuracy >= 40 -> "Çok yaklaştın! Dinle butonuna basıp tekrar deneyebilirsin."
            else -> "Hadi bir kez daha dinleyip mikrofonla tekrar edelim!"
        }

        return SpeechEvaluation(
            spokenText = spokenRaw,
            targetText = targetRaw,
            accuracy = accuracy,
            isPassed = isPassed,
            stars = stars,
            feedbackMessage = feedback
        )
    }

    private fun normalizeText(text: String): String {
        val clean = text.substringBefore("(").substringBefore("[").substringBefore("/")
        return clean.lowercase(Locale.US)
            .replace("ı", "i")
            .replace("ş", "s")
            .replace("ç", "c")
            .replace("ğ", "g")
            .replace("ö", "o")
            .replace("ü", "u")
            .replace(Regex("[^a-z0-9\\s]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private val numberEquivalents = mapOf(
        "0" to listOf("zero", "0", "ziro", "o", "oh"),
        "zero" to listOf("0", "zero", "ziro", "o", "oh"),
        "1" to listOf("one", "1", "won", "van", "bir"),
        "one" to listOf("1", "one", "won", "van", "bir"),
        "2" to listOf("two", "2", "to", "too", "tu", "iki"),
        "two" to listOf("2", "two", "to", "too", "tu", "iki"),
        "3" to listOf("three", "3", "tree", "free", "tri", "uc"),
        "three" to listOf("3", "three", "tree", "free", "tri", "uc"),
        "4" to listOf("four", "4", "for", "fore", "forr", "dort"),
        "four" to listOf("4", "four", "for", "fore", "forr", "dort"),
        "5" to listOf("five", "5", "faiv", "faif", "fiv", "bes"),
        "five" to listOf("5", "five", "faiv", "faif", "fiv", "bes"),
        "6" to listOf("six", "6", "siks", "sikis", "alti"),
        "six" to listOf("6", "six", "siks", "sikis", "alti"),
        "7" to listOf("seven", "7", "sevin", "sevin", "yedi"),
        "seven" to listOf("7", "seven", "sevin", "sevin", "yedi"),
        "8" to listOf("eight", "8", "ate", "eyt", "ayt", "sekiz"),
        "eight" to listOf("8", "eight", "ate", "eyt", "ayt", "sekiz"),
        "9" to listOf("nine", "9", "nayn", "nain", "dokuz"),
        "nine" to listOf("9", "nine", "nayn", "nain", "dokuz"),
        "10" to listOf("ten", "10", "tan", "on"),
        "ten" to listOf("10", "ten", "tan", "on"),
        "11" to listOf("eleven", "11", "elevin"),
        "eleven" to listOf("11", "eleven", "elevin"),
        "12" to listOf("twelve", "12", "tvelv"),
        "twelve" to listOf("12", "twelve", "tvelv"),
        "13" to listOf("thirteen", "13", "tortin", "törtin"),
        "thirteen" to listOf("13", "thirteen", "tortin"),
        "14" to listOf("fourteen", "14", "fortin"),
        "fourteen" to listOf("14", "fourteen", "fortin"),
        "15" to listOf("fifteen", "15", "fiftin"),
        "fifteen" to listOf("15", "fifteen", "fiftin"),
        "16" to listOf("sixteen", "16", "sikstin"),
        "sixteen" to listOf("16", "sixteen", "sikstin"),
        "17" to listOf("seventeen", "17", "sevintin"),
        "seventeen" to listOf("17", "seventeen", "sevintin"),
        "18" to listOf("eighteen", "18", "eytin"),
        "eighteen" to listOf("18", "eighteen", "eytin"),
        "19" to listOf("nineteen", "19", "nayntin"),
        "nineteen" to listOf("19", "nineteen", "nayntin"),
        "20" to listOf("twenty", "20", "tventi"),
        "twenty" to listOf("20", "twenty", "tventi")
    )

    private fun isNumberOrDigitMatch(spoken: String, target: String): Boolean {
        val targetTokens = target.split(" ")
        val spokenTokens = spoken.split(" ")

        for (t in targetTokens) {
            val equivalents = numberEquivalents[t]
            if (equivalents != null) {
                if (spokenTokens.any { s -> equivalents.contains(s) }) {
                    return true
                }
            }
        }
        return false
    }

    private val kidPhoneticEquivalents = mapOf(
        "ambulance" to listOf("ambulans", "ambılıns", "ambilans", "ambulance"),
        "supermarket" to listOf("supermarket", "süpermarket", "supermaket"),
        "taxi" to listOf("taksi", "taxi"),
        "train" to listOf("tren", "treyn", "train"),
        "plane" to listOf("pleyn", "ucak", "plane"),
        "bus" to listOf("bas", "otobus", "bus"),
        "doctor" to listOf("doktor", "daktır", "doctor"),
        "nurse" to listOf("nörs", "nörse", "hemsire", "nurse"),
        "hotel" to listOf("otel", "hotel"),
        "cafe" to listOf("kafe", "cafe"),
        "stop" to listOf("dur", "stap", "stop"),
        "hello" to listOf("helo", "merhaba", "hi", "hello"),
        "goodbye" to listOf("gudbay", "bay", "baybay", "hoscakal", "goodbye"),
        "good morning" to listOf("gud morning", "gut morning", "gunaydin", "good morning"),
        "good afternoon" to listOf("gud aftırnun", "good afternoon"),
        "good night" to listOf("gud nayt", "iyi geceler", "good night"),
        "pencil" to listOf("pensıl", "pensil", "kalem", "pencil"),
        "ruler" to listOf("rulır", "cetvel", "ruler"),
        "eraser" to listOf("ireyzır", "silgi", "eraser"),
        "book" to listOf("buk", "kitap", "book"),
        "door" to listOf("dor", "kapi", "door"),
        "window" to listOf("vindov", "pencere", "window"),
        "table" to listOf("teybıl", "masa", "table"),
        "chair" to listOf("ceyr", "sandalye", "chair"),
        "school" to listOf("skul", "okul", "school"),
        "teacher" to listOf("tiçır", "ticir", "ogretmen", "teacher"),
        "friend" to listOf("frend", "arkadas", "friend"),
        "red" to listOf("kirmizi", "red"),
        "blue" to listOf("mavi", "blu", "blue"),
        "yellow" to listOf("sari", "yelov", "yellow"),
        "green" to listOf("yesil", "grin", "green"),
        "pink" to listOf("pembe", "pink"),
        "purple" to listOf("mor", "pörpıl", "purple"),
        "orange" to listOf("turuncu", "orınc", "orange"),
        "black" to listOf("siyah", "blek", "black"),
        "white" to listOf("beyaz", "vayt", "white"),
        "brown" to listOf("kahverengi", "bravn", "brown"),
        "cat" to listOf("kedi", "ket", "cat"),
        "dog" to listOf("kopek", "dog", "dogi"),
        "bird" to listOf("kus", "börd", "bird"),
        "fish" to listOf("balik", "fis", "fish"),
        "rabbit" to listOf("tavsan", "rebit", "rabbit"),
        "turtle" to listOf("kaplumbaga", "törtıl", "turtle"),
        "apple" to listOf("elma", "epıl", "apple"),
        "banana" to listOf("muz", "bınana", "banana")
    )

    private fun isKidPhoneticMatch(spoken: String, target: String): Boolean {
        val equivalents = kidPhoneticEquivalents[target]
        if (equivalents != null) {
            val spokenTokens = spoken.split(" ")
            if (equivalents.any { eq -> spoken.contains(eq) || spokenTokens.contains(eq) }) {
                return true
            }
        }
        return false
    }

    private fun containsTargetWord(spoken: String, target: String): Boolean {
        if (target.isBlank() || spoken.isBlank()) return false
        if (spoken.contains(target) || target.contains(spoken)) return true
        val targetWords = target.split(" ")
        val spokenWords = spoken.split(" ")
        if (targetWords.size == 1 && spokenWords.contains(target)) return true
        val matchCount = targetWords.count { spokenWords.contains(it) }
        return matchCount.toDouble() / targetWords.size >= 0.6
    }

    private fun calculateAccuracy(spoken: String, target: String): Int {
        if (target.isBlank()) return 0
        if (spoken == target) return 100
        if (spoken.contains(target) || target.contains(spoken)) {
            val ratio = min(spoken.length, target.length).toDouble() / max(spoken.length, target.length)
            return (75 + (ratio * 25)).toInt().coerceIn(75, 100)
        }

        val distance = levenshteinDistance(spoken, target)
        val maxLen = max(spoken.length, target.length)
        if (maxLen == 0) return 100
        val similarity = (1.0 - (distance.toDouble() / maxLen)) * 100.0
        return similarity.toInt().coerceIn(0, 100)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    dp[i - 1][j] + 1,
                    min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    fun shutdown() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.e("SpeechService", "Error destroying speech recognizer", e)
            }
        }
    }
}
