package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Collections
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class AudioService(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var toneGenerator: ToneGenerator? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val utteranceCallbacks = ConcurrentHashMap<String, () -> Unit>()
    private val pendingRunnables = Collections.synchronizedList(mutableListOf<Runnable>())

    var isSoundFxEnabled: Boolean = true
    var speechRate: Float = 0.88f // Natural, warm conversational pace (like Duolingo/Buddy)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.e("AudioService", "Error initializing ToneGenerator", e)
        }

        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.UK)
                }
                selectBestVoice(Locale.US)
                tts?.setSpeechRate(speechRate)
                tts?.setPitch(1.02f) // Warm, natural friendly teacher pitch (not chipmunk/synthetic)
                isTtsReady = true

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        if (utteranceId != null) {
                            val cb = utteranceCallbacks.remove(utteranceId)
                            if (cb != null) {
                                mainHandler.post { cb.invoke() }
                            }
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        if (utteranceId != null) {
                            val cb = utteranceCallbacks.remove(utteranceId)
                            if (cb != null) {
                                mainHandler.post { cb.invoke() }
                            }
                        }
                    }
                })
            }
        }
    }

    /**
     * Finds and applies the most natural, human-sounding neural voice available on the device.
     * Prioritizes High Quality / Very High Quality Google speech engine voices.
     */
    private fun selectBestVoice(targetLocale: Locale) {
        try {
            val allVoices = tts?.voices ?: return
            val matching = allVoices.filter { it.locale.language.equals(targetLocale.language, ignoreCase = true) }
            if (matching.isEmpty()) return

            // Prefer high-quality youthful child/friendly voices (Google youth/sfg/child/female neural)
            val bestVoice = matching.firstOrNull { voice ->
                !voice.isNetworkConnectionRequired && (
                    voice.name.contains("child", ignoreCase = true) ||
                    voice.name.contains("kid", ignoreCase = true) ||
                    voice.name.contains("sfg", ignoreCase = true) ||
                    voice.name.contains("young", ignoreCase = true) ||
                    voice.name.contains("female", ignoreCase = true)
                ) && voice.quality >= Voice.QUALITY_HIGH
            } ?: matching.firstOrNull { voice ->
                !voice.isNetworkConnectionRequired && (
                    voice.name.contains("sfg", ignoreCase = true) ||
                    voice.name.contains("neural", ignoreCase = true) ||
                    voice.name.contains("female", ignoreCase = true)
                )
            } ?: matching.firstOrNull { v ->
                !v.isNetworkConnectionRequired && v.quality >= Voice.QUALITY_HIGH
            } ?: matching.firstOrNull { v -> !v.isNetworkConnectionRequired }
              ?: matching.firstOrNull()

            if (bestVoice != null) {
                tts?.setVoice(bestVoice)
                Log.d("AudioService", "Selected natural voice: ${bestVoice.name} for ${targetLocale.language}")
            }
        } catch (e: Exception) {
            Log.e("AudioService", "Error selecting voice", e)
        }
    }

    companion object {
        // Regex to strip all emojis, pictographs, stars, flags, and special symbols
        private val EMOJI_AND_SYMBOLS_REGEX = Regex(
            "[\\p{So}\\p{Cn}\\p{Cs}\\p{Sk}\\p{Sm}\\uD83C-\\uDBFF\\uDC00-\\uDFFF\\u2600-\\u27BF\\uFE00-\\uFE0F\\u200D]+"
        )

        fun cleanTextForSpeech(text: String): String {
            return text
                .replace(EMOJI_AND_SYMBOLS_REGEX, " ")
                .replace(Regex("[*#_~`^<>{}\\[\\]|•⭐🌟✨🎉🎈🎮🎤📚🏆🪙]"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()
        }
    }

    /**
     * Instantly stops any ongoing speech and cancels all queued or delayed audio sequences.
     */
    fun stopSpeech() {
        try {
            synchronized(pendingRunnables) {
                for (runnable in pendingRunnables) {
                    mainHandler.removeCallbacks(runnable)
                }
                pendingRunnables.clear()
            }
            utteranceCallbacks.clear()
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e("AudioService", "Error stopping speech", e)
        }
    }

    private fun postDelayedSafe(delayMillis: Long, action: () -> Unit) {
        val runnable = object : Runnable {
            override fun run() {
                pendingRunnables.remove(this)
                action()
            }
        }
        pendingRunnables.add(runnable)
        mainHandler.postDelayed(runnable, delayMillis)
    }

    fun speakEnglish(text: String, isSlow: Boolean = false, onDone: (() -> Unit)? = null) {
        val cleanText = cleanTextForSpeech(text)
        if (!isTtsReady || cleanText.isBlank() || tts == null) {
            onDone?.invoke()
            return
        }

        // Cancel previous pending runnables and speech
        synchronized(pendingRunnables) {
            for (runnable in pendingRunnables) {
                mainHandler.removeCallbacks(runnable)
            }
            pendingRunnables.clear()
        }
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("AudioService", "Error stopping previous speech", e)
        }

        val utteranceId = "en_${System.currentTimeMillis()}_${(100..999).random()}"
        if (onDone != null) {
            utteranceCallbacks[utteranceId] = onDone
        }

        tts?.setLanguage(Locale.US)
        selectBestVoice(Locale.US)
        tts?.setSpeechRate(if (isSlow) 0.68f else 0.92f)
        tts?.setPitch(1.36f) // Joyful, vibrant 7-year-old child friend tone
        val speakResult = tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        if (speakResult != TextToSpeech.SUCCESS) {
            if (onDone != null) {
                utteranceCallbacks.remove(utteranceId)
                mainHandler.post { onDone.invoke() }
            }
        }
    }

    fun speakTurkish(text: String, isSlow: Boolean = false, onDone: (() -> Unit)? = null) {
        val cleanText = cleanTextForSpeech(text)
        if (!isTtsReady || cleanText.isBlank() || tts == null) {
            onDone?.invoke()
            return
        }

        // Cancel previous pending runnables and speech
        synchronized(pendingRunnables) {
            for (runnable in pendingRunnables) {
                mainHandler.removeCallbacks(runnable)
            }
            pendingRunnables.clear()
        }
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("AudioService", "Error stopping previous speech", e)
        }

        val utteranceId = "tr_${System.currentTimeMillis()}_${(100..999).random()}"
        if (onDone != null) {
            utteranceCallbacks[utteranceId] = onDone
        }

        val turkishLocale = Locale("tr", "TR")
        val result = tts?.setLanguage(turkishLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("tr"))
        }
        selectBestVoice(turkishLocale)
        tts?.setSpeechRate(if (isSlow) 0.74f else 0.95f)
        tts?.setPitch(1.34f) // Warm, cheerful 7-year-old child friend tone
        val speakResult = tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        if (speakResult != TextToSpeech.SUCCESS) {
            if (onDone != null) {
                utteranceCallbacks.remove(utteranceId)
                mainHandler.post { onDone.invoke() }
            }
        }
    }

    /**
     * Speaks Turkish guidance first, then immediately speaks English target word/sentence,
     * and when the target finishes, triggers onComplete (e.g. to automatically open the microphone).
     * All delayed actions are tracked so calling stopSpeech() instantly cancels them.
     */
    fun speakGuideThenEnglish(
        guideTr: String,
        targetEn: String,
        isSlow: Boolean = false,
        onComplete: (() -> Unit)? = null
    ) {
        val cleanGuide = cleanTextForSpeech(guideTr)
        val cleanTarget = cleanTextForSpeech(targetEn)
        if (cleanGuide.isBlank()) {
            speakEnglish(cleanTarget, isSlow, onComplete)
            return
        }

        speakTurkish(cleanGuide) {
            // Safe cancellable 200ms pause before English target
            postDelayedSafe(200) {
                speakEnglish(cleanTarget, isSlow, onComplete)
            }
        }
    }

    fun speakPraise(
        praiseTr: String = "Harika!",
        onComplete: (() -> Unit)? = null
    ) {
        val clean = cleanTextForSpeech(praiseTr).ifBlank { "Harika!" }
        speakTurkish(clean, false, onComplete)
    }

    fun speakRetry(
        retryTr: String = "Tekrar deneyelim!",
        targetEn: String,
        onComplete: (() -> Unit)? = null
    ) {
        val cleanRetry = cleanTextForSpeech(retryTr).ifBlank { "Tekrar deneyelim!" }
        speakGuideThenEnglish(cleanRetry, targetEn, isSlow = true, onComplete = onComplete)
    }

    fun playCorrectSound() {
        if (!isSoundFxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun playWrongSound() {
        if (!isSoundFxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun playPopSound() {
        if (!isSoundFxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun playCoinSound() {
        if (!isSoundFxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun playFanfareSound() {
        if (!isSoundFxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 400)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun updateSpeechRate(rate: Float) {
        speechRate = rate
        tts?.setSpeechRate(rate)
    }

    fun shutdown() {
        try {
            stopSpeech()
            tts?.shutdown()
            toneGenerator?.release()
        } catch (e: Exception) {
            // Safe shutdown
        }
    }
}
