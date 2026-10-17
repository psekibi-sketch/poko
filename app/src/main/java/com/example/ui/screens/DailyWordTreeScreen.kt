package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
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
import com.example.data.curriculum.CurriculumData
import com.example.data.model.WordItem
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import java.util.Calendar

data class TreeFruit(
    val id: Int,
    val word: WordItem,
    val fruitEmoji: String,
    val isGolden: Boolean = false,
    val offsetX: Float,
    val offsetY: Float
)

@Composable
fun DailyWordTreeScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allWords = remember { CurriculumData.getAllWords().shuffled() }
    
    // Pick 5 daily fruits on the tree based on day of year
    val dayOfYear = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val fruits = remember(dayOfYear) {
        listOf(
            TreeFruit(1, allWords[(dayOfYear) % allWords.size], "🍎", false, -85f, -30f),
            TreeFruit(2, allWords[(dayOfYear + 1) % allWords.size], "🍊", false, 80f, -45f),
            TreeFruit(3, allWords[(dayOfYear + 2) % allWords.size], "⭐", true, 0f, -90f), // Golden Center Fruit
            TreeFruit(4, allWords[(dayOfYear + 3) % allWords.size], "🍓", false, -55f, 35f),
            TreeFruit(5, allWords[(dayOfYear + 4) % allWords.size], "🍇", false, 65f, 25f)
        )
    }

    var selectedFruit by remember { mutableStateOf<TreeFruit?>(null) }
    var harvestedFruitIds by remember { mutableStateOf(setOf<Int>()) }
    var streakDays by remember { mutableIntStateOf(5) }

    val infiniteTransition = rememberInfiniteTransition(label = "tree_anim")
    val fruitWobble by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fruit_wobble"
    )

    val goldenGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "golden_glow"
    )

    LaunchedEffect(selectedFruit) {
        selectedFruit?.let {
            audioService.speakEnglish(it.word.english)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF7DD3FC), // Bright sunny sky
                        Color(0xFFBAE6FD),
                        Color(0xFFDCFCE7),
                        Color(0xFF86EFAC)
                    )
                )
            )
            .testTag("daily_word_tree_screen")
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
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color(0xFF0F172A))
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.5.dp, SunGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streakDays Günlük Seri",
                            fontWeight = FontWeight.Black,
                            color = SunOrange,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Tree Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🌳 GÜNÜN KELİMESİ AĞACI 🌳",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF065F46),
                    fontSize = 18.sp
                )
                Text(
                    text = "Ağaçtaki meyvelere dokun, yeni kelimeleri ve altınları topla!",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Big Illustrated Tree Canvas with Hanging Fruits
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .clip(RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Tree Trunk & Leaves Foliage Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Trunk
                    drawRect(
                        color = Color(0xFF78350F),
                        topLeft = Offset(w * 0.42f, h * 0.55f),
                        size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.45f)
                    )

                    // Big Green Canopy Circles
                    drawCircle(color = Color(0xFF15803D), radius = 105.dp.toPx(), center = Offset(w * 0.5f, h * 0.38f))
                    drawCircle(color = Color(0xFF16A34A), radius = 90.dp.toPx(), center = Offset(w * 0.35f, h * 0.35f))
                    drawCircle(color = Color(0xFF22C55E), radius = 90.dp.toPx(), center = Offset(w * 0.65f, h * 0.35f))
                    drawCircle(color = Color(0xFF4ADE80), radius = 65.dp.toPx(), center = Offset(w * 0.5f, h * 0.22f))
                }

                // Hanging Interactive Fruits
                fruits.forEach { fruit ->
                    val isHarvested = harvestedFruitIds.contains(fruit.id)
                    val wobble = if (fruit.isGolden) fruitWobble * 1.5f else fruitWobble

                    Box(
                        modifier = Modifier
                            .offset(x = fruit.offsetX.dp, y = fruit.offsetY.dp)
                            .rotate(if (!isHarvested) wobble else 0f)
                            .scale(if (fruit.isGolden && !isHarvested) goldenGlowPulse else 1f)
                            .clickable(enabled = !isHarvested) {
                                selectedFruit = fruit
                                harvestedFruitIds = harvestedFruitIds + fruit.id
                                audioService.playPopSound()
                                onRewardCoins(if (fruit.isGolden) 50 else 20)
                            }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isHarvested) Color.White.copy(alpha = 0.3f) else (if (fruit.isGolden) Color(0xFFFEF08A) else Color.White),
                            border = BorderStroke(2.dp, if (fruit.isGolden) SunGold else Color.White),
                            shadowElevation = if (!isHarvested) 6.dp else 0.dp,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isHarvested) "✨" else fruit.fruitEmoji,
                                    fontSize = if (fruit.isGolden) 28.sp else 24.sp
                                )
                            }
                        }
                    }
                }
            }

            // Word Reveal Bottom Sheet / Card
            if (selectedFruit != null) {
                val fruit = selectedFruit!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, if (fruit.isGolden) SunGold else Color(0xFF10B981)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (fruit.isGolden) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = if (fruit.isGolden) "⭐ ALTIN GÜNÜN KELİMESİ" else "🍃 GÜNÜN MEYVESİ",
                                    fontWeight = FontWeight.Black,
                                    color = if (fruit.isGolden) Color(0xFFB45309) else Color(0xFF15803D),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = if (fruit.isGolden) "+50 Altın 🪙" else "+20 Altın 🪙",
                                fontWeight = FontWeight.Black,
                                color = SunGold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = fruit.word.emoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = fruit.word.english,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "🗣️ Okunuşu: [ ${fruit.word.phonetic} ]",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PlayfulPurple,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "🇹🇷 Türkçe Anlamı: \"${fruit.word.turkish}\"",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Kid3DButton(
                                text = "Dinle 🔊",
                                onClick = { audioService.speakEnglish(fruit.word.english) },
                                color = BrandBlue,
                                shadowColor = Color(0xFF1E40AF),
                                modifier = Modifier.weight(1f)
                            )

                            Kid3DButton(
                                text = "Yavaş 🐢",
                                onClick = { audioService.speakEnglish(fruit.word.english, isSlow = true) },
                                color = SunGold,
                                shadowColor = Color(0xFFB45309),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PokoMascot(size = 50.dp, mood = PokoMood.HAPPY)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ağaçtaki altın ve meyveleri toplayarak günlük İngilizce serini koru!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
