package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import kotlinx.coroutines.delay

data class VideoCallQuestion(
    val pokoPromptEn: String,
    val pokoPromptTr: String,
    val expectedAnswerEn: String,
    val phonetic: String,
    val acceptedKeywords: List<String>,
    val emoji: String
)

/**
 * POKO İLE CANLI GÖRÜNTÜLÜ ARAMA SİMÜLASYONU
 *
 * Gerçek bir görüntülü konuşma arayüzü:
 * - Çalan telefon animasyonu ve "Poko Arıyor..." ekranı
 * - Görüntülü arama arayüzü, Poko'nun neşeli yüz ifadeleri
 * - Poko'nun sesli soru sorması ve çocuğun mikrofona cevap vermesi
 * - Canlı telaffuz algılama, yıldız ve altın ödülleri
 */
@Composable
fun PokoVideoCallScreen(
    audioService: AudioService,
    speechService: SpeechRecognitionService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val callScenarios = remember {
        listOf(
            VideoCallQuestion(
                pokoPromptEn = "Hello my friend! How are you today?",
                pokoPromptTr = "Merhaba arkadaşım! Bugün nasılsın?",
                expectedAnswerEn = "I am great, thank you!",
                phonetic = "ay em greyt tenk yu",
                acceptedKeywords = listOf("great", "fine", "good", "happy", "thank"),
                emoji = "😄"
            ),
            VideoCallQuestion(
                pokoPromptEn = "What is your favorite fruit?",
                pokoPromptTr = "En sevdiğin meyve nedir?",
                expectedAnswerEn = "I like sweet red apples!",
                phonetic = "ay layk sviit red epılz",
                acceptedKeywords = listOf("apple", "banana", "orange", "strawberry", "like"),
                emoji = "🍎"
            ),
            VideoCallQuestion(
                pokoPromptEn = "What color is the bright sun?",
                pokoPromptTr = "Parlak güneş hangi renktir?",
                expectedAnswerEn = "The sun is yellow!",
                phonetic = "dı san iz yelov",
                acceptedKeywords = listOf("yellow", "sun", "is"),
                emoji = "☀️"
            ),
            VideoCallQuestion(
                pokoPromptEn = "Do you like cute animals?",
                pokoPromptTr = "Sevimli hayvanları sever misin?",
                expectedAnswerEn = "Yes! I love cats and dogs!",
                phonetic = "yes ay lav kets end dogz",
                acceptedKeywords = listOf("yes", "cat", "dog", "animal", "love", "like"),
                emoji = "🐱"
            )
        )
    }

    var isCallAnswered by remember { mutableStateOf(false) }
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var isListening by remember { mutableStateOf(false) }
    var userSpokenText by remember { mutableStateOf("") }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var totalEarnedCoins by remember { mutableIntStateOf(0) }
    var isCallFinished by remember { mutableStateOf(false) }

    val currentScenario = callScenarios[currentQuestionIdx % callScenarios.size]

    val infiniteTransition = rememberInfiniteTransition(label = "call_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Automatically speak Poko's prompt when question changes
    LaunchedEffect(isCallAnswered, currentQuestionIdx, isCallFinished) {
        if (isCallAnswered && !isCallFinished) {
            audioService.speakEnglish(currentScenario.pokoPromptEn)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioService.stopSpeech()
            speechService.cancelListening()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF334155)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("poko_video_call_screen")
    ) {
        if (!isCallAnswered) {
            // ==========================================
            // 1. INCOMING CALL SCREEN (Gelen Arama)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                }

                // Avatar and Caller info
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .scale(pulseScale)
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(BrandBlue.copy(alpha = 0.3f))
                            .border(3.dp, BrandBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        PokoMascot(
                            size = 110.dp,
                            mood = PokoMood.EXCITED
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "🦊 Poko",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "İngilizce Görüntülü Arama Geliyor...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    )
                }

                // Call Action Buttons (Answer & Decline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decline
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .clickable { onBack() }
                                .testTag("decline_call_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Reddet",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Kapat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Answer
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .scale(pulseScale)
                                .size(78.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                                .clickable {
                                    isCallAnswered = true
                                    audioService.playCorrectSound()
                                }
                                .testTag("answer_call_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Cevapla",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Cevapla", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        } else if (!isCallFinished) {
            // ==========================================
            // 2. ACTIVE VIDEO CALL (Aktif Görüntülü Sohbet)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Status Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Canlı Sohbet • ${currentQuestionIdx + 1}/${callScenarios.size}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SunGold.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "🪙 +$totalEarnedCoins Altın",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SunGold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Video Feed Box (Poko's Camera)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF334155), Color(0xFF0F172A))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PokoMascot(
                                size = 130.dp,
                                mood = if (isAnswerCorrect) PokoMood.CELEBRATING else if (isListening) PokoMood.LISTENING else PokoMood.HAPPY
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Poko's Question Balloon
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth(0.95f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = currentScenario.emoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = currentScenario.pokoPromptEn,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF0F172A),
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { audioService.speakEnglish(currentScenario.pokoPromptEn) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Dinle",
                                                tint = BrandBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = currentScenario.pokoPromptTr,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B),
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }

                        // Child's Self Camera Thumbnail (Bottom Right)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF475569),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(80.dp, 100.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "👧 / 👦", fontSize = 28.sp)
                                    Text(
                                        text = "Sen",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Speaking & Response Controls
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Expected Answer Hint with phonetic pronunciation
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(
                                    text = "💡 Örnek Cevap: \"${currentScenario.expectedAnswerEn}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF334155)
                                    )
                                )
                                Text(
                                    text = "Okunuşu: [${currentScenario.phonetic}]",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PlayfulPurple
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mic Button
                            Kid3DButton(
                                text = if (isListening) "Dinliyorum... 🎙️" else "Cevap Ver 🎤",
                                onClick = {
                                    isListening = true
                                    speechService.startListening(
                                        target = currentScenario.expectedAnswerEn,
                                        onResult = { eval ->
                                            isListening = false
                                            userSpokenText = eval.spokenText
                                            val isMatch = currentScenario.acceptedKeywords.any { kw ->
                                                eval.spokenText.contains(kw, ignoreCase = true)
                                            } || eval.isPassed

                                            if (isMatch) {
                                                isAnswerCorrect = true
                                                audioService.playCorrectSound()
                                                totalEarnedCoins += 30
                                                onRewardCoins(30)
                                            } else {
                                                audioService.playWrongSound()
                                            }
                                        }
                                    )
                                },
                                color = if (isListening) Color(0xFFEF4444) else BrandBlue,
                                shadowColor = Color(0xFF1E40AF),
                                modifier = Modifier.weight(1f),
                                testTag = "call_mic_btn"
                            )

                            // Next / End Button
                            if (isAnswerCorrect) {
                                Kid3DButton(
                                    text = if (currentQuestionIdx + 1 >= callScenarios.size) "Bitir 🏆" else "İleri 🚀",
                                    onClick = {
                                        isAnswerCorrect = false
                                        userSpokenText = ""
                                        if (currentQuestionIdx + 1 >= callScenarios.size) {
                                            isCallFinished = true
                                        } else {
                                            currentQuestionIdx++
                                        }
                                    },
                                    color = LeafGreen,
                                    shadowColor = Color(0xFF15803D),
                                    modifier = Modifier.weight(0.7f),
                                    testTag = "call_next_btn"
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .clickable { onBack() }
                                        .testTag("end_call_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallEnd,
                                        contentDescription = "Aramayı Sonlandır",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // 3. CALL FINISHED REWARD SUMMARY
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🎉👑🌟", fontSize = 54.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "HARİKA BİR SOHBETTİ!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = SunGold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Poko ile İngilizce konuştun ve özgüven kazandın!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🪙 Toplam Kazanılan Ödül",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                        )
                        Text(
                            text = "+$totalEarnedCoins Altın",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SunGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Kid3DButton(
                    text = "Odama Dön 🏠",
                    onClick = onBack,
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.fillMaxWidth(0.85f),
                    testTag = "call_finish_back_btn"
                )
            }
        }
    }
}
