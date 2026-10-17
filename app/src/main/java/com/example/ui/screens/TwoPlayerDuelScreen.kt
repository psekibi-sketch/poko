package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioService
import com.example.data.curriculum.CurriculumData
import com.example.data.model.WordItem
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

@Composable
fun TwoPlayerDuelScreen(
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allWords = remember { CurriculumData.getAllWords().shuffled() }

    var p1Score by remember { mutableIntStateOf(0) }
    var p2Score by remember { mutableIntStateOf(0) }
    var roundNumber by remember { mutableIntStateOf(1) }
    val maxRounds = 5

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val targetWord = allWords.getOrElse(currentQuestionIndex % allWords.size) { allWords.first() }

    val options = remember(targetWord, currentQuestionIndex) {
        val wrongOptions = allWords.filter { it.id != targetWord.id }.take(3)
        (wrongOptions + targetWord).shuffled()
    }

    val isEnglishQuestionMode = (roundNumber % 2 == 1)

    LaunchedEffect(targetWord, isEnglishQuestionMode) {
        if (isEnglishQuestionMode) {
            audioService.speakEnglish(targetWord.english)
        }
    }

    val isGameOver = roundNumber > maxRounds

    fun handlePlayerAnswer(playerNumber: Int, selected: WordItem) {
        if (isGameOver) return

        if (selected.id == targetWord.id) {
            audioService.playCorrectSound()
            audioService.playCoinSound()
            if (playerNumber == 1) p1Score++ else p2Score++
            roundNumber++
            currentQuestionIndex++
        } else {
            audioService.playWrongSound()
        }

        if (roundNumber > maxRounds) {
            audioService.playFanfareSound()
            onRewardCoins(30)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("two_player_duel_screen")
    ) {
        if (isGameOver) {
            // Game Over Celebration Card
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🏆", fontSize = 56.sp)
                    Text(
                        text = "Düello Tamamlandı!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = when {
                            p1Score > p2Score -> "🎉 1. Oyuncu (Mavi) Kazandı!"
                            p2Score > p1Score -> "🎉 2. Oyuncu (Kırmızı) Kazandı!"
                            else -> "🤝 Berabere Bitti! İkiniz de Harikasınız!"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BrandBlue
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Skor: 1. Oyuncu: $p1Score - 2. Oyuncu: $p2Score",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Kid3DButton(
                        text = "Tekrar Oyna 🔄",
                        onClick = {
                            p1Score = 0
                            p2Score = 0
                            roundNumber = 1
                            currentQuestionIndex++
                        },
                        color = LeafGreen,
                        shadowColor = Color(0xFF15803D),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Kid3DButton(
                        text = "Çıkış ❌",
                        onClick = onBack,
                        color = CandyRed,
                        shadowColor = Color(0xFF991B1B),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // TOP PLAYER SIDE (Player 2 - Red, Rotated 180 degrees for face-to-face play!)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF831843))
                        .rotate(180f)
                        .padding(10.dp)
                ) {
                    PlayerDeck(
                        playerLabel = "2. Oyuncu (Kırmızı)",
                        score = p2Score,
                        options = options,
                        isEnglishQuestionMode = isEnglishQuestionMode,
                        onSelect = { handlePlayerAnswer(2, it) },
                        accentColor = Color(0xFFF43F5E)
                    )
                }

                // CENTER REFEREE STRIP (Question & Audio Trigger)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color(0xFF1E293B))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isEnglishQuestionMode) "Bu İngilizce kelimenin anlamını bul! ⚡" else "Bu Türkçe kelimenin İngilizcesini bul! ⚡",
                                fontWeight = FontWeight.Black,
                                color = PlayfulPurple,
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (isEnglishQuestionMode) targetWord.english else targetWord.turkish,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SunGold.copy(alpha = 0.2f),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "Tur $roundNumber/$maxRounds",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB45309),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // BOTTOM PLAYER SIDE (Player 1 - Blue)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF1E3A8A))
                        .padding(10.dp)
                ) {
                    PlayerDeck(
                        playerLabel = "1. Oyuncu (Mavi)",
                        score = p1Score,
                        options = options,
                        isEnglishQuestionMode = isEnglishQuestionMode,
                        onSelect = { handlePlayerAnswer(1, it) },
                        accentColor = Color(0xFF38BDF8)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerDeck(
    playerLabel: String,
    score: Int,
    options: List<WordItem>,
    isEnglishQuestionMode: Boolean,
    onSelect: (WordItem) -> Unit,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = playerLabel,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 13.sp
            )
            Text(
                text = "Puan: $score ⭐",
                fontWeight = FontWeight.Black,
                color = SunGold,
                fontSize = 13.sp
            )
        }

        // 2x2 Grid Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.take(2).forEach { word ->
                DuelChoiceCard(word = word, isEnglishQuestionMode = isEnglishQuestionMode, onSelect = onSelect, modifier = Modifier.weight(1f), accentColor = accentColor)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.drop(2).take(2).forEach { word ->
                DuelChoiceCard(word = word, isEnglishQuestionMode = isEnglishQuestionMode, onSelect = onSelect, modifier = Modifier.weight(1f), accentColor = accentColor)
            }
        }
    }
}

@Composable
private fun DuelChoiceCard(
    word: WordItem,
    isEnglishQuestionMode: Boolean,
    onSelect: (WordItem) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(2.dp, accentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .height(76.dp)
            .clickable { onSelect(word) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = word.emoji, fontSize = 28.sp)
            Text(
                text = if (isEnglishQuestionMode) word.turkish else word.english,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                fontSize = 13.sp
            )
        }
    }
}
