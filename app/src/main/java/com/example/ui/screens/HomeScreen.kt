package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.LessonProgressEntity
import com.example.data.model.GradeLevel
import com.example.data.model.Lesson
import com.example.data.model.UnitTopic
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

@Composable
fun HomeScreen(
    units: List<UnitTopic>,
    lessonProgressMap: Map<String, LessonProgressEntity>,
    activeGrade: GradeLevel,
    equippedCostumeId: String,
    onStartLesson: (Lesson) -> Unit,
    onMascotSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lockedLessonMessage by remember { mutableStateOf<String?>(null) }

    // Strict sequential unlocking calculation for the entire grade:
    val allGradeLessons = remember(units) { units.flatMap { it.lessons } }
    val unlockedLessonIds = remember(allGradeLessons, lessonProgressMap) {
        val unlocked = mutableSetOf<String>()
        var canUnlockNext = true // First lesson is ALWAYS unlocked by default

        for (lesson in allGradeLessons) {
            if (canUnlockNext) {
                unlocked.add(lesson.id)
            }
            val prog = lessonProgressMap[lesson.id]
            // STRICT 100% CORRECT REQUIREMENT: isCompleted == true AND starsEarned == 3
            val is100Percent = prog != null && prog.isCompleted && prog.starsEarned >= 3
            canUnlockNext = is100Percent
        }
        unlocked
    }

    if (lockedLessonMessage != null) {
        LockedLessonDialog(
            message = lockedLessonMessage!!,
            onDismiss = { lockedLessonMessage = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FC)),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Mascot Banner Header
        item {
            MascotHeroCard(
                activeGrade = activeGrade,
                equippedCostumeId = equippedCostumeId,
                onMascotSpeak = onMascotSpeak,
                onMascotClick = onMascotSpeak
            )
        }

        // List of Units and Adventure Path Nodes
        itemsIndexed(units) { index, unit ->
            val isUnitUnlocked = unit.lessons.any { unlockedLessonIds.contains(it.id) } || index == 0
            UnitRoadmapSection(
                unit = unit,
                unitIndex = index + 1,
                isUnitUnlocked = isUnitUnlocked,
                unlockedLessonIds = unlockedLessonIds,
                lessonProgressMap = lessonProgressMap,
                onStartLesson = onStartLesson,
                onLockedClick = { lessonTitle ->
                    lockedLessonMessage = "🔒 \"$lessonTitle\" henüz kilitli!\n\nKilidi açmak için lütfen önceki dersi %100 hatasız (3 Kalp / 3 Yıldız ⭐⭐⭐) tamamla!"
                }
            )
        }
    }
}

@Composable
private fun LockedLessonDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("locked_lesson_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PokoMascot(
                    size = 90.dp,
                    mood = PokoMood.THINKING
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                ) {
                    Text(
                        text = "🔒 Kilitli Aşama",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Kid3DButton(
                    text = "Anladım! 🚀",
                    onClick = onDismiss,
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    testTag = "locked_dialog_ok_btn"
                )
            }
        }
    }
}

@Composable
private fun MascotHeroCard(
    activeGrade: GradeLevel,
    equippedCostumeId: String,
    onMascotSpeak: () -> Unit,
    onMascotClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PokoMascot(
                size = 110.dp,
                mood = PokoMood.HAPPY,
                costumeId = equippedCostumeId,
                onMascotClick = onMascotClick
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandBlue.copy(alpha = 0.12f),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "MEB ${activeGrade.title} Müfredatı",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Adım Adım İlerle! 🚀",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                )

                Text(
                    text = "Her dersi %100 başarıyla bitirerek yeni ünitelerin ve derslerin kilidini aç!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onMascotSpeak() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔊 Poko'yu Dinle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitRoadmapSection(
    unit: UnitTopic,
    unitIndex: Int,
    isUnitUnlocked: Boolean,
    unlockedLessonIds: Set<String>,
    lessonProgressMap: Map<String, LessonProgressEntity>,
    onStartLesson: (Lesson) -> Unit,
    onLockedClick: (lessonTitle: String) -> Unit
) {
    val totalLessons = unit.lessons.size
    val completedCount = unit.lessons.count { lessonProgressMap[it.id]?.isCompleted == true && (lessonProgressMap[it.id]?.starsEarned ?: 0) >= 3 }
    val progress = if (totalLessons > 0 && isUnitUnlocked) completedCount.toFloat() / totalLessons else 0f

    val bannerColor = if (isUnitUnlocked) Color(unit.colorHex) else Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Unit Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = bannerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isUnitUnlocked) 4.dp else 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isUnitUnlocked) unit.iconEmoji else "🔒",
                                    fontSize = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isUnitUnlocked) "ÜNİTE $unitIndex • ${unit.mebCode}" else "🔒 KİLİTLİ ÜNİTE • ${unit.mebCode}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            )
                            Text(
                                text = unit.titleTr,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = if (isUnitUnlocked) "$completedCount / $totalLessons" else "🔒 Kilitli",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }

        // Winding Road Map Nodes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            unit.lessons.forEachIndexed { idx, lesson ->
                val progressEntity = lessonProgressMap[lesson.id]
                val isCompleted = progressEntity?.isCompleted == true && (progressEntity.starsEarned >= 3)
                val stars = progressEntity?.starsEarned ?: 0
                val isUnlocked = unlockedLessonIds.contains(lesson.id)

                // Winding zigzag x offset calculation (balanced for all screen widths)
                val offsetX = when (idx % 3) {
                    0 -> (-32).dp
                    1 -> 0.dp
                    else -> 32.dp
                }

                LessonMapNode(
                    lesson = lesson,
                    isUnlocked = isUnlocked,
                    isCompleted = isCompleted,
                    starsEarned = stars,
                    onClick = {
                        if (isUnlocked) {
                            onStartLesson(lesson)
                        } else {
                            onLockedClick(lesson.titleTr)
                        }
                    },
                    modifier = Modifier.offset(x = offsetX)
                )

                if (idx < unit.lessons.size - 1) {
                    // Stepping stone dots
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .size(6.dp)
                                    .background(
                                        if (isUnlocked) Color(0xFFCBD5E1) else Color(0xFFE2E8F0),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonMapNode(
    lesson: Lesson,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    starsEarned: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "node_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (!isCompleted && isUnlocked) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("lesson_node_${lesson.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(76.dp)
                .shadow(
                    elevation = if (isUnlocked) 8.dp else 2.dp,
                    shape = CircleShape
                )
                .background(
                    brush = when {
                        isCompleted -> Brush.verticalGradient(listOf(LeafGreen, Color(0xFF16A34A)))
                        isUnlocked -> Brush.verticalGradient(listOf(BrandBlue, Color(0xFF2563EB)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
                    },
                    shape = CircleShape
                )
                .border(
                    width = 4.dp,
                    color = if (isCompleted) SunGold else if (isUnlocked) Color.White else Color(0xFFCBD5E1),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Kilitli",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = lesson.iconEmoji, fontSize = 28.sp)
                }
            }
        }

        // Lesson Title & Stars
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isUnlocked) Color.White else Color(0xFFF1F5F9),
            shadowElevation = if (isUnlocked) 2.dp else 0.dp,
            modifier = Modifier
                .padding(top = 6.dp)
                .border(
                    1.dp,
                    if (isUnlocked) Color(0xFFE2E8F0) else Color(0xFFCBD5E1),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 2.dp)
                        )
                    }
                    Text(
                        text = lesson.titleTr,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF94A3B8)
                        )
                    )
                }
                if (isCompleted && starsEarned > 0) {
                    StarRatingRow(starsEarned = starsEarned, size = 14.dp)
                }
            }
        }
    }
}
