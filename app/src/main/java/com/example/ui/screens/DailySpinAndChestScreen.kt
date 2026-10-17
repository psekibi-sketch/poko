package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WheelPrize(
    val title: String,
    val emoji: String,
    val coinReward: Int = 0,
    val starFruitReward: Int = 0,
    val chestReward: Int = 0,
    val color: Color
)

@Composable
fun DailySpinAndChestScreen(
    userCoins: Int,
    starFruits: Int,
    mysteryChests: Int,
    onRewardCoins: (Int) -> Unit,
    onRewardStarFruits: (Int) -> Unit,
    onRewardChests: (Int) -> Unit,
    onRewardCardPack: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onPlayPopFx: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPrizeDialog by remember { mutableStateOf<WheelPrize?>(null) }
    var openedChestReward by remember { mutableStateOf<String?>(null) }

    val wheelPrizes = remember {
        listOf(
            WheelPrize("100 Altın", "🪙", coinReward = 100, color = Color(0xFF3B82F6)),
            WheelPrize("2x Yıldız Meyvesi", "⭐🍎", starFruitReward = 2, color = Color(0xFF10B981)),
            WheelPrize("Gizemli Sandık", "🎁", chestReward = 1, color = Color(0xFF8B5CF6)),
            WheelPrize("50 Altın", "🪙", coinReward = 50, color = Color(0xFFF59E0B)),
            WheelPrize("3x Yıldız Meyvesi", "⭐🍎", starFruitReward = 3, color = Color(0xFFEC4899)),
            WheelPrize("200 Altın", "💰", coinReward = 200, color = Color(0xFF06B6D4)),
            WheelPrize("Kart Paketi", "🃏", color = Color(0xFF6366F1)),
            WheelPrize("150 Altın", "🪙", coinReward = 150, color = Color(0xFFF97316))
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0F19), Color(0xFF1E1B4B), Color(0xFF3B0764))
                )
            ),
        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .testTag("spin_back_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                }

                Text(
                    text = "🎡 Şans Çarkı & Gizemli Sandıklar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.5.dp, SunGold)
                ) {
                    Text(
                        text = "🪙 $userCoins",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 1. LUCKY SPIN WHEEL STAGE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
                border = BorderStroke(2.dp, Color(0xFFFDE047)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌟 Günlük Galaktik Şans Çarkı",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047)
                        )
                    )
                    Text(
                        text = "Çarkı çevir, altınlar, kart paketleri ve sandıklar kazan!",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8)),
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // The Spinning Wheel Canvas & Indicator Pointer
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // The Circular Wheel
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(rotationAnim.value)
                        ) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.width / 2f
                            val sliceAngle = 360f / wheelPrizes.size

                            wheelPrizes.forEachIndexed { i, prize ->
                                drawArc(
                                    color = prize.color,
                                    startAngle = i * sliceAngle,
                                    sweepAngle = sliceAngle,
                                    useCenter = true,
                                    size = size
                                )
                                drawArc(
                                    color = Color.White.copy(alpha = 0.4f),
                                    startAngle = i * sliceAngle,
                                    sweepAngle = sliceAngle,
                                    useCenter = true,
                                    style = Stroke(width = 2.dp.toPx()),
                                    size = size
                                )
                            }

                            // Outer gold ring
                            drawCircle(
                                color = Color(0xFFFDE047),
                                radius = radius,
                                style = Stroke(width = 6.dp.toPx())
                            )
                        }

                        // Center Hub
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = CircleShape,
                            color = Color(0xFF0F172A),
                            border = BorderStroke(3.dp, Color(0xFFFDE047)),
                            shadowElevation = 6.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⭐", fontSize = 22.sp)
                            }
                        }

                        // Top Pointer Arrow (Indicator)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-6).dp)
                        ) {
                            Text("🔻", fontSize = 32.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Kid3DButton(
                        text = if (isSpinning) "Çevriliyor... 🌀" else "ÇARKI ÇEVİR! 🎯",
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                onPlayPopFx()
                                coroutineScope.launch {
                                    val randomLaps = Random.nextInt(5, 9)
                                    val winningIndex = Random.nextInt(wheelPrizes.size)
                                    val targetDegree = (randomLaps * 360f) + (winningIndex * (360f / wheelPrizes.size)) + 22.5f

                                    rotationAnim.snapTo(0f)
                                    rotationAnim.animateTo(
                                        targetValue = targetDegree,
                                        animationSpec = tween(
                                            durationMillis = 3500,
                                            easing = FastOutSlowInEasing
                                        )
                                    )

                                    val prize = wheelPrizes[winningIndex]
                                    wonPrizeDialog = prize
                                    onPlayFanfareFx()

                                    // Grant Rewards
                                    if (prize.coinReward > 0) onRewardCoins(prize.coinReward)
                                    if (prize.starFruitReward > 0) onRewardStarFruits(prize.starFruitReward)
                                    if (prize.chestReward > 0) onRewardChests(prize.chestReward)
                                    if (prize.title.contains("Kart", ignoreCase = true)) onRewardCardPack()

                                    isSpinning = false
                                }
                            }
                        },
                        color = SunOrange,
                        shadowColor = Color(0xFFB45309),
                        enabled = !isSpinning,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "spin_wheel_btn"
                    )
                }
            }
        }

        // 2. MYSTERY CHESTS OPENING STAGE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
                border = BorderStroke(2.dp, Color(0xFF818CF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "🎁 Gizemli Hazine Sandıkları",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Mevcut Sandıkların: $mysteryChests Adet",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF67E8F9), fontWeight = FontWeight.Bold)
                            )
                        }

                        if (mysteryChests > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFDCFCE7),
                                border = BorderStroke(1.dp, LeafGreen)
                            ) {
                                Text(
                                    text = "AÇILABİLİR!",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF166534)
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ChestCard(
                            title = "Galaktik Sandık",
                            emoji = "🎁",
                            badge = if (mysteryChests > 0) "$mysteryChests Hazır" else "Kilitli",
                            canOpen = mysteryChests > 0,
                            onOpen = {
                                onRewardChests(-1)
                                val earnedCoins = Random.nextInt(80, 160)
                                val earnedFruits = Random.nextInt(2, 5)
                                onRewardCoins(earnedCoins)
                                onRewardStarFruits(earnedFruits)
                                onRewardCardPack()
                                onPlayFanfareFx()
                                openedChestReward = "🎉 Sandık Patladı!\n+$earnedCoins Altın 🪙\n+$earnedFruits Yıldız Meyvesi ⭐🍎\n+1 Hologram Kart Paketi 🃏"
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ChestCard(
                            title = "Kristal Sandık",
                            emoji = "💎",
                            badge = "300 Altın",
                            canOpen = userCoins >= 300,
                            onOpen = {
                                onRewardCoins(-300)
                                val earnedCoins = Random.nextInt(150, 400)
                                val earnedFruits = 5
                                onRewardCoins(earnedCoins)
                                onRewardStarFruits(earnedFruits)
                                onRewardCardPack()
                                onPlayFanfareFx()
                                openedChestReward = "💎 Kristal Sandık Açıldı!\n+$earnedCoins Altın 🪙\n+5 Yıldız Meyvesi ⭐🍎\n+2 Hologram Kart Paketi 🃏"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Win Prize Dialog
    val prizeWon = wonPrizeDialog
    if (prizeWon != null) {
        Dialog(onDismissRequest = { wonPrizeDialog = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = prizeWon.emoji, fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "TEBRİKLER! 🎉",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Text(
                        text = "Çarktan \"${prizeWon.title}\" kazandın!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Kid3DButton(
                        text = "Harika! Ödülü Al ⭐",
                        onClick = { wonPrizeDialog = null },
                        color = LeafGreen,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Chest Loot Dialog
    val chestReward = openedChestReward
    if (chestReward != null) {
        Dialog(onDismissRequest = { openedChestReward = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👑", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "HAZİNE SANDIĞI AÇILDI!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Text(
                        text = chestReward,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            lineHeight = 22.sp
                        ),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Kid3DButton(
                        text = "Koleksiyona Ekle 🎁",
                        onClick = { openedChestReward = null },
                        color = SunGold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ChestCard(
    title: String,
    emoji: String,
    badge: String,
    canOpen: Boolean,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = canOpen, onClick = onOpen),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF334155),
        border = BorderStroke(1.5.dp, if (canOpen) SunGold else Color(0xFF475569))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 42.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (canOpen) Color(0xFFFEF3C7) else Color(0xFF1E293B)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = if (canOpen) Color(0xFF78350F) else Color(0xFF94A3B8),
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
