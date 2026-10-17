package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.curriculum.CurriculumData
import com.example.data.local.LessonProgressEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WordProgressEntity
import com.example.data.model.GradeLevel
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.launch

@Composable
fun ParentReportScreen(
    userProfile: UserProfileEntity,
    lessonProgress: List<LessonProgressEntity>,
    wordProgress: List<WordProgressEntity>,
    isSoundOn: Boolean,
    speechRate: Float,
    onSoundToggle: () -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onTestVoice: () -> Unit,
    onResetProgress: () -> Unit,
    onResetGradeProgress: (GradeLevel) -> Unit = {},
    onExportBackup: suspend () -> String,
    onRestoreBackup: suspend (String) -> Result<Unit>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var isParentUnlocked by remember { mutableStateOf(false) }
    var showFullResetDialog by remember { mutableStateOf(false) }
    var gradeToReset by remember { mutableStateOf<GradeLevel?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var restoreInputText by remember { mutableStateOf("") }
    var restoreError by remember { mutableStateOf<String?>(null) }
    var restoreSuccess by remember { mutableStateOf(false) }
    var isCopiedToast by remember { mutableStateOf(false) }
    var successToastMessage by remember { mutableStateOf<String?>(null) }

    var mathAnswer by remember { mutableStateOf("") }
    var mathError by remember { mutableStateOf(false) }
    var showPrintableCardsDialog by remember { mutableStateOf(false) }

    val totalWords = remember { CurriculumData.getAllWords().size }
    val learnedWordsCount = remember(wordProgress) { wordProgress.count { it.isLearned } }
    val completedLessonsCount = remember(lessonProgress) { lessonProgress.count { it.isCompleted } }
    val totalUnits = remember { CurriculumData.getAllUnits() }

    val accuracyPercent = remember(userProfile) {
        if (userProfile.totalExercisesPlayed > 0) {
            ((userProfile.totalCorrectAnswers.toFloat() / userProfile.totalExercisesPlayed.toFloat()) * 100).toInt()
        } else {
            100
        }
    }

    if (!isParentUnlocked) {
        // Parent Gate Security Prompt (Math Question)
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF4F7FC))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFE0F2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Veli & Ebeveyn Alanı 👨‍👩‍👧‍👦",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )

                    Text(
                        text = "Bu bölüm ebeveynler içindir. Dersleri sıfırlamak ve raporları görmek için işlemi yanıtlayın:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "7 + 8 = ?",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = BrandBlue),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = mathAnswer,
                        onValueChange = {
                            mathAnswer = it
                            mathError = false
                        },
                        placeholder = { Text("Sonucu yazın (15)") },
                        modifier = Modifier.fillMaxWidth().testTag("parent_gate_input"),
                        singleLine = true,
                        isError = mathError
                    )

                    if (mathError) {
                        Text(
                            text = "Hatalı yanıt, lütfen tekrar deneyin.",
                            color = CandyRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Kid3DButton(
                        text = "Giriş Yap 🔓",
                        onClick = {
                            if (mathAnswer.trim() == "15" || mathAnswer.trim() == "15.0") {
                                isParentUnlocked = true
                            } else {
                                mathError = true
                            }
                        },
                        color = BrandBlue,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "parent_gate_submit"
                    )
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FC)),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Gelişim Raporu & Veli Paneli 📊",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                )
            )
            Text(
                text = "MEB müfredatına göre kazanımları takip edin, dersleri sıfırlayın veya yedekleyin.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
            )
        }

        // Success Toast Message Banner if present
        if (successToastMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFDCFCE7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, LeafGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✅", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = successToastMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        )
                    }
                }
            }
        }

        // Stats Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Öğrenilen Kelime",
                    value = "$learnedWordsCount / $totalWords",
                    emoji = "📚",
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Tamamlanan Ders",
                    value = "$completedLessonsCount",
                    emoji = "⭐",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Başarı Oranı",
                    value = "%$accuracyPercent",
                    emoji = "🎯",
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Çalışma Serisi",
                    value = "${userProfile.streakDays} Gün",
                    emoji = "🔥",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // PRINTABLE FLASHCARDS CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPrintableCardsDialog = true }
                    .testTag("parent_printable_cards_btn"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF93C5FD))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🖨️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Yazdırılabilir / Boyanabilir Kelime Kartları",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E3A8A)
                            )
                        )
                        Text(
                            text = "Buzdolabına asmak veya boyatmak için tek dokunuşla inceleyin.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF3B82F6)
                            )
                        )
                    }
                    Text(
                        text = "İncele ➔",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2563EB),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // DERSLERİ SIFIRLAMA BÖLÜMÜ (Grade by Grade or All)
        // -------------------------------------------------------------
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🔄", fontSize = 22.sp)
                        Text(
                            text = "Kademelere Göre Ders Sıfırlama",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E293B)
                            )
                        )
                    }

                    Text(
                        text = "İstediğiniz sınıfın ders ilerlemesini sıfırlayarak çocuğunuzun o sınıfı baştan tekrar etmesini sağlayabilirsiniz:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // 1. Sınıf Sıfırla
                    GradeResetRow(
                        grade = GradeLevel.GRADE_1,
                        lessonProgress = lessonProgress,
                        onReset = { gradeToReset = GradeLevel.GRADE_1 }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Sınıf Sıfırla
                    GradeResetRow(
                        grade = GradeLevel.GRADE_2,
                        lessonProgress = lessonProgress,
                        onReset = { gradeToReset = GradeLevel.GRADE_2 }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Sınıf Sıfırla
                    GradeResetRow(
                        grade = GradeLevel.GRADE_3,
                        lessonProgress = lessonProgress,
                        onReset = { gradeToReset = GradeLevel.GRADE_3 }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Sınıf Sıfırla
                    GradeResetRow(
                        grade = GradeLevel.GRADE_4,
                        lessonProgress = lessonProgress,
                        onReset = { gradeToReset = GradeLevel.GRADE_4 }
                    )
                }
            }
        }

        // MEB Curriculum Units Checklist
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MEB Müfredat Kazanım Durumu 📋",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    totalUnits.forEach { unit ->
                        val completedInUnit = unit.lessons.count { l ->
                            lessonProgress.any { it.lessonId == l.id && it.isCompleted }
                        }
                        val isUnitDone = completedInUnit == unit.lessons.size && unit.lessons.isNotEmpty()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = unit.iconEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${unit.mebCode} • ${unit.titleTr}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                    )
                                    Text(
                                        text = "${unit.grade.title} • $completedInUnit/${unit.lessons.size} ders tamamlandı",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                                    )
                                }
                            }

                            if (isUnitDone) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }
        }

        // Voice & Settings Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ses ve Telaffuz Ayarları 🎧",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Ses Efektleri (Alkış, Bip, vb.)", fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = isSoundOn,
                            onCheckedChange = { onSoundToggle() },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Konuşma Hızı: ${"%.2f".format(speechRate)}x",
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = speechRate,
                        onValueChange = onSpeechRateChange,
                        valueRange = 0.5f..1.2f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = BrandBlue,
                            activeTrackColor = BrandBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Kid3DButton(
                        text = "İngilizce Sesi Test Et 🔊",
                        onClick = onTestVoice,
                        color = BrandBlue,
                        shadowColor = Color(0xFF1E40AF),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Backup & Restore
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "İlerleme Yedekleme & Aktarma 💾",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Text(
                        text = "İlerlemeyi yedekleyin veya başka bir telefona aktarın.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Kid3DButton(
                            text = "Yedek Al 📤",
                            onClick = {
                                coroutineScope.launch {
                                    exportedJsonText = onExportBackup()
                                    showExportDialog = true
                                }
                            },
                            color = BrandBlue,
                            modifier = Modifier.weight(1f)
                        )

                        Kid3DButton(
                            text = "Yedeği Yükle 📥",
                            onClick = {
                                restoreInputText = ""
                                restoreError = null
                                restoreSuccess = false
                                showRestoreDialog = true
                            },
                            color = PlayfulPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Danger Zone: Full Reset
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fabrika Ayarlarına Dön ⚠️",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = CandyRed
                        )
                    )
                    Text(
                        text = "Tüm kazanılan yıldızları, altınları, kostümleri ve tüm sınıfların ilerlemesini tamamen siler.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Kid3DButton(
                        text = "Tüm İlerlemeyi Sıfırla (Fabrika Ayarları)",
                        onClick = { showFullResetDialog = true },
                        color = CandyRed,
                        shadowColor = Color(0xFFB91C1C),
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "reset_progress_btn"
                    )
                }
            }
        }
    }

    // Printable Cards Dialog
    if (showPrintableCardsDialog) {
        PrintableCardsDialog(
            audioService = com.example.audio.AudioService(context),
            onDismiss = { showPrintableCardsDialog = false }
        )
    }

    // Single Grade Reset Dialog
    val targetGrade = gradeToReset
    if (targetGrade != null) {
        Dialog(onDismissRequest = { gradeToReset = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔄", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${targetGrade.title} Sıfırlansın mı?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = "${targetGrade.title} kapsamındaki tüm dersler sıfırlanacak ve 1. dersten itibaren tekrar açılabilecektir. Altınlarınız ve diğer sınıflar korunacaktır.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = { gradeToReset = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Vazgeç", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        }

                        Kid3DButton(
                            text = "Evet, Sıfırla",
                            onClick = {
                                onResetGradeProgress(targetGrade)
                                successToastMessage = "${targetGrade.title} dersleri başarıyla sıfırlandı! Baştan başlayabilirsiniz."
                                gradeToReset = null
                            },
                            color = SunOrange,
                            modifier = Modifier.weight(1.3f)
                        )
                    }
                }
            }
        }
    }

    // Full Reset Dialog
    if (showFullResetDialog) {
        Dialog(onDismissRequest = { showFullResetDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tüm İlerleme Sıfırlansın mı?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = "Tüm sınıflar (1, 2, 3, 4. Sınıf), öğrenilen kelimeler ve yıldızlar sıfırlanacaktır. Bu işlem geri alınamaz!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = { showFullResetDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Vazgeç", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        }

                        Kid3DButton(
                            text = "Evet, Tamamen Sıfırla",
                            onClick = {
                                onResetProgress()
                                successToastMessage = "Tüm ilerleme sıfırlandı ve 1. Sınıf 1. Derse dönüldü!"
                                showFullResetDialog = false
                            },
                            color = CandyRed,
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        Dialog(onDismissRequest = { showExportDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📤", fontSize = 24.sp)
                        Text(
                            text = "Yedek Verisi (JSON)",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    Text(
                        text = "Aşağıdaki metni kopyalayarak güvenli bir yere kaydedebilir veya doğrudan yeni telefona aktarabilirsiniz.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = exportedJsonText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155)
                                )
                            )
                        }
                    }

                    if (isCopiedToast) {
                        Text(
                            text = "✅ Yedek panoya başarıyla kopyalandı!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LeafGreen,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Kid3DButton(
                            text = "Kopyala 📋",
                            onClick = {
                                clipboardManager.setText(AnnotatedString(exportedJsonText))
                                isCopiedToast = true
                            },
                            color = BrandBlue,
                            modifier = Modifier.weight(1f),
                            testTag = "copy_backup_btn"
                        )

                        Kid3DButton(
                            text = "Paylaş 📤",
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, exportedJsonText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Poko İngilizce Yedeği")
                                context.startActivity(shareIntent)
                            },
                            color = PlayfulPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Restore Dialog
    if (showRestoreDialog) {
        Dialog(onDismissRequest = { showRestoreDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📥", fontSize = 24.sp)
                        Text(
                            text = "Yedeği Geri Yükle",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    Text(
                        text = "Daha önce kopyaladığınız JSON yedek metnini buraya yapıştırın:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = restoreInputText,
                        onValueChange = {
                            restoreInputText = it
                            restoreError = null
                        },
                        placeholder = { Text("Yedek JSON metnini buraya yapıştırın...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        maxLines = 6
                    )

                    if (restoreError != null) {
                        Text(
                            text = "❌ $restoreError",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CandyRed,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    if (restoreSuccess) {
                        Text(
                            text = "✅ İlerleme başarıyla geri yüklendi!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LeafGreen,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showRestoreDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kapat", color = Color(0xFF64748B))
                        }

                        Kid3DButton(
                            text = "Yükle 🚀",
                            onClick = {
                                coroutineScope.launch {
                                    val result = onRestoreBackup(restoreInputText)
                                    if (result.isSuccess) {
                                        restoreSuccess = true
                                        restoreError = null
                                    } else {
                                        restoreError = "Geçersiz yedek verisi: ${result.exceptionOrNull()?.message}"
                                    }
                                }
                            },
                            color = BrandBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GradeResetRow(
    grade: GradeLevel,
    lessonProgress: List<LessonProgressEntity>,
    onReset: () -> Unit
) {
    val gradeUnits = remember(grade) { CurriculumData.getUnitsForGrade(grade) }
    val totalLessonsInGrade = remember(gradeUnits) { gradeUnits.flatMap { it.lessons }.size }
    val completedInGrade = remember(gradeUnits, lessonProgress) {
        val lessonIds = gradeUnits.flatMap { it.lessons }.map { it.id }.toSet()
        lessonProgress.count { it.lessonId in lessonIds && it.isCompleted }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${grade.title} (${grade.badgeText})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                )
                Text(
                    text = "Tamamlanan: $completedInGrade / $totalLessonsInGrade Ders",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (completedInGrade > 0) LeafGreen else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (completedInGrade > 0) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (completedInGrade > 0) Color(0xFFFCA5A5) else Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(enabled = completedInGrade > 0, onClick = onReset)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sıfırla",
                        tint = if (completedInGrade > 0) CandyRed else Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Sıfırla",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = if (completedInGrade > 0) CandyRed else Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                )
            }
        }
    }
}
