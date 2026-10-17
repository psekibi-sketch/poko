package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeItem
import com.example.data.model.CostumeCategory
import com.example.data.model.PokoCostume
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

@Composable
fun AvatarShopScreen(
    userCoins: Int,
    costumes: List<PokoCostume>,
    badges: List<BadgeItem>,
    equippedCostumeId: String = "costume_default",
    equippedWeaponId: String = "weapon_red_saber",
    equippedHelmetId: String = "helmet_vader",
    equippedDecorId: String = "decor_holotable",
    equippedThemeId: String = "theme_starship",
    onEquipItem: (PokoCostume) -> Unit = {},
    onUnequipItem: (PokoCostume) -> Unit = {},
    onBuyCostume: (PokoCostume) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0: Costumes, 1: Weapons, 2: Helmets, 3: Room Decor, 4: Room Themes, 5: Badges
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories = remember {
        listOf(
            "🎽 Kıyafetler" to CostumeCategory.COSTUME,
            "⚔️ Işın Kılıçları" to CostumeCategory.LIGHTSABER,
            "🪖 Kask & Şapka" to CostumeCategory.HELMET,
            "🛸 Oda Eşyaları" to CostumeCategory.ROOM_DECOR,
            "🌌 Oda Teması" to CostumeCategory.ROOM_THEME
        )
    }

    val currentEquippedCostumeName = remember(costumes, equippedCostumeId) {
        costumes.find { it.id == equippedCostumeId }?.name ?: "Lord Vader Zırhı"
    }
    val currentEquippedWeaponName = remember(costumes, equippedWeaponId) {
        costumes.find { it.id == equippedWeaponId }?.name ?: "Kırmızı Plazma Kılıcı"
    }
    val currentEquippedHelmetName = remember(costumes, equippedHelmetId) {
        costumes.find { it.id == equippedHelmetId }?.name ?: "Lord Vader Miğferi"
    }
    val currentEquippedDecorName = remember(costumes, equippedDecorId) {
        costumes.find { it.id == equippedDecorId }?.name ?: "Hologram Yıldız Masası"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FC))
    ) {
        // Top Avatar Live Display Box (Shows all equipped equipment together)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Poko'nun Gardırobu & Odası ✨",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E293B)
                            )
                        )
                        Text(
                            text = "Kıyafet, kask ve kılıcı birlikte giyebilirsin!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🪙 $userCoins Altın", fontWeight = FontWeight.Black, color = Color(0xFFB45309), fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PokoMascot(
                    size = 110.dp,
                    mood = PokoMood.HAPPY,
                    costumeId = equippedCostumeId,
                    speechBubbleText = "Tüm eşyaların hazır! İstediğin gibi kombinle! ⚔️"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Active Equipped Slots Summary Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EquippedSlotPill("🎽", currentEquippedCostumeName, Modifier.weight(1f))
                    EquippedSlotPill("⚔️", currentEquippedWeaponName, Modifier.weight(1f))
                    EquippedSlotPill("🪖", currentEquippedHelmetName, Modifier.weight(1f))
                    EquippedSlotPill("🛸", currentEquippedDecorName, Modifier.weight(1f))
                }
            }
        }

        // Multi-Category Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            containerColor = Color.White,
            contentColor = BrandBlue,
            edgePadding = 12.dp,
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            categories.forEachIndexed { index, (title, _) ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = { selectedCategoryIndex = index },
                    text = { Text(title, fontWeight = if (selectedCategoryIndex == index) FontWeight.Black else FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("tab_category_$index")
                )
            }
            Tab(
                selected = selectedCategoryIndex == 5,
                onClick = { selectedCategoryIndex = 5 },
                text = { Text("🏆 Rozetler", fontWeight = if (selectedCategoryIndex == 5) FontWeight.Black else FontWeight.Bold, fontSize = 12.sp) },
                modifier = Modifier.testTag("tab_badges")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedCategoryIndex < 5) {
            val currentCategory = categories[selectedCategoryIndex].second
            val categoryItems = costumes.filter { it.category == currentCategory }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categoryItems) { item ->
                    val isEquippedInSlot = when (item.category) {
                        CostumeCategory.COSTUME -> item.id == equippedCostumeId
                        CostumeCategory.LIGHTSABER -> item.id == equippedWeaponId
                        CostumeCategory.HELMET -> item.id == equippedHelmetId
                        CostumeCategory.ROOM_DECOR -> item.id == equippedDecorId
                        CostumeCategory.ROOM_THEME -> item.id == equippedThemeId
                    }

                    ItemShopCard(
                        item = item,
                        userCoins = userCoins,
                        isEquipped = isEquippedInSlot,
                        onEquip = { onEquipItem(item) },
                        onUnequip = { onUnequipItem(item) },
                        onBuy = { onBuyCostume(item) }
                    )
                }
            }
        } else {
            // Badges List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(badges) { badge ->
                    BadgeItemCard(badge = badge)
                }
            }
        }
    }
}

@Composable
private fun EquippedSlotPill(emoji: String, name: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = name.take(8),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ItemShopCard(
    item: PokoCostume,
    userCoins: Int,
    isEquipped: Boolean,
    onEquip: () -> Unit,
    onUnequip: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shop_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (isEquipped) LeafGreen else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (isEquipped) Color(0xFFDCFCE7) else Color(0xFFF8FAFC),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                ),
                maxLines = 1
            )

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                ),
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )

            if (item.isUnlocked) {
                if (isEquipped) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LeafGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onUnequip)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kuşanıldı ✓ (Çıkar)",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF15803D),
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Kid3DButton(
                        text = "Kuşan / Giy",
                        onClick = onEquip,
                        color = BrandBlue,
                        shadowColor = Color(0xFF1E40AF),
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "equip_btn_${item.id}"
                    )
                }
            } else {
                val canAfford = userCoins >= item.price
                Kid3DButton(
                    text = "${item.price} 🪙",
                    onClick = onBuy,
                    color = if (canAfford) SunGold else Color(0xFF94A3B8),
                    shadowColor = if (canAfford) Color(0xFFD97706) else Color(0xFF64748B),
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "buy_btn_${item.id}"
                )
            }
        }
    }
}

@Composable
private fun BadgeItemCard(badge: BadgeItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_${badge.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 3.dp else 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (badge.isUnlocked) SunGold else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        if (badge.isUnlocked) Color(0xFFFEF3C7) else Color(0xFFE2E8F0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (badge.isUnlocked) badge.iconEmoji else "🔒", fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) Color(0xFF1E293B) else Color(0xFF64748B)
                    )
                )
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (!badge.isUnlocked) {
                    val progressFraction = (badge.progress.toFloat() / badge.requiredValue.toFloat()).coerceIn(0f, 1f)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BrandBlue,
                        trackColor = Color(0xFFCBD5E1)
                    )
                }
            }

            if (badge.isUnlocked) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
