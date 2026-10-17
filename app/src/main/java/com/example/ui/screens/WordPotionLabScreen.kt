package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.audio.AudioService
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold

data class PotionRecipe(
    val potionNameTr: String,
    val potionEmoji: String,
    val targetPhraseEn: String,
    val targetPhraseTr: String,
    val ingredientA: String, // e.g. "Red"
    val ingredientB: String, // e.g. "Apple"
    val potionColor: Color
)

/**
 * KELİME BÜYÜCÜSÜ & İKSİR KAZANI (Word Potion Lab)
 *
 * Kelimeleri sihirli kazana atıp kaynatarak yeni cümleler ve renkli iksirler üretme oyunu!
 */
@Composable
fun WordPotionLabScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val recipes = remember {
        listOf(
            PotionRecipe("Kızıl Ateş İksiri", "🧪🔴", "Red Apple", "Kırmızı Elma", "Red 🔴", "Apple 🍎", Color(0xFFEF4444)),
            PotionRecipe("Gök Mavisi Hız İksiri", "🧪🔵", "Blue Sky", "Mavi Gökyüzü", "Blue 🔵", "Sky ☁️", Color(0xFF0284C7)),
            PotionRecipe("Altın Güneş Şifası", "🧪🟡", "Yellow Sun", "Sarı Güneş", "Yellow 🟡", "Sun ☀️", Color(0xFFF59E0B)),
            PotionRecipe("Zümrüt Doğa İksiri", "🧪🟢", "Green Tree", "Yeşil Ağaç", "Green 🟢", "Tree 🌳", Color(0xFF10B981)),
            PotionRecipe("Mor Büyü İksiri", "🧪💜", "Happy Cat", "Mutlu Kedi", "Happy 😄", "Cat 🐱", Color(0xFF8B5CF6))
        )
    }

    var currentRecipeIdx by remember { mutableIntStateOf(0) }
    var addedIngredients by remember { mutableStateOf(setOf<String>()) }
    var isBrewed by remember { mutableStateOf(false) }
    var totalCoinsEarned by remember { mutableIntStateOf(0) }

    val currentRecipe = recipes[currentRecipeIdx % recipes.size]

    val availableIngredients = remember(currentRecipe) {
        listOf(
            currentRecipe.ingredientA,
            currentRecipe.ingredientB,
            "Cold ❄️",
            "Big 🐘",
            "Book 📖"
        ).shuffled()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cauldron_bubble")
    val bubbleScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81),
                        Color(0xFF4338CA)
                    )
                )
            )
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("word_potion_lab_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🧙‍♂️ Kelime İksir Kazanı",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = SunGold
                    )
                )
                Text(
                    text = "İksir ${currentRecipeIdx + 1} / ${recipes.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SunGold.copy(alpha = 0.25f)
            ) {
                Text(
                    text = "🪙 +$totalCoinsEarned",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = SunGold
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // TARGET RECIPE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hedef İksir: ${currentRecipe.potionNameTr} ${currentRecipe.potionEmoji}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = currentRecipe.potionColor
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "İksiri Kaynatmak İçin: \"${currentRecipe.targetPhraseEn}\" (${currentRecipe.targetPhraseTr})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // BUBBLING CAULDRON (Sihirli Kazan)
        Box(
            modifier = Modifier
                .scale(bubbleScale)
                .size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            // Cauldron Outer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (isBrewed) currentRecipe.potionColor else Color(0xFF6D28D9),
                                Color(0xFF1E1B4B)
                            )
                        )
                    )
                    .border(4.dp, if (isBrewed) SunGold else Color(0xFFA855F7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBrewed) currentRecipe.potionEmoji else "🍲✨",
                        fontSize = if (isBrewed) 54.sp else 46.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBrewed) "İKSİR HAZIR!" else if (addedIngredients.size == 1) "1 Malzeme Eklendi..." else "Malzemeleri Seç!",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // INGREDIENTS SHELF (Malzeme Rafı)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🪄 Kazana Atılacak Kelimeleri Seç:",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableIngredients) { ingredient ->
                        val isSelected = addedIngredients.contains(ingredient)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) LeafGreen else BrandBlue,
                            shadowElevation = 3.dp,
                            modifier = Modifier.clickable {
                                if (!isBrewed) {
                                    val nextSet = if (isSelected) addedIngredients - ingredient else addedIngredients + ingredient
                                    addedIngredients = nextSet

                                    if (nextSet.contains(currentRecipe.ingredientA) && nextSet.contains(currentRecipe.ingredientB)) {
                                        isBrewed = true
                                        audioService.playCorrectSound()
                                        audioService.speakEnglish(currentRecipe.targetPhraseEn)
                                        totalCoinsEarned += 35
                                        onRewardCoins(35)
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = ingredient,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // BOTTOM ACTION BUTTON
        if (isBrewed) {
            Kid3DButton(
                text = if (currentRecipeIdx + 1 >= recipes.size) "Tüm İksirleri Tamamladın! 🏆" else "Sonraki İksire Geç! 🧪✨",
                onClick = {
                    if (currentRecipeIdx + 1 >= recipes.size) {
                        onBack()
                    } else {
                        currentRecipeIdx++
                        addedIngredients = emptySet()
                        isBrewed = false
                    }
                },
                color = LeafGreen,
                shadowColor = Color(0xFF15803D),
                modifier = Modifier.fillMaxWidth(),
                testTag = "potion_next_btn"
            )
        } else {
            Kid3DButton(
                text = "Kazanı Sıfırla 🔄",
                onClick = { addedIngredients = emptySet() },
                color = Color(0xFF64748B),
                shadowColor = Color(0xFF334155),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
