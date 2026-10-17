package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioService
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PixelBlockType(
    val titleTr: String,
    val titleEn: String,
    val emoji: String,
    val primaryColor: Color,
    val darkColor: Color,
    val lightColor: Color,
    val rewardPoints: Int
) {
    GRASS("Çimen Blok", "Grass Block", "🟩", Color(0xFF5B8C3A), Color(0xFF385723), Color(0xFF7CB342), 5),
    DIRT("Toprak Blok", "Dirt Block", "🟫", Color(0xFF8D6E63), Color(0xFF5D4037), Color(0xFFA1887F), 5),
    STONE("Taş Blok", "Stone Block", "🪨", Color(0xFF78909C), Color(0xFF455A64), Color(0xFFB0BEC5), 8),
    WOOD("Meşe Odunu", "Oak Wood", "🪵", Color(0xFF6D4C41), Color(0xFF3E2723), Color(0xFF8D6E63), 10),
    GOLD("Altın Madeni", "Gold Ore", "🟡", Color(0xFFFFD54F), Color(0xFFFFA000), Color(0xFFFFF59D), 20),
    REDSTONE("Kızıltaş", "Redstone", "🔴", Color(0xFFE53935), Color(0xFFB71C1C), Color(0xFFEF5350), 25),
    EMERALD("Zümrüt Blok", "Emerald Block", "🟢", Color(0xFF00E676), Color(0xFF00A348), Color(0xFF69F0AE), 35),
    DIAMOND("Elmas Madeni", "Diamond Ore", "💎", Color(0xFF00E5FF), Color(0xFF0091EA), Color(0xFF84FFFF), 50),
    TNT("TNT Patlayıcı", "TNT Block", "🧨", Color(0xFFD32F2F), Color(0xFF212121), Color(0xFFFF5252), 40)
}

data class PixelMiningQuestion(
    val englishWord: String,
    val phonetic: String,
    val turkishMeaning: String,
    val emoji: String,
    val blockReward: PixelBlockType,
    val options: List<String>, // Turkish meaning choices
    val correctOptionIndex: Int
)

@Composable
fun MinecraftPixelCraftGameScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val questions = remember {
        listOf(
            PixelMiningQuestion("Sword", "soord", "Kılıç", "⚔️", PixelBlockType.DIAMOND, listOf("Kılıç", "Kalkan", "Yay", "Kazma"), 0),
            PixelMiningQuestion("Pickaxe", "pik-eks", "Kazma", "⛏️", PixelBlockType.GOLD, listOf("Balta", "Kazma", "Kılıç", "Kürek"), 1),
            PixelMiningQuestion("Apple", "epıl", "Elma", "🍎", PixelBlockType.EMERALD, listOf("Muz", "Portakal", "Elma", "Armut"), 2),
            PixelMiningQuestion("Castle", "kaasıl", "Şato / Kale", "🏰", PixelBlockType.STONE, listOf("Ev", "Okul", "Şato / Kale", "Köprü"), 2),
            PixelMiningQuestion("Tree", "trii", "Ağaç", "🌳", PixelBlockType.WOOD, listOf("Ağaç", "Çiçek", "Taş", "Nehir"), 0),
            PixelMiningQuestion("Torch", "torç", "Meşale", "🔦", PixelBlockType.REDSTONE, listOf("Meşale", "Lamba", "Güneş", "Ateş"), 0),
            PixelMiningQuestion("Diamond", "daymınd", "Elma / Elmas", "💎", PixelBlockType.DIAMOND, listOf("Gümüş", "Bakır", "Elmas", "Kömür"), 2),
            PixelMiningQuestion("Wolf", "vulf", "Kurt / Köpek", "🐺", PixelBlockType.GRASS, listOf("Kedi", "Kurt", "Aslan", "Ayı"), 1),
            PixelMiningQuestion("TNT", "ti-en-ti", "Dinamit", "🧨", PixelBlockType.TNT, listOf("Su", "Taş", "Dinamit / TNT", "Kalkan"), 2),
            PixelMiningQuestion("Gold", "gold", "Altın", "🪙", PixelBlockType.GOLD, listOf("Taş", "Altın", "Demir", "Ahşap"), 1),
            PixelMiningQuestion("Door", "door", "Kapı", "🚪", PixelBlockType.WOOD, listOf("Pencere", "Masa", "Kapı", "Sandalye"), 2),
            PixelMiningQuestion("Shield", "şiild", "Kalkan", "🛡️", PixelBlockType.DIAMOND, listOf("Kalkan", "Kılıç", "Ok", "Zırh"), 0)
        ).shuffled()
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = questions[currentQuestionIndex % questions.size]

    var score by remember { mutableIntStateOf(0) }
    var diamondsMined by remember { mutableIntStateOf(0) }
    var blocksMinedCount by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isCorrectFeedback by remember { mutableStateOf<Boolean?>(null) }

    // Inventory of collected block counts
    val inventory = remember {
        mutableStateListOf(
            Pair(PixelBlockType.GRASS, 4),
            Pair(PixelBlockType.WOOD, 3),
            Pair(PixelBlockType.STONE, 2),
            Pair(PixelBlockType.GOLD, 0),
            Pair(PixelBlockType.EMERALD, 0),
            Pair(PixelBlockType.DIAMOND, 0)
        )
    }

    // Build grid (8x5 grid of placed blocks)
    val buildWorldGrid = remember {
        mutableStateListOf<PixelBlockType?>().apply {
            repeat(40) { add(null) }
            // Bottom row pre-filled with grass
            for (i in 32..39) {
                this[i] = PixelBlockType.GRASS
            }
        }
    }

    var selectedBuildBlock by remember { mutableStateOf<PixelBlockType?>(PixelBlockType.GRASS) }
    var isBuildMode by remember { mutableStateOf(false) }

    // Pickaxe swing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pickaxe_anim")
    val pickaxeAngle by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swing"
    )

    LaunchedEffect(currentQuestionIndex) {
        selectedOptionIndex = null
        isCorrectFeedback = null
        audioService.speakEnglish(currentQuestion.englishWord)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E293B),
                        Color(0xFF0F172A),
                        Color(0xFF090D16)
                    )
                )
            )
            .testTag("minecraft_pixel_craft_screen")
    ) {
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
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                // Title & Mode Switcher
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF334155),
                    border = BorderStroke(2.dp, Color(0xFF64748B))
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (!isBuildMode) Color(0xFF0284C7) else Color.Transparent,
                            modifier = Modifier.clickable { isBuildMode = false }
                        ) {
                            Text(
                                text = "⛏️ Maden Kaz",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isBuildMode) LeafGreen else Color.Transparent,
                            modifier = Modifier.clickable { isBuildMode = true }
                        ) {
                            Text(
                                text = "🏰 Şato İnşa Et",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Diamonds & Score Badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.5.dp, Color(0xFF00E5FF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "💎", fontSize = 14.sp)
                        Text(
                            text = "$diamondsMined",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF84FFFF),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!isBuildMode) {
                // ==========================================
                // 1. MINING QUIZ MODE (Kazma & Blok Çıkarma)
                // ==========================================
                Text(
                    text = "⛏️ Kelimeyi Bil, Madeni Kaz!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFDE047)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Big Pixel Ore Block Target Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(3.dp, currentQuestion.blockReward.primaryColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Pixel Block 3D Cube Icon
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(currentQuestion.blockReward.primaryColor, RoundedCornerShape(12.dp))
                                    .border(3.dp, currentQuestion.blockReward.darkColor, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentQuestion.emoji,
                                    fontSize = 32.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = currentQuestion.englishWord,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "🗣️ /${currentQuestion.phonetic}/",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF67E8F9)
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = currentQuestion.blockReward.primaryColor.copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, currentQuestion.blockReward.primaryColor),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "+1 ${currentQuestion.blockReward.titleTr} ⛏️",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Audio button
                        IconButton(
                            onClick = { audioService.speakEnglish(currentQuestion.englishWord) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Dinle",
                                tint = Color(0xFFFDE047)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Options (4 Choices)
                Text(
                    text = "Doğru Türkçe karşılığını seç:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = index == currentQuestion.correctOptionIndex

                        val btnColor = when {
                            isSelected && isCorrect -> LeafGreen
                            isSelected && !isCorrect -> CandyRed
                            else -> Color(0xFF334155)
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = btnColor,
                            border = BorderStroke(
                                2.dp,
                                if (isSelected) Color.White else Color(0xFF475569)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = selectedOptionIndex == null) {
                                    selectedOptionIndex = index
                                    if (isCorrect) {
                                        isCorrectFeedback = true
                                        audioService.playPopSound()
                                        score += currentQuestion.blockReward.rewardPoints
                                        blocksMinedCount++
                                        if (currentQuestion.blockReward == PixelBlockType.DIAMOND) {
                                            diamondsMined++
                                        }

                                        // Add to inventory
                                        val existingIdx = inventory.indexOfFirst { it.first == currentQuestion.blockReward }
                                        if (existingIdx != -1) {
                                            val currentCount = inventory[existingIdx].second
                                            inventory[existingIdx] = Pair(currentQuestion.blockReward, currentCount + 1)
                                        } else {
                                            inventory.add(Pair(currentQuestion.blockReward, 1))
                                        }

                                        coroutineScope.launch {
                                            delay(700)
                                            if (currentQuestionIndex < questions.size - 1) {
                                                currentQuestionIndex++
                                            } else {
                                                audioService.playFanfareSound()
                                                onRewardCoins(40)
                                                currentQuestionIndex = 0
                                            }
                                        }
                                    } else {
                                        isCorrectFeedback = false
                                        audioService.playPopSound()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )

                                if (isSelected) {
                                    Text(
                                        text = if (isCorrect) "⛏️ +1 Blok!" else "❌ Tekrar Dene",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // 2. PIXEL FORTRESS BUILDER MODE (Şato İnşa Et)
                // ==========================================
                Text(
                    text = "🏰 Kazandığın Bloklarla Şatonu Yap!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = LeafGreen
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Inventory Palette
                Text(
                    text = "Kullanmak istediğin bloğa dokun:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inventory) { item ->
                        val block = item.first
                        val count = item.second
                        val isSelected = selectedBuildBlock == block

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = block.primaryColor,
                            border = BorderStroke(
                                2.5.dp,
                                if (isSelected) Color.White else block.darkColor
                            ),
                            modifier = Modifier
                                .clickable { selectedBuildBlock = block }
                                .scale(if (isSelected) 1.08f else 1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = block.emoji, fontSize = 20.sp)
                                Text(
                                    text = "x$count",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Build Canvas Grid (8x5 = 40 cells)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(2.dp, Color(0xFF475569)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(8),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(buildWorldGrid.size) { index ->
                            val currentBlock = buildWorldGrid[index]

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = currentBlock?.primaryColor ?: Color(0xFF1E293B),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = currentBlock?.darkColor ?: Color(0xFF334155),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        val activeBlock = selectedBuildBlock
                                        if (activeBlock != null) {
                                            val invItem = inventory.find { it.first == activeBlock }
                                            if (invItem != null && invItem.second > 0) {
                                                audioService.playPopSound()
                                                buildWorldGrid[index] = activeBlock
                                                // Deduct
                                                val invIdx = inventory.indexOf(invItem)
                                                inventory[invIdx] = Pair(activeBlock, invItem.second - 1)
                                            } else {
                                                // Mined out, clear block if clicked again
                                                buildWorldGrid[index] = null
                                            }
                                        } else {
                                            buildWorldGrid[index] = null
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (currentBlock != null) {
                                    Text(text = currentBlock.emoji, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Clean Canvas button
                Kid3DButton(
                    text = "🧹 Dünyayı Sıfırla",
                    onClick = {
                        for (i in 0 until 32) buildWorldGrid[i] = null
                        for (i in 32..39) buildWorldGrid[i] = PixelBlockType.GRASS
                    },
                    color = Color(0xFF64748B),
                    shadowColor = Color(0xFF334155),
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }
    }
}
