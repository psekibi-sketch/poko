package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.audio.AudioService
import com.example.audio.SpeechEvaluation
import com.example.audio.SpeechRecognitionService
import com.example.audio.SpeechState
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPink
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay

/**
 * Interactive Automated Kid-Friendly Speech-to-Text Pronunciation Dialog.
 * Flow:
 *  1. Speaks Turkish guidance ("Şimdi dinle ve mikrofona tekrar et!")
 *  2. Speaks English target word
 *  3. Automatically opens microphone without needing manual button click
 *  4. Evaluates spoken accuracy
 *  5. If correct: praises verbally in Turkish and automatically completes
 *  6. If wrong: speaks "Tekrar deneyelim!", re-pronounces and re-opens microphone automatically
 */
@Composable
fun SpeechPracticeDialog(
    targetText: String,
    translationTr: String,
    phonetic: String = "",
    emoji: String = "🗣️",
    speechService: SpeechRecognitionService,
    audioService: AudioService? = null,
    onSpeakTts: (text: String, isSlow: Boolean) -> Unit,
    onSuccess: (evaluation: SpeechEvaluation) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val effectiveAudioService = remember(audioService) {
        audioService ?: AudioService(context)
    }

    val speechState by speechService.speechState.collectAsState()
    val recognizedText by speechService.recognizedText.collectAsState()
    val soundRms by speechService.soundRmsDb.collectAsState()
    val lastEvaluation by speechService.lastEvaluation.collectAsState()
    val errorMessage by speechService.errorMessage.collectAsState()

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
        if (isGranted) {
            speechService.startListening(targetText)
        }
    }

    fun startAutoListeningFlow() {
        if (hasAudioPermission) {
            speechService.startListening(targetText)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Auto-guidance flow upon opening dialog
    LaunchedEffect(targetText, translationTr) {
        effectiveAudioService.speakGuideThenEnglish(
            guideTr = "Hadi mikrofona '$targetText' de veya Dinle'ye dokun!",
            targetEn = targetText,
            isSlow = false,
            onComplete = {
                startAutoListeningFlow()
            }
        )
    }

    // Handle speech evaluation state transitions
    LaunchedEffect(speechState, lastEvaluation) {
        if (speechState == SpeechState.SUCCESS && lastEvaluation != null) {
            effectiveAudioService.speakPraise("Harika telaffuz!") {
                onSuccess(lastEvaluation!!)
            }
        } else if (speechState == SpeechState.TRY_AGAIN && lastEvaluation != null) {
            effectiveAudioService.speakRetry(
                retryTr = "Tekrar deneyelim!",
                targetEn = targetText,
                onComplete = {
                    startAutoListeningFlow()
                }
            )
        }
    }

    // Lifecycle observer to cancel background operations
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE || event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                speechService.cancelListening()
                effectiveAudioService.stopSpeech()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            speechService.cancelListening()
            effectiveAudioService.stopSpeech()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("speech_practice_dialog"),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Title and Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = PlayfulPurple.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎤", fontSize = 18.sp)
                            }
                        }
                        Text(
                            text = "Sesli Telaffuz & Tekrar",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E293B)
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .testTag("close_speech_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Word Hero Card
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = emoji, fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = targetText,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BrandBlue
                            ),
                            textAlign = TextAlign.Center
                        )
                        if (phonetic.isNotBlank()) {
                            Text(
                                text = "[ $phonetic ]",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                        Text(
                            text = translationTr,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Listen buttons (Normal & Slow TTS)
                        Row(
                            modifier = Modifier.fillMaxWidth(0.92f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Kid3DButton(
                                text = "Dinle",
                                emoji = "🔊",
                                onClick = { onSpeakTts(targetText, false) },
                                color = BrandBlue,
                                shadowColor = Color(0xFF1E40AF),
                                modifier = Modifier.weight(1f),
                                testTag = "speech_listen_btn"
                            )
                            Kid3DButton(
                                text = "Yavaş",
                                emoji = "🐢",
                                onClick = { onSpeakTts(targetText, true) },
                                color = SunOrange,
                                shadowColor = Color(0xFFC2410C),
                                modifier = Modifier.weight(1f),
                                testTag = "speech_listen_slow_btn"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Microphone Wave & Pulse Center Area
                SpeechMicPulseButton(
                    state = speechState,
                    soundRms = soundRms,
                    onClick = {
                        if (speechState == SpeechState.LISTENING) {
                            speechService.stopListening()
                        } else {
                            if (!hasAudioPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                speechService.startListening(targetText)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time speech status and recognized text display
                when (speechState) {
                    SpeechState.IDLE, SpeechState.READY -> {
                        Text(
                            text = "🎤 Mikrofona söyle veya Dinle'ye dokun!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    SpeechState.LISTENING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "👂 Seni Dinliyorum... Şimdi Söyle! 🎙️",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CandyRed
                                ),
                                textAlign = TextAlign.Center
                            )
                            if (recognizedText.isNotBlank()) {
                                Surface(
                                    color = Color(0xFFFEF2F2),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    Text(
                                        text = "Duydum: \"$recognizedText\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CandyRed
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    SpeechState.PROCESSING -> {
                        Text(
                            text = "⏳ Telaffuzun inceleniyor...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SunOrange
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    SpeechState.SUCCESS -> {
                        lastEvaluation?.let { eval ->
                            SpeechEvaluationResultView(
                                evaluation = eval,
                                onContinue = {
                                    onSuccess(eval)
                                    onDismiss()
                                },
                                onRetry = {
                                    speechService.startListening(targetText)
                                }
                            )
                        }
                    }

                    SpeechState.TRY_AGAIN, SpeechState.ERROR -> {
                        lastEvaluation?.let { eval ->
                            SpeechEvaluationResultView(
                                evaluation = eval,
                                onContinue = {
                                    onDismiss()
                                },
                                onRetry = {
                                    speechService.startListening(targetText)
                                }
                            )
                        } ?: run {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = errorMessage ?: "Ses tam algılanamadı.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CandyRed
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Kid3DButton(
                                        text = "Tekrar Dene",
                                        emoji = "🔄",
                                        onClick = { speechService.startListening(targetText) },
                                        color = SunOrange,
                                        shadowColor = Color(0xFFC2410C)
                                    )
                                    // Simulation button for test
                                    Kid3DButton(
                                        text = "Doğru Say",
                                        emoji = "✅",
                                        onClick = {
                                            speechService.simulateRecognition(targetText, targetText)
                                        },
                                        color = LeafGreen,
                                        shadowColor = Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline Hands-Free Speech Recognition Card embedded directly inside exercises (e.g. Flashcard, Quiz, Sentence Builder).
 */
@Composable
fun InlineSpeechBar(
    targetText: String,
    speechService: SpeechRecognitionService,
    audioService: AudioService,
    hasAudioPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speechState by speechService.speechState.collectAsState()
    val recognizedText by speechService.recognizedText.collectAsState()
    val soundRms by speechService.soundRmsDb.collectAsState()
    val lastEvaluation by speechService.lastEvaluation.collectAsState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = when (speechState) {
            SpeechState.LISTENING -> Color(0xFFFEF2F2)
            SpeechState.SUCCESS -> Color(0xFFF0FDF4)
            SpeechState.PROCESSING -> Color(0xFFFFFBEB)
            SpeechState.TRY_AGAIN -> Color(0xFFFFF7ED)
            else -> Color(0xFFF8FAFC)
        },
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            when (speechState) {
                SpeechState.LISTENING -> CandyRed
                SpeechState.SUCCESS -> LeafGreen
                SpeechState.PROCESSING -> SunOrange
                SpeechState.TRY_AGAIN -> SunOrange
                else -> Color(0xFFE2E8F0)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Pulsing Mini Mic Icon
                SpeechMicPulseButton(
                    state = speechState,
                    soundRms = soundRms,
                    onClick = {
                        if (speechState == SpeechState.LISTENING) {
                            speechService.stopListening()
                        } else {
                            if (!hasAudioPermission) {
                                onRequestPermission()
                            } else {
                                speechService.startListening(targetText)
                            }
                        }
                    },
                    sizeDp = 50
                )

                Column {
                    when (speechState) {
                        SpeechState.LISTENING -> {
                            Text(
                                text = "🎙️ Seni dinliyorum... Şimdi söyle!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CandyRed
                                )
                            )
                            Text(
                                text = if (recognizedText.isNotBlank()) "\"$recognizedText\"" else "İngilizce telaffuz et",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        SpeechState.PROCESSING -> {
                            Text(
                                text = "⏳ İnceleniyor...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SunOrange
                                )
                            )
                        }
                        SpeechState.SUCCESS -> {
                            Text(
                                text = "Harika Telaffuz!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = LeafGreen
                                )
                            )
                            Text(
                                text = lastEvaluation?.feedbackMessage ?: "Doğru!",
                                style = MaterialTheme.typography.bodySmall.copy(color = LeafGreen)
                            )
                        }
                        SpeechState.TRY_AGAIN -> {
                            Text(
                                text = "🎯 Tekrar Deneyelim!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SunOrange
                                )
                            )
                            Text(
                                text = "Doğru seslendiriliyor, mikrofona söyle",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                            )
                        }
                        else -> {
                            Text(
                                text = "🎤 Otomatik Mikrofon Aktif",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155)
                                )
                            )
                            Text(
                                text = "Sesi dinledikten sonra tekrar et",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                            )
                        }
                    }
                }
            }

            // Quick simulation pass button (for noisy/emulator testing)
            IconButton(
                onClick = {
                    speechService.simulateRecognition(targetText, targetText)
                },
                modifier = Modifier
                    .size(34.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                    .testTag("inline_quick_pass_btn")
            ) {
                Text("✨", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SpeechMicPulseButton(
    state: SpeechState,
    soundRms: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sizeDp: Int = 96
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val isListening = state == SpeechState.LISTENING
    val scale = if (isListening) pulseScale + (soundRms * 0.03f) else 1f

    val buttonBgColor by animateColorAsState(
        targetValue = when (state) {
            SpeechState.LISTENING -> CandyRed
            SpeechState.SUCCESS -> LeafGreen
            SpeechState.PROCESSING -> SunOrange
            SpeechState.TRY_AGAIN -> SunOrange
            else -> BrandBlue
        },
        label = "mic_color"
    )

    Box(
        modifier = modifier
            .size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer sound wave ring when listening
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(sizeDp.dp)
                    .scale(scale)
                    .background(CandyRed.copy(alpha = 0.25f), CircleShape)
            )
        }

        Surface(
            shape = CircleShape,
            color = buttonBgColor,
            shadowElevation = if (isListening) 8.dp else 4.dp,
            modifier = Modifier
                .size((sizeDp * 0.8f).dp)
                .clickable { onClick() }
                .testTag("speech_mic_action_btn")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (state) {
                        SpeechState.SUCCESS -> Icons.Default.CheckCircle
                        SpeechState.PROCESSING -> Icons.Default.VolumeUp
                        SpeechState.TRY_AGAIN -> Icons.Default.Refresh
                        else -> Icons.Default.Mic
                    },
                    contentDescription = "Mikrofon",
                    tint = Color.White,
                    modifier = Modifier.size((sizeDp * 0.38f).dp)
                )
            }
        }
    }
}

@Composable
private fun SpeechEvaluationResultView(
    evaluation: SpeechEvaluation,
    onContinue: () -> Unit,
    onRetry: () -> Unit
) {
    Surface(
        color = if (evaluation.isPassed) Color(0xFFF0FDF4) else Color(0xFFFFF7ED),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (evaluation.isPassed) LeafGreen else SunOrange
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = evaluation.feedbackMessage,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (evaluation.isPassed) LeafGreen else Color(0xFFC2410C)
                ),
                textAlign = TextAlign.Center
            )

            // Star Rating for Pronunciation
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val isEarned = index < evaluation.stars
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isEarned) SunGold else Color(0xFFCBD5E1),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = "Benzerlik Doğruluğu: %${evaluation.accuracy}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (evaluation.isPassed) {
                Kid3DButton(
                    text = "Devam Et 🚀",
                    onClick = onContinue,
                    color = LeafGreen,
                    shadowColor = Color(0xFF15803D),
                    modifier = Modifier.fillMaxWidth(0.85f),
                    testTag = "speech_eval_continue_btn"
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Kid3DButton(
                        text = "Tekrar Dene 🔄",
                        onClick = onRetry,
                        color = SunOrange,
                        shadowColor = Color(0xFFC2410C),
                        testTag = "speech_eval_retry_btn"
                    )
                }
            }
        }
    }
}
