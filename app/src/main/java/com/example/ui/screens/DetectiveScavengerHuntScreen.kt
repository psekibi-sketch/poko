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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.audio.SpeechRecognitionService
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

data class ScavengerMission(
    val id: String,
    val titleEn: String,
    val titleTr: String,
    val targetEnglishWord: String,
    val phonetic: String,
    val targetTurkish: String,
    val itemEmoji: String,
    val clueVoicePrompt: String,
    val acceptedKeywords: List<String>
)

@Composable
fun DetectiveScavengerHuntScreen(
    audioService: AudioService,
    speechService: SpeechRecognitionService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val speechState by speechService.speechState.collectAsState()
    val recognizedText by speechService.recognizedText.collectAsState()

    val missions = remember {
        listOf(
            ScavengerMission(
                id = "hunt_red",
                titleEn = "Find something RED!",
                titleTr = "Odanda KIRMIZI bir eşya bul!",
                targetEnglishWord = "RED",
                phonetic = "red",
                targetTurkish = "Kırmızı",
                itemEmoji = "🔴",
                clueVoicePrompt = "Detective mission! Go find something red in your room!",
                acceptedKeywords = listOf("red", "found", "apple", "car", "kirmizi", "yes")
            ),
            ScavengerMission(
                id = "hunt_pencil",
                titleEn = "Find a PENCIL!",
                titleTr = "Bir KURŞUN KALEM bul ve getir!",
                targetEnglishWord = "PENCIL",
                phonetic = "pensıl",
                targetTurkish = "Kalem",
                itemEmoji = "✏️",
                clueVoicePrompt = "Quick! Find a pencil on your desk and bring it!",
                acceptedKeywords = listOf("pencil", "pen", "write", "kalem", "here")
            ),
            ScavengerMission(
                id = "hunt_book",
                titleEn = "Find a BOOK!",
                titleTr = "Bir KİTAP bul ve göster!",
                targetEnglishWord = "BOOK",
                phonetic = "buk",
                targetTurkish = "Kitap",
                itemEmoji = "📖",
                clueVoicePrompt = "Detective! Search for an English or story book!",
                acceptedKeywords = listOf("book", "read", "kitap", "page")
            ),
            ScavengerMission(
                id = "hunt_toy",
                titleEn = "Find a TOY or CAR!",
                titleTr = "En sevdiğin OYUNCAĞINI kap!",
                targetEnglishWord = "TOY",
                phonetic = "toy",
                targetTurkish = "Oyuncak",
                itemEmoji = "🚗",
                clueVoicePrompt = "Look around! Find your favorite toy or car!",
                acceptedKeywords = listOf("toy", "car", "doll", "robot", "oyuncak")
            ),
            ScavengerMission(
                id = "hunt_water",
                titleEn = "Find a CUP of WATER!",
                titleTr = "Bir bardak SU bul ve göster!",
                targetEnglishWord = "WATER",
                phonetic = "votır",
                targetTurkish = "Su",
                itemEmoji = "💧",
                clueVoicePrompt = "Stay healthy! Go find a glass of water!",
                acceptedKeywords = listOf("water", "cup", "drink", "su", "glass")
            ),
            ScavengerMission(
                id = "hunt_shoes",
                titleEn = "Find your SHOES!",
                titleTr = "AYAKKABILARINI bul!",
                targetEnglishWord = "SHOES",
                phonetic = "şuuz",
                targetTurkish = "Ayakkabı",
                itemEmoji = "👟",
                clueVoicePrompt = "Mission time! Where are your shoes?",
                acceptedKeywords = listOf("shoes", "shoe", "feet", "ayakkabi")
            )
        )
    }

    var currentMissionIndex by remember { mutableIntStateOf(0) }
    val currentMission = missions[currentMissionIndex % missions.size]

    var isMissionCompleted by remember { mutableStateOf(false) }
    var detectiveBadgesEarned by remember { mutableIntStateOf(0) }
    var isMicrophoneListening by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "detective_lens")
    val lensPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(currentMissionIndex) {
        isMissionCompleted = false
        isMicrophoneListening = false
        audioService.speakEnglish(currentMission.clueVoicePrompt)
    }

    // Check speech recognition
    LaunchedEffect(recognizedText) {
        val result = recognizedText.lowercase()
        if (result.isNotBlank() && isMicrophoneListening) {
            val matched = currentMission.acceptedKeywords.any { kw -> result.contains(kw) }
            if (matched || result.contains("buldum") || result.contains("yes") || result.contains("here")) {
                isMissionCompleted = true
                isMicrophoneListening = false
                detectiveBadgesEarned++
                audioService.playFanfareSound()
                onRewardCoins(25)
            }
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
                        Color(0xFF0F172A)
                    )
                )
            )
            .testTag("detective_scavenger_screen")
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
                    onClick = {
                        speechService.cancelListening()
                        onBack()
                    },
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

                // Title
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF4338CA),
                    border = BorderStroke(1.5.dp, Color(0xFF818CF8))
                ) {
                    Text(
                        text = "🕵️‍♂️ POKO DEDEKTİF AVCI",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Detective Medals
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SunGold,
                    border = BorderStroke(1.5.dp, Color(0xFFB45309))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🎖️", fontSize = 14.sp)
                        Text(
                            text = "$detectiveBadgesEarned",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detective Lens Visual Banner
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(lensPulse)
                    .background(Color(0xFF312E81), CircleShape)
                    .border(4.dp, Color(0xFF818CF8), CircleShape)
                    .shadow(12.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentMission.itemEmoji,
                    fontSize = 46.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mission Instruction Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                border = BorderStroke(2.5.dp, Color(0xFFA5B4FC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔍 DEDEKTİF GÖREVİ:",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentMission.titleEn,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Text(
                            text = "🗣️ Okunuşu: /${currentMission.phonetic}/",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF67E8F9),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentMission.titleTr,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDE047),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IconButton(
                        onClick = { audioService.speakEnglish(currentMission.clueVoicePrompt) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Tekrar Dinle",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Success or Action
            if (isMissionCompleted) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = LeafGreen,
                    border = BorderStroke(2.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉 HARİKA DEDEKTİF!", fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
                        Text(text = "Nesneyi buldun ve İngilizcesini söyledin! +25 Altın 🪙", fontWeight = FontWeight.Bold, color = Color(0xFFFEF08A), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Kid3DButton(
                            text = "Sıradaki Dedektif Görevi 🚀",
                            onClick = {
                                if (currentMissionIndex < missions.size - 1) {
                                    currentMissionIndex++
                                } else {
                                    currentMissionIndex = 0
                                }
                            },
                            color = SunGold,
                            shadowColor = Color(0xFFB45309),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        )
                    }
                }
            } else {
                Text(
                    text = "Odanda nesneyi buldun mu? 'Buldum!' butonuna bas veya adını söyle:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC7D2FE),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Manual Found It Button
                    Kid3DButton(
                        text = "✅ Buldum! (${currentMission.targetEnglishWord})",
                        onClick = {
                            isMissionCompleted = true
                            detectiveBadgesEarned++
                            audioService.playFanfareSound()
                            audioService.speakEnglish("Awesome job! You found the ${currentMission.targetEnglishWord}!")
                            onRewardCoins(25)
                        },
                        color = LeafGreen,
                        shadowColor = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )

                    // Speak into microphone
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isMicrophoneListening) CandyRed else Color(0xFF6366F1),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier
                            .clickable {
                                if (!isMicrophoneListening) {
                                    isMicrophoneListening = true
                                    speechService.startListening(currentMission.targetEnglishWord)
                                } else {
                                    isMicrophoneListening = false
                                    speechService.stopListening()
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Sesli Söyle",
                                tint = Color.White
                            )
                            Text(
                                text = if (isMicrophoneListening) "Dinliyor..." else "Söyle 🎙️",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
