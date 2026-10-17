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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class MonsterDietQuestion(
    val targetWord: WordItem,
    val monsterAskEn: String,
    val monsterAskTr: String,
    val options: List<WordItem>
)

@Composable
fun HungryMonsterGameScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val allWords = remember { CurriculumData.getAllWords().shuffled() }

    var questionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var isEating by remember { mutableStateOf(false) }
    var isSneezing by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    val currentTarget = allWords[questionIndex % allWords.size]
    val currentOptions = remember(questionIndex) {
        val decoys = allWords.filter { it.id != currentTarget.id }.take(3)
        (listOf(currentTarget) + decoys).shuffled()
    }

    val isEnglishTargetMode = (questionIndex % 2 == 0)

    val infiniteTransition = rememberInfiniteTransition(label = "monster_anim")
    val monsterBreathe by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "monster_breathe"
    )

    LaunchedEffect(questionIndex) {
        if (isEnglishTargetMode) {
            audioService.speakEnglish(currentTarget.english)
        }
    }

    fun handleFeed(selected: WordItem) {
        if (isEating || isSneezing) return

        if (selected.id == currentTarget.id) {
            isEating = true
            audioService.playPopSound()
            score += 20
            streak++
            onRewardCoins(10)
            coroutineScope.launch {
                audioService.speakPraise("Yummy! Nom nom! Very delicious!")
                delay(900)
                isEating = false
                if (questionIndex < 10) {
                    questionIndex++
                } else {
                    isGameOver = true
                    audioService.playFanfareSound()
                    onRewardCoins(50)
                }
            }
        } else {
            isSneezing = true
            audioService.playWrongSound()
            streak = 0
            coroutineScope.launch {
                delay(650)
                isSneezing = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF4C1D95), // Deep monster purple
                        Color(0xFF581C87),
                        Color(0xFF6B21A8),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .testTag("hungry_monster_game_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = "⭐ $score Skor",
                            fontWeight = FontWeight.Black,
                            color = SunGold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    if (streak > 1) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFFEDD5)
                        ) {
                            Text(
                                text = "🔥 x$streak Obur!",
                                fontWeight = FontWeight.Black,
                                color = SunOrange,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (isGameOver) {
                // Victory Screen
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "👾💖", fontSize = 56.sp)
                        Text(
                            text = "Canavarın Karnı Doydu!",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tüm kelimeleri doğru besledin ve +50 Altın kazandın! Nom-Nom çok mutlu!",
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Kid3DButton(
                            text = "Tekrar Besle 🔄",
                            onClick = {
                                questionIndex = 0
                                score = 0
                                streak = 0
                                isGameOver = false
                            },
                            color = LeafGreen,
                            shadowColor = Color(0xFF15803D),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // Monster Avatar & Request Bubble
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = monsterBreathe.dp)
                ) {
                    // Speech Request Bubble
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isEnglishTargetMode) "Karnım çok acıktı! Bana bunu yedir:" else "I am so hungry! Feed me:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isEnglishTargetMode) "\"${currentTarget.english}\"" else "\"${currentTarget.turkish}\"",
                                fontWeight = FontWeight.Black,
                                color = PlayfulPurple,
                                fontSize = 24.sp
                            )
                            if (isEnglishTargetMode && currentTarget.phonetic.isNotBlank()) {
                                Text(
                                    text = "🗣️ Okunuşu: [ ${currentTarget.phonetic} ]",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0284C7),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // The Animated Nom-Nom Monster Body
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .background(if (isSneezing) Color(0xFFEF4444) else Color(0xFF10B981), CircleShape)
                            .border(4.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                isEating -> "😋"
                                isSneezing -> "🤧"
                                else -> "👾"
                            },
                            fontSize = 68.sp
                        )
                    }

                    Text(
                        text = if (isSneezing) "Açuuu! Yanlış yemek! 🤧" else (if (isEating) "Hammm! Nefis! 😋" else "Nom-Nom Canavar"),
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Food Plates Grid (4 Choices)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🍽️ Doğru tabağa dokunup canavara yedir:",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(currentOptions) { opt ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(84.dp)
                                    .clickable { handleFeed(opt) },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(2.dp, Color(0xFFFDE68A)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = opt.emoji, fontSize = 28.sp)
                                    Column {
                                        Text(
                                            text = if (isEnglishTargetMode) opt.turkish else opt.english,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF0F172A),
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "🍽️ Yedir",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981),
                                            fontSize = 10.sp
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
}
