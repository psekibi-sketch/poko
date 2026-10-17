package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.audio.AudioService
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPink
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

data class ColoringMission(
    val id: String,
    val objectNameEn: String,
    val objectNameTr: String,
    val targetColorNameEn: String,
    val targetColorNameTr: String,
    val targetColor: Color,
    val outlineEmoji: String,
    val promptEn: String,
    val promptTr: String
)

@Composable
fun MagicColoringScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val missions = remember {
        listOf(
            ColoringMission(
                id = "red_apple",
                objectNameEn = "Apple",
                objectNameTr = "Elma",
                targetColorNameEn = "Red",
                targetColorNameTr = "Kırmızı",
                targetColor = Color(0xFFEF4444),
                outlineEmoji = "🍏",
                promptEn = "Color the sweet apple RED!",
                promptTr = "Tatlı elmayı KIRMIZI renge boya!"
            ),
            ColoringMission(
                id = "yellow_sun",
                objectNameEn = "Sun",
                objectNameTr = "Güneş",
                targetColorNameEn = "Yellow",
                targetColorNameTr = "Sarı",
                targetColor = Color(0xFFFACC15),
                outlineEmoji = "☀️",
                promptEn = "Color the bright sun YELLOW!",
                promptTr = "Parlak güneşi SARI renge boya!"
            ),
            ColoringMission(
                id = "blue_balloon",
                objectNameEn = "Balloon",
                objectNameTr = "Balon",
                targetColorNameEn = "Blue",
                targetColorNameTr = "Mavi",
                targetColor = Color(0xFF3B82F6),
                outlineEmoji = "🎈",
                promptEn = "Color the flying balloon BLUE!",
                promptTr = "Uçan balonu MAVİ renge boya!"
            ),
            ColoringMission(
                id = "green_tree",
                objectNameEn = "Tree",
                objectNameTr = "Ağaç",
                targetColorNameEn = "Green",
                targetColorNameTr = "Yeşil",
                targetColor = Color(0xFF22C55E),
                outlineEmoji = "🌳",
                promptEn = "Color the big tree GREEN!",
                promptTr = "Büyük ağacı YEŞİL renge boya!"
            ),
            ColoringMission(
                id = "orange_cat",
                objectNameEn = "Cat",
                objectNameTr = "Kedi",
                targetColorNameEn = "Orange",
                targetColorNameTr = "Turuncu",
                targetColor = Color(0xFFF97316),
                outlineEmoji = "🐱",
                promptEn = "Color the cute cat ORANGE!",
                promptTr = "Sevimli kediyi TURUNCU renge boya!"
            )
        )
    }

    val paletteColors = remember {
        listOf(
            Color(0xFFEF4444) to "Red ❤️",
            Color(0xFF3B82F6) to "Blue 💙",
            Color(0xFFFACC15) to "Yellow 💛",
            Color(0xFF22C55E) to "Green 💚",
            Color(0xFFF97316) to "Orange 🧡",
            Color(0xFFA855F7) to "Purple 💜",
            Color(0xFFEC4899) to "Pink 💖"
        )
    }

    var currentMissionIndex by remember { mutableIntStateOf(0) }
    val currentMission = missions[currentMissionIndex % missions.size]

    var selectedBrushColor by remember { mutableStateOf(paletteColors.first().first) }
    var paintedColor by remember { mutableStateOf(Color(0xFFE2E8F0)) }
    var isMissionSolved by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81),
                        Color(0xFF4C1D95)
                    )
                )
            )
            .testTag("magic_coloring_screen")
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
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF472B6))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🎨", fontSize = 16.sp)
                        Text(
                            text = "Sihirli Boyama & Çizim Atölyesi",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24))
                ) {
                    Text(
                        text = "+20 🪙",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mission Instruction Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, currentMission.targetColor)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = currentMission.promptEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                fontSize = 16.sp
                            )
                        )
                        IconButton(
                            onClick = { audioService.speakEnglish(currentMission.promptEn) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Dinle",
                                tint = BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = currentMission.promptTr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA),
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Giant Interactive Canvas with Fill Color
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(240.dp)
                        .shadow(12.dp, CircleShape)
                        .clickable {
                            paintedColor = selectedBrushColor
                            if (selectedBrushColor == currentMission.targetColor) {
                                isMissionSolved = true
                                audioService.playCorrectSound()
                                audioService.playCoinSound()
                                audioService.speakEnglish("Excellent! Red Apple!")
                                onRewardCoins(20)
                            } else {
                                audioService.playPopSound()
                            }
                        }
                        .testTag("coloring_canvas_area"),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = paintedColor),
                    border = androidx.compose.foundation.BorderStroke(5.dp, Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentMission.outlineEmoji,
                            fontSize = 110.sp
                        )
                    }
                }
            }

            // Color Palette Selector Row
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fırça Rengini Seç & Resme Dokun:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(paletteColors) { (color, name) ->
                            val isSelected = selectedBrushColor == color
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 46.dp else 38.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.5.dp,
                                        color = if (isSelected) Color(0xFF1E1B4B) else Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedBrushColor = color
                                        audioService.speakEnglish(name.substringBefore(" "))
                                        audioService.playPopSound()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Brush,
                                        contentDescription = "Seçili",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Next mission button
            Kid3DButton(
                text = if (isMissionSolved) "Harika! Sonraki Resim ▶️" else "Sonraki Resme Geç 🔄",
                onClick = {
                    currentMissionIndex++
                    paintedColor = Color(0xFFE2E8F0)
                    isMissionSolved = false
                    audioService.playPopSound()
                },
                color = if (isMissionSolved) LeafGreen else SunGold,
                shadowColor = if (isMissionSolved) Color(0xFF15803D) else Color(0xFFB45309),
                modifier = Modifier.fillMaxWidth(),
                testTag = "coloring_next_btn"
            )
        }
    }
}
