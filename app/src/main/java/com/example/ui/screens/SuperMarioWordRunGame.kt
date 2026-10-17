package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.curriculum.CurriculumData
import com.example.data.model.WordItem
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SuperMarioWordRunGame(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allWords = remember { CurriculumData.getAllWords().shuffled() }
    val coroutineScope = rememberCoroutineScope()

    var score by remember { mutableIntStateOf(0) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var coinsCollected by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isJumping by remember { mutableStateOf(false) }
    var isPoweredUp by remember { mutableStateOf(false) }

    val targetWord = allWords.getOrElse(currentQuestionIndex % allWords.size) { allWords.first() }
    val isEnglishQuestionMode = (currentQuestionIndex % 2 == 0)

    val blockChoices = remember(targetWord, currentQuestionIndex) {
        val wrongChoices = allWords.filter { it.id != targetWord.id }.take(2)
        (wrongChoices + targetWord).shuffled()
    }

    LaunchedEffect(targetWord, isEnglishQuestionMode) {
        if (isEnglishQuestionMode) {
            audioService.speakEnglish(targetWord.english)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mario_world_anim")
    val cloudScroll by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clouds"
    )

    fun handleBlockHit(selected: WordItem) {
        isJumping = true
        coroutineScope.launch {
            delay(350)
            isJumping = false
            if (selected.id == targetWord.id) {
                audioService.playCorrectSound()
                audioService.playCoinSound()
                coinsCollected += 10
                score += 150
                isPoweredUp = true
                currentQuestionIndex++
                onRewardCoins(15)
            } else {
                audioService.playWrongSound()
                lives--
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF60A5FA), // Mario Retro Blue Sky
                        Color(0xFF93C5FD),
                        Color(0xFF4ADE80), // Green hills
                        Color(0xFF15803D)
                    )
                )
            )
            .testTag("super_mario_word_run_game")
    ) {
        // Floating Clouds Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cloud 1
            drawCircle(Color.White.copy(alpha = 0.85f), radius = 26f, center = Offset((cloudScroll * 1.2f) % w, h * 0.12f))
            drawCircle(Color.White.copy(alpha = 0.85f), radius = 34f, center = Offset(((cloudScroll * 1.2f) % w) + 30f, h * 0.12f))

            // Cloud 2
            drawCircle(Color.White.copy(alpha = 0.85f), radius = 22f, center = Offset(((cloudScroll * 0.8f + 200f) % w), h * 0.22f))
            drawCircle(Color.White.copy(alpha = 0.85f), radius = 30f, center = Offset(((cloudScroll * 0.8f + 230f) % w), h * 0.22f))

            // Ground Brown Bricks Base
            drawRect(
                color = Color(0xFFD97706),
                topLeft = Offset(0f, h * 0.82f),
                size = androidx.compose.ui.geometry.Size(w, h * 0.18f)
            )
            // Green grass top line
            drawRect(
                color = Color(0xFF16A34A),
                topLeft = Offset(0f, h * 0.82f),
                size = androidx.compose.ui.geometry.Size(w, 14f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar: Navigation, Mario HUD (Score, Coins, Lives)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color(0xFFDC2626)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, SunGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "MARIO 🍄",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "SCORE: $score",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "🪙 x$coinsCollected",
                            fontWeight = FontWeight.Black,
                            color = SunGold,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CandyRed)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        repeat(lives) {
                            Text(text = "❤️", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Banner (Find this word!)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFDC2626)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isEnglishQuestionMode) "İngilizce kelimenin anlamını bul! 🍄" else "Türkçe kelimenin İngilizcesini bul! 🍄",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isEnglishQuestionMode) targetWord.english else targetWord.turkish,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isEnglishQuestionMode) Color(0xFF0F172A) else Color(0xFF0369A1)
                                )
                            )
                        }
                    }

                    if (isEnglishQuestionMode) {
                        IconButton(
                            onClick = { audioService.speakEnglish(targetWord.english) },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFFEF3C7), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Dinle",
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Floating Question Blocks ('?' Mystery Blocks)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                blockChoices.forEach { word ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .clickable { handleBlockHit(word) }
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B)),
                        border = androidx.compose.foundation.BorderStroke(3.5.dp, Color(0xFF78350F))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "❓",
                                fontSize = 20.sp
                            )
                            Text(
                                text = word.emoji,
                                fontSize = 26.sp
                            )
                            Text(
                                text = if (isEnglishQuestionMode) word.turkish else word.english,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Central Stage with Mario Runner Character & Green Pipe
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Green Warp Pipe on the right
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    color = Color(0xFF22C55E),
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF15803D)),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp)
                        .width(68.dp)
                        .height(85.dp)
                ) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        Text(text = "🌱", fontSize = 24.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                // Super Mario / Pixel Hero Character with Jump Animation
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = if (isJumping) (-85).dp else (-10).dp)
                        .scale(if (isPoweredUp) 1.2f else 1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isJumping) "🍄⬆️" else "⭐",
                            fontSize = 20.sp
                        )
                        // Mario Emoji / Costume Avatar
                        Text(
                            text = "👨‍🔧",
                            fontSize = 72.sp
                        )
                    }
                }
            }
        }
    }
}
