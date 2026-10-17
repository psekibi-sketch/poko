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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.PetType
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

@Composable
fun PetCareScreen(
    petType: String,
    petName: String,
    petLevel: Int,
    petExp: Int,
    petHunger: Int,
    starFruits: Int,
    onFeedPet: () -> Unit,
    onPetPet: () -> Unit,
    onSelectPetType: (String) -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pet_anim")

    val petFloat by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pet_float"
    )

    val petHeartPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_pulse"
    )

    var petReactionText by remember { mutableStateOf("Hello! I am ${petName}! Feed me star fruits! ⭐") }

    val petEmoji = when {
        petType.contains("droid", ignoreCase = true) -> if (petLevel >= 3) "🤖" else "🔩"
        petType.contains("cat", ignoreCase = true) -> if (petLevel >= 3) "🦁" else "🐱"
        else -> if (petLevel >= 3) "🐉" else if (petLevel >= 2) "🐲" else "🥚"
    }

    val petStageName = when {
        petLevel >= 4 -> "Galaktik Efsane Form (Lv.$petLevel)"
        petLevel >= 3 -> "Kanatlı Şampiyon (Lv.$petLevel)"
        petLevel >= 2 -> "Büyüyen Yavru (Lv.$petLevel)"
        else -> "Yavru Bebek (Lv.$petLevel)"
    }

    val expNeeded = petLevel * 100
    val expProgress = (petExp.toFloat() / expNeeded.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81))
                )
            ),
        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Navigation Bar
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
                        .testTag("pet_back_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                }

                Text(
                    text = "🐾 Poko Pet & Ejderha Bahçesi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )

                // Star Fruit Inventory Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF08A),
                    border = BorderStroke(1.5.dp, SunGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐🍎 $starFruits", fontWeight = FontWeight.Black, color = Color(0xFF78350F), fontSize = 13.sp)
                    }
                }
            }
        }

        // 3D Pet Showcase Stage
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
                border = BorderStroke(2.dp, Color(0xFF818CF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Speech Bubble
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clickable {
                                onSpeak(petReactionText, false)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = petReactionText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B),
                                    fontSize = 13.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Animated Pet Avatar
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .offset(y = petFloat.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF818CF8).copy(alpha = 0.4f), Color.Transparent)
                                ),
                                CircleShape
                            )
                            .clickable {
                                onPetPet()
                                petReactionText = "I love you! You are my best English partner! ❤️"
                                onSpeak("I love you! Let's learn English together!", false)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = petEmoji,
                            fontSize = if (petLevel >= 3) 76.sp else 64.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$petName - $petStageName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // EXP Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "✨ Seviye İlerlemesi (EXP)", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$petExp / $expNeeded EXP", color = Color(0xFFE2E8F0), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { expProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF00E676),
                        trackColor = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Interactive Action Buttons (Feed & Love)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Kid3DButton(
                            text = "Yıldız Meyvesi Yedir ⭐🍎",
                            onClick = {
                                if (starFruits > 0) {
                                    onFeedPet()
                                    petReactionText = "Yummy! Delicious star fruit! +30 EXP! 🌟"
                                    onSpeak("Yummy! Delicious star fruit! Thank you!", false)
                                } else {
                                    petReactionText = "Yıldız meyven bitti! Yeni meyveler için dersleri tamamla! 📚"
                                }
                            },
                            color = if (starFruits > 0) SunGold else Color(0xFF64748B),
                            shadowColor = if (starFruits > 0) Color(0xFFB45309) else Color(0xFF334155),
                            enabled = starFruits > 0,
                            modifier = Modifier.weight(1.3f),
                            testTag = "pet_feed_btn"
                        )

                        Kid3DButton(
                            text = "Sev & Oyna ❤️",
                            onClick = {
                                onPetPet()
                                petReactionText = "Purr! You are doing awesome! 💫"
                                onSpeak("Great job! You are awesome!", false)
                            },
                            color = PlayfulPurple,
                            shadowColor = Color(0xFF581C87),
                            modifier = Modifier.weight(1f),
                            testTag = "pet_love_btn"
                        )
                    }
                }
            }
        }

        // Pet Choice Switcher (Ejderha / Droid / Kedi)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, Color(0xFF475569))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🐾 Galaktik Petini Seç",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PetTypeOptionPill(
                            title = "Ejderha Drako",
                            emoji = "🐉",
                            isSelected = petType == "DRAGON",
                            onClick = { onSelectPetType("DRAGON") },
                            modifier = Modifier.weight(1f)
                        )
                        PetTypeOptionPill(
                            title = "BB-8 Droid",
                            emoji = "🤖",
                            isSelected = petType == "DROID",
                            onClick = { onSelectPetType("DROID") },
                            modifier = Modifier.weight(1f)
                        )
                        PetTypeOptionPill(
                            title = "Kozmik Kedi",
                            emoji = "🐱",
                            isSelected = petType == "COSMO_CAT",
                            onClick = { onSelectPetType("COSMO_CAT") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // How to earn star fruits guide
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF312E81).copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, Color(0xFF6366F1))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Her tamamladığın İngilizce dersi ve mini oyun 2x Yıldız Meyvesi kazandırır! Petini besleyerek efsanevi forma dönüştür!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFE0E7FF),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PetTypeOptionPill(
    title: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) BrandBlue else Color(0xFF334155),
        border = BorderStroke(1.5.dp, if (isSelected) Color(0xFFFDE047) else Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
