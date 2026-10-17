package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AudioService
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

data class TownBuildingWord(
    val english: String,
    val turkish: String,
    val emoji: String
)

data class TownBuilding(
    val id: String,
    val nameEn: String,
    val nameTr: String,
    val emoji: String,
    val unlockCost: Int,
    val words: List<TownBuildingWord>,
    val color: Color
)

@Composable
fun PokoTownScreen(
    audioService: AudioService,
    userCoins: Int,
    onBuyBuilding: (Int) -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buildings = remember {
        listOf(
            TownBuilding(
                id = "ice_cream",
                nameEn = "Ice Cream Parlor",
                nameTr = "Dondurma Dükkanı",
                emoji = "🍦",
                unlockCost = 40,
                color = Color(0xFFEC4899),
                words = listOf(
                    TownBuildingWord("Ice cream", "Dondurma", "🍦"),
                    TownBuildingWord("Strawberry", "Çilek", "🍓"),
                    TownBuildingWord("Vanilla", "Vanilya", "🍨"),
                    TownBuildingWord("Cone", "Külah", "🧇")
                )
            ),
            TownBuilding(
                id = "dino_park",
                nameEn = "Jurassic Dino Park",
                nameTr = "Dinozor Parkı",
                emoji = "🦖",
                unlockCost = 60,
                color = Color(0xFF10B981),
                words = listOf(
                    TownBuildingWord("Dinosaur", "Dinozor", "🦖"),
                    TownBuildingWord("Egg", "Yumurta", "🥚"),
                    TownBuildingWord("Tree", "Ağaç", "🌳"),
                    TownBuildingWord("Forest", "Orman", "🌲")
                )
            ),
            TownBuilding(
                id = "space_base",
                nameEn = "Galactic Space Base",
                nameTr = "Lego Uzay İstasyonu",
                emoji = "🚀",
                unlockCost = 80,
                color = Color(0xFF6366F1),
                words = listOf(
                    TownBuildingWord("Rocket", "Roket", "🚀"),
                    TownBuildingWord("Planet", "Gezegen", "🪐"),
                    TownBuildingWord("Astronaut", "Astronot", "👨‍🚀"),
                    TownBuildingWord("Star", "Yıldız", "⭐")
                )
            ),
            TownBuilding(
                id = "bakery",
                nameEn = "Sweet Bakery & Cafe",
                nameTr = "Fırın & Tatlıcı",
                emoji = "🥐",
                unlockCost = 50,
                color = Color(0xFFF59E0B),
                words = listOf(
                    TownBuildingWord("Bread", "Ekmek", "🍞"),
                    TownBuildingWord("Cake", "Pasta", "🎂"),
                    TownBuildingWord("Cookie", "Kurabiye", "🍪"),
                    TownBuildingWord("Milk", "Süt", "🥛")
                )
            ),
            TownBuilding(
                id = "amusement_park",
                nameEn = "Wonder Ferris Wheel",
                nameTr = "Lunapark Dönme Dolabı",
                emoji = "🎡",
                unlockCost = 70,
                color = Color(0xFF8B5CF6),
                words = listOf(
                    TownBuildingWord("Ferris wheel", "Dönme dolap", "🎡"),
                    TownBuildingWord("Balloon", "Balon", "🎈"),
                    TownBuildingWord("Ticket", "Bilet", "🎟️"),
                    TownBuildingWord("Popcorn", "Patlamış mısır", "🍿")
                )
            ),
            TownBuilding(
                id = "toy_store",
                nameEn = "Magic Toy Store",
                nameTr = "Sihirli Oyuncakçı",
                emoji = "🧸",
                unlockCost = 50,
                color = Color(0xFF0284C7),
                words = listOf(
                    TownBuildingWord("Teddy bear", "Oyuncak ayı", "🧸"),
                    TownBuildingWord("Robot", "Robot", "🤖"),
                    TownBuildingWord("Car", "Araba", "🚗"),
                    TownBuildingWord("Doll", "Oyuncak bebek", "🪆")
                )
            )
        )
    }

    val unlockedBuildingIds = remember { mutableStateListOf("ice_cream") }
    var selectedBuildingForInspection by remember { mutableStateOf<TownBuilding?>(null) }

    if (selectedBuildingForInspection != null) {
        val building = selectedBuildingForInspection!!
        val isUnlocked = unlockedBuildingIds.contains(building.id)

        Dialog(onDismissRequest = { selectedBuildingForInspection = null }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, building.color),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = building.emoji, fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = building.nameEn,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = building.color,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = building.nameTr,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isUnlocked) {
                        Text(
                            text = "Bu binayı kasabana inşa etmek için ${building.unlockCost} Altın gerekir.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1E293B))
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Kid3DButton(
                            text = if (userCoins >= building.unlockCost) "İnşa Et (${building.unlockCost} 🪙)" else "Yetersiz Altın (${building.unlockCost} 🪙)",
                            onClick = {
                                if (userCoins >= building.unlockCost) {
                                    onBuyBuilding(building.unlockCost)
                                    unlockedBuildingIds.add(building.id)
                                    audioService.playCorrectSound()
                                    audioService.playFanfareSound()
                                } else {
                                    audioService.playWrongSound()
                                }
                            },
                            color = if (userCoins >= building.unlockCost) LeafGreen else Color(0xFF94A3B8),
                            shadowColor = if (userCoins >= building.unlockCost) Color(0xFF15803D) else Color(0xFF64748B),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Binadaki Kelimeler (Dinle ve Öğren):",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E293B)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        building.words.forEach { word ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = building.color.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, building.color.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        audioService.speakEnglish(word.english)
                                        audioService.playPopSound()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = word.emoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = word.english,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF0F172A),
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = word.turkish,
                                                fontWeight = FontWeight.Bold,
                                                color = building.color,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Dinle",
                                        tint = building.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Kid3DButton(
                            text = "Kapat",
                            onClick = { selectedBuildingForInspection = null },
                            color = BrandBlue,
                            shadowColor = Color(0xFF1E40AF),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
                        Color(0xFF38BDF8), // Morning sky
                        Color(0xFFBAE6FD),
                        Color(0xFF86EFAC), // Green meadow
                        Color(0xFF22C55E)
                    )
                )
            )
            .testTag("poko_town_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
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
                        tint = BrandBlue
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🏰", fontSize = 16.sp)
                        Text(
                            text = "Poko'nun Büyüyen Kasabası",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            fontSize = 13.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24))
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

            Spacer(modifier = Modifier.height(12.dp))

            // Town Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(buildings) { b ->
                    val isUnlocked = unlockedBuildingIds.contains(b.id)

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.75f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (isUnlocked) b.color else Color(0xFFCBD5E1)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 6.dp else 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedBuildingForInspection = b
                                audioService.playPopSound()
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(
                                        if (isUnlocked) b.color.copy(alpha = 0.15f) else Color(0xFFE2E8F0),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isUnlocked) b.emoji else "🔒",
                                    fontSize = 34.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = b.nameEn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isUnlocked) Color(0xFF0F172A) else Color(0xFF64748B),
                                    fontSize = 14.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = b.nameTr,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) b.color else Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isUnlocked) LeafGreen.copy(alpha = 0.15f) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = if (isUnlocked) "Açık & Keşfet 🌟" else "İnşa Et: ${b.unlockCost} 🪙",
                                    fontWeight = FontWeight.Black,
                                    color = if (isUnlocked) LeafGreen else Color(0xFFB45309),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
