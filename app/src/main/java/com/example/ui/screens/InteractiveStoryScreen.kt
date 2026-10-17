package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
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
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

data class StoryInteractiveItem(
    val nameEn: String,
    val nameTr: String,
    val emoji: String
)

data class StoryPage(
    val titleEn: String,
    val textEn: String,
    val textTr: String,
    val illustrationEmoji: String,
    val interactiveItems: List<StoryInteractiveItem>
)

data class StoryBook(
    val id: String,
    val titleEn: String,
    val titleTr: String,
    val coverEmoji: String,
    val themeColor: Color,
    val pages: List<StoryPage>
)

@Composable
fun InteractiveStoryScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storyBooks = remember {
        listOf(
            StoryBook(
                id = "lost_star",
                titleEn = "Poko and the Lost Star",
                titleTr = "Poko ve Kayıp Galaktik Yıldız",
                coverEmoji = "🌟",
                themeColor = Color(0xFF6366F1),
                pages = listOf(
                    StoryPage(
                        titleEn = "A Star is Missing!",
                        textEn = "One night, Poko looked up at the sky. A tiny golden star was crying!",
                        textTr = "Bir gece, Poko gökyüzüne baktı. Minik altın bir yıldız ağlıyordu!",
                        illustrationEmoji = "🌌🌟🦊",
                        interactiveItems = listOf(
                            StoryInteractiveItem("Star", "Yıldız", "⭐"),
                            StoryInteractiveItem("Sky", "Gökyüzü", "🌌"),
                            StoryInteractiveItem("Moon", "Ay", "🌙")
                        )
                    ),
                    StoryPage(
                        titleEn = "The Space Rocket",
                        textEn = "Poko built a fast rocket and flew high into space with his friends.",
                        textTr = "Poko hızlı bir roket inşa etti ve arkadaşlarıyla uzayın derinliklerine uçtu.",
                        illustrationEmoji = "🚀🪐✨",
                        interactiveItems = listOf(
                            StoryInteractiveItem("Rocket", "Roket", "🚀"),
                            StoryInteractiveItem("Planet", "Gezegen", "🪐"),
                            StoryInteractiveItem("Friends", "Arkadaşlar", "🦊🤖")
                        )
                    ),
                    StoryPage(
                        titleEn = "Back Home!",
                        textEn = "They placed the star back into the constellation. The universe is shining bright!",
                        textTr = "Yıldızı tekrar takımyıldızına yerleştirdiler. Evren ışıl ışıl parıldıyor!",
                        illustrationEmoji = "🎉⭐👑",
                        interactiveItems = listOf(
                            StoryInteractiveItem("Smile", "Gülümseme", "😄"),
                            StoryInteractiveItem("Crown", "Taç", "👑"),
                            StoryInteractiveItem("Joy", "Neşe", "✨")
                        )
                    )
                )
            ),
            StoryBook(
                id = "dino_birthday",
                titleEn = "Dino's Space Birthday",
                titleTr = "Uzay Dinozorunun Doğum Günü",
                coverEmoji = "🦖",
                themeColor = Color(0xFF10B981),
                pages = listOf(
                    StoryPage(
                        titleEn = "A Big Birthday Surprise",
                        textEn = "Today is Dino's birthday! Poko brings a huge chocolate cake with candles.",
                        textTr = "Bugün Dinozorun doğum günü! Poko mumlarla süslü kocaman bir çikolatalı pasta getirdi.",
                        illustrationEmoji = "🦖🎂🎈",
                        interactiveItems = listOf(
                            StoryInteractiveItem("Dinosaur", "Dinozor", "🦖"),
                            StoryInteractiveItem("Cake", "Pasta", "🎂"),
                            StoryInteractiveItem("Candle", "Mum", "🕯️")
                        )
                    ),
                    StoryPage(
                        titleEn = "Opening the Gifts",
                        textEn = "Dino opened a big red gift box. Inside was a glowing lightsaber!",
                        textTr = "Dino büyük kırmızı hediye kutusunu açtı. İçinde ışıl ışıl bir ışın kılıcı vardı!",
                        illustrationEmoji = "🎁⚔️✨",
                        interactiveItems = listOf(
                            StoryInteractiveItem("Gift", "Hediye", "🎁"),
                            StoryInteractiveItem("Lightsaber", "Işın kılıcı", "⚔️"),
                            StoryInteractiveItem("Balloon", "Balon", "🎈")
                        )
                    )
                )
            ),
            StoryBook(
                id = "magic_forest",
                titleEn = "Colors of the Magic Forest",
                titleTr = "Sihirli Ormanın Renkleri",
                coverEmoji = "🌲",
                themeColor = Color(0xFFF59E0B),
                pages = listOf(
                    StoryPage(
                        titleEn = "The Rainbow Path",
                        textEn = "Poko walks through the green forest. Red apples, yellow birds, and blue butterflies appear!",
                        textTr = "Poko yemyeşil ormanda yürüyor. Kırmızı elmalar, sarı kuşlar ve mavi kelebekler ortaya çıkıyor!",
                        illustrationEmoji = "🌲🍎🦋",
                        interactiveItems = listOf(
                            StoryInteractiveItem("Tree", "Ağaç", "🌳"),
                            StoryInteractiveItem("Apple", "Elma", "🍎"),
                            StoryInteractiveItem("Butterfly", "Kelebek", "🦋")
                        )
                    )
                )
            )
        )
    }

    var selectedBookIndex by remember { mutableIntStateOf(0) }
    val currentBook = storyBooks[selectedBookIndex % storyBooks.size]
    var currentPageIndex by remember { mutableIntStateOf(0) }
    val activePage = currentBook.pages[currentPageIndex.coerceIn(0, currentBook.pages.size - 1)]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81)
                    )
                )
            )
            .testTag("interactive_story_screen")
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
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, currentBook.themeColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "📖", fontSize = 16.sp)
                        Text(
                            text = "Sesli Çizgi Masallar",
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
                        text = "${currentPageIndex + 1}/${currentBook.pages.size}",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Book Selection Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(storyBooks) { idx, book ->
                    val isSelected = idx == selectedBookIndex
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) book.themeColor else Color.White.copy(alpha = 0.15f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null,
                        modifier = Modifier.clickable {
                            selectedBookIndex = idx
                            currentPageIndex = 0
                            audioService.playPopSound()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = book.coverEmoji, fontSize = 16.sp)
                            Text(
                                text = book.titleEn,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Story Page Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(3.dp, currentBook.themeColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = activePage.titleEn,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = currentBook.themeColor,
                                fontSize = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Giant Story Emoji Illustration
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = currentBook.themeColor.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = activePage.illustrationEmoji,
                                    fontSize = 52.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // English Text
                        Text(
                            text = activePage.textEn,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                fontSize = 16.sp,
                                lineHeight = 22.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Turkish Subtitle Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = activePage.textTr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4338CA),
                                    fontSize = 13.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // Interactive Tap-to-Talk Words in the Scene
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "👇 Resimdeki Nesnelere Dokun & Dinle:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF64748B)
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activePage.interactiveItems.forEach { item ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = currentBook.themeColor.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, currentBook.themeColor),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            audioService.speakEnglish(item.nameEn)
                                            audioService.playPopSound()
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = item.emoji, fontSize = 24.sp)
                                        Text(
                                            text = item.nameEn,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF0F172A),
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = item.nameTr,
                                            fontWeight = FontWeight.Bold,
                                            color = currentBook.themeColor,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Page Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Kid3DButton(
                            text = "Sesli Oku 🔊",
                            onClick = {
                                audioService.speakEnglish(activePage.textEn)
                            },
                            color = LeafGreen,
                            shadowColor = Color(0xFF15803D),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        if (currentPageIndex < currentBook.pages.size - 1) {
                            Kid3DButton(
                                text = "Sonraki Sayfa ▶️",
                                onClick = {
                                    currentPageIndex++
                                    audioService.playPopSound()
                                },
                                color = SunGold,
                                shadowColor = Color(0xFFB45309),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Kid3DButton(
                                text = "Bitir & Yıldız Al 🌟",
                                onClick = {
                                    audioService.playFanfareSound()
                                    audioService.playCoinSound()
                                    onRewardCoins(20)
                                    currentPageIndex = 0
                                },
                                color = PlayfulPurple,
                                shadowColor = Color(0xFF6B21A8),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
