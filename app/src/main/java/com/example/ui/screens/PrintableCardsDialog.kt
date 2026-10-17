package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioService
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.SunGold

data class PrintableCardItem(
    val english: String,
    val phonetic: String,
    val turkish: String,
    val emoji: String,
    val coloringHint: String
)

@Composable
fun PrintableCardsDialog(
    audioService: AudioService,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleCards = remember {
        listOf(
            PrintableCardItem("Apple", "epıl", "Elma", "🍎", "Kırmızıya boya"),
            PrintableCardItem("Cat", "ket", "Kedi", "🐱", "Sarıya boya"),
            PrintableCardItem("Dog", "dog", "Köpek", "🐶", "Kahverengiye boya"),
            PrintableCardItem("Sun", "san", "Güneş", "☀️", "Sarıya boya"),
            PrintableCardItem("Car", "kar", "Araba", "🚗", "Maviye boya"),
            PrintableCardItem("Star", "star", "Yıldız", "⭐", "Altın rengine boya")
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onDismiss() }
            .testTag("printable_cards_dialog"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(3.dp, BrandBlue),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🖨️ Yazdırılabilir MEB Kartları",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Text(
                    text = "Buzdolabına asmak veya boyatmak için tek tıkla inceleyebilirsiniz:",
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Grid of 6 Flashcards
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sampleCards) { card ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.clickable {
                                audioService.speakEnglish(card.english)
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = card.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = card.english,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "🗣️ /${card.phonetic}/",
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlue,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = card.turkish,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "🎨 ${card.coloringHint}",
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Kid3DButton(
                    text = "Ekran Görüntüsü Al / Yazdır 🖨️",
                    onClick = {
                        audioService.speakEnglish("Ready to print flashcards for practice!")
                        onDismiss()
                    },
                    color = LeafGreen,
                    shadowColor = Color(0xFF15803D),
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }
    }
}
