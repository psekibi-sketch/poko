package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class KaraokeLyricLine(
    val english: String,
    val turkish: String,
    val highlightKeyword: String = "",
    val durationMs: Long = 3200
)

data class KaraokeSong(
    val id: String,
    val titleEn: String,
    val titleTr: String,
    val emoji: String,
    val themeColor: Color,
    val lines: List<KaraokeLyricLine>
)

@Composable
fun PokoKaraokeScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val songs = remember {
        listOf(
            KaraokeSong(
                id = "head_shoulders",
                titleEn = "Head, Shoulders, Knees and Toes",
                titleTr = "Baş, Omuzlar, Dizler ve Parmaklar",
                emoji = "🤸",
                themeColor = Color(0xFF6366F1),
                lines = listOf(
                    KaraokeLyricLine("Head, shoulders, knees and toes! 🎵", "Baş, omuzlar, dizler ve ayak parmakları!", "Head"),
                    KaraokeLyricLine("Knees and toes! Knees and toes! 🎶", "Dizler ve parmaklar! Dizler ve parmaklar!", "Knees"),
                    KaraokeLyricLine("And eyes, and ears, and mouth, and nose! 🌟", "Ve gözler, ve kulaklar, ve ağız, ve burun!", "Eyes"),
                    KaraokeLyricLine("Head, shoulders, knees and toes! 🥳", "Baş, omuzlar, dizler ve ayak parmakları!", "Toes")
                )
            ),
            KaraokeSong(
                id = "abc_song",
                titleEn = "The Alphabet Song (ABC)",
                titleTr = "Alfabe Şarkısı",
                emoji = "🔤",
                themeColor = Color(0xFFEC4899),
                lines = listOf(
                    KaraokeLyricLine("A B C D E F G! 🎶", "A B C D E F G!", "A B C"),
                    KaraokeLyricLine("H I J K L M N O P! 🎵", "H I J K L M N O P!", "L M N"),
                    KaraokeLyricLine("Q R S and T U V! ✨", "Q R S ve T U V!", "T U V"),
                    KaraokeLyricLine("W X Y and Z! 🌟", "W X Y ve Z!", "Z"),
                    KaraokeLyricLine("Now I know my ABCs! Next time won't you sing with me? 🎉", "Artık alfabemi biliyorum! Bir dahaki sefere sen de söyle!", "ABC")
                )
            ),
            KaraokeSong(
                id = "if_you_are_happy",
                titleEn = "If You're Happy and You Know It",
                titleTr = "Mutluysan Alkışla!",
                emoji = "👏",
                themeColor = Color(0xFF10B981),
                lines = listOf(
                    KaraokeLyricLine("If you're happy and you know it, clap your hands! 👏", "Mutluysan ve biliyorsan, ellerini çırp!", "Clap"),
                    KaraokeLyricLine("If you're happy and you know it, clap your hands! 👏", "Mutluysan ve biliyorsan, ellerini çırp!", "Happy"),
                    KaraokeLyricLine("If you're happy and you know it, and you really want to show it! 😄", "Mutluysan ve bunu gerçekten göstermek istiyorsan!", "Show"),
                    KaraokeLyricLine("If you're happy and you know it, clap your hands! 🥳", "Mutluysan ve biliyorsan, ellerini çırp!", "Clap")
                )
            ),
            KaraokeSong(
                id = "old_macdonald",
                titleEn = "Old MacDonald Had a Farm",
                titleTr = "Ali Babanın Çiftliği",
                emoji = "🚜",
                themeColor = Color(0xFFF59E0B),
                lines = listOf(
                    KaraokeLyricLine("Old MacDonald had a farm, E-I-E-I-O! 🌾", "Yaşlı MacDonald'ın bir çiftliği vardı, E-I-E-I-O!", "Farm"),
                    KaraokeLyricLine("And on his farm he had a cow, E-I-E-I-O! 🐮", "Ve çiftliğinde bir ineği vardı, E-I-E-I-O!", "Cow"),
                    KaraokeLyricLine("With a moo-moo here, and a moo-moo there! 🐮", "Burada bir möö-möö, orada bir möö-möö!", "Moo"),
                    KaraokeLyricLine("Old MacDonald had a farm, E-I-E-I-O! 🎉", "Yaşlı MacDonald'ın bir çiftliği vardı, E-I-E-I-O!", "Farm")
                )
            ),
            KaraokeSong(
                id = "twinkle_star",
                titleEn = "Twinkle, Twinkle, Little Star",
                titleTr = "Parla Parla Küçük Yıldız",
                emoji = "⭐",
                themeColor = Color(0xFF8B5CF6),
                lines = listOf(
                    KaraokeLyricLine("Twinkle, twinkle, little star! ⭐", "Parla, parla, küçük yıldız!", "Star"),
                    KaraokeLyricLine("How I wonder what you are! ✨", "Nasıl da merak ederim kim olduğunu!", "Wonder"),
                    KaraokeLyricLine("Up above the world so high! 🌌", "Dünyanın yukarısında çok yüksekte!", "High"),
                    KaraokeLyricLine("Like a diamond in the sky! 💎", "Gökyüzündeki bir elmas gibi!", "Diamond"),
                    KaraokeLyricLine("Twinkle, twinkle, little star! 🌟", "Parla, parla, küçük yıldız!", "Twinkle")
                )
            )
        )
    }

    var selectedSongIndex by remember { mutableIntStateOf(0) }
    val currentSong = songs[selectedSongIndex % songs.size]

    var currentLineIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var mascotMood by remember { mutableStateOf<PokoMood>(PokoMood.DANCING) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "karaoke_lights")
    val discoLightScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "disco"
    )

    fun startPlayingSong() {
        isPlaying = true
        currentLineIndex = 0
        mascotMood = PokoMood.DANCING
        audioService.stopSpeech()

        coroutineScope.launch {
            for (i in currentSong.lines.indices) {
                if (!isPlaying) break
                currentLineIndex = i
                val line = currentSong.lines[i]
                audioService.speakEnglish(line.english)
                delay(line.durationMs)
            }
            if (isPlaying) {
                audioService.playFanfareSound()
                audioService.playCoinSound()
                onRewardCoins(25)
                mascotMood = PokoMood.CELEBRATING
                isPlaying = false
            }
        }
    }

    fun stopPlaying() {
        isPlaying = false
        audioService.stopSpeech()
        mascotMood = PokoMood.HAPPY
    }

    DisposableEffect(Unit) {
        onDispose {
            audioService.stopSpeech()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81),
                        Color(0xFF4C1D95),
                        Color(0xFF701A75)
                    )
                )
            )
            .testTag("poko_karaoke_screen")
    ) {
        // Stage Lights Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Spotlight onto Disco Stage
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(currentSong.themeColor.copy(alpha = 0.35f * discoLightScale), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.6f
                ),
                center = Offset(w * 0.5f, h * 0.35f),
                radius = w * 0.6f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        stopPlaying()
                        onBack()
                    },
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
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, currentSong.themeColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🎤", fontSize = 16.sp)
                        Text(
                            text = "Poko Şarkı Sahnesi",
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
                        text = "+25 🪙",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Song Selection Horizontal List
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(songs) { idx, song ->
                    val isSelected = idx == selectedSongIndex
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) song.themeColor else Color.White.copy(alpha = 0.15f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null,
                        modifier = Modifier.clickable {
                            stopPlaying()
                            selectedSongIndex = idx
                            currentLineIndex = 0
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = song.emoji, fontSize = 16.sp)
                            Text(
                                text = song.titleEn,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Song Singing Card (Karaoke Bouncing Lyrics)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, currentSong.themeColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val activeLine = currentSong.lines.getOrElse(currentLineIndex) { currentSong.lines.first() }

                    Text(
                        text = currentSong.titleEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = currentSong.themeColor,
                            fontSize = 17.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = currentSong.titleTr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bouncing Glowing Lyric Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = currentSong.themeColor.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, currentSong.themeColor.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = activeLine.english,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeLine.turkish,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4338CA),
                                    fontSize = 13.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Dancing Poko Mascot in the center
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PokoMascot(
                    size = 180.dp,
                    mood = mascotMood
                )
            }

            // Bottom Karaoke Play / Stop Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Kid3DButton(
                    text = if (isPlaying) "Şarkıyı Durdur ⏹️" else "Şarkıyı Başlat & Dans Et! ▶️",
                    onClick = {
                        if (isPlaying) {
                            stopPlaying()
                        } else {
                            startPlayingSong()
                        }
                    },
                    color = if (isPlaying) CandyRed else LeafGreen,
                    shadowColor = if (isPlaying) Color(0xFF991B1B) else Color(0xFF15803D),
                    modifier = Modifier.weight(1f),
                    testTag = "karaoke_play_btn"
                )
            }
        }
    }
}
