package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Flight
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

data class WorldCity(
    val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val landmarkEmoji: String,
    val landmarkName: String,
    val dialogEn: String,
    val phonetic: String,
    val dialogTr: String,
    val questionEn: String,
    val optionsTr: List<String>,
    val correctIndex: Int
)

/**
 * SANAL DÜNYA TURU & ŞEHİR KEŞFİ (World Explorer)
 *
 * Londra, New York, Tokyo, Paris ve Sydney şehirlerine sanal seyahat ve mini diyaloglar!
 */
@Composable
fun WorldExplorerScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val cities = remember {
        listOf(
            WorldCity(
                id = "city_london",
                name = "Londra",
                country = "İngiltere",
                flag = "🇬🇧",
                landmarkEmoji = "🕰️🚌",
                landmarkName = "Big Ben & Kırmızı Otobüsler",
                dialogEn = "Welcome to London! One ticket for the double decker bus, please!",
                phonetic = "vel-kım tu lan-dın van ti-kıt for dı dabıl dekır bas pliiz",
                dialogTr = "Londra'ya hoş geldiniz! İki katlı otobüs için bir bilet lütfen!",
                questionEn = "What color are London's famous buses?",
                optionsTr = listOf("Kırmızı 🔴", "Mavi 🔵", "Yeşil 🟢"),
                correctIndex = 0
            ),
            WorldCity(
                id = "city_ny",
                name = "New York",
                country = "Amerika",
                flag = "🇺🇸",
                landmarkEmoji = "🗽🏙️",
                landmarkName = "Özgürlük Heykeli & Gökdelenler",
                dialogEn = "Good afternoon! I want to visit Central Park and take photos!",
                phonetic = "gud af-tır-nuun ay vont tu vi-zit sen-tır-ıl park",
                dialogTr = "Tünaydın! Central Park'ı ziyaret etmek ve fotoğraf çekmek istiyorum!",
                questionEn = "Where is the Statue of Liberty?",
                optionsTr = listOf("New York 🗽", "Londra 🇬🇧", "Tokyo 🗾"),
                correctIndex = 0
            ),
            WorldCity(
                id = "city_tokyo",
                name = "Tokyo",
                country = "Japonya",
                flag = "🇯🇵",
                landmarkEmoji = "🚅🌸",
                landmarkName = "Hızlı Tren (Shinkansen) & Kiraz Çiçekleri",
                dialogEn = "Konnichiwa! The bullet train is very fast and modern!",
                phonetic = "dı bu-lıt treyn iz ve-ri fast end ma-dırn",
                dialogTr = "Merhaba! Hızlı tren çok süratli ve moderatedir!",
                questionEn = "How does the bullet train move?",
                optionsTr = listOf("Çok Hızlı 🚅", "Çok Yavaş 🐢", "Yüzerek 🏊"),
                correctIndex = 0
            ),
            WorldCity(
                id = "city_paris",
                name = "Paris",
                country = "Fransa",
                flag = "🇫🇷",
                landmarkEmoji = "🗼🥐",
                landmarkName = "Eyfel Kulesi & Kruvasan",
                dialogEn = "Bonjour! Look at the tall Eiffel Tower touching the clouds!",
                phonetic = "luk et dı tool ay-fıl tavır ta-çiñ dı klavdz",
                dialogTr = "Merhaba! Bulutlara değen uzun Eyfel Kulesi'ne bak!",
                questionEn = "What is the famous tower in Paris?",
                optionsTr = listOf("Eiffel Tower 🗼", "Big Ben 🕰️", "Pyramid 🔺"),
                correctIndex = 0
            )
        )
    }

    var selectedCityIdx by remember { mutableIntStateOf(0) }
    var isAnsweredCorrectly by remember { mutableStateOf(false) }
    var totalEarnedCoins by remember { mutableIntStateOf(0) }

    val currentCity = cities[selectedCityIdx]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0369A1),
                        Color(0xFF0284C7),
                        Color(0xFF38BDF8)
                    )
                )
            )
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("world_explorer_screen"),
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
                    text = "🌍 Sanal Dünya Turu",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Text(
                    text = "Şehirleri Gez, İngilizce Keşfet!",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SunGold.copy(alpha = 0.35f)
            ) {
                Text(
                    text = "🪙 +$totalEarnedCoins",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = SunGold
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // CITY SELECTOR STRIP
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cities.indices.toList()) { idx ->
                val city = cities[idx]
                val isSelected = (idx == selectedCityIdx)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) SunGold else Color.White.copy(alpha = 0.85f),
                    shadowElevation = if (isSelected) 6.dp else 2.dp,
                    modifier = Modifier.clickable {
                        selectedCityIdx = idx
                        isAnsweredCorrectly = false
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = city.flag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = city.name,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color(0xFF92400E) else Color(0xFF1E293B),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // CITY LANDMARK HERO CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = currentCity.landmarkEmoji, fontSize = 48.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${currentCity.name} - ${currentCity.landmarkName}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "\"${currentCity.dialogEn}\"",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF15803D)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { audioService.speakEnglish(currentCity.dialogEn) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Dinle",
                                    tint = LeafGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "[${currentCity.phonetic}]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PlayfulPurple
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentCity.dialogTr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // MINI QUIZ
                Text(
                    text = "❓ ${currentCity.questionEn}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                currentCity.optionsTr.forEachIndexed { optIdx, opt ->
                    Kid3DButton(
                        text = opt,
                        onClick = {
                            if (optIdx == currentCity.correctIndex) {
                                isAnsweredCorrectly = true
                                audioService.playCorrectSound()
                                totalEarnedCoins += 40
                                onRewardCoins(40)
                            } else {
                                audioService.playWrongSound()
                            }
                        },
                        color = if (isAnsweredCorrectly && optIdx == currentCity.correctIndex) LeafGreen else BrandBlue,
                        shadowColor = Color(0xFF1E40AF),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        testTag = "city_quiz_opt_$optIdx"
                    )
                }
            }
        }

        // BOTTOM ACTION
        if (isAnsweredCorrectly) {
            Kid3DButton(
                text = if (selectedCityIdx + 1 >= cities.size) "Tüm Şehirleri Gezdin! 🌍🏆" else "Sonraki Şehre Uç ✈️",
                onClick = {
                    if (selectedCityIdx + 1 >= cities.size) {
                        onBack()
                    } else {
                        selectedCityIdx++
                        isAnsweredCorrectly = false
                    }
                },
                color = LeafGreen,
                shadowColor = Color(0xFF15803D),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
