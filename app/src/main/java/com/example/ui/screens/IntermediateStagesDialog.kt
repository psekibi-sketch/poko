package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.audio.SpeechState
import com.example.data.curriculum.ComicPanel
import com.example.data.curriculum.IntermediateStagesData
import com.example.data.curriculum.PassportQuestion
import com.example.data.curriculum.ReviewWordItem
import com.example.data.curriculum.SentenceLabStage
import com.example.data.curriculum.TraceLetterStage
import com.example.data.model.GradeLevel
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

enum class IntermediateStageType(val title: String, val emoji: String) {
    COMIC_STORY("Çizgi Roman", "📖"),
    SENTENCE_LAB("Cümle Lab", "🧪"),
    SMART_REVIEW("Hata Dedektifi", "🔍"),
    PASSPORT_STAMP("Pasaport Damgası", "🛂"),
    ROLEPLAY_SNAPSHOT("Rol Canlandırma", "🎙️"),
    TRACE_WRITE("İz Çiz & Yaz", "🖐️"),
    CULTURE_FACT("Biliyor Musun?", "🇬🇧")
}

@Composable
fun IntermediateStagesDialog(
    grade: GradeLevel,
    unitId: Int,
    initialStage: IntermediateStageType = IntermediateStageType.COMIC_STORY,
    audioService: AudioService,
    speechService: SpeechRecognitionService,
    onRewardCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStageType by remember { mutableStateOf(initialStage) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                .testTag("intermediate_stages_dialog"),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, bottom = 12.dp)
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✨ DERS ARA KADEMELERİ ✨",
                            fontWeight = FontWeight.Black,
                            color = SunGold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "MEB ${grade.title} • Ünite $unitId Köprüsü",
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SunGold.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, SunGold)
                    ) {
                        Text(
                            text = "7 Ara Kademe",
                            fontWeight = FontWeight.Black,
                            color = SunGold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // STAGE TABS
                ScrollableTabRow(
                    selectedTabIndex = selectedStageType.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedStageType.ordinal]),
                            color = SunGold,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    IntermediateStageType.values().forEach { stage ->
                        val isSelected = selectedStageType == stage
                        Tab(
                            selected = isSelected,
                            onClick = { selectedStageType = stage },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = stage.emoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stage.title,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) SunGold else Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // CONTENT BOX
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    when (selectedStageType) {
                        IntermediateStageType.COMIC_STORY -> {
                            val comicData = remember(grade, unitId) {
                                IntermediateStagesData.getComicStoriesForGrade(grade, unitId)
                            }
                            ComicStoryView(
                                comic = comicData,
                                audioService = audioService,
                                onRewardCoins = onRewardCoins
                            )
                        }
                        IntermediateStageType.SENTENCE_LAB -> {
                            val sentenceLabs = remember(grade, unitId) {
                                IntermediateStagesData.getSentenceLabForGrade(grade, unitId)
                            }
                            SentenceLabView(
                                labList = sentenceLabs,
                                audioService = audioService,
                                onRewardCoins = onRewardCoins
                            )
                        }
                        IntermediateStageType.SMART_REVIEW -> {
                            val smartReview = remember(grade, unitId) {
                                IntermediateStagesData.getSmartReviewForGrade(grade, unitId)
                            }
                            SmartReviewView(
                                stage = smartReview,
                                audioService = audioService,
                                onRewardCoins = onRewardCoins
                            )
                        }
                        IntermediateStageType.PASSPORT_STAMP -> {
                            val passport = remember(grade, unitId) {
                                IntermediateStagesData.getPassportStampForGrade(grade, unitId)
                            }
                            PassportStampView(
                                passport = passport,
                                audioService = audioService,
                                onRewardCoins = onRewardCoins
                            )
                        }
                        IntermediateStageType.ROLEPLAY_SNAPSHOT -> {
                            val roleplay = remember(grade, unitId) {
                                IntermediateStagesData.getRoleplaySnapshotForGrade(grade, unitId)
                            }
                            RoleplaySnapshotView(
                                stage = roleplay,
                                audioService = audioService,
                                speechService = speechService,
                                onRewardCoins = onRewardCoins
                            )
                        }
                        IntermediateStageType.TRACE_WRITE -> {
                            val traceStages = remember(grade, unitId) {
                                IntermediateStagesData.getTraceLettersForGrade(grade, unitId)
                            }
                            TraceWriteView(
                                traceList = traceStages,
                                audioService = audioService,
                                onRewardCoins = onRewardCoins
                            )
                        }
                        IntermediateStageType.CULTURE_FACT -> {
                            val cultureFact = remember(grade, unitId) {
                                IntermediateStagesData.getCultureFactForGrade(grade, unitId)
                            }
                            CultureFactView(
                                fact = cultureFact,
                                audioService = audioService,
                                onRewardCoins = onRewardCoins
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 1. COMIC STORY VIEW (SESLİ ÇİZGİ ROMAN KÖPRÜSÜ)
// =========================================================================
@Composable
private fun ComicStoryView(
    comic: com.example.data.curriculum.ComicStoryStage,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit
) {
    var currentPanelIndex by remember { mutableIntStateOf(0) }
    val panel = comic.panels[currentPanelIndex % comic.panels.size]

    LaunchedEffect(currentPanelIndex) {
        audioService.speakEnglish(panel.textEn)
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(2.dp, Color(0xFF6366F1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comic.titleTr,
                    fontWeight = FontWeight.Black,
                    color = SunGold,
                    fontSize = 15.sp
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF334155)
                ) {
                    Text(
                        text = "Kare ${currentPanelIndex + 1} / ${comic.panels.size}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Comic Panel Scene
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(2.dp, Color(0xFF818CF8))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = panel.backgroundEmoji,
                        fontSize = 54.sp,
                        modifier = Modifier.scale(1.2f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = panel.characterEmoji, fontSize = 36.sp)
                            }
                        }

                        // Speech bubble
                        Surface(
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
                            color = Color.White,
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxWidth(0.75f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = panel.speaker,
                                    fontWeight = FontWeight.Black,
                                    color = BrandBlue,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "\"${panel.textEn}\"",
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Turkish Translation and Phonetics
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF334155),
                border = BorderStroke(1.dp, Color(0xFF64748B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🗣️ Okunuşu: [ ${panel.phonetic} ]",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "🇹🇷 \"${panel.textTr}\"",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom Navigation & Listen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Kid3DButton(
                    text = "Dinle 🔊",
                    onClick = { audioService.speakEnglish(panel.textEn) },
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    paddingVertical = 10.dp,
                    fontSize = 13.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (currentPanelIndex > 0) {
                        Kid3DButton(
                            text = "Önceki ◀",
                            onClick = { currentPanelIndex-- },
                            color = Color(0xFF64748B),
                            shadowColor = Color(0xFF334155),
                            paddingVertical = 10.dp,
                            fontSize = 13.sp
                        )
                    }

                    if (currentPanelIndex < comic.panels.size - 1) {
                        Kid3DButton(
                            text = "Sonraki Kare ▶",
                            onClick = {
                                currentPanelIndex++
                                onRewardCoins(5)
                            },
                            color = LeafGreen,
                            shadowColor = Color(0xFF15803D),
                            paddingVertical = 10.dp,
                            fontSize = 13.sp
                        )
                    } else {
                        Kid3DButton(
                            text = "Tamamla! 🏆",
                            onClick = {
                                audioService.playFanfareSound()
                                onRewardCoins(25)
                            },
                            color = SunGold,
                            shadowColor = Color(0xFFB45309),
                            paddingVertical = 10.dp,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 2. SENTENCE LAB (SİHİRLİ CÜMLE BİRLEŞTİRİCİ)
// =========================================================================
@Composable
private fun SentenceLabView(
    labList: List<SentenceLabStage>,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val lab = labList[currentIndex % labList.size]

    var isPartASelected by remember { mutableStateOf(false) }
    var isPartBSelected by remember { mutableStateOf(false) }
    val isMerged = isPartASelected && isPartBSelected

    LaunchedEffect(currentIndex) {
        isPartASelected = false
        isPartBSelected = false
    }

    LaunchedEffect(isMerged) {
        if (isMerged) {
            audioService.playCorrectSound()
            audioService.speakEnglish(lab.targetSentenceEn)
            onRewardCoins(10)
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(2.dp, Color(0xFF10B981))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🧪 ${lab.titleTr}",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF34D399),
                    fontSize = 15.sp
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF334155)
                ) {
                    Text(
                        text = "Kalıp ${currentIndex + 1}/${labList.size}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Kelimeleri seç ve sihirli tüpte birleştir!",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )

            // Fusion Lab Beaker Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(2.5.dp, if (isMerged) Color(0xFF34D399) else Color(0xFF475569))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isMerged) {
                        Text(text = "✨ ${lab.fullEmoji} ✨", fontSize = 46.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lab.targetSentenceEn,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4ADE80),
                            fontSize = 24.sp
                        )
                        Text(
                            text = "(${lab.targetSentenceTr})",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(text = "🧪", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${if (isPartASelected) lab.partA else "___"} + ${if (isPartBSelected) lab.partB else "___"}",
                            fontWeight = FontWeight.Black,
                            color = SunGold,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            // Ingredient Blocks to Tap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isPartASelected) Color(0xFF065F46) else Color(0xFF334155),
                    border = BorderStroke(2.dp, if (isPartASelected) Color(0xFF34D399) else Color(0xFF64748B)),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable {
                            isPartASelected = !isPartASelected
                            audioService.playPopSound()
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "1️⃣", fontSize = 16.sp)
                        Text(
                            text = lab.partA,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isPartBSelected) Color(0xFF065F46) else Color(0xFF334155),
                    border = BorderStroke(2.dp, if (isPartBSelected) Color(0xFF34D399) else Color(0xFF64748B)),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable {
                            isPartBSelected = !isPartBSelected
                            audioService.playPopSound()
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = lab.partBEmoji, fontSize = 20.sp)
                        Text(
                            text = lab.partB,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Next / Action Button
            if (isMerged) {
                Kid3DButton(
                    text = if (currentIndex < labList.size - 1) "Sıradaki Kalıba Geç ➔" else "Laboratuvarı Tamamla! 🏆",
                    onClick = {
                        if (currentIndex < labList.size - 1) {
                            currentIndex++
                        } else {
                            audioService.playFanfareSound()
                            onRewardCoins(25)
                        }
                    },
                    color = LeafGreen,
                    shadowColor = Color(0xFF15803D),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Her iki parçaya da dokunup birleştir!",
                    fontWeight = FontWeight.Bold,
                    color = SunGold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// =========================================================================
// 3. SMART REVIEW / ERROR DETECTIVE VIEW
// =========================================================================
@Composable
private fun SmartReviewView(
    stage: com.example.data.curriculum.SmartReviewStage,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit
) {
    var selectedWordIndex by remember { mutableIntStateOf(0) }
    val activeWord = stage.targetWords[selectedWordIndex % stage.targetWords.size]

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(2.dp, Color(0xFFF59E0B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stage.titleTr,
                    fontWeight = FontWeight.Black,
                    color = SunGold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${selectedWordIndex + 1}/${stage.targetWords.size}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Target Review Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(2.dp, Color(0xFFFBBF24))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = activeWord.emoji, fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeWord.english,
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "🗣️ Okunuşu: [ ${activeWord.phonetic} ]",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🇹🇷 ${activeWord.turkish}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "💡 ${activeWord.hintTr}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Audio Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Kid3DButton(
                    text = "Dinle 🔊",
                    onClick = { audioService.speakEnglish(activeWord.english) },
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.weight(1f)
                )

                Kid3DButton(
                    text = "Yavaş 🐢",
                    onClick = { audioService.speakEnglish(activeWord.english, isSlow = true) },
                    color = SunOrange,
                    shadowColor = Color(0xFFC2410C),
                    modifier = Modifier.weight(1f)
                )
            }

            // Word Switcher Bottom Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stage.targetWords.indices.toList()) { idx ->
                    val isSelected = selectedWordIndex == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SunGold else Color(0xFF334155),
                        border = BorderStroke(1.dp, if (isSelected) Color.White else Color.Transparent),
                        modifier = Modifier
                            .size(46.dp)
                            .clickable {
                                selectedWordIndex = idx
                                audioService.playPopSound()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = stage.targetWords[idx].emoji, fontSize = 22.sp)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. PASSPORT STAMP & VİZE DAMGASI VIEW
// =========================================================================
@Composable
private fun PassportStampView(
    passport: com.example.data.curriculum.PassportStampStage,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit
) {
    var questionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isStampUnlocked by remember { mutableStateOf(false) }

    val currentQ = passport.questions.getOrNull(questionIndex)

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(2.dp, Color(0xFFE11D48))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛂 ${passport.stampTitleTr}",
                    fontWeight = FontWeight.Black,
                    color = SunGold,
                    fontSize = 15.sp
                )
                Text(
                    text = passport.stampEmoji,
                    fontSize = 24.sp
                )
            }

            if (isStampUnlocked) {
                // Celebration of Stamp Acquired
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(3.dp, SunGold)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎖️✨", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "VİZE DAMGASI BASILDI!",
                            fontWeight = FontWeight.Black,
                            color = SunGold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Tebrikler! Ünite ara pasaport sınavını başarıyla geçtin ve +40 Altın kazandın!",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (currentQ != null) {
                // Passport Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.5.dp, Color(0xFF64748B))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Soru ${questionIndex + 1} / ${passport.questions.size}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQ.questionEn,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "(${currentQ.questionTr})",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp
                        )
                    }
                }

                // Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentQ.options.forEachIndexed { optIdx, optText ->
                        val isCorrect = optIdx == currentQ.correctIndex
                        val isSelected = selectedOptionIndex == optIdx

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = when {
                                isSelected && isCorrect -> LeafGreen
                                isSelected && !isCorrect -> CandyRed
                                else -> Color(0xFF334155)
                            },
                            border = BorderStroke(2.dp, if (isSelected) Color.White else Color(0xFF475569)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = selectedOptionIndex == null) {
                                    selectedOptionIndex = optIdx
                                    if (isCorrect) {
                                        audioService.playCorrectSound()
                                        if (questionIndex < passport.questions.size - 1) {
                                            questionIndex++
                                            selectedOptionIndex = null
                                        } else {
                                            isStampUnlocked = true
                                            audioService.playFanfareSound()
                                            onRewardCoins(40)
                                        }
                                    } else {
                                        audioService.playWrongSound()
                                    }
                                }
                        ) {
                            Text(
                                text = optText,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Pasaportunu doldurarak dünya turuna devam et! 🌍",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
    }
}

// =========================================================================
// 5. REAL-LIFE ROLEPLAY SNAPSHOT VIEW
// =========================================================================
@Composable
private fun RoleplaySnapshotView(
    stage: com.example.data.curriculum.RoleplaySnapshotStage,
    audioService: AudioService,
    speechService: SpeechRecognitionService,
    onRewardCoins: (Int) -> Unit
) {
    val context = LocalContext.current
    val speechState by speechService.speechState.collectAsState()
    val recognizedText by speechService.recognizedText.collectAsState()
    var isSuccess by remember { mutableStateOf(false) }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            speechService.startListening(stage.phraseToSayEn)
        }
    }

    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotBlank()) {
            val lower = recognizedText.lowercase()
            val match = stage.acceptedKeywords.any { lower.contains(it.lowercase()) }
            if (match) {
                isSuccess = true
                audioService.playFanfareSound()
                onRewardCoins(30)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(2.dp, Color(0xFFA855F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎙️ ${stage.scenarioTitleTr}",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFC084FC),
                    fontSize = 15.sp
                )
                Text(text = stage.npcEmoji, fontSize = 26.sp)
            }

            // Situation Description
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF334155),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎭 Durum: ${stage.scenarioSituationTr}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Target Phrase Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(2.dp, if (isSuccess) LeafGreen else Color(0xFFA855F7))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Söylemen Gereken Cümle:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${stage.phraseToSayEn}\"",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "🗣️ Okunuşu: [ ${stage.phonetic} ]",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "🇹🇷 \"${stage.phraseToSayTr}\"",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp
                    )
                }
            }

            // Actions (Listen & Speak)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Kid3DButton(
                    text = "Dinle 🔊",
                    onClick = { audioService.speakEnglish(stage.phraseToSayEn) },
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.weight(1f)
                )

                Kid3DButton(
                    text = if (speechState == SpeechState.LISTENING) "Dinliyor... 👂" else "Mikrofonla Söyle 🎤",
                    onClick = {
                        if (hasAudioPermission) {
                            speechService.startListening(stage.phraseToSayEn)
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    color = if (isSuccess) LeafGreen else PlayfulPurple,
                    shadowColor = Color(0xFF7E22CE),
                    modifier = Modifier.weight(1.3f)
                )
            }

            if (isSuccess) {
                Text(
                    text = "🎉 Mükemmel söyledin! Harika bir canlandırma!",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4ADE80),
                    fontSize = 13.sp
                )
            }
        }
    }
}

// =========================================================================
// 6. TRACE & DRAW KINESTHETIC VIEW (PARMAKLA İZ ÇİZME)
// =========================================================================
@Composable
private fun TraceWriteView(
    traceList: List<TraceLetterStage>,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val trace = traceList[currentIndex % traceList.size]

    val pathPoints = remember(currentIndex) { mutableStateListOf<Offset>() }
    var isDoneDrawn by remember { mutableStateOf(false) }

    LaunchedEffect(currentIndex) {
        pathPoints.clear()
        isDoneDrawn = false
        audioService.speakEnglish(trace.targetWordOrLetter)
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(2.dp, Color(0xFF0284C7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🖐️ İz Çiz & Yaz: ${trace.targetWordOrLetter} (${trace.meaningTr})",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8),
                    fontSize = 15.sp
                )
                Text(text = trace.emoji, fontSize = 24.sp)
            }

            Text(
                text = trace.strokeGuideTipTr,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            // Drawing Canvas Area with Background Template Watermark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .border(2.dp, Color(0xFF0284C7), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Watermark Guide Text
                Text(
                    text = trace.targetWordOrLetter,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.15f),
                    fontSize = if (trace.targetWordOrLetter.length <= 2) 110.sp else 54.sp
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(currentIndex) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    pathPoints.add(offset)
                                },
                                onDrag = { change, _ ->
                                    pathPoints.add(change.position)
                                    if (pathPoints.size > 15 && !isDoneDrawn) {
                                        isDoneDrawn = true
                                        audioService.playPopSound()
                                        onRewardCoins(5)
                                    }
                                }
                            )
                        }
                ) {
                    if (pathPoints.size > 1) {
                        for (i in 0 until pathPoints.size - 1) {
                            drawLine(
                                color = Color(0xFF38BDF8),
                                start = pathPoints[i],
                                end = pathPoints[i + 1],
                                strokeWidth = 14f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            // Canvas Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        pathPoints.clear()
                        isDoneDrawn = false
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF334155), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Temizle", tint = Color.White)
                }

                Kid3DButton(
                    text = if (currentIndex < traceList.size - 1) "Sıradaki Harf ➔" else "Tamamla! ⭐",
                    onClick = {
                        if (currentIndex < traceList.size - 1) {
                            currentIndex++
                        } else {
                            audioService.playFanfareSound()
                            onRewardCoins(20)
                        }
                    },
                    color = LeafGreen,
                    shadowColor = Color(0xFF15803D),
                    paddingVertical = 10.dp,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// =========================================================================
// 7. CULTURE & FUN FACT CAPSULE VIEW
// =========================================================================
@Composable
private fun CultureFactView(
    fact: com.example.data.curriculum.CultureFactStage,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(2.dp, Color(0xFFFBBF24))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🇬🇧 BİLİYOR MUSUN?",
                    fontWeight = FontWeight.Black,
                    color = SunGold,
                    fontSize = 16.sp
                )
                Text(text = "🌍✨", fontSize = 24.sp)
            }

            // Big Emoji Banner
            Text(text = fact.emojiIllustration, fontSize = 56.sp)

            Text(
                text = fact.titleTr,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )

            // Fact Body Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = fact.factBodyTr,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // English Phrase of the Fact
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0284C7).copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color(0xFF0284C7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🇬🇧 \"${fact.factEnglishPhrase}\"",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "🗣️ Okunuşu: [ ${fact.phonetic} ]",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp
                    )
                }
            }

            // Action
            Kid3DButton(
                text = "İngilizcesini Dinle & +20 Altın Al 🪙",
                onClick = {
                    audioService.speakEnglish(fact.factEnglishPhrase)
                    audioService.playCoinSound()
                    onRewardCoins(20)
                },
                color = SunGold,
                shadowColor = Color(0xFFB45309),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
