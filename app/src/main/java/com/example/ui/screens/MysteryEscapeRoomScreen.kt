package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold

data class RoomItem(
    val id: String,
    val nameEn: String,
    val nameTr: String,
    val emoji: String,
    val isKeyHolder: Boolean = false
)

data class EscapeClue(
    val clueTextEn: String,
    val clueTextTr: String,
    val targetItemId: String,
    val hintTr: String
)

/**
 * GİZEMLİ KAÇIŞ ODASI (Mystery Escape Room)
 *
 * İngilizce ipuçlarını dinleyerek ve okuyarak odadaki gizli eşyaları bulup kapının anahtarını açma oyunu!
 */
@Composable
fun MysteryEscapeRoomScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val clues = remember {
        listOf(
            EscapeClue("Find the yellow book on the shelf!", "Raftaki sarı kitabı bul!", "item_book", "Kitaplıkta duruyor 📖"),
            EscapeClue("Tap the shiny silver key under the table!", "Masanın altındaki parlak gümüş anahtara dokun!", "item_key", "Masanın ayaklarına bak 🗝️"),
            EscapeClue("Look at the golden wall clock!", "Duvardaki altın saate bak!", "item_clock", "Saatin kaç olduğunu gösteriyor ⏰"),
            EscapeClue("Open the wooden treasure safe!", "Ahşap hazine kasasını aç!", "item_chest", "Şifre çözüldü, kasa açıldı 🎁")
        )
    }

    var currentClueIdx by remember { mutableIntStateOf(0) }
    var isDoorUnlocked by remember { mutableStateOf(false) }
    var discoveredItems by remember { mutableStateOf(setOf<String>()) }
    var messageBanner by remember { mutableStateOf("İpucunu dinle ve odayı araştır! 🔍") }

    val currentClue = clues[currentClueIdx % clues.size]

    val roomItems = remember {
        listOf(
            RoomItem("item_book", "Book", "Kitap", "📖"),
            RoomItem("item_key", "Key", "Anahtar", "🗝️", isKeyHolder = true),
            RoomItem("item_clock", "Clock", "Saat", "⏰"),
            RoomItem("item_chest", "Safe Chest", "Kasa", "🗄️"),
            RoomItem("item_lamp", "Lamp", "Lamba", "💡"),
            RoomItem("item_chair", "Chair", "Sandalye", "🪑"),
            RoomItem("item_window", "Window", "Pencere", "🪟"),
            RoomItem("item_door", "Locked Door", "Kilitli Kapı", "🚪")
        )
    }

    LaunchedEffect(currentClueIdx, isDoorUnlocked) {
        if (!isDoorUnlocked) {
            audioService.speakEnglish(currentClue.clueTextEn)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF334155)
                    )
                )
            )
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("mystery_escape_room_screen"),
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
                    text = "🕵️‍♂️ Gizemli Kaçış Odası",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = SunGold
                    )
                )
                Text(
                    text = "İpucu ${currentClueIdx + 1} / ${clues.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDoorUnlocked) LeafGreen else Color(0xFFEF4444)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDoorUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDoorUnlocked) "Açık!" else "Kilitli",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // CLUE BANNER
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔍", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentClue.clueTextEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    )
                    IconButton(
                        onClick = { audioService.speakEnglish(currentClue.clueTextEn) },
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

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = currentClue.clueTextTr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // INTERACTIVE ROOM GRID (Odadaki Eşyalar)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF475569))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏠 Odayı İncele ve İpucundaki Eşyaya Dokun:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    roomItems.take(4).forEach { item ->
                        RoomItemButton(
                            item = item,
                            isDiscovered = discoveredItems.contains(item.id),
                            onClick = {
                                if (item.id == currentClue.targetItemId) {
                                    discoveredItems = discoveredItems + item.id
                                    audioService.playCorrectSound()
                                    messageBanner = "Tebrikler! ${item.nameEn} (${item.nameTr}) bulundu! 🎉"

                                    if (currentClueIdx + 1 >= clues.size) {
                                        isDoorUnlocked = true
                                        audioService.playFanfareSound()
                                        onRewardCoins(60)
                                    } else {
                                        currentClueIdx++
                                    }
                                } else {
                                    audioService.playWrongSound()
                                    messageBanner = "Bu eşya değil, ipucunu tekrar oku! 🔍"
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    roomItems.drop(4).take(4).forEach { item ->
                        RoomItemButton(
                            item = item,
                            isDiscovered = discoveredItems.contains(item.id),
                            onClick = {
                                if (item.id == currentClue.targetItemId) {
                                    discoveredItems = discoveredItems + item.id
                                    audioService.playCorrectSound()
                                    messageBanner = "Tebrikler! ${item.nameEn} (${item.nameTr}) bulundu! 🎉"

                                    if (currentClueIdx + 1 >= clues.size) {
                                        isDoorUnlocked = true
                                        audioService.playFanfareSound()
                                        onRewardCoins(60)
                                    } else {
                                        currentClueIdx++
                                    }
                                } else {
                                    audioService.playWrongSound()
                                    messageBanner = "Bu eşya değil, ipucunu tekrar oku! 🔍"
                                }
                            }
                        )
                    }
                }
            }
        }

        // BOTTOM STATUS & ACTION
        if (!isDoorUnlocked) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = messageBanner,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }
        } else {
            Kid3DButton(
                text = "KAPI AÇILDI! ODADAN KAÇIŞ BAŞARILI 🏆 (+60 Altın)",
                onClick = onBack,
                color = LeafGreen,
                shadowColor = Color(0xFF15803D),
                modifier = Modifier.fillMaxWidth(),
                testTag = "escape_success_btn"
            )
        }
    }
}

@Composable
private fun RoomItemButton(
    item: RoomItem,
    isDiscovered: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDiscovered) LeafGreen.copy(alpha = 0.25f) else Color(0xFF334155),
        border = androidx.compose.foundation.BorderStroke(
            if (isDiscovered) 2.dp else 1.dp,
            if (isDiscovered) LeafGreen else Color(0xFF64748B)
        ),
        modifier = Modifier
            .size(68.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.emoji, fontSize = 24.sp)
            Text(
                text = item.nameEn,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp
            )
        }
    }
}
