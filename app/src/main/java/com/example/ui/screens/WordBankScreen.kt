package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.CurriculumData
import com.example.data.local.WordProgressEntity
import com.example.data.model.GradeLevel
import com.example.data.model.WordItem
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.SunGold

import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.platform.LocalContext
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.ui.components.SpeechPracticeDialog
import com.example.ui.theme.PlayfulPurple

@Composable
fun WordBankScreen(
    wordProgressList: List<WordProgressEntity>,
    speechService: SpeechRecognitionService? = null,
    audioService: AudioService? = null,
    onSpeakWord: (String, Boolean) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val effectiveSpeechService = remember(speechService) {
        speechService ?: SpeechRecognitionService(context)
    }
    val effectiveAudioService = remember(audioService) {
        audioService ?: AudioService(context)
    }

    val allWords = remember { CurriculumData.getAllWords() }
    val progressMap = remember(wordProgressList) { wordProgressList.associateBy { it.wordId } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tümü") }
    var selectedWordForSpeech by remember { mutableStateOf<WordItem?>(null) }

    if (selectedWordForSpeech != null) {
        val w = selectedWordForSpeech!!
        SpeechPracticeDialog(
            targetText = w.english,
            translationTr = w.turkish,
            phonetic = w.phonetic,
            emoji = w.emoji,
            speechService = effectiveSpeechService,
            audioService = effectiveAudioService,
            onSpeakTts = { _, isSlow -> onSpeakWord(w.english, isSlow) },
            onSuccess = {
                selectedWordForSpeech = null
            },
            onDismiss = { selectedWordForSpeech = null }
        )
    }

    val categories = listOf("Tümü", "Favoriler ❤️", "1. Sınıf", "2. Sınıf", "Animals", "Colors", "Numbers", "Food", "Body", "Toys")

    val filteredWords = remember(searchQuery, selectedFilter, wordProgressList) {
        allWords.filter { word ->
            val matchesQuery = word.english.contains(searchQuery, ignoreCase = true) ||
                    word.turkish.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Tümü" -> true
                "Favoriler ❤️" -> progressMap[word.id]?.isFavorite == true
                "1. Sınıf" -> word.grade == GradeLevel.GRADE_1
                "2. Sınıf" -> word.grade == GradeLevel.GRADE_2
                else -> word.category.equals(selectedFilter, ignoreCase = true)
            }

            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FC))
    ) {
        // Search & Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kelime Sandığım 📚",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Text(
                        text = "${filteredWords.size} kelime listeleniyor",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE0F2FE)
                ) {
                    Text(
                        text = "${wordProgressList.count { it.isLearned }} / ${allWords.size} Öğrenildi",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Kelime ara (örn: Cat, Elma)...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Ara", tint = Color(0xFF94A3B8))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("word_search_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedFilter == category
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BrandBlue else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clickable { selectedFilter = category }
                            .testTag("category_chip_$category")
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF475569)
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Word Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredWords) { word ->
                val progress = progressMap[word.id]
                val isFav = progress?.isFavorite == true
                val isLearned = progress?.isLearned == true

                WordBankCard(
                    word = word,
                    isFavorite = isFav,
                    isLearned = isLearned,
                    onSpeakNormal = { onSpeakWord(word.english, false) },
                    onSpeakSlow = { onSpeakWord(word.english, true) },
                    onSpeakSpeech = { selectedWordForSpeech = word },
                    onToggleFav = { onToggleFavorite(word.id, !isFav) }
                )
            }
        }
    }
}

@Composable
private fun WordBankCard(
    word: WordItem,
    isFavorite: Boolean,
    isLearned: Boolean,
    onSpeakNormal: () -> Unit,
    onSpeakSlow: () -> Unit,
    onSpeakSpeech: () -> Unit,
    onToggleFav: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("word_card_${word.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Big Emoji
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = word.emoji, fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Word Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        fontSize = 19.sp
                    )
                )

                if (word.phonetic.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PlayfulPurple.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PlayfulPurple.copy(alpha = 0.25f)),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "🗣️ Okunuşu: [ ${word.phonetic} ]",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PlayfulPurple,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = word.turkish,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (word.exampleSentenceEn.isNotBlank()) {
                    Text(
                        text = "💬 ${word.exampleSentenceEn}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Poko Memory Trick / Mnemonic Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "💡 ${word.displayMnemonic}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            fontSize = 11.5.sp,
                            lineHeight = 14.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Audio Action Buttons, Mic & Favorite
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSpeakNormal,
                    modifier = Modifier
                        .size(34.dp)
                        .background(BrandBlue.copy(alpha = 0.12f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Dinle",
                        tint = BrandBlue,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onSpeakSpeech,
                    modifier = Modifier
                        .size(34.dp)
                        .background(PlayfulPurple.copy(alpha = 0.15f), CircleShape)
                        .testTag("word_card_mic_${word.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Sesle Söyle",
                        tint = PlayfulPurple,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onSpeakSlow,
                    modifier = Modifier
                        .size(34.dp)
                        .background(SunGold.copy(alpha = 0.2f), CircleShape)
                ) {
                    Text(text = "🐢", fontSize = 15.sp)
                }

                IconButton(
                    onClick = onToggleFav,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favori",
                        tint = if (isFavorite) CandyRed else Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
