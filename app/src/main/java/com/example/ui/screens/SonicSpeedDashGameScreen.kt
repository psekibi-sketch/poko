package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioService
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SonicRingTarget(
    val englishWord: String,
    val phonetic: String,
    val turkishMeaning: String,
    val emoji: String,
    val ringOptions: List<String>, // English options for rings
    val correctIndex: Int
)

@Composable
fun SonicSpeedDashGameScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val targets = remember {
        listOf(
            SonicRingTarget("FAST", "fast", "Hızlı", "⚡", listOf("FAST", "SLOW", "SLEEP"), 0),
            SonicRingTarget("RUN", "ran", "Koşmak", "🏃", listOf("JUMP", "RUN", "WALK"), 1),
            SonicRingTarget("RING", "riñ", "Altın Halka", "💍", listOf("BALL", "STAR", "RING"), 2),
            SonicRingTarget("BLUE", "blu", "Mavi", "🔵", listOf("BLUE", "RED", "GREEN"), 0),
            SonicRingTarget("JUMP", "camp", "Zıplamak", "🦘", listOf("FLY", "JUMP", "SWIM"), 1),
            SonicRingTarget("STAR", "star", "Yıldız", "⭐", listOf("MOON", "SUN", "STAR"), 2),
            SonicRingTarget("FLY", "flay", "Uçmak", "🦅", listOf("FLY", "CRAWL", "CLIMB"), 0),
            SonicRingTarget("SUPER", "supır", "Süper / Harika", "🦸", listOf("SMALL", "SUPER", "TINY"), 1)
        ).shuffled()
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentTarget = targets[currentIndex % targets.size]

    var collectedRings by remember { mutableIntStateOf(0) }
    var currentSpeedKmh by remember { mutableIntStateOf(120) }
    var streakCombo by remember { mutableIntStateOf(0) }
    var selectedRingIndex by remember { mutableStateOf<Int?>(null) }
    var isCorrectFeedback by remember { mutableStateOf<Boolean?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "sonic_spin")
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    val ringGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(currentIndex) {
        selectedRingIndex = null
        isCorrectFeedback = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0C4A6E),
                        Color(0xFF0284C7),
                        Color(0xFF0369A1),
                        Color(0xFF0F172A)
                    )
                )
            )
            .testTag("sonic_speed_dash_screen")
    ) {
        // Speed lines canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            for (i in 0..15) {
                val y = (i * 50f + spinRotation * 2) % h
                drawLine(
                    color = Color.White.copy(alpha = 0.25f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 2f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                // Speedometer Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(2.dp, Color(0xFF38BDF8))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Hız",
                            tint = SunGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$currentSpeedKmh KM/H",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp
                        )
                    }
                }

                // Rings count
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SunGold,
                    border = BorderStroke(1.5.dp, Color(0xFFB45309))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "💍", fontSize = 14.sp)
                        Text(
                            text = "$collectedRings",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sonic Runner Mascot & Speed Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spinning Sonic Ball
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .rotate(spinRotation)
                        .background(Color(0xFF2563EB), CircleShape)
                        .border(3.dp, Color(0xFF93C5FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🦔", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "SONIC HIZ KELİME KOŞUSU!",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Doğru altın halkadan geç ve hızını katla!",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Turkish Word Target Challenge Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f)),
                border = BorderStroke(2.5.dp, SunGold),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aranan Kelime (Türkçe):",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = currentTarget.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentTarget.turkishMeaning,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SunGold
                            )
                        )
                    }

                    if (selectedRingIndex != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🗣️ Okunuşu: /${currentTarget.phonetic}/",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "⚡ Doğru İngilizce Halkanın İçinden Geç:",
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3 Big Sonic Golden Ring Choices
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentTarget.ringOptions.forEachIndexed { index, option ->
                    val isSelected = selectedRingIndex == index
                    val isCorrect = index == currentTarget.correctIndex

                    val ringBorderColor = when {
                        isSelected && isCorrect -> LeafGreen
                        isSelected && !isCorrect -> CandyRed
                        else -> SunGold
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .scale(if (isSelected) 1.1f else ringGlowPulse)
                                .background(Color(0xFF0F172A), CircleShape)
                                .border(5.dp, ringBorderColor, CircleShape)
                                .shadow(8.dp, CircleShape)
                                .clickable(enabled = selectedRingIndex == null) {
                                    selectedRingIndex = index
                                    if (isCorrect) {
                                        isCorrectFeedback = true
                                        audioService.playPopSound()
                                        audioService.speakEnglish(currentTarget.englishWord)
                                        collectedRings += 5
                                        currentSpeedKmh += 45
                                        streakCombo++

                                        coroutineScope.launch {
                                            delay(600)
                                            if (currentIndex < targets.size - 1) {
                                                currentIndex++
                                            } else {
                                                audioService.playFanfareSound()
                                                onRewardCoins(35)
                                                currentIndex = 0
                                            }
                                        }
                                    } else {
                                        isCorrectFeedback = false
                                        audioService.playPopSound()
                                        currentSpeedKmh = maxOf(80, currentSpeedKmh - 20)
                                        streakCombo = 0
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "💍",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = option,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Feedback Banner
            if (selectedRingIndex != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCorrectFeedback == true) LeafGreen else CandyRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCorrectFeedback == true) "🎉 SÜPER HIZ! +5 HALKA 💍" else "💨 Yavaşladın! Tekrar Dene!",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
