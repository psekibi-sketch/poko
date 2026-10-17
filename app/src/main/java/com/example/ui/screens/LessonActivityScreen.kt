package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.audio.SpeechState
import com.example.data.model.Exercise
import com.example.data.model.ExerciseType
import com.example.data.model.Lesson
import com.example.data.model.WordItem
import com.example.data.model.WordOption
import com.example.ui.components.CompanionMascot
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.components.SpeechPracticeDialog
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LessonActivityScreen(
    lesson: Lesson,
    initialExerciseIndex: Int = 0,
    equippedCostumeId: String = "costume_default",
    audioService: AudioService? = null,
    speechService: SpeechRecognitionService? = null,
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayPopFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onProgressUpdate: ((Int) -> Unit)? = null,
    onFinishLesson: (stars: Int, score: Int, correctCount: Int, totalCount: Int, isPerfectScore: Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val effectiveAudioService = remember(audioService) {
        audioService ?: AudioService(context)
    }
    val effectiveSpeechService = remember(speechService) {
        speechService ?: SpeechRecognitionService(context)
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    val allOriginalExercises = remember(lesson) { lesson.exercises }
    val masteredExerciseIds = remember { mutableStateListOf<String>() }
    val mistakenExerciseIds = remember { mutableStateListOf<String>() }
    val firstTryMistakenIds = remember { mutableStateListOf<String>() }
    var questionAttempts by remember { mutableIntStateOf(0) }
    var isAdaptiveRetryMode by remember { mutableStateOf(false) }
    var activeExerciseList by remember { mutableStateOf(allOriginalExercises) }

    val totalExercises = activeExerciseList.size
    val safeInitialIndex = remember(initialExerciseIndex, totalExercises) {
        if (initialExerciseIndex in 0 until totalExercises) initialExerciseIndex else 0
    }

    var currentIndex by remember { mutableIntStateOf(safeInitialIndex) }
    var hearts by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var correctAnswersCount by remember { mutableIntStateOf(0) }
    var isLessonCompleted by remember { mutableStateOf(false) }
    var mascotGuideText by remember { mutableStateOf("") }
    var isResumedFromPrevious by remember { mutableStateOf(safeInitialIndex > 0) }

    // 3-second smart hint state
    var userInteractedCurrentQuestion by remember { mutableStateOf(false) }
    var pokoHintText by remember { mutableStateOf<String?>(null) }

    val currentExercise = activeExerciseList.getOrNull(currentIndex)

    val speechState by effectiveSpeechService.speechState.collectAsState()
    val lastEvaluation by effectiveSpeechService.lastEvaluation.collectAsState()

    // Request permission at lesson start if not granted
    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Lifecycle observer to completely stop all speech and listening when app goes to background
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE || event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                try {
                    effectiveSpeechService.cancelListening()
                    effectiveAudioService.stopSpeech()
                } catch (e: Exception) {
                    // Safe ignore
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                effectiveSpeechService.cancelListening()
                effectiveAudioService.stopSpeech()
            } catch (e: Exception) {
                // Safe ignore
            }
        }
    }

    // Safe dispose on exit without dangerous onProgressUpdate inside onDispose
    DisposableEffect(Unit) {
        onDispose {
            try {
                effectiveSpeechService.cancelListening()
                effectiveAudioService.stopSpeech()
            } catch (e: Exception) {
                // Safe ignore on exit
            }
        }
    }

    fun triggerAutoListening(targetText: String) {
        if (hasAudioPermission) {
            coroutineScope.launch {
                delay(50) // Fast, zero-lag microphone opening!
                effectiveSpeechService.startListening(targetText)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun advanceToNext() {
        val finishedEx = currentExercise
        if (finishedEx != null) {
            if (!masteredExerciseIds.contains(finishedEx.id)) {
                masteredExerciseIds.add(finishedEx.id)
            }
            mistakenExerciseIds.remove(finishedEx.id)
        }
        userInteractedCurrentQuestion = true
        pokoHintText = null
        questionAttempts = 0

        if (currentIndex + 1 < activeExerciseList.size) {
            currentIndex++
            onProgressUpdate?.invoke(currentIndex)
        } else {
            // Check if there are unmastered exercises that need adaptive retry
            val unmastered = allOriginalExercises.filter { !masteredExerciseIds.contains(it.id) || mistakenExerciseIds.contains(it.id) }
            if (unmastered.isNotEmpty() && !isAdaptiveRetryMode) {
                isAdaptiveRetryMode = true
                activeExerciseList = activeExerciseList + unmastered
                currentIndex++
                onProgressUpdate?.invoke(currentIndex)
                effectiveAudioService.speakTurkish("Şimdi pekiştirme zamanı! Eksik sorularımızı birlikte tamamlayalım!")
            } else {
                isLessonCompleted = true
                onProgressUpdate?.invoke(0)
            }
        }
    }

    fun handleExerciseMistake(ex: Exercise) {
        userInteractedCurrentQuestion = true
        questionAttempts++
        if (ex.type != ExerciseType.FLASHCARD) {
            firstTryMistakenIds.add(ex.id)
            mistakenExerciseIds.add(ex.id)
            masteredExerciseIds.remove(ex.id)
            if (hearts > 1) hearts--
            onPlayWrongFx()
        }

        if (questionAttempts >= 3) {
            // 3 denemede olmadıysa çocuk sıkılmasın:
            pokoHintText = "Harika çaba! 🌟 Bunu dersin sonuna bıraktık, sonra tekrar deneyeceğiz!"
            effectiveAudioService.speakTurkish("Harika çaba! Bunu şimdilik dersin sonuna bırakıyoruz.")
            if (!activeExerciseList.takeLast(2).any { it.id == ex.id }) {
                activeExerciseList = activeExerciseList + ex
            }
            coroutineScope.launch {
                delay(1300)
                advanceToNext()
            }
        } else {
            val target = when (ex.type) {
                ExerciseType.SENTENCE_BUILDER -> ex.promptEn.ifBlank { ex.sentenceTokens.joinToString(" ") }
                else -> ex.targetWord.english
            }
            effectiveAudioService.speakRetry(
                retryTr = "Tekrar deneyelim!",
                targetEn = target,
                onComplete = {
                    if (ex.type == ExerciseType.FLASHCARD) {
                        triggerAutoListening(target)
                    }
                }
            )
        }
    }

    // 3-Second Inactivity Hint Trigger (Helpful pedagogical hints without answer spoilers)
    LaunchedEffect(currentExercise?.id) {
        userInteractedCurrentQuestion = false
        pokoHintText = null
        questionAttempts = 0
        delay(3000)
        if (!userInteractedCurrentQuestion && currentExercise != null && !isLessonCompleted) {
            val hint = when (currentExercise.type) {
                ExerciseType.FLASHCARD -> "İpucu: Kelimeyi duymak için 'Dinle' butonuna dokunabilirsin veya mikrofona '${currentExercise.targetWord.english}' de!"
                ExerciseType.AUDIO_QUIZ -> "İpucu: Aradığın Türkçe anlam '${currentExercise.targetWord.turkish.firstOrNull()?.uppercase()}' harfiyle başlıyor! 'Dinle' butonuna basıp tekrar dinle."
                ExerciseType.PICTURE_MATCH -> "İpucu: Bu kelimenin Türkçe karşılığı '${currentExercise.targetWord.turkish.firstOrNull()?.uppercase()}' harfiyle başlıyor! Doğru karta dokun."
                ExerciseType.SENTENCE_BUILDER -> "İpucu: Cümleye ilk kelimeyle başla! Kelimelere sırayla dokun."
                ExerciseType.BALLOON_POP -> "İpucu: Aradığın İngilizce kelime '${currentExercise.targetWord.english.firstOrNull()?.uppercase()}' harfiyle başlıyor! Balona dokun."
                ExerciseType.WORD_BUILDER -> "İpucu: Kelime '${currentExercise.targetWord.english.firstOrNull()?.uppercase()}' harfiyle başlıyor! Harflere dokun."
            }
            // Visual-only hint balloon without audio pollution
            pokoHintText = hint
        }
    }

    // Automated Voice Guidance for each exercise (Clean native pronunciation; NO hint TTS or audio pollution)
    LaunchedEffect(currentIndex, activeExerciseList) {
        effectiveSpeechService.cancelListening()
        effectiveAudioService.stopSpeech()
        delay(200)

        currentExercise?.let { ex ->
            when (ex.type) {
                ExerciseType.FLASHCARD -> {
                    // Sadece hedef İngilizce kelimeyi net seslendir ve kelime biter bitmez MİKROFONU OTOMATİK AÇ!
                    mascotGuideText = "${ex.targetWord.english} (${ex.targetWord.turkish})"
                    effectiveAudioService.speakEnglish(ex.targetWord.english, false) {
                        triggerAutoListening(ex.targetWord.english)
                    }
                }

                ExerciseType.AUDIO_QUIZ -> {
                    mascotGuideText = "Sesi dinle ve doğru Türkçe anlamına dokun!"
                    effectiveAudioService.speakEnglish(ex.targetWord.english, false)
                }

                ExerciseType.PICTURE_MATCH -> {
                    mascotGuideText = "\"${ex.targetWord.english}\" kelimesini anlatan kartı seç!"
                    effectiveAudioService.speakEnglish(ex.targetWord.english, false)
                }

                ExerciseType.SENTENCE_BUILDER -> {
                    val targetSentenceEn = ex.promptEn.ifBlank { ex.sentenceTokens.joinToString(" ") }
                    mascotGuideText = "Cümleyi sırayla oluştur!"
                    effectiveAudioService.speakEnglish(targetSentenceEn, false)
                }

                ExerciseType.BALLOON_POP -> {
                    mascotGuideText = "\"${ex.targetWord.turkish}\" balonunu patlat!"
                    effectiveAudioService.speakEnglish(ex.targetWord.english, false)
                }

                ExerciseType.WORD_BUILDER -> {
                    mascotGuideText = "\"${ex.targetWord.turkish}\" kelimesini harflerle yaz!"
                    effectiveAudioService.speakEnglish(ex.targetWord.english, false)
                }
            }
        }
    }

    // Auto-advancement and speech evaluation handler for speech recognition
    LaunchedEffect(speechState, lastEvaluation) {
        val ex = currentExercise ?: return@LaunchedEffect

        if (speechState == SpeechState.SUCCESS && lastEvaluation != null) {
            val eval = lastEvaluation!!
            userInteractedCurrentQuestion = true
            pokoHintText = null
            onPlayCorrectFx()
            correctAnswersCount++
            score += 120
            if (!masteredExerciseIds.contains(ex.id)) {
                masteredExerciseIds.add(ex.id)
            }
            mistakenExerciseIds.remove(ex.id)

            effectiveAudioService.speakPraise("Harika!")
            coroutineScope.launch {
                delay(750)
                advanceToNext()
            }
        } else if (speechState == SpeechState.TRY_AGAIN && lastEvaluation != null) {
            handleExerciseMistake(ex)
        }
    }

    val isPerfectScore = mistakenExerciseIds.isEmpty()

    if (isLessonCompleted || currentExercise == null) {
        LessonCompleteView(
            lesson = lesson,
            score = score,
            correctCount = correctAnswersCount,
            totalCount = allOriginalExercises.size,
            heartsLeft = hearts,
            isPerfectScore = isPerfectScore,
            onPlayFanfareFx = onPlayFanfareFx,
            onRetry = {
                val pendingMistakes = allOriginalExercises.filter { mistakenExerciseIds.contains(it.id) || !masteredExerciseIds.contains(it.id) }
                activeExerciseList = if (pendingMistakes.isNotEmpty()) pendingMistakes else allOriginalExercises
                mistakenExerciseIds.clear()
                isAdaptiveRetryMode = true
                currentIndex = 0
                hearts = 3
                isLessonCompleted = false
            },
            onContinue = { isPerfect ->
                val stars = if (isPerfect) 3 else (if (hearts >= 2) 2 else 1)
                onFinishLesson(stars, score, correctAnswersCount, allOriginalExercises.size, isPerfect)
            }
        )
    } else {
        var isPaused by remember { mutableStateOf(false) }

    if (isPaused) {
        PauseGameDialog(
            onResume = { isPaused = false },
            onRestart = {
                isPaused = false
                currentIndex = 0
                hearts = 3
                score = 0
                correctAnswersCount = 0
                mistakenExerciseIds.clear()
                masteredExerciseIds.clear()
                isLessonCompleted = false
            },
            onExit = {
                isPaused = false
                onClose()
            }
        )
    }

    StadiumArenaBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Activity Header with Buddy-style Pause, Meter & Coins
            BuddyTopBar(
                currentIndex = currentIndex + 1,
                totalCount = totalExercises,
                hearts = hearts,
                score = score,
                isAdaptiveRetry = isAdaptiveRetryMode,
                onPauseClick = { isPaused = true }
            )

            // 1. Interactive Exercise Area (DEDICATED SAFE ZONE: FULL VISIBILITY & ZERO OVERLAP)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                key(currentExercise.id) {
                        when (currentExercise.type) {
                            ExerciseType.FLASHCARD -> {
                                FlashcardExercise(
                                    exercise = currentExercise,
                                    previousExercise = activeExerciseList.getOrNull(currentIndex - 1),
                                    nextExercise = activeExerciseList.getOrNull(currentIndex + 1),
                                    speechService = effectiveSpeechService,
                                    audioService = effectiveAudioService,
                                    hasAudioPermission = hasAudioPermission,
                                    onRequestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                    onSpeak = { textToSpeak, isSlow ->
                                        effectiveAudioService.speakEnglish(textToSpeak, isSlow) {
                                            triggerAutoListening(textToSpeak)
                                        }
                                    },
                                    onTriggerListening = {
                                        triggerAutoListening(currentExercise.targetWord.english)
                                    },
                                    onNext = {
                                        correctAnswersCount++
                                        score += 100
                                        onPlayCorrectFx()
                                        if (!masteredExerciseIds.contains(currentExercise.id)) {
                                            masteredExerciseIds.add(currentExercise.id)
                                        }
                                        mistakenExerciseIds.remove(currentExercise.id)
                                        advanceToNext()
                                    }
                                )
                            }

                    ExerciseType.PICTURE_MATCH, ExerciseType.AUDIO_QUIZ -> {
                        MultipleChoiceExercise(
                            exercise = currentExercise,
                            speechService = effectiveSpeechService,
                            audioService = effectiveAudioService,
                            isAudioMode = currentExercise.type == ExerciseType.AUDIO_QUIZ,
                            hasAudioPermission = hasAudioPermission,
                            onRequestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            onSpeak = { textToSpeak, isSlow ->
                                effectiveAudioService.speakEnglish(textToSpeak, isSlow)
                            },
                            onOptionSelected = { isCorrect ->
                                userInteractedCurrentQuestion = true
                                pokoHintText = null
                                if (isCorrect) {
                                    correctAnswersCount++
                                    score += 120
                                    onPlayCorrectFx()
                                    if (!masteredExerciseIds.contains(currentExercise.id)) {
                                        masteredExerciseIds.add(currentExercise.id)
                                    }
                                    mistakenExerciseIds.remove(currentExercise.id)
                                    effectiveAudioService.speakPraise("Harika!")
                                    coroutineScope.launch {
                                        delay(750)
                                        advanceToNext()
                                    }
                                } else {
                                    handleExerciseMistake(currentExercise)
                                }
                            },
                            onNext = { advanceToNext() }
                        )
                    }

                    ExerciseType.BALLOON_POP -> {
                        BalloonPopExercise(
                            exercise = currentExercise,
                            onPop = { isCorrect ->
                                userInteractedCurrentQuestion = true
                                pokoHintText = null
                                onPlayPopFx()
                                if (isCorrect) {
                                    correctAnswersCount++
                                    score += 150
                                    onPlayCorrectFx()
                                    if (!masteredExerciseIds.contains(currentExercise.id)) {
                                        masteredExerciseIds.add(currentExercise.id)
                                    }
                                    mistakenExerciseIds.remove(currentExercise.id)
                                    effectiveAudioService.speakPraise("Harika!")
                                    coroutineScope.launch {
                                        delay(650)
                                        advanceToNext()
                                    }
                                } else {
                                    handleExerciseMistake(currentExercise)
                                }
                            }
                        )
                    }

                    ExerciseType.WORD_BUILDER -> {
                        WordBuilderExercise(
                            exercise = currentExercise,
                            onSpeak = { textToSpeak, _ ->
                                userInteractedCurrentQuestion = true
                                pokoHintText = null
                                effectiveAudioService.speakEnglish(textToSpeak, isSlow = true)
                            },
                            onCompleted = { isCorrect ->
                                userInteractedCurrentQuestion = true
                                pokoHintText = null
                                if (isCorrect) {
                                    correctAnswersCount++
                                    score += 150
                                    onPlayCorrectFx()
                                    if (!masteredExerciseIds.contains(currentExercise.id)) {
                                        masteredExerciseIds.add(currentExercise.id)
                                    }
                                    mistakenExerciseIds.remove(currentExercise.id)
                                    effectiveAudioService.speakPraise("Süper!")
                                    coroutineScope.launch {
                                        delay(750)
                                        advanceToNext()
                                    }
                                } else {
                                    handleExerciseMistake(currentExercise)
                                }
                            },
                            onNext = { advanceToNext() }
                        )
                    }

                    ExerciseType.SENTENCE_BUILDER -> {
                        SentenceBuilderExercise(
                            exercise = currentExercise,
                            speechService = effectiveSpeechService,
                            audioService = effectiveAudioService,
                            hasAudioPermission = hasAudioPermission,
                            onRequestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            onSpeak = { textToSpeak, isSlow ->
                                userInteractedCurrentQuestion = true
                                pokoHintText = null
                                effectiveAudioService.speakEnglish(textToSpeak, isSlow)
                            },
                            onCompleted = { isCorrect ->
                                userInteractedCurrentQuestion = true
                                pokoHintText = null
                                if (isCorrect) {
                                    correctAnswersCount++
                                    score += 150
                                    onPlayCorrectFx()
                                    if (!masteredExerciseIds.contains(currentExercise.id)) {
                                        masteredExerciseIds.add(currentExercise.id)
                                    }
                                    mistakenExerciseIds.remove(currentExercise.id)
                                    effectiveAudioService.speakPraise("Harika!")
                                    coroutineScope.launch {
                                        delay(750)
                                        advanceToNext()
                                    }
                                } else {
                                    handleExerciseMistake(currentExercise)
                                }
                            },
                            onNext = { advanceToNext() }
                        )
                    }
                }
            }
        }

        // 2. Mascot Safe Zone Stage (DEDICATED BOTTOM RUNWAY: ZERO OVERLAP FOR CARDS & QUESTIONS)
        MascotSafeZoneStage(
            mood = if (pokoHintText != null) PokoMood.THINKING else if (lastEvaluation?.isPassed == true) PokoMood.CELEBRATING else PokoMood.HAPPY,
            guideOrHintText = pokoHintText ?: if (mascotGuideText.isNotBlank()) mascotGuideText else null,
            targetWord = if (currentExercise?.type == ExerciseType.AUDIO_QUIZ) null else currentExercise?.targetWord,
            isCompanionCheering = lastEvaluation?.isPassed == true,
            equippedCostumeId = equippedCostumeId,
            onMascotClick = {
                effectiveAudioService.speakPraise("Güç seninle genç Jedi! Harika gidiyorsun! ⚔️")
            },
            onSpeechClick = {
                val target = when (currentExercise?.type) {
                    ExerciseType.SENTENCE_BUILDER -> currentExercise.promptEn.ifBlank { currentExercise.sentenceTokens.joinToString(" ") }
                    else -> currentExercise?.targetWord?.english ?: ""
                }
                if (target.isNotBlank()) {
                    effectiveAudioService.speakEnglish(target, false)
                }
            },
            onTurkishSpeechClick = {
                val trTarget = currentExercise?.targetWord?.turkish ?: ""
                if (trTarget.isNotBlank()) {
                    effectiveAudioService.speakTurkish(trTarget)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(122.dp)
                .padding(horizontal = 14.dp, vertical = 2.dp)
        )
    }
}
}
}

// ----------------------------------------------------
// DEDICATED MASCOT SAFE ZONE STAGE (BOTTOM RUNWAY WITH 100% TURKISH SUBTITLES)
// ----------------------------------------------------
@Composable
private fun MascotSafeZoneStage(
    mood: PokoMood,
    guideOrHintText: String?,
    targetWord: WordItem?,
    isCompanionCheering: Boolean,
    equippedCostumeId: String = "costume_default",
    onMascotClick: (() -> Unit)? = null,
    onSpeechClick: () -> Unit,
    onTurkishSpeechClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "safe_zone_mascot_anim")
    val walkOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_walk"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: 3D Living Glowing Lego Darth Vader Mascot (Stationed safely on bottom track)
        Box(
            modifier = Modifier
                .offset(x = walkOffset.dp)
                .testTag("safe_zone_poko"),
            contentAlignment = Alignment.Center
        ) {
            PokoMascot(
                size = 90.dp,
                mood = mood,
                costumeId = equippedCostumeId,
                isJetFlying = mood != PokoMood.THINKING,
                isWalking = mood == PokoMood.HAPPY,
                isWaving = mood == PokoMood.TALKING || mood == PokoMood.CELEBRATING || guideOrHintText != null,
                onMascotClick = onMascotClick
            )
        }

        // Center: Dynamic Safe Subtitle Capsule (Zero overlap with exercise cards)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.98f),
            shadowElevation = 6.dp,
            border = BorderStroke(2.dp, Color(0xFF818CF8)),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .testTag("safe_zone_hint_bubble")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                // Header pill: "CANLI ALT YAZI"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF22C55E), CircleShape)
                        )
                        Text(
                            text = "TÜRKÇE ALT YAZI",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4F46E5),
                            fontSize = 9.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // English sound icon
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEEF2FF),
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { onSpeechClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "İngilizce Dinle",
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Turkish sound icon
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFEF2F2),
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { onTurkishSpeechClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🇹🇷", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Text: Spoken English target & Turkish translation
                if (targetWord != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇬🇧 ${targetWord.english}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "🇹🇷 ${targetWord.turkish}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4338CA),
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else if (!guideOrHintText.isNullOrBlank()) {
                    Text(
                        text = guideOrHintText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Right: Cheering Companion Gigi (Stationed safely on bottom track)
        Box(
            modifier = Modifier.testTag("safe_zone_companion"),
            contentAlignment = Alignment.Center
        ) {
            CompanionMascot(
                size = 72.dp,
                isCheering = isCompanionCheering
            )
        }
    }
}

// ----------------------------------------------------
// BUDDY-STYLE TOP BAR (PAUSE, METER & COIN CAPSULE)
// ----------------------------------------------------
@Composable
private fun BuddyTopBar(
    currentIndex: Int,
    totalCount: Int,
    hearts: Int,
    score: Int,
    isAdaptiveRetry: Boolean = false,
    onPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Purple 3D Squircle Pause button with dark border (matching screenshot)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF8B5CF6),
            border = BorderStroke(3.dp, Color(0xFF1E1B4B)),
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(46.dp)
                .clickable { onPauseClick() }
                .testTag("buddy_pause_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.width(5.dp).height(18.dp).background(Color.White, RoundedCornerShape(3.dp)))
                    Box(modifier = Modifier.width(5.dp).height(18.dp).background(Color.White, RoundedCornerShape(3.dp)))
                }
            }
        }

        // 2. Center Sleek Dark Capsule Progress Meter (matching screenshot)
        Surface(
            color = Color(0xFF1E1B4B).copy(alpha = 0.88f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF4C1D95)),
            modifier = Modifier.height(30.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isAdaptiveRetry) {
                    Text(
                        text = "🎯 Akıllı Tekrar: $currentIndex/$totalCount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SunGold,
                            fontSize = 11.sp
                        )
                    )
                } else {
                    repeat(totalCount.coerceAtMost(10)) { index ->
                        val isDone = index < currentIndex
                        val isCurrent = index == currentIndex - 1
                        Box(
                            modifier = Modifier
                                .size(if (isCurrent) 10.dp else 7.dp)
                                .background(
                                    when {
                                        isCurrent -> Color(0xFF38BDF8)
                                        isDone -> Color(0xFF4ADE80)
                                        else -> Color(0xFF64748B)
                                    },
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }

        // 3. Right Sleek Dark Capsule Coins counter (matching screenshot `294 🪙`)
        Surface(
            color = Color(0xFF1E1B4B).copy(alpha = 0.88f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF4C1D95)),
            modifier = Modifier.height(34.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            Brush.radialGradient(listOf(Color(0xFFFFDF00), Color(0xFFF59E0B))),
                            CircleShape
                        )
                        .border(1.5.dp, Color(0xFFB45309), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🪙", fontSize = 11.sp)
                }
            }
        }
    }
}

// ----------------------------------------------------
// PAUSE GAME DIALOG
// ----------------------------------------------------
@Composable
private fun PauseGameDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(3.dp, Color(0xFF8B5CF6)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⏸️ Oyun Duraklatıldı",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1B4B)
                    )
                )
                Spacer(modifier = Modifier.height(18.dp))
                Kid3DButton(
                    text = "Devam Et ▶️",
                    onClick = onResume,
                    color = LeafGreen,
                    shadowColor = Color(0xFF15803D),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Kid3DButton(
                    text = "Tekrar Başlat 🔄",
                    onClick = onRestart,
                    color = SunGold,
                    shadowColor = Color(0xFFD97706),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Kid3DButton(
                    text = "Dersten Çık ❌",
                    onClick = onExit,
                    color = CandyRed,
                    shadowColor = Color(0xFF991B1B),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ----------------------------------------------------
// STADIUM ARENA BACKGROUND (RUNNING TRACK & SUNSET SKY)
// ----------------------------------------------------
@Composable
private fun StadiumArenaBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Sunset Sky Gradient (top half)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF312E81),
                        Color(0xFF4C1D95),
                        Color(0xFF701A75),
                        Color(0xFFBE185D),
                        Color(0xFFF97316),
                        Color(0xFFFDE047)
                    ),
                    startY = 0f,
                    endY = h * 0.50f
                ),
                topLeft = Offset(0f, 0f),
                size = Size(w, h * 0.52f)
            )

            // 2. Stadium Bleachers Silhouette
            drawRect(
                color = Color(0xFF1E1B4B).copy(alpha = 0.50f),
                topLeft = Offset(0f, h * 0.36f),
                size = Size(w, h * 0.14f)
            )

            // 3. Running Track Magenta & Purple
            val trackPath = Path().apply {
                moveTo(0f, h * 0.48f)
                lineTo(w, h * 0.48f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                trackPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF701A75),
                        Color(0xFF831843),
                        Color(0xFF9D174D),
                        Color(0xFFBE185D)
                    ),
                    startY = h * 0.48f,
                    endY = h
                )
            )

            // 4. Converging White Lane Lines (perspective)
            val horizonY = h * 0.48f
            val vanishingPointX = w * 0.5f
            val lanes = 6
            for (i in 0..lanes) {
                val startBottomX = -w * 0.2f + (w * 1.4f / lanes) * i
                val startTopX = vanishingPointX - w * 0.35f + (w * 0.70f / lanes) * i
                drawLine(
                    color = Color.White.copy(alpha = 0.65f),
                    start = Offset(startTopX, horizonY),
                    end = Offset(startBottomX, h),
                    strokeWidth = 3f
                )
            }

            // 5. Finish Line
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = Offset(0f, h * 0.90f),
                end = Offset(w, h * 0.90f),
                strokeWidth = 5f
            )

            // 6. Stadium Floodlights Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(w * 0.12f, h * 0.10f),
                    radius = w * 0.25f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFDE047).copy(alpha = 0.30f), Color.Transparent),
                    center = Offset(w * 0.88f, h * 0.10f),
                    radius = w * 0.25f
                )
            )
        }

        content()
    }
}

// ----------------------------------------------------
// INTERACTIVE PULSE ORB BUTTON (BUDDY-STYLE SCREENSHOT 1)
// ----------------------------------------------------
@Composable
private fun PulseOrbButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    Box(
        modifier = modifier
            .size(72.dp)
            .scale(scale)
            .clickable { onClick() }
            .testTag("interactive_pulse_orb"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)

            // Outer soft glow halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE879F9).copy(alpha = 0.45f), Color.Transparent),
                    radius = w * 0.48f,
                    center = center
                )
            )

            // Center Orb sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFF472B6), Color(0xFFC084FC), Color(0xFF7E22CE)),
                    radius = w * 0.35f,
                    center = Offset(center.x - 4f, center.y - 4f)
                ),
                radius = w * 0.34f,
                center = center
            )

            // Inner gloss specular highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.65f),
                radius = w * 0.10f,
                center = Offset(center.x - w * 0.10f, center.y - h * 0.10f)
            )

            // Orbiting particle dots
            val orbitRadius = w * 0.40f
            val rad = Math.toRadians(orbitAngle.toDouble())
            val orbX = center.x + (orbitRadius * Math.cos(rad)).toFloat()
            val orbY = center.y + (orbitRadius * Math.sin(rad)).toFloat()
            drawCircle(color = Color.White, radius = 3.5f, center = Offset(orbX, orbY))

            val rad2 = Math.toRadians((orbitAngle + 180).toDouble())
            val orbX2 = center.x + (orbitRadius * Math.cos(rad2)).toFloat()
            val orbY2 = center.y + (orbitRadius * Math.sin(rad2)).toFloat()
            drawCircle(color = Color(0xFF38BDF8), radius = 3f, center = Offset(orbX2, orbY2))
        }

        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Seslendir",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

// ----------------------------------------------------
// FLASHCARD VIEW (BUDDY-STYLE 3D CAROUSEL & PULSE ORB)
// ----------------------------------------------------
@Composable
private fun FlashcardExercise(
    exercise: Exercise,
    previousExercise: Exercise? = null,
    nextExercise: Exercise? = null,
    speechService: SpeechRecognitionService,
    audioService: AudioService,
    hasAudioPermission: Boolean,
    onRequestPermission: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    onTriggerListening: () -> Unit,
    onNext: () -> Unit
) {
    val word = exercise.targetWord
    val speechState by speechService.speechState.collectAsState()
    val recognizedText by speechService.recognizedText.collectAsState()
    var showSpeechDialog by remember { mutableStateOf(false) }

    if (showSpeechDialog) {
        SpeechPracticeDialog(
            targetText = word.english,
            translationTr = word.turkish,
            phonetic = word.phonetic,
            emoji = word.emoji,
            speechService = speechService,
            audioService = audioService,
            onSpeakTts = { textToSpeak, isSlow -> onSpeak(textToSpeak, isSlow) },
            onSuccess = {
                showSpeechDialog = false
                onNext()
            },
            onDismiss = { showSpeechDialog = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 3D Carousel Stage: Left Peek Card, Center Active Card, Right Peek Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Left Preview Card (previous)
            if (previousExercise != null) {
                Card(
                    modifier = Modifier
                        .offset(x = (-95).dp)
                        .scale(0.74f)
                        .rotate(-6f)
                        .width(160.dp)
                        .height(180.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                    border = BorderStroke(3.dp, Color(0xFF2DD4BF).copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = previousExercise.targetWord.emoji, fontSize = 52.sp)
                    }
                }
            }

            // Right Preview Card (next)
            if (nextExercise != null) {
                Card(
                    modifier = Modifier
                        .offset(x = 95.dp)
                        .scale(0.74f)
                        .rotate(6f)
                        .width(160.dp)
                        .height(180.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                    border = BorderStroke(3.dp, Color(0xFF2DD4BF).copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = nextExercise.targetWord.emoji, fontSize = 52.sp)
                    }
                }
            }

            // Center Active 3D Card (Prominent & Glossy Mint Border)
            Card(
                modifier = Modifier
                    .width(225.dp)
                    .shadow(12.dp, RoundedCornerShape(28.dp))
                    .testTag("flashcard_center_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(4.dp, Color(0xFF2DD4BF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Illustration Emoji
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFE0F2FE), Color(0xFFF0FDF4), Color.White)
                                ),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = word.emoji,
                            fontSize = 68.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Target English Word
                    Text(
                        text = word.english,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            fontSize = 24.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    if (word.phonetic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PlayfulPurple.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayfulPurple.copy(alpha = 0.3f)),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "🗣️ Okunuşu: [ ${word.phonetic} ]",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PlayfulPurple,
                                    fontSize = 16.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Mint Curved Badge for Turkish Meaning
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF2DD4BF),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = word.turkish,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 17.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Poko's Memory Secret Mnemonic Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = word.displayMnemonic,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    fontSize = 11.sp,
                                    lineHeight = 13.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        val isSpeaking by audioService.isSpeaking.collectAsState()

        Spacer(modifier = Modifier.height(14.dp))

        // Large Ergonomic Audio Controls (Dinle & Yavaş - Kid friendly & spacious)
        Row(
            modifier = Modifier
                .fillMaxWidth(0.96f),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Kid3DButton(
                text = if (isSpeaking) "Dinletiliyor..." else "Dinle",
                emoji = "🔊",
                onClick = { onSpeak(word.english, false) },
                color = if (isSpeaking) Color(0xFF0284C7) else BrandBlue,
                shadowColor = Color(0xFF1E40AF),
                paddingVertical = 18.dp,
                fontSize = 21.sp,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 64.dp),
                testTag = "listen_normal_btn"
            )

            Kid3DButton(
                text = "Yavaş",
                emoji = "🐢",
                onClick = { onSpeak(word.english, true) },
                color = SunGold,
                shadowColor = Color(0xFFD97706),
                paddingVertical = 18.dp,
                fontSize = 21.sp,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 64.dp),
                testTag = "listen_slow_btn"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Direct One-Touch Microphone Trigger (Extra Wide, Spacious & Fully Synchronized)
        val isListening = speechState == SpeechState.LISTENING
        val isProcessing = speechState == SpeechState.PROCESSING
        val isSuccess = speechState == SpeechState.SUCCESS

        Kid3DButton(
            text = when {
                isSuccess -> "Harika! İlerleniyor... 🌟"
                isListening -> "Dinliyorum... \"${word.english}\" de! 🎙️"
                isProcessing -> "Kontrol Ediliyor... ⏳"
                else -> "Mikrofona Söyle 🎤"
            },
            onClick = {
                if (isListening) {
                    speechService.stopListening()
                } else {
                    onTriggerListening()
                }
            },
            color = when {
                isSuccess -> LeafGreen
                isListening -> CandyRed
                else -> PlayfulPurple
            },
            shadowColor = when {
                isSuccess -> Color(0xFF15803D)
                isListening -> Color(0xFF991B1B)
                else -> Color(0xFF6B21A8)
            },
            paddingVertical = 20.dp,
            fontSize = 22.sp,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(min = 70.dp),
            testTag = "flashcard_mic_btn"
        )

        if (recognizedText.isNotBlank()) {
            Surface(
                color = if (isSuccess) Color(0xFFDCFCE7) else Color(0xFFFEF2F2),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, if (isSuccess) LeafGreen else Color(0xFFFECACA)),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = if (isSuccess) "🎉 Harika Söyledin: \"$recognizedText\" ⭐⭐⭐" else "Duydum: \"$recognizedText\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = if (isSuccess) LeafGreen else CandyRed,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Kid3DButton(
            text = "Öğrendim! İlerle 🚀",
            onClick = onNext,
            color = LeafGreen,
            shadowColor = Color(0xFF15803D),
            paddingVertical = 20.dp,
            fontSize = 22.sp,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(min = 70.dp),
            testTag = "flashcard_continue_btn"
        )
    }
}

// ----------------------------------------------------
// MULTIPLE CHOICE / AUDIO QUIZ VIEW (3D GAMIFIED CARDS)
// ----------------------------------------------------
@Composable
private fun MultipleChoiceExercise(
    exercise: Exercise,
    speechService: SpeechRecognitionService,
    audioService: AudioService,
    isAudioMode: Boolean,
    hasAudioPermission: Boolean,
    onRequestPermission: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    onOptionSelected: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val wrongSelectedOptions = remember { mutableStateListOf<WordOption>() }
    var correctSelectedOption by remember { mutableStateOf<WordOption?>(null) }
    val isSpeaking by audioService.isSpeaking.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prompt with glowing card
        Surface(
            color = Color(0xFF1E1B4B).copy(alpha = 0.85f),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(2.dp, Color(0xFF2DD4BF)),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = exercise.promptTr,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
            )
        }

        if (isAudioMode) {
            // Audio Quiz Big Ergonomic Listen Button
            Kid3DButton(
                text = if (isSpeaking) "Dinletiliyor..." else "Sesi Dinle",
                emoji = "🔊",
                onClick = { onSpeak(exercise.targetWord.english, false) },
                color = if (isSpeaking) Color(0xFF0284C7) else BrandBlue,
                shadowColor = Color(0xFF1E40AF),
                paddingVertical = 18.dp,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .heightIn(min = 66.dp)
                    .padding(bottom = 12.dp),
                testTag = "audio_quiz_speak_btn"
            )
        }

        if (wrongSelectedOptions.isNotEmpty() && correctSelectedOption == null) {
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "💡 Yanlış seçim! Diğer kartları dene.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CandyRed
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Gamified 3D Card Selection (matching screenshots & logic requirements)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(0.96f)
        ) {
            items(exercise.options) { option ->
                val isCorrectSelected = correctSelectedOption == option
                val isWrongSelected = wrongSelectedOptions.contains(option)
                val isClickable = correctSelectedOption == null && !isWrongSelected

                val cardBgColor = when {
                    isCorrectSelected -> Color(0xFFDCFCE7)
                    isWrongSelected -> Color(0xFFFEE2E2)
                    else -> Color.White
                }

                val borderColor = when {
                    isCorrectSelected -> LeafGreen
                    isWrongSelected -> CandyRed
                    else -> Color(0xFF2DD4BF)
                }

                // Soru ve seçenek dili mantık uyumu (Kesinlikle ipucu vermez):
                // 1. Soru İngilizce kelimeyi soruyorsa ("'Book' kelimesini anlatan kartı seç!") veya sesli soru ise:
                //    Şıklarda direkt İngilizce kelime yazmaz, Türkçe anlamı yer alır ("Kitap").
                // 2. Soru Türkçe kelimeyi soruyorsa ("'Defter' hangisi?" veya "'Defter' anlamına gelen kartı seç!"):
                //    Şıklarda İngilizce karşılığı yer alır ("Notebook"). Asla sorudaki Türkçe kelime şıkta tekrar edilmez.
                val promptClean = exercise.promptTr.lowercase(java.util.Locale.ROOT)
                val targetEnClean = exercise.targetWord.english.lowercase(java.util.Locale.ROOT).trim()
                val targetTrClean = exercise.targetWord.turkish.substringBefore("(").substringBefore("/").trim().lowercase(java.util.Locale.ROOT)
                val optTrClean = option.translation.substringBefore("(").substringBefore("/").trim().lowercase(java.util.Locale.ROOT)
                val optEnClean = option.word.lowercase(java.util.Locale.ROOT).trim()

                var chosenText = when {
                    isAudioMode -> option.translation
                    targetEnClean.isNotBlank() && promptClean.contains(targetEnClean) -> option.translation
                    targetTrClean.isNotBlank() && promptClean.contains(targetTrClean) -> option.word
                    optTrClean.isNotBlank() && promptClean.contains(optTrClean) -> option.word
                    optEnClean.isNotBlank() && promptClean.contains(optEnClean) -> option.translation
                    else -> option.word
                }

                // Kesin ipucu engelleme kuralı: Şıktaki metin soru metninde geçiyorsa kesinlikle diğer dile çevir
                if (chosenText == option.translation && optTrClean.isNotBlank() && promptClean.contains(optTrClean)) {
                    chosenText = option.word
                } else if (chosenText == option.word && optEnClean.isNotBlank() && promptClean.contains(optEnClean)) {
                    chosenText = option.translation
                }

                val cardDisplayText = chosenText

                // 3D Tactile Game Option Card with Depth Shadow & Highlights
                val depthShadowColor = when {
                    isCorrectSelected -> Color(0xFF15803D)
                    isWrongSelected -> Color(0xFF991B1B)
                    else -> Color(0xFFCBD5E1)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(144.dp)
                        .testTag("option_card_${option.word.lowercase()}")
                ) {
                    // Bottom 3D shadow block
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(138.dp)
                            .offset(y = 5.dp)
                            .background(depthShadowColor, RoundedCornerShape(24.dp))
                    )

                    // Top interactive surface
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(138.dp)
                            .clickable(enabled = isClickable) {
                                if (option.isCorrect) {
                                    correctSelectedOption = option
                                    onOptionSelected(true)
                                } else {
                                    wrongSelectedOptions.add(option)
                                    onOptionSelected(false)
                                }
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        border = BorderStroke(2.5.dp, borderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Specular Gloss Highlight Top Ribbon
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Circular orb for emoji
                                Surface(
                                    shape = CircleShape,
                                    color = if (isCorrectSelected) Color(0xFFBBF7D0) else if (isWrongSelected) Color(0xFFFECACA) else Color(0xFFF1F5F9),
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isWrongSelected) "❌" else option.emoji,
                                            fontSize = 32.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = cardDisplayText,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = when {
                                            isCorrectSelected -> Color(0xFF15803D)
                                            isWrongSelected -> CandyRed
                                            else -> Color(0xFF0F172A)
                                        },
                                        fontSize = 17.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        if (correctSelectedOption != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Kid3DButton(
                text = "Sonraki Soru ➔",
                onClick = onNext,
                color = LeafGreen,
                shadowColor = Color(0xFF15803D),
                paddingVertical = 14.dp,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .heightIn(min = 54.dp),
                testTag = "multiple_choice_next_btn"
            )
        }
    }
}

// ----------------------------------------------------
// BALLOON POP VIEW
// ----------------------------------------------------
@Composable
private fun BalloonPopExercise(
    exercise: Exercise,
    onPop: (Boolean) -> Unit
) {
    val targetWord = exercise.targetWord
    val poppedIndices = remember { mutableStateListOf<Int>() }
    var isCorrectPopped by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "balloon_float")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.promptTr,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BrandBlue.copy(alpha = 0.12f),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Hedef: \"${targetWord.turkish}\" (${targetWord.emoji})",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                ),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        // Balloons
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(exercise.options.indices.toList()) { index ->
                val option = exercise.options[index]
                val isPopped = poppedIndices.contains(index)
                val balloonColors = listOf(BrandBlue, SunOrange, PlayfulPurple, LeafGreen)
                val balloonColor = balloonColors[index % balloonColors.size]
                val isClickable = !isPopped && !isCorrectPopped

                Box(
                    modifier = Modifier
                        .offset(y = if (index % 2 == 0) floatAnim.dp else (-floatAnim).dp)
                        .clickable(enabled = isClickable) {
                            poppedIndices.add(index)
                            if (option.isCorrect) {
                                isCorrectPopped = true
                                onPop(true)
                            } else {
                                onPop(false)
                            }
                        }
                        .testTag("balloon_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPopped) {
                        Text(text = if (option.isCorrect) "💥✨" else "💨❌", fontSize = 48.sp)
                    } else {
                        Surface(
                            shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp, bottomStart = 38.dp, bottomEnd = 16.dp),
                            color = balloonColor,
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(115.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = option.emoji, fontSize = 32.sp)
                                Text(
                                    text = option.word,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// WORD BUILDER VIEW (70% ASSISTANCE & NON-DESTRUCTIVE ERROR TRIMMING)
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordBuilderExercise(
    exercise: Exercise,
    onSpeak: (String, Boolean) -> Unit,
    onCompleted: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val targetWord = remember(exercise.targetWord.english) {
        exercise.targetWord.english.uppercase().filter { it.isLetter() }
    }
    val enteredLetters = remember { mutableStateListOf<String>() }
    val usedTileIndices = remember { mutableStateListOf<Int>() }
    var isSubmitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    // Beginner 70% Assistance:
    // Help the child with proactive glowing letter hints until 70% of the word length is reached!
    val assistanceCount = remember(targetWord.length) {
        kotlin.math.max(1, (targetWord.length * 0.70f + 0.5f).toInt())
    }
    var hintTileIndex by remember { mutableStateOf<Int?>(null) }
    var hintMessage by remember { mutableStateOf<String?>(null) }
    var userActionTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    // Smart Non-destructive Trimming:
    // Keeps correctly written letters up to the mistake, and only deletes the wrong suffix!
    fun trimIncorrectSuffix() {
        val correctPrefixCount = enteredLetters.indices.takeWhile { idx ->
            idx < targetWord.length && enteredLetters[idx].equals(targetWord[idx].toString(), ignoreCase = true)
        }.count()

        while (enteredLetters.size > correctPrefixCount) {
            val lastIdx = enteredLetters.lastIndex
            enteredLetters.removeAt(lastIdx)
            if (lastIdx < usedTileIndices.size) {
                usedTileIndices.removeAt(lastIdx)
            }
        }
        isSubmitted = false
        isCorrect = false
        userActionTimestamp = System.currentTimeMillis()
        hintTileIndex = null
        hintMessage = if (correctPrefixCount > 0) {
            "Doğru yazdığın $correctPrefixCount harf korundu! 🌟 Sadece yanlış harf silindi, kalan harfleri tamamla!"
        } else {
            "Hadi ilk harften başlayalım! ✨"
        }
    }

    LaunchedEffect(enteredLetters.size, isSubmitted, userActionTimestamp) {
        hintTileIndex = null
        hintMessage = null
        if (!isSubmitted && enteredLetters.size < targetWord.length) {
            if (enteredLetters.size < assistanceCount) {
                // Within first 70% of word: proactive instant beginner guidance!
                delay(350)
                val nextExpectedChar = targetWord[enteredLetters.size].toString()
                val targetIndex = exercise.letterTiles.indices.firstOrNull { idx ->
                    !usedTileIndices.contains(idx) && exercise.letterTiles[idx].equals(nextExpectedChar, ignoreCase = true)
                }
                if (targetIndex != null) {
                    hintTileIndex = targetIndex
                    hintMessage = "🌟 Sıradaki harf '$nextExpectedChar'! Parlayan harfe dokun!"
                }
            } else {
                // Remaining 30%: child completes independently, gentle hint after 2.5s
                hintMessage = "Harikasın! İlk %70'i geçtin, şimdi son harfi sen tamamla! 🚀"
                delay(2500)
                val nextExpectedChar = targetWord[enteredLetters.size].toString()
                val targetIndex = exercise.letterTiles.indices.firstOrNull { idx ->
                    !usedTileIndices.contains(idx) && exercise.letterTiles[idx].equals(nextExpectedChar, ignoreCase = true)
                }
                if (targetIndex != null) {
                    hintTileIndex = targetIndex
                    hintMessage = "İpucu: Sıradaki harf '$nextExpectedChar'! Dokun ve bitir! ✨"
                }
            }
        }
    }

    // Wobble shake and glow pulse animation for hinted tile
    val infiniteTransition = rememberInfiniteTransition(label = "hint_tile_shake")
    val shakeAngle by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.promptTr,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Large Emoji and Re-listen Sound Buttons with Slow Child Pronunciation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = exercise.targetWord.emoji,
                fontSize = 50.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Kid3DButton(
                text = "Yavaş Dinle 🐢",
                onClick = { onSpeak(exercise.targetWord.english, true) },
                color = SunGold,
                shadowColor = Color(0xFFD97706),
                paddingVertical = 10.dp,
                fontSize = 14.sp,
                modifier = Modifier.heightIn(min = 46.dp),
                testTag = "word_builder_slow_btn"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Kid3DButton(
                text = "Kelimeyi Dinle 🔊",
                onClick = { onSpeak(exercise.targetWord.english, true) },
                color = BrandBlue,
                shadowColor = Color(0xFF1E40AF),
                paddingVertical = 10.dp,
                fontSize = 14.sp,
                modifier = Modifier.heightIn(min = 46.dp),
                testTag = "word_builder_listen_btn"
            )
        }

        if (exercise.targetWord.phonetic.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PlayfulPurple.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlayfulPurple.copy(alpha = 0.25f)),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = "🗣️ Okunuşu: [ ${exercise.targetWord.phonetic} ]",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PlayfulPurple,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Responsive dynamic sizing for Slot Boxes based on targetWord length (Zero overflow)
        val wordLen = targetWord.length
        val slotBoxSize = when {
            wordLen <= 4 -> 52.dp
            wordLen <= 6 -> 44.dp
            wordLen <= 8 -> 36.dp
            wordLen <= 10 -> 31.dp
            else -> 27.dp
        }
        val slotTextSize = when {
            wordLen <= 4 -> 24.sp
            wordLen <= 6 -> 20.sp
            wordLen <= 8 -> 17.sp
            wordLen <= 10 -> 14.sp
            else -> 12.sp
        }
        val slotSpacing = when {
            wordLen <= 6 -> 6.dp
            wordLen <= 8 -> 4.dp
            else -> 3.dp
        }

        // Slot Boxes in a centered FlowRow (Guaranteed to wrap or scale cleanly on all screens)
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(bottom = 14.dp)
        ) {
            for (i in targetWord.indices) {
                val letter = enteredLetters.getOrNull(i) ?: ""
                val isSlotCorrect = i < targetWord.length && letter.equals(targetWord[i].toString(), ignoreCase = true)
                val boxBorderColor = when {
                    isSubmitted && isCorrect -> LeafGreen
                    isSubmitted && !isCorrect -> if (isSlotCorrect) LeafGreen else CandyRed
                    letter.isNotEmpty() -> BrandBlue
                    else -> Color(0xFFCBD5E1)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (letter.isNotEmpty()) Color.White else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(2.dp, boxBorderColor),
                    modifier = Modifier
                        .padding(horizontal = (slotSpacing / 2))
                        .size(slotBoxSize)
                        .clickable(enabled = letter.isNotEmpty() && (!isSubmitted || !isCorrect)) {
                            if (i < enteredLetters.size) {
                                enteredLetters.removeAt(i)
                                if (i < usedTileIndices.size) {
                                    usedTileIndices.removeAt(i)
                                }
                                userActionTimestamp = System.currentTimeMillis()
                                hintTileIndex = null
                                hintMessage = null
                                isSubmitted = false
                                isCorrect = false
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = slotTextSize,
                                color = if (isSubmitted && !isCorrect && !isSlotCorrect) CandyRed else BrandBlue
                            )
                        )
                    }
                }
            }
        }

        // Intelligent Poko Guidance & Encouragement Banner
        if (hintMessage != null) {
            Surface(
                color = if (hintTileIndex != null) Color(0xFFFEF9C3) else Color(0xFFDCFCE7),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, if (hintTileIndex != null) Color(0xFFFACC15) else LeafGreen),
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .testTag("word_builder_hint_banner")
            ) {
                Text(
                    text = hintMessage!!,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (hintTileIndex != null) Color(0xFF854D0E) else Color(0xFF15803D)
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        // Letter Tiles Grid (with dynamic responsive tile sizing & animated wobble hint)
        val tileCount = exercise.letterTiles.size
        val tileSize = when {
            tileCount <= 5 -> 54.dp
            tileCount <= 7 -> 46.dp
            tileCount <= 10 -> 39.dp
            tileCount <= 12 -> 34.dp
            else -> 30.dp
        }
        val tileTextSize = when {
            tileCount <= 5 -> 24.sp
            tileCount <= 7 -> 20.sp
            tileCount <= 10 -> 17.sp
            else -> 14.sp
        }
        val tilePadding = if (tileCount > 8) 3.dp else 4.dp

        FlowRow(
            modifier = Modifier.fillMaxWidth(0.96f),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercise.letterTiles.forEachIndexed { idx, charStr ->
                val isUsed = usedTileIndices.contains(idx)
                val isHinted = (idx == hintTileIndex)

                val tileScale = if (isHinted) pulseScale else 1f
                val tileRotate = if (isHinted) shakeAngle else 0f
                val tileBg = when {
                    isHinted -> Color(0xFFFEF08A)
                    isUsed -> Color(0xFFF1F5F9)
                    else -> Color.White
                }
                val tileBorder = when {
                    isHinted -> BorderStroke(3.dp, Color(0xFFEAB308))
                    isUsed -> BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                    else -> BorderStroke(2.dp, Color(0xFFCBD5E1))
                }
                val tileTextColor = when {
                    isHinted -> Color(0xFF854D0E)
                    isUsed -> Color(0xFF94A3B8)
                    else -> BrandBlue
                }

                Box(
                    modifier = Modifier
                        .padding(tilePadding)
                        .size(tileSize)
                        .scale(tileScale)
                        .rotate(tileRotate)
                        .shadow(if (isHinted) 10.dp else 4.dp, RoundedCornerShape(12.dp))
                        .background(tileBg, RoundedCornerShape(12.dp))
                        .border(tileBorder, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isUsed && !isCorrect) {
                            userActionTimestamp = System.currentTimeMillis()
                            hintTileIndex = null
                            hintMessage = null
                            if (isSubmitted && !isCorrect) {
                                // DO NOT wipe the whole word! Keep correct prefix!
                                trimIncorrectSuffix()
                            }
                            if (enteredLetters.size < targetWord.length) {
                                usedTileIndices.add(idx)
                                enteredLetters.add(charStr)
                                if (enteredLetters.size == targetWord.length) {
                                    val spelled = enteredLetters.joinToString("")
                                    isSubmitted = true
                                    val correct = spelled == targetWord
                                    isCorrect = correct
                                    onCompleted(correct)
                                    if (correct) {
                                        onSpeak(exercise.targetWord.english, true)
                                    } else {
                                        val correctPrefixCount = enteredLetters.indices.takeWhile { i ->
                                            i < targetWord.length && enteredLetters[i].equals(targetWord[i].toString(), ignoreCase = true)
                                        }.count()
                                        hintMessage = if (correctPrefixCount > 0) {
                                            "Doğru yazdığın $correctPrefixCount harf korundu! Sadece yanlış harf silinecek. 'Düzelt' butonuna bas! 🎯"
                                        } else {
                                            "Kelimeyi tekrar deneyelim! 💡"
                                        }
                                    }
                                }
                            }
                        }
                        .testTag("letter_tile_$idx"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = charStr,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = tileTextSize,
                            color = tileTextColor
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isSubmitted) {
            Kid3DButton(
                text = if (isCorrect) "Süper! İlerle 🚀" else "Yanlışı Düzelt & Devam Et 🔄",
                onClick = {
                    if (isCorrect) {
                        onNext()
                    } else {
                        trimIncorrectSuffix()
                    }
                },
                color = if (isCorrect) LeafGreen else SunOrange,
                shadowColor = if (isCorrect) Color(0xFF15803D) else Color(0xFFC2410C),
                modifier = Modifier.fillMaxWidth(0.85f),
                testTag = "word_builder_continue_btn"
            )
        }
    }
}

// ----------------------------------------------------
// SENTENCE BUILDER VIEW (HANDS-FREE AUTOMATED SPEECH)
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SentenceBuilderExercise(
    exercise: Exercise,
    speechService: SpeechRecognitionService,
    audioService: AudioService,
    hasAudioPermission: Boolean,
    onRequestPermission: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    onCompleted: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val selectedTokens = remember { mutableStateListOf<String>() }
    var isSubmitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    val correctSentence = remember(exercise.promptEn, exercise.sentenceTokens) {
        exercise.promptEn.ifBlank { exercise.sentenceTokens.joinToString(" ") }
    }

    // 3-Second Inactivity Hint (Strictly limited to 50% of sentence length)
    val expectedTokens = remember(correctSentence) { correctSentence.split(" ").filter { it.isNotBlank() } }
    val maxAllowedHints = remember(expectedTokens.size) { expectedTokens.size / 2 }
    var hintToken by remember { mutableStateOf<String?>(null) }
    var userActionTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(selectedTokens.size, isSubmitted, userActionTimestamp) {
        hintToken = null
        if (!isSubmitted && selectedTokens.size < expectedTokens.size) {
            delay(3000)
            if (selectedTokens.size < maxAllowedHints) {
                hintToken = expectedTokens.getOrNull(selectedTokens.size)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "sentence_hint_anim")
    val shakeAngle by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.promptTr,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Kid3DButton(
            text = "Cümleyi Dinle 🔊",
            onClick = { onSpeak(correctSentence, false) },
            color = BrandBlue,
            shadowColor = Color(0xFF1E40AF),
            paddingVertical = 8.dp,
            fontSize = 14.sp,
            modifier = Modifier.heightIn(min = 42.dp),
            testTag = "sentence_builder_listen_btn"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sentence Slot Display
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.dp, BrandBlue.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedTokens.isEmpty()) {
                    Text(
                        text = "Cümleyi oluşturmak için kelimelere sırayla dokun...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
                    )
                }
                selectedTokens.forEachIndexed { idx, token ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BrandBlue,
                        modifier = Modifier.clickable(enabled = !isSubmitted) {
                            selectedTokens.removeAt(idx)
                        }
                    ) {
                        Text(
                            text = token,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Available tokens
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercise.sentenceTokens.forEach { token ->
                val isUsed = selectedTokens.contains(token)
                val isHinted = (!isUsed && token == hintToken)

                val tokenScale = if (isHinted) pulseScale else 1f
                val tokenRotate = if (isHinted) shakeAngle else 0f
                val tokenBg = if (isHinted) Color(0xFFFEF08A) else (if (isUsed) Color(0xFFE2E8F0) else Color.White)
                val tokenBorder = if (isHinted) androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFEAB308)) else (if (isUsed) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)) else androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2DD4BF)))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokenBg,
                    border = tokenBorder,
                    shadowElevation = if (isHinted) 10.dp else (if (isUsed) 0.dp else 3.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .scale(tokenScale)
                        .rotate(tokenRotate)
                        .clickable(enabled = !isUsed && !isSubmitted) {
                            userActionTimestamp = System.currentTimeMillis()
                            hintToken = null
                            selectedTokens.add(token)
                        }
                ) {
                    Text(
                        text = token,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isHinted) Color(0xFF854D0E) else (if (isUsed) Color(0xFF94A3B8) else Color(0xFF1E293B))
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (!isSubmitted) {
            Kid3DButton(
                text = "Kontrol Et 🔍",
                onClick = {
                    val constructed = selectedTokens.joinToString(" ").trim().lowercase().replace(Regex("[^a-z0-9 ]"), "")
                    val expected = correctSentence.trim().lowercase().replace(Regex("[^a-z0-9 ]"), "")
                    isSubmitted = true
                    val correct = (constructed == expected)
                    isCorrect = correct
                    onCompleted(correct)
                    if (correct) onSpeak(correctSentence, false)
                },
                color = BrandBlue,
                shadowColor = Color(0xFF1E40AF),
                modifier = Modifier.fillMaxWidth(0.85f),
                enabled = selectedTokens.isNotEmpty()
            )
        } else {
            Kid3DButton(
                text = if (isCorrect) "Harika! İlerle 🚀" else "Tekrar Dene 🔄",
                onClick = {
                    if (isCorrect) {
                        onNext()
                    } else {
                        selectedTokens.clear()
                        isSubmitted = false
                    }
                },
                color = if (isCorrect) LeafGreen else SunOrange,
                shadowColor = if (isCorrect) Color(0xFF15803D) else Color(0xFFC2410C),
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}

// ----------------------------------------------------
// LESSON VICTORY CELEBRATION VIEW
// ----------------------------------------------------
@Composable
private fun LessonCompleteView(
    lesson: Lesson,
    score: Int,
    correctCount: Int,
    totalCount: Int,
    heartsLeft: Int,
    isPerfectScore: Boolean,
    onPlayFanfareFx: () -> Unit,
    onRetry: () -> Unit,
    onContinue: (isPerfectScore: Boolean) -> Unit
) {
    val stars = when {
        isPerfectScore -> 3
        heartsLeft >= 2 -> 2
        else -> 1
    }

    var hasContinued by remember { mutableStateOf(false) }
    fun triggerContinue(perfect: Boolean) {
        if (!hasContinued) {
            hasContinued = true
            onContinue(perfect)
        }
    }

    LaunchedEffect(Unit) {
        if (isPerfectScore) onPlayFanfareFx()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FC))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PokoMascot(
            size = 120.dp,
            mood = if (isPerfectScore) PokoMood.CELEBRATING else PokoMood.THINKING,
            speechBubbleText = if (isPerfectScore) "Tebrikler! %100 Başarı ile Kilidi Açtın! 🏆" else "Eksik soruları çözerek kilidi açabilirsin! 🎯"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isPerfectScore) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LeafGreen),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "KİLİT AÇILDI! Sonraki ders aktif! 🔓",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = LeafGreen
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF2F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "🔒 Kilit Açılmadı (%100 Başarı Gerekli)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                Text(
                    text = if (isPerfectScore) "Ders %100 Tamamlandı! 🎉" else "Ders Tamamlanamadı 👍",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                )

                Text(
                    text = lesson.titleTr,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF64748B)
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (!isPerfectScore) {
                    Text(
                        text = "Sonraki dersin kilidini açabilmek için bu dersi %100 tamamlamalısın! Akıllı tekrar ile sadece eksik soruları çözebilirsin.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC2410C)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Star Rating
                StarRatingRow(starsEarned = stars, size = 34.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // Rewards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RewardBadge(title = "Puan", value = "+$score", emoji = "⚡")
                    RewardBadge(title = "Altın", value = "+${if (isPerfectScore) stars * 10 + 25 else 15}", emoji = "🪙")
                    RewardBadge(title = "Kalan Kalp", value = "$heartsLeft / 3", emoji = "❤️")
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (isPerfectScore) {
            Kid3DButton(
                text = "Sıradaki Teste Geç 🚀",
                onClick = { triggerContinue(true) },
                color = LeafGreen,
                shadowColor = Color(0xFF15803D),
                modifier = Modifier.fillMaxWidth(0.9f),
                testTag = "finish_lesson_continue_btn"
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Kid3DButton(
                    text = "Sıradaki Teste Geç 🚀",
                    onClick = { triggerContinue(false) },
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "finish_lesson_next_btn"
                )

                Kid3DButton(
                    text = "Eksik Soruları Çöz 🎯 (%100 Tamamla)",
                    onClick = onRetry,
                    color = LeafGreen,
                    shadowColor = Color(0xFF15803D),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "finish_lesson_retry_btn"
                )
            }
        }
    }
}

@Composable
private fun RewardBadge(title: String, value: String, emoji: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = BrandBlue
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            )
        }
    }
}
