package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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

data class AlbumSticker(
    val id: String,
    val pageId: String,
    val number: Int,
    val nameEn: String,
    val phonetic: String,
    val nameTr: String,
    val emoji: String,
    val isHolographic: Boolean = false
)

enum class StickerPageCategory(val id: String, val titleTr: String, val emoji: String) {
    STAR_WARS("star_wars", "Star Wars & Galaksi", "⚔️"),
    MARIO_WORLD("mario_world", "Mario & Mantar Dünyası", "🍄"),
    ANIMALS("animals", "Sevimli Hayvanlar", "🐾"),
    SUPERHEROES("superheroes", "Süper Kahramanlar", "🦸")
}

@Composable
fun StickerAlbumScreen(
    audioService: AudioService,
    userCoins: Int,
    onRewardCoins: (Int) -> Unit,
    onSpendCoins: (Int) -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allStickers = remember {
        listOf(
            // Star Wars Page
            AlbumSticker("sw_1", "star_wars", 1, "Lord Vader", "lord veydır", "Karanlık Lord", "⚔️", true),
            AlbumSticker("sw_2", "star_wars", 2, "Lightsaber", "laytseybır", "Işın Kılıcı", "⚡", true),
            AlbumSticker("sw_3", "star_wars", 3, "Master Yoda", "mastır yoda", "Bilge Yoda", "🟢", true),
            AlbumSticker("sw_4", "star_wars", 4, "Starship", "starşip", "Uzay Gemisi", "🚀", false),
            AlbumSticker("sw_5", "star_wars", 5, "Robo Droid", "robo droyd", "Yardımcı Robot", "🤖", false),
            AlbumSticker("sw_6", "star_wars", 6, "Cosmic Star", "kozmik star", "Kozmik Yıldız", "⭐", false),

            // Mario Page
            AlbumSticker("mr_1", "mario_world", 1, "Super Mario", "supır mario", "Süper Mario", "🍄", true),
            AlbumSticker("mr_2", "mario_world", 2, "Magic Mushroom", "mecik maşrum", "Büyülü Mantar", "🔴", false),
            AlbumSticker("mr_3", "mario_world", 3, "Golden Star", "goldın star", "Yenilmez Yıldız", "⭐", true),
            AlbumSticker("mr_4", "mario_world", 4, "Dragon Yoshi", "dregın yoşi", "Dinozor Yoshi", "🦖", false),
            AlbumSticker("mr_5", "mario_world", 5, "Gold Coin", "gold koyn", "Altın Sikke", "🪙", false),
            AlbumSticker("mr_6", "mario_world", 6, "Castle Flag", "kasıl fleg", "Kale Bayrağı", "🚩", false),

            // Animals Page
            AlbumSticker("an_1", "animals", 1, "Golden Lion", "goldın layın", "Altın Aslan", "🦁", true),
            AlbumSticker("an_2", "animals", 2, "Cute Puppy", "kyuut papi", "Tatlı Köpekçik", "🐶", false),
            AlbumSticker("an_3", "animals", 3, "Fluffy Cat", "flafi ket", "Pofuduk Kedi", "🐱", false),
            AlbumSticker("an_4", "animals", 4, "Baby Dino", "beybi dayno", "Yavru Dino", "🦕", true),
            AlbumSticker("an_5", "animals", 5, "Wise Owl", "wayz avl", "Bilge Baykuş", "🦉", false),
            AlbumSticker("an_6", "animals", 6, "Magic Dolphin", "mecik dolfin", "Sihirli Yunus", "🐬", false),

            // Superheroes Page
            AlbumSticker("sh_1", "superheroes", 1, "Cyber Knight", "saybır nayt", "Siber Şövalye", "🦸", true),
            AlbumSticker("sh_2", "superheroes", 2, "Laser Shield", "leyzır şiild", "Lazer Kalkan", "🛡️", false),
            AlbumSticker("sh_3", "superheroes", 3, "Thunder Bolt", "tandır bolt", "Şimşek Gücü", "⚡", true),
            AlbumSticker("sh_4", "superheroes", 4, "Speed Runner", "spiid ranır", "Hızlı Koşucu", "🏃", false),
            AlbumSticker("sh_5", "superheroes", 5, "Fire Phoenix", "fayır fiiniks", "Ateş Ankası", "🔥", true),
            AlbumSticker("sh_6", "superheroes", 6, "Hero Mask", "hiro mask", "Kahraman Maskesi", "🎭", false)
        )
    }

    var activeCategory by remember { mutableStateOf(StickerPageCategory.STAR_WARS) }
    val unlockedStickerIds = remember { mutableStateListOf("sw_1", "sw_4", "mr_1", "an_2") }

    var isPackOpeningModalOpen by remember { mutableStateOf(false) }
    var newlyOpenedStickers by remember { mutableStateOf<List<AlbumSticker>>(emptyList()) }

    val infiniteTransition = rememberInfiniteTransition(label = "holo_glimmer")
    val holoShine by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "holo"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF311042),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .testTag("sticker_album_screen")
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
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                Text(
                    text = "📖 HOLOGRAFİK ÇIKARTMA ALBÜMÜ",
                    fontWeight = FontWeight.Black,
                    color = SunGold,
                    fontSize = 13.sp
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.5.dp, SunGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Text(
                            text = "$userCoins",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Booster Pack Open Action Button
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PlayfulPurple,
                border = BorderStroke(2.dp, SunGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Open Booster Pack (3 random stickers)
                        audioService.playFanfareSound()
                        val randomPack = allStickers.shuffled().take(3)
                        newlyOpenedStickers = randomPack
                        randomPack.forEach { s ->
                            if (!unlockedStickerIds.contains(s.id)) {
                                unlockedStickerIds.add(s.id)
                            }
                        }
                        isPackOpeningModalOpen = true
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sürpriz Çıkartma Paketi Aç!",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "3 Adet Sürpriz Holografik Çıkartma İçerir ✨",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFEF08A),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(text = "ÜCRETSİZ 🌟", fontWeight = FontWeight.Black, color = SunGold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Page Switcher Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(StickerPageCategory.entries) { cat ->
                    val isSelected = activeCategory == cat
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SunGold else Color.White.copy(alpha = 0.15f),
                        border = if (isSelected) BorderStroke(1.5.dp, Color(0xFFB45309)) else null,
                        modifier = Modifier.clickable {
                            audioService.playPopSound()
                            activeCategory = cat
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = cat.emoji, fontSize = 14.sp)
                            Text(
                                text = cat.titleTr,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color(0xFF78350F) else Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sticker Album Book Page Grid (6 cards per page)
            val pageStickers = allStickers.filter { it.pageId == activeCategory.id }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pageStickers) { sticker ->
                    val isUnlocked = unlockedStickerIds.contains(sticker.id)

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) {
                                if (sticker.isHolographic) Color(0xFF1E1B4B) else Color(0xFF334155)
                            } else {
                                Color(0xFF0F172A).copy(alpha = 0.6f)
                            }
                        ),
                        border = BorderStroke(
                            width = if (sticker.isHolographic && isUnlocked) 2.5.dp else 1.5.dp,
                            color = if (isUnlocked) {
                                if (sticker.isHolographic) SunGold else Color(0xFF64748B)
                            } else {
                                Color(0xFF334155)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 6.dp else 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isUnlocked) {
                                audioService.speakEnglish(sticker.nameEn)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "#${sticker.number}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )

                                if (sticker.isHolographic) {
                                    Text(text = "✨ HOLO", fontWeight = FontWeight.Black, color = SunGold, fontSize = 9.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .scale(if (sticker.isHolographic) holoShine else 1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = sticker.emoji, fontSize = 38.sp)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = sticker.nameEn,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "🗣️ /${sticker.phonetic}/",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF67E8F9),
                                    fontSize = 10.5.sp
                                )

                                Text(
                                    text = sticker.nameTr,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE047),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🔒", fontSize = 32.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Gizli Çıkartma",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Paket Açarak Bul!",
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF475569),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Newly Opened Pack Dialog
        if (isPackOpeningModalOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { isPackOpeningModalOpen = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = BorderStroke(3.dp, SunGold),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false) {}
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉 TEBRİKLER!", fontWeight = FontWeight.Black, color = SunGold, fontSize = 20.sp)
                        Text(text = "3 Yeni Çıkartma Albümüne Eklendi:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            newlyOpenedStickers.forEach { s ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = s.emoji, fontSize = 36.sp)
                                    Text(text = s.nameEn, fontWeight = FontWeight.Black, color = Color.White, fontSize = 11.sp)
                                    Text(text = "🗣️ /${s.phonetic}/", color = Color(0xFF67E8F9), fontSize = 9.sp)
                                    Text(text = s.nameTr, color = Color(0xFFFDE047), fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Kid3DButton(
                            text = "Albümde Gör 📖 ✨",
                            onClick = { isPackOpeningModalOpen = false },
                            color = SunGold,
                            shadowColor = Color(0xFFB45309),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                }
            }
        }
    }
}
