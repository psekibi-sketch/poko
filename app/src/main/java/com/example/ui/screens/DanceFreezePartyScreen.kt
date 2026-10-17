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
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioService
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPink
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DanceCommand(
    val commandEn: String,
    val phonetic: String,
    val commandTr: String,
    val emoji: String,
    val isFreeze: Boolean = false,
    val durationSec: Int = 4
)

@Composable
fun DanceFreezePartyScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val danceSequence = remember {
        listOf(
            DanceCommand("DANCE AND JUMP!", "dens end camp", "Dans Et ve Zıpla!", "🕺", false),
            DanceCommand("CLAP YOUR HANDS!", "klep yor hendz", "Ellerini Alkışla!", "👏", false),
            DanceCommand("FREEZE! DON'T MOVE!", "friiz dont muuv", "DON! Sakın Kımıldama!", "🧊", true),
            DanceCommand("TOUCH YOUR HEAD!", "taç yor hed", "Başına Dokun!", "🙆", false),
            DanceCommand("SPIN AROUND!", "spin eraund", "Kendi Etrafında Dön!", "🔄", false),
            DanceCommand("FREEZE LIKE A STATUE!", "friiz layk e stetyuu", "Heykel Gibi DON!", "🗿", true),
            DanceCommand("SHAKE YOUR SHOULDERS!", "şeyk yor şoldırz", "Omuzlarını Salla!", "💃", false),
            DanceCommand("SUPER HERO POSE!", "supır hiro poz", "Süper Kahraman Duruşu!", "🦸", false)
        )
    }

    var isPartyActive by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentCommand = danceSequence[currentStepIndex % danceSequence.size]
    var partyScore by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "disco_party")
    val discoColorPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "disco"
    )

    DisposableEffect(Unit) {
        onDispose {
            audioService.stopSpeech()
        }
    }

    LaunchedEffect(isPartyActive, currentStepIndex) {
        if (isPartyActive) {
            audioService.speakEnglish(currentCommand.commandEn)
            delay(3800)
            if (isPartyActive) {
                partyScore += 10
                audioService.playPopSound()
                if (currentStepIndex < danceSequence.size - 1) {
                    currentStepIndex++
                } else {
                    audioService.playFanfareSound()
                    onRewardCoins(30)
                    isPartyActive = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF4A044E),
                        Color(0xFF701A75),
                        Color(0xFF86198F),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .testTag("dance_freeze_party_screen")
    ) {
        // Disco lights
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PlayfulPink.copy(alpha = 0.25f * discoColorPulse), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.3f),
                    radius = w * 0.6f
                ),
                center = Offset(w * 0.5f, h * 0.3f),
                radius = w * 0.6f
            )
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

                Text(
                    text = "🕺 POKO DANS & FREEZE PARTİSİ",
                    fontWeight = FontWeight.Black,
                    color = SunGold,
                    fontSize = 13.sp
                )

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
                        Text(text = "⭐", fontSize = 14.sp)
                        Text(
                            text = "$partyScore",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Dancing Mascot
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                PokoMascot(
                    size = 130.dp,
                    mood = if (currentCommand.isFreeze) PokoMood.THINKING else PokoMood.DANCING
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Active Command Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentCommand.isFreeze) Color(0xFF0284C7) else Color(0xFF1E1B4B)
                ),
                border = BorderStroke(
                    3.dp,
                    if (currentCommand.isFreeze) Color(0xFFE0F2FE) else SunGold
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentCommand.emoji,
                        fontSize = 42.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentCommand.commandEn,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0369A1).copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Text(
                            text = "🗣️ Okunuşu: /${currentCommand.phonetic}/",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF67E8F9),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentCommand.commandTr,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDE047),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Start / Stop Party Button
            Kid3DButton(
                text = if (!isPartyActive) "Müziği ve Dansı Başlat! 🎵 💃" else "Dans Ediliyor... Durdur ⏸️",
                onClick = {
                    if (!isPartyActive) {
                        isPartyActive = true
                        currentStepIndex = 0
                    } else {
                        isPartyActive = false
                    }
                },
                color = if (!isPartyActive) LeafGreen else CandyRed,
                shadowColor = if (!isPartyActive) Color(0xFF15803D) else Color(0xFF991B1B),
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
