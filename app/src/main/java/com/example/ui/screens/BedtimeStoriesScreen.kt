package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.theme.SunGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class BedtimeStory(
    val id: String,
    val titleEn: String,
    val titleTr: String,
    val emoji: String,
    val durationMin: String,
    val storyLines: List<Pair<String, String>> // English, Turkish
)

@Composable
fun BedtimeStoriesScreen(
    audioService: AudioService,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val stories = remember {
        listOf(
            BedtimeStory(
                id = "story_sleepy_bear",
                titleEn = "The Sleepy Bear and the Moon",
                titleTr = "Uykucu Ayıcık ve Ay Dede",
                emoji = "🐻",
                durationMin = "3 dk",
                storyLines = listOf(
                    Pair("Once upon a time, a little brown bear looked at the moon.", "Bir varmış bir yokmuş, küçük kahverengi bir ayı aya bakmış."),
                    Pair("The moon was bright and gentle in the night sky.", "Ay, gece gökyüzünde parlak ve çok huzurluymuş."),
                    Pair("Good night stars, said the sleepy bear.", "İyi geceler yıldızlar, demiş uykucu ayı."),
                    Pair("He closed his eyes and had the sweetest dreams.", "Gözlerini kapatmış ve en tatlı rüyalara dalmış.")
                )
            ),
            BedtimeStory(
                id = "story_star_lullaby",
                titleEn = "Little Star's Lullaby",
                titleTr = "Küçük Yıldızın Ninnisi",
                emoji = "🌟",
                durationMin = "2 dk",
                storyLines = listOf(
                    Pair("High above the clouds, a little star was shining.", "Bulutların üzerinde minik bir yıldız parlıyormuş."),
                    Pair("The soft wind sang a gentle lullaby to all children.", "Tatlı rüzgar tüm çocuklara huzurlu bir ninni söylemiş."),
                    Pair("Close your eyes, sleep tight and wake up happy.", "Gözlerini kapa, mışıl mışıl uyu ve neşeyle uyan."),
                    Pair("Sweet dreams, little hero.", "Tatlı rüyalar, küçük kahramanım.")
                )
            )
        )
    }

    var selectedStoryIndex by remember { mutableIntStateOf(0) }
    val currentStory = stories[selectedStoryIndex % stories.size]
    var isReadingStory by remember { mutableStateOf(false) }
    var currentLineIndex by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "moon_glow")
    val moonGlow by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moon"
    )

    DisposableEffect(Unit) {
        onDispose {
            audioService.stopSpeech()
        }
    }

    fun startReading() {
        isReadingStory = true
        currentLineIndex = 0
        coroutineScope.launch {
            for (i in currentStory.storyLines.indices) {
                if (!isReadingStory) break
                currentLineIndex = i
                val line = currentStory.storyLines[i]
                audioService.speakEnglish(line.first, isSlow = true)
                delay(6000)
            }
            isReadingStory = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF030712),
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .testTag("bedtime_stories_screen")
    ) {
        // Night sky stars
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            listOf(
                Offset(w * 0.15f, h * 0.12f),
                Offset(w * 0.85f, h * 0.18f),
                Offset(w * 0.45f, h * 0.28f),
                Offset(w * 0.72f, h * 0.42f),
                Offset(w * 0.25f, h * 0.65f)
            ).forEach { pos ->
                drawCircle(Color.White.copy(alpha = 0.75f), radius = 2.5f, center = pos)
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
                    onClick = {
                        audioService.stopSpeech()
                        onBack()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                Text(
                    text = "🌙 POKO UYKU MASALLARI",
                    fontWeight = FontWeight.Black,
                    color = SunGold,
                    fontSize = 13.sp
                )

                Box(modifier = Modifier.size(42.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Glowing Moon Banner
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(moonGlow)
                    .background(Color(0xFFFEF08A).copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌙", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Story Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                border = BorderStroke(2.dp, Color(0xFF818CF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentStory.emoji,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentStory.titleEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = currentStory.titleTr,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDE047),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reading current sentence
                    val activeLine = currentStory.storyLines[currentLineIndex % currentStory.storyLines.size]
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = activeLine.first,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeLine.second,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Kid3DButton(
                text = if (!isReadingStory) "Masalı Sakince Oku 🎧 ✨" else "Masalı Durdur ⏸️",
                onClick = {
                    if (!isReadingStory) {
                        startReading()
                    } else {
                        isReadingStory = false
                        audioService.stopSpeech()
                    }
                },
                color = Color(0xFF6366F1),
                shadowColor = Color(0xFF4338CA),
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
