package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import com.example.data.curriculum.ExtraGameFeaturesData
import com.example.data.model.CardRarity
import com.example.data.model.HologramCardItem
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

@Composable
fun CardCollectionScreen(
    unlockedCardIds: Set<String>,
    userCoins: Int,
    onSpeak: (String, Boolean) -> Unit,
    onBuyPack: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var viewingCard by remember { mutableStateOf<HologramCardItem?>(null) }

    val allCards = remember { ExtraGameFeaturesData.allHologramCards }
    val filters = remember { listOf("Hepsi", "Efsanevi 🌟", "Epik 🟣", "Nadir 🔵", "Sıradan ⚪") }

    val filteredCards = remember(selectedFilterIndex, unlockedCardIds) {
        when (selectedFilterIndex) {
            1 -> allCards.filter { it.rarity == CardRarity.LEGENDARY }
            2 -> allCards.filter { it.rarity == CardRarity.EPIC }
            3 -> allCards.filter { it.rarity == CardRarity.RARE }
            4 -> allCards.filter { it.rarity == CardRarity.COMMON }
            else -> allCards
        }
    }

    val unlockedCount = remember(unlockedCardIds) {
        allCards.count { it.id in unlockedCardIds }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "holo_card_shine")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "holo_shine"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042))
                )
            )
            .testTag("card_collection_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp, 16.dp, 8.dp),
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🃏 Holografik Kart Koleksiyonu",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Text(
                    text = "$unlockedCount / ${allCards.size} Kart Toplandı",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFFDE047),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Buy Booster Pack
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SunGold,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onBuyPack() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎁 Paket Aç", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFF78350F))
                }
            }
        }

        // Filter Pills Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters.size) { index ->
                val isSelected = selectedFilterIndex == index
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) BrandBlue else Color(0xFF334155),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF93C5FD) else Color.Transparent),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedFilterIndex = index }
                ) {
                    Text(
                        text = filters[index],
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCards) { card ->
                val isUnlocked = card.id in unlockedCardIds
                HologramCardGridItem(
                    card = card,
                    isUnlocked = isUnlocked,
                    shineScale = if (card.rarity == CardRarity.LEGENDARY) shineOffset else 1f,
                    onClick = {
                        if (isUnlocked) {
                            viewingCard = card
                            onSpeak("${card.nameEn}! ${card.descriptionEn}", false)
                        }
                    }
                )
            }
        }
    }

    // Card Details Modal Dialog
    val cardDetail = viewingCard
    if (cardDetail != null) {
        Dialog(onDismissRequest = { viewingCard = null }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(3.dp, Color(cardDetail.rarity.borderGlowHex)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card Rarity Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(cardDetail.rarity.colorHex),
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = "${cardDetail.rarity.titleTr} • Güç ${cardDetail.powerLevel}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Card Big Avatar
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1E293B),
                        border = BorderStroke(2.dp, Color(cardDetail.rarity.borderGlowHex)),
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = cardDetail.emoji, fontSize = 48.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = cardDetail.nameEn,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "(${cardDetail.nameTr})",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF67E8F9)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "\"${cardDetail.descriptionEn}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = cardDetail.descriptionTr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    Kid3DButton(
                        text = "Sesli Oku 🔊",
                        onClick = {
                            onSpeak("${cardDetail.nameEn}! ${cardDetail.descriptionEn}", false)
                        },
                        color = BrandBlue,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun HologramCardGridItem(
    card: HologramCardItem,
    isUnlocked: Boolean,
    shineScale: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(if (isUnlocked) shineScale else 1f)
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("card_${card.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            if (isUnlocked) 2.dp else 1.dp,
            if (isUnlocked) Color(card.rarity.borderGlowHex) else Color(0xFF334155)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.rarity.titleTr.take(8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(card.rarity.colorHex)
                )
                Text(
                    text = if (isUnlocked) "⚡${card.powerLevel}" else "🔒",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFDE047)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = CircleShape,
                color = if (isUnlocked) Color(0xFF334155) else Color(0xFF1E293B),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = if (isUnlocked) card.emoji else "❓", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isUnlocked) card.nameEn else "Gizemli Kart",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isUnlocked) Color.White else Color(0xFF64748B)
                ),
                maxLines = 1
            )
            Text(
                text = if (isUnlocked) card.nameTr else "Açılmadı",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isUnlocked) Color(0xFF94A3B8) else Color(0xFF475569),
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}
