package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.audio.SpeechState
import com.example.data.curriculum.CurriculumData
import com.example.data.model.WordItem
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SpaceBattleGameScreen(
    speechService: SpeechRecognitionService,
    audioService: AudioService,
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val speechState by speechService.speechState.collectAsStateWithLifecycle()
    val recognizedText by speechService.recognizedText.collectAsStateWithLifecycle()
    val lastEvaluation by speechService.lastEvaluation.collectAsStateWithLifecycle()

    val battleWords = remember { CurriculumData.getAllWords().shuffled().take(15) }
    var currentTargetIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(1) }
    var hearts by remember { mutableIntStateOf(3) }
    var isGameOver by remember { mutableStateOf(false) }
    var isVictory by remember { mutableStateOf(false) }
    var isLaserBlasting by remember { mutableStateOf(false) }

    val currentWord: WordItem? = battleWords.getOrNull(currentTargetIndex)

    val infiniteTransition = rememberInfiniteTransition(label = "battle_anim")
    val meteorFloat by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "meteor_float"
    )

    // Voice evaluation listener
    LaunchedEffect(lastEvaluation) {
        val eval = lastEvaluation
        if (eval != null && currentWord != null && !isGameOver && !isVictory) {
            if (eval.isPassed) {
                // Correct! Blast with laser
                isLaserBlasting = true
                onPlayCorrectFx()
                score += 20 * combo
                combo += 1

                delay(600)
                isLaserBlasting = false

                if (currentTargetIndex + 1 < battleWords.size) {
                    currentTargetIndex += 1
                } else {
                    isVictory = true
                    onPlayFanfareFx()
                    onRewardCoins(80)
                }
            } else {
                // Missed
                onPlayWrongFx()
                combo = 1
                hearts = maxOf(0, hearts - 1)
                if (hearts <= 0) {
                    isGameOver = true
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF030712), Color(0xFF0F172A), Color(0xFF1E1B4B))
                )
            )
            .testTag("space_battle_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top HUD (Back button, Score, Hearts & Combo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                }

                // Hearts
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { i ->
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (i < hearts) CandyRed else Color(0xFF475569),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Score Badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.5.dp, SunGold)
                ) {
                    Text(
                        text = "⭐ $score Skor (x$combo)",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Target Enemy Meteor / Droid with Word
            if (currentWord != null && !isGameOver && !isVictory) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .offset(y = meteorFloat.dp)
                        .scale(if (isLaserBlasting) 1.15f else 1f),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLaserBlasting) Color(0xFFDC2626) else Color(0xFF1E293B)
                    ),
                    border = BorderStroke(
                        3.dp,
                        if (isLaserBlasting) Color(0xFFFDE047) else Color(0xFF00F0FF)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(60.dp),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = currentWord.emoji, fontSize = 34.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentWord.english,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 28.sp
                            )
                        )

                        Text(
                            text = "(${currentWord.turkish})",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF67E8F9)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0284C7).copy(alpha = 0.4f),
                            modifier = Modifier.clickable {
                                onSpeakWord(currentWord.english, false)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Doğru Telaffuzu Dinle", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Bottom Hero (Lego Lord Vader with Lightsaber Laser)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PokoMascot(
                    size = 110.dp,
                    mood = if (isLaserBlasting) PokoMood.CELEBRATING else PokoMood.HAPPY,
                    costumeId = "costume_default"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Big Voice Trigger Button
                if (!isGameOver && !isVictory && currentWord != null) {
                    val isListening = speechState == SpeechState.LISTENING

                    Kid3DButton(
                        text = if (isListening) "Dinliyorum... Söyle! 🎙️" else "Mikrofona Söyle & Vur! ⚔️",
                        onClick = {
                            speechService.startListening(currentWord.english)
                        },
                        color = if (isListening) Color(0xFFDC2626) else BrandBlue,
                        shadowColor = if (isListening) Color(0xFF991B1B) else Color(0xFF1E40AF),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(58.dp),
                        testTag = "voice_attack_btn"
                    )

                    if (recognizedText.isNotBlank()) {
                        Text(
                            text = "Duyulan: \"$recognizedText\"",
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        // Game Over Overlay
        if (isGameOver) {
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💥", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "GÖREV TAMAMLANAMADI",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Skorun: $score - Tekrar deneyip kelimeleri fırla!",
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Kid3DButton(
                        text = "Tekrar Dene 🔄",
                        onClick = {
                            hearts = 3
                            score = 0
                            combo = 1
                            currentTargetIndex = 0
                            isGameOver = false
                        },
                        color = SunOrange,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                }
            }
        }

        // Victory Overlay
        if (isVictory) {
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("👑", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "GALAKSİ KAHRAMANI! 🎉",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047)
                        )
                    )
                    Text(
                        text = "Tüm kelimeleri doğru telaffuz ederek düşmanları yendin!\n+80 Altın Kazandın! 🪙",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Kid3DButton(
                        text = "Ödülü Al & Devam Et 🚀",
                        onClick = onBack,
                        color = LeafGreen,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                }
            }
        }
    }
}
