package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.audio.SpeechState
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPink
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 7 Days of the Week Data Model
 */
data class DayOfWeekInfo(
    val dayNumber: Int,
    val english: String,
    val turkish: String,
    val pronunciationTr: String,
    val emoji: String,
    val themeColor: Color,
    val isWeekend: Boolean
)

val DAYS_OF_WEEK_LIST: List<DayOfWeekInfo> = listOf(
    DayOfWeekInfo(1, "Monday", "Pazartesi", "Mandey", "🎒", Color(0xFF2563EB), false),
    DayOfWeekInfo(2, "Tuesday", "Salı", "Tyuz-dey", "🎨", Color(0xFFEA580C), false),
    DayOfWeekInfo(3, "Wednesday", "Çarşamba", "Venz-dey", "🐪", Color(0xFF9333EA), false),
    DayOfWeekInfo(4, "Thursday", "Perşembe", "Törz-dey", "⚡", Color(0xFFD97706), false),
    DayOfWeekInfo(5, "Friday", "Cuma", "Fray-dey", "🎉", Color(0xFFE11D48), false),
    DayOfWeekInfo(6, "Saturday", "Cumartesi", "Set-ır-dey", "🏖️", Color(0xFF0D9488), true),
    DayOfWeekInfo(7, "Sunday", "Pazar", "San-dey", "☀️", Color(0xFFF59E0B), true)
)

enum class QuestionType(val titleTr: String, val badgeColor: Color) {
    TR_TO_EN("🇹🇷 ➡️ 🇬🇧 TÜRKÇE'DEN İNGİLİZCEYE", BrandBlue),
    EN_TO_TR("🇬🇧 ➡️ 🇹🇷 İNGİLİZCE'DEN TÜRKÇEYE", PlayfulPurple),
    SPEAK_PRONOUNCE("🎙️ TELAFFUZ: SEN SÖYLE!", LeafGreen),
    SEQUENCE_AFTER("⏭️ SIRADAKİ GÜNÜ BUL", SunOrange),
    SEQUENCE_BEFORE("⏮️ ÖNCEKİ GÜNÜ BUL", Color(0xFF0284C7)),
    WEEKEND_WEEKDAY("⚖️ HAFTA İÇİ Mİ HAFTA SONU MU?", Color(0xFFD97706))
}

data class DayQuizQuestion(
    val id: Int,
    val type: QuestionType,
    val questionTextTr: String,
    val targetDayEn: String,
    val targetDayTr: String,
    val audioPrompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationTr: String
)

/**
 * Generates a full comprehensive 21-Question Exam explicitly covering all 7 days in multiple directions:
 * - 7 TR -> EN questions (each day)
 * - 7 EN -> TR questions (each day)
 * - 3 Pronunciation / Speaking questions
 * - 4 Ordering & Weekend questions
 */
fun build21QuestionsList(): List<DayQuizQuestion> {
    val questions = mutableListOf<DayQuizQuestion>()
    var idCounter = 1

    // 1. TÜRKÇE SORUP İNGİLİZCE İSTEYENLER (7 Soru - Her gün için birer adet)
    DAYS_OF_WEEK_LIST.forEach { day ->
        val wrongOptions = DAYS_OF_WEEK_LIST.filter { it.english != day.english }.shuffled().take(3).map { "${it.emoji} ${it.english}" }
        val correctOption = "${day.emoji} ${day.english}"
        val allOptions = (wrongOptions + correctOption).shuffled()
        val correctIdx = allOptions.indexOf(correctOption)

        questions.add(
            DayQuizQuestion(
                id = idCounter++,
                type = QuestionType.TR_TO_EN,
                questionTextTr = "\"${day.turkish}\" gününün İngilizcesi hangisidir?",
                targetDayEn = day.english,
                targetDayTr = day.turkish,
                audioPrompt = "${day.turkish} gününün İngilizcesi nedir?",
                options = allOptions,
                correctIndex = correctIdx,
                explanationTr = "Doğru! ${day.turkish} = ${day.english} [${day.pronunciationTr}] ${day.emoji}"
            )
        )
    }

    // 2. İNGİLİZCE SORUP TÜRKÇE İSTEYENLER (7 Soru - Her gün için birer adet)
    DAYS_OF_WEEK_LIST.forEach { day ->
        val wrongOptions = DAYS_OF_WEEK_LIST.filter { it.turkish != day.turkish }.shuffled().take(3).map { "${it.emoji} ${it.turkish}" }
        val correctOption = "${day.emoji} ${day.turkish}"
        val allOptions = (wrongOptions + correctOption).shuffled()
        val correctIdx = allOptions.indexOf(correctOption)

        questions.add(
            DayQuizQuestion(
                id = idCounter++,
                type = QuestionType.EN_TO_TR,
                questionTextTr = "\"${day.english}\" Türkçe hangi gündür?",
                targetDayEn = day.english,
                targetDayTr = day.turkish,
                audioPrompt = day.english,
                options = allOptions,
                correctIndex = correctIdx,
                explanationTr = "Tebrikler! ${day.english} = ${day.turkish} anlamına gelir! ${day.emoji}"
            )
        )
    }

    // 3. SÖYLEMESİNİ / TELAFFUZ ETMESİNİ İSTEYENLER (3 Soru)
    val speakDays = listOf(
        DAYS_OF_WEEK_LIST[1], // Tuesday
        DAYS_OF_WEEK_LIST[2], // Wednesday (silent 'd')
        DAYS_OF_WEEK_LIST[4]  // Friday
    )
    speakDays.forEach { day ->
        val wrongOptions = DAYS_OF_WEEK_LIST.filter { it.english != day.english }.shuffled().take(3).map { "${it.emoji} ${it.english}" }
        val correctOption = "${day.emoji} ${day.english}"
        val allOptions = (wrongOptions + correctOption).shuffled()
        val correctIdx = allOptions.indexOf(correctOption)

        questions.add(
            DayQuizQuestion(
                id = idCounter++,
                type = QuestionType.SPEAK_PRONOUNCE,
                questionTextTr = "Mikrofona bas ve bu günü söyle: \"${day.english}\" [${day.pronunciationTr}]",
                targetDayEn = day.english,
                targetDayTr = day.turkish,
                audioPrompt = day.english,
                options = allOptions,
                correctIndex = correctIdx,
                explanationTr = "Harika telaffuz! \"${day.english}\" (${day.turkish}) [${day.pronunciationTr}] dedin! 🌟"
            )
        )
    }

    // 4. SIRALAMA VE ÖĞRETMEN ZAMAN SORULARI (4 Soru)
    // Soru A: After Tuesday
    run {
        val day = DAYS_OF_WEEK_LIST[2] // Wednesday
        val wrong = listOf("🎒 Monday", "🎉 Friday", "⚡ Thursday")
        val correct = "🐪 Wednesday"
        val all = (wrong + correct).shuffled()
        questions.add(
            DayQuizQuestion(
                id = idCounter++,
                type = QuestionType.SEQUENCE_AFTER,
                questionTextTr = "\"Tuesday\" (Salı) gününden HEMEN SONRA hangi gün gelir?",
                targetDayEn = "Wednesday",
                targetDayTr = "Çarşamba",
                audioPrompt = "What day comes after Tuesday?",
                options = all,
                correctIndex = all.indexOf(correct),
                explanationTr = "Doğru! Tuesday'den sonra Wednesday (Çarşamba) gelir! 🐪"
            )
        )
    }

    // Soru B: After Friday
    run {
        val day = DAYS_OF_WEEK_LIST[5] // Saturday
        val wrong = listOf("☀️ Sunday", "🎨 Tuesday", "🎒 Monday")
        val correct = "🏖️ Saturday"
        val all = (wrong + correct).shuffled()
        questions.add(
            DayQuizQuestion(
                id = idCounter++,
                type = QuestionType.SEQUENCE_AFTER,
                questionTextTr = "Bugün \"Friday\" (Cuma) ise, YARIN (Tomorrow) hangi gündür?",
                targetDayEn = "Saturday",
                targetDayTr = "Cumartesi",
                audioPrompt = "If today is Friday, tomorrow is what day?",
                options = all,
                correctIndex = all.indexOf(correct),
                explanationTr = "Bravo! Friday'den sonra weekend başlar: Saturday (Cumartesi)! 🏖️"
            )
        )
    }

    // Soru C: Before Friday
    run {
        val day = DAYS_OF_WEEK_LIST[3] // Thursday
        val wrong = listOf("🐪 Wednesday", "🎨 Tuesday", "☀️ Sunday")
        val correct = "⚡ Thursday"
        val all = (wrong + correct).shuffled()
        questions.add(
            DayQuizQuestion(
                id = idCounter++,
                type = QuestionType.SEQUENCE_BEFORE,
                questionTextTr = "\"Friday\" (Cuma) gününden HEMEN ÖNCE gelen gün hangisidir?",
                targetDayEn = "Thursday",
                targetDayTr = "Perşembe",
                audioPrompt = "What day comes before Friday?",
                options = all,
                correctIndex = all.indexOf(correct),
                explanationTr = "Mükemmel! Friday'den önceki gün Thursday'dir (Perşembe)! ⚡"
            )
        )
    }

    // Soru D: Weekend question
    run {
        val wrong = listOf("🎒 Monday", "🎨 Tuesday", "⚡ Thursday")
        val correct = "☀️ Sunday"
        val all = (wrong + correct).shuffled()
        questions.add(
            DayQuizQuestion(
                id = idCounter++,
                type = QuestionType.WEEKEND_WEEKDAY,
                questionTextTr = "Aşağıdakilerden hangisi bir HAFTA SONU (Weekend) günüdür?",
                targetDayEn = "Sunday",
                targetDayTr = "Pazar",
                audioPrompt = "Which one is a weekend day?",
                options = all,
                correctIndex = all.indexOf(correct),
                explanationTr = "Doğru! Saturday (Cumartesi) ve Sunday (Pazar) hafta sonudur! ☀️"
            )
        )
    }

    return questions
}

/**
 * DAYS OF THE WEEK INTENSIVE EXAM & QUIZ ENGINE (21 QUESTIONS)
 * Doğrudan 7 Gün Hakkında Kesintisiz Soru Sorma, Okuma, Seçme, Telaffuz ve Yüzdelik Başarı Analizi
 */
@Composable
fun DaysOfWeekGameScreen(
    audioService: AudioService,
    speechService: SpeechRecognitionService? = null,
    onRewardCoins: (Int) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Question Bank (21 Questions)
    var questionsList by remember { mutableStateOf(build21QuestionsList().shuffled()) }
    var currentIndex by remember { mutableIntStateOf(0) }

    // Scoring & Results
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerEvaluated by remember { mutableStateOf(false) }
    var isExamFinished by remember { mutableStateOf(false) }

    // User answer history for final review
    val answerHistory = remember { mutableStateListOf<Boolean>() }

    // Speech Recognition state
    var isListening by remember { mutableStateOf(false) }
    var spokenRecognizedText by remember { mutableStateOf<String?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && !isExamFinished && currentIndex < questionsList.size) {
            val q = questionsList[currentIndex]
            isListening = true
            speechService?.startListening(q.targetDayEn) { eval ->
                isListening = false
                spokenRecognizedText = eval.spokenText
                if (eval.isPassed) {
                    audioService.playCorrectSound()
                    selectedOptionIndex = q.correctIndex
                    isAnswerEvaluated = true
                    correctCount++
                    answerHistory.add(true)
                }
            }
        }
    }

    val currentQuestion = if (currentIndex < questionsList.size) questionsList[currentIndex] else null

    // Read question audio when new question appears
    LaunchedEffect(currentIndex, isExamFinished) {
        if (!isExamFinished && currentQuestion != null) {
            selectedOptionIndex = null
            isAnswerEvaluated = false
            spokenRecognizedText = null
            isListening = false

            delay(200)
            if (currentQuestion.type == QuestionType.EN_TO_TR || currentQuestion.type == QuestionType.SPEAK_PRONOUNCE) {
                audioService.speakEnglish(currentQuestion.audioPrompt)
            } else {
                audioService.speakTurkish(currentQuestion.audioPrompt)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioService.stopSpeech()
            speechService?.cancelListening()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .testTag("days_of_week_exam_screen")
    ) {
        // TOP HEADER BAR
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    IconButton(
                        onClick = {
                            audioService.playPopSound()
                            onBack()
                        },
                        modifier = Modifier.testTag("btn_back_days_exam")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri Dön",
                            tint = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🗓️ Haftanın Günleri Sınavı",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        )
                        Text(
                            text = if (!isExamFinished) "Soru ${currentIndex + 1} / ${questionsList.size} (Toplam 21 Soru)" else "Sınav Sonucu ve Başarı Karnesi",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Score pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "✅ $correctCount", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF166534))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "❌ $wrongCount", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF991B1B))
                        }
                    }
                }

                if (!isExamFinished) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Progress Bar for 21 Questions
                    val progressValue = (currentIndex.toFloat() / questionsList.size.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BrandBlue,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }

        if (isExamFinished) {
            // ==========================================
            // EXAM RESULT & SUCCESS PERCENTAGE SCREEN
            // ==========================================
            DaysExamResultScreen(
                totalQuestions = questionsList.size,
                correctCount = correctCount,
                wrongCount = wrongCount,
                questionsList = questionsList,
                answerHistory = answerHistory,
                audioService = audioService,
                onRewardCoins = onRewardCoins,
                onRestartExam = {
                    audioService.playPopSound()
                    questionsList = build21QuestionsList().shuffled()
                    currentIndex = 0
                    correctCount = 0
                    wrongCount = 0
                    selectedOptionIndex = null
                    isAnswerEvaluated = false
                    isExamFinished = false
                    answerHistory.clear()
                },
                onBack = onBack
            )
        } else if (currentQuestion != null) {
            // ==========================================
            // ACTIVE QUESTION SCREEN
            // ==========================================
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 680.dp)
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                // QUESTION TYPE BADGE & AUDIOCLICK
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = currentQuestion.type.badgeColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, currentQuestion.type.badgeColor)
                        ) {
                            Text(
                                text = currentQuestion.type.titleTr,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = currentQuestion.type.badgeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (currentQuestion.type == QuestionType.EN_TO_TR || currentQuestion.type == QuestionType.SPEAK_PRONOUNCE) {
                                    audioService.speakEnglish(currentQuestion.audioPrompt)
                                } else {
                                    audioService.speakTurkish(currentQuestion.audioPrompt)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Seslendir",
                                tint = BrandBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // QUESTION MAIN CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Target Big Visual & Audio Text
                            Text(
                                text = currentQuestion.questionTextTr,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Highlight badge
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = BrandBlue.copy(alpha = 0.08f),
                                border = BorderStroke(1.5.dp, BrandBlue.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (currentQuestion.type == QuestionType.EN_TO_TR) {
                                            "🇬🇧 \"${currentQuestion.targetDayEn}\""
                                        } else {
                                            "🇹🇷 \"${currentQuestion.targetDayTr}\""
                                        },
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrandBlueDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            audioService.speakEnglish(currentQuestion.targetDayEn)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Dinle",
                                            tint = BrandBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // IF SPEAKING QUESTION: SHOW MIC RECORD BUTTON
                            if (currentQuestion.type == QuestionType.SPEAK_PRONOUNCE) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Mikrofona basıp '${currentQuestion.targetDayEn}' de:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    val micScale by animateFloatAsState(
                                        targetValue = if (isListening) 1.2f else 1.0f,
                                        animationSpec = spring(),
                                        label = "mic_scale"
                                    )

                                    Surface(
                                        shape = CircleShape,
                                        color = if (isListening) CandyRed else LeafGreen,
                                        shadowElevation = 6.dp,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .scale(micScale)
                                            .clickable {
                                                val hasPermission = ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.RECORD_AUDIO
                                                ) == PackageManager.PERMISSION_GRANTED

                                                if (hasPermission) {
                                                    isListening = true
                                                    speechService?.startListening(currentQuestion.targetDayEn) { eval ->
                                                        isListening = false
                                                        spokenRecognizedText = eval.spokenText
                                                        if (eval.isPassed) {
                                                            audioService.playCorrectSound()
                                                            selectedOptionIndex = currentQuestion.correctIndex
                                                            isAnswerEvaluated = true
                                                            correctCount++
                                                            answerHistory.add(true)
                                                        } else {
                                                            audioService.playWrongSound()
                                                        }
                                                    }
                                                } else {
                                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                }
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = "Mikrofon",
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isListening) "Dinleniyor... Konuş!" else "Mikrofona Dokun",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isListening) CandyRed else LeafGreen
                                    )

                                    if (spokenRecognizedText != null) {
                                        Text(
                                            text = "Duyulan: \"$spokenRecognizedText\"",
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4 MULTIPLE CHOICE OPTIONS
                item {
                    Text(
                        text = "Doğru Seçeneği İşaretle:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }

                itemsIndexed(currentQuestion.options) { optIndex, optText ->
                    val isSelected = selectedOptionIndex == optIndex
                    val isCorrect = optIndex == currentQuestion.correctIndex

                    val cardColor = when {
                        !isAnswerEvaluated -> Color.White
                        isCorrect -> Color(0xFFDCFCE7)
                        isSelected -> Color(0xFFFEE2E2)
                        else -> Color.White
                    }

                    val borderColor = when {
                        !isAnswerEvaluated -> Color(0xFFCBD5E1)
                        isCorrect -> LeafGreen
                        isSelected -> CandyRed
                        else -> Color(0xFFE2E8F0)
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = cardColor,
                        border = BorderStroke(2.dp, borderColor),
                        shadowElevation = if (isSelected) 4.dp else 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswerEvaluated) {
                                selectedOptionIndex = optIndex
                                isAnswerEvaluated = true

                                if (optIndex == currentQuestion.correctIndex) {
                                    audioService.playCorrectSound()
                                    correctCount++
                                    answerHistory.add(true)
                                } else {
                                    audioService.playWrongSound()
                                    wrongCount++
                                    answerHistory.add(false)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isAnswerEvaluated && isCorrect) LeafGreen else if (isAnswerEvaluated && isSelected) CandyRed else Color(0xFFF1F5F9),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = when (optIndex) {
                                            0 -> "A"
                                            1 -> "B"
                                            2 -> "C"
                                            else -> "D"
                                        },
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = if (isAnswerEvaluated && (isCorrect || isSelected)) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = optText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )

                            if (isAnswerEvaluated && isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Doğru",
                                    tint = LeafGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else if (isAnswerEvaluated && isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Yanlış",
                                    tint = CandyRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // EXPLANATION AND NEXT QUESTION BUTTON
                if (isAnswerEvaluated) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (selectedOptionIndex == currentQuestion.correctIndex) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, if (selectedOptionIndex == currentQuestion.correctIndex) Color(0xFF86EFAC) else Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (selectedOptionIndex == currentQuestion.correctIndex) "🌟 HARİKA CEVAP!" else "💡 DOĞRU BİLGİ:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedOptionIndex == currentQuestion.correctIndex) Color(0xFF166534) else Color(0xFF92400E)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentQuestion.explanationTr,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Kid3DButton(
                            text = if (currentIndex < questionsList.size - 1) "Sıradaki Soruya Geç (${currentIndex + 2}/21) ▶️" else "Sınavı Bitir ve Başarı Yüzdesini Gör 🏆",
                            onClick = {
                                audioService.playPopSound()
                                if (currentIndex < questionsList.size - 1) {
                                    currentIndex++
                                } else {
                                    isExamFinished = true
                                }
                            },
                            color = if (currentIndex < questionsList.size - 1) BrandBlue else SunGold,
                            shadowColor = if (currentIndex < questionsList.size - 1) Color(0xFF1E40AF) else Color(0xFFB45309),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
}

/**
 * EXAM RESULT SCREEN
 * Tam Yüzdelik Başarı Analizi (%100, %95 vb.), Detaylı Karne ve Tekrar Sınav Ol Butonu
 */
@Composable
private fun DaysExamResultScreen(
    totalQuestions: Int,
    correctCount: Int,
    wrongCount: Int,
    questionsList: List<DayQuizQuestion>,
    answerHistory: List<Boolean>,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onRestartExam: () -> Unit,
    onBack: () -> Unit
) {
    val percentage = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0

    LaunchedEffect(Unit) {
        audioService.playFanfareSound()
        val earnedCoins = (percentage * 1.5).toInt()
        onRewardCoins(earnedCoins)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // GOLDEN CERTIFICATE CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(3.dp, if (percentage >= 80) SunGold else BrandBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (percentage >= 80) "🏆" else if (percentage >= 50) "⭐" else "💪",
                        fontSize = 54.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "HAFTANIN GÜNLERİ SINAV KARNESİ",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF64748B),
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // BIG PERCENTAGE CIRCLE / BADGE
                    Surface(
                        shape = CircleShape,
                        color = when {
                            percentage >= 85 -> Color(0xFFDCFCE7)
                            percentage >= 60 -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        },
                        border = BorderStroke(
                            3.dp,
                            when {
                                percentage >= 85 -> LeafGreen
                                percentage >= 60 -> SunGold
                                else -> CandyRed
                            }
                        ),
                        modifier = Modifier.size(110.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "%$percentage",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = when {
                                    percentage >= 85 -> Color(0xFF166534)
                                    percentage >= 60 -> Color(0xFF92400E)
                                    else -> Color(0xFF991B1B)
                                }
                            )
                            Text(
                                text = "BAŞARI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Evaluation message
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when {
                                    percentage >= 90 -> "🌟 MÜKEMMEL EZBERLEDİN! Öğretmenin sorduğunda tüm günleri tereyağından kıl çeker gibi bileceksin!"
                                    percentage >= 70 -> "👏 ÇOK İYİ! Birkaç soruyu daha tekrar edersen %100 başarıya ulaşacaksın!"
                                    else -> "💪 GÜZEL ÇABA! Hemen bir kez daha çözerek eksik günleri tamamlayabilirsin!"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF1E293B),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row: Toplam Soru, Doğru, Yanlış
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$totalQuestions Soru", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                            Text(text = "Toplam", fontSize = 11.sp, color = Color(0xFF64748B))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$correctCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = LeafGreen)
                            Text(text = "Doğru ✅", fontSize = 11.sp, color = Color(0xFF64748B))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$wrongCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CandyRed)
                            Text(text = "Yanlış ❌", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }

        // POKO MASCOT SPEECH
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PokoMascot(
                    size = 85.dp,
                    mood = if (percentage >= 70) PokoMood.CELEBRATING else PokoMood.THINKING
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (percentage >= 85) "🦊 Poko: 'Harikasın Şampiyon!'" else "🦊 Poko: 'Bir Kez Daha Deneyelim!'",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Haftanın 7 gününü pekiştirmek için dilediğin zaman 21 soruluk sınavı tekrar başlatabilirsin!",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                }
            }
        }

        // 7 GÜN HIZLI BAŞVURU ŞERİDİ (Çocuğun aklında kalması için)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🗓️ 7 Gün Hatırlatma Listesi (Sırasıyla):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        itemsIndexed(DAYS_OF_WEEK_LIST) { idx, day ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = day.themeColor.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, day.themeColor)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "${idx + 1}. ${day.emoji}", fontSize = 12.sp)
                                    Text(text = day.english, fontSize = 11.sp, fontWeight = FontWeight.Black, color = day.themeColor)
                                    Text(text = day.turkish, fontSize = 9.sp, color = Color(0xFF475569))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ACTION BUTTONS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // IPAD & TABLET HINT BADGE
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📲", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "iPad İpucu: Safari'de Paylaş simgesine (⬆️) basıp \"Ana Ekrana Ekle\" diyerek iPad masaüstünüze uygulama logosunu koyabilirsiniz!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E40AF),
                            lineHeight = 15.sp
                        )
                    }
                }

                Kid3DButton(
                    text = "Tekrar Sınav Ol (Yeni 21 Soru) 🔄",
                    onClick = onRestartExam,
                    color = LeafGreen,
                    shadowColor = Color(0xFF15803D),
                    modifier = Modifier.fillMaxWidth()
                )

                Kid3DButton(
                    text = "Odaya Dön 🏠",
                    onClick = onBack,
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
}
