package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.data.local.LessonProgressEntity
import com.example.data.model.GradeLevel
import com.example.data.model.Lesson
import com.example.data.model.UnitTopic
import com.example.data.model.WordItem
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * HONEYCOMB (PETEK) DERS VE ÜNİTE HARİTASI
 *
 * Canlı, parlak renkli petek örgü yapısında, akıcı ve animasyonlu yol haritası:
 * - 6 köşeli gerçek petek (hexagon) geometrisi ve 3D derinlik kabartması
 * - Zigzag petek örgü akışı ve birbirine bağlanan parlak bal kanalları
 * - Tamamlanan kademelerde altın sarısı / zümrüt yeşili ışıltılı petekler ve yıldızlar
 * - Aktif açık kademede parlayan, nefes alan mavi petek ve animasyonlu zıplayan simge
 * - Kilitli kademelerde buzlu cam dokulu petek ve şık asma kilit
 * - 5 Özel İnteraktif Petek: 🎁 Hazine Sandığı, 👾 Boss Arenası, ⚡ 60sn Hız Koşusu, 🎵 Şarkı & Ritim, ⚔️ Arkadaş Düellosu
 * - Başta "🏠 Odama Dön" navigasyon butonu
 */
@Composable
fun HoneycombMapScreen(
    units: List<UnitTopic>,
    lessonProgressMap: Map<String, LessonProgressEntity>,
    activeGrade: GradeLevel,
    audioService: AudioService? = null,
    speechService: SpeechRecognitionService? = null,
    onRewardCoins: (Int) -> Unit = {},
    onStartLesson: (Lesson) -> Unit,
    onBackToRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val effectiveAudioService = remember(audioService) { audioService ?: AudioService(context) }
    val effectiveSpeechService = remember(speechService) { speechService ?: SpeechRecognitionService(context) }

    var lockedLessonMessage by remember { mutableStateOf<String?>(null) }
    var selectedUnitForMnemonic by remember { mutableStateOf<UnitTopic?>(null) }
    var selectedIntermediateStage by remember { mutableStateOf<Pair<UnitTopic, IntermediateStageType>?>(null) }

    // 5 SPECIAL GAMIFIED HONEYCOMB DIALOG STATES
    var selectedUnitForTreasureChest by remember { mutableStateOf<UnitTopic?>(null) }
    var selectedUnitForBossBattle by remember { mutableStateOf<UnitTopic?>(null) }
    var selectedUnitForSpeedDash by remember { mutableStateOf<UnitTopic?>(null) }
    var selectedUnitForSingAlong by remember { mutableStateOf<UnitTopic?>(null) }
    var selectedUnitForDuel by remember { mutableStateOf<UnitTopic?>(null) }

    // Sequential unlocking logic for the grade:
    val allGradeLessons = remember(units) { units.flatMap { it.lessons } }
    val unlockedLessonIds = remember(allGradeLessons, lessonProgressMap) {
        val unlocked = mutableSetOf<String>()
        var canUnlockNext = true // First lesson is always unlocked

        for (lesson in allGradeLessons) {
            if (canUnlockNext) {
                unlocked.add(lesson.id)
            }
            val prog = lessonProgressMap[lesson.id]
            // Any completion unlocks the next stage so child can smoothly progress
            val isDone = prog != null && prog.isCompleted
            canUnlockNext = isDone
        }
        unlocked
    }

    if (lockedLessonMessage != null) {
        LockedHoneycombDialog(
            message = lockedLessonMessage!!,
            onDismiss = { lockedLessonMessage = null }
        )
    }

    if (selectedUnitForMnemonic != null) {
        UnitMnemonicDialog(
            unit = selectedUnitForMnemonic!!,
            onDismiss = { selectedUnitForMnemonic = null }
        )
    }

    if (selectedIntermediateStage != null) {
        val (unit, stageType) = selectedIntermediateStage!!
        IntermediateStagesDialog(
            grade = activeGrade,
            unitId = unit.number,
            initialStage = stageType,
            audioService = effectiveAudioService,
            speechService = effectiveSpeechService,
            onRewardCoins = onRewardCoins,
            onDismiss = { selectedIntermediateStage = null }
        )
    }

    // 1. 🎁 MYSTERY TREASURE CHEST DIALOG
    if (selectedUnitForTreasureChest != null) {
        HoneycombTreasureChestDialog(
            unit = selectedUnitForTreasureChest!!,
            audioService = effectiveAudioService,
            onRewardCoins = onRewardCoins,
            onDismiss = { selectedUnitForTreasureChest = null }
        )
    }

    // 2. 👾 UNIT BOSS BATTLE DIALOG
    if (selectedUnitForBossBattle != null) {
        HoneycombBossBattleDialog(
            unit = selectedUnitForBossBattle!!,
            audioService = effectiveAudioService,
            onRewardCoins = onRewardCoins,
            onDismiss = { selectedUnitForBossBattle = null }
        )
    }

    // 3. ⚡ 60s SPEED DASH DIALOG
    if (selectedUnitForSpeedDash != null) {
        HoneycombSpeedDashDialog(
            unit = selectedUnitForSpeedDash!!,
            audioService = effectiveAudioService,
            onRewardCoins = onRewardCoins,
            onDismiss = { selectedUnitForSpeedDash = null }
        )
    }

    // 4. 🎵 SING-ALONG KARAOKE DIALOG
    if (selectedUnitForSingAlong != null) {
        HoneycombSingAlongDialog(
            unit = selectedUnitForSingAlong!!,
            audioService = effectiveAudioService,
            speechService = effectiveSpeechService,
            onRewardCoins = onRewardCoins,
            onDismiss = { selectedUnitForSingAlong = null }
        )
    }

    // 5. ⚔️ TWO-PLAYER FRIEND DUEL DIALOG
    if (selectedUnitForDuel != null) {
        HoneycombFriendDuelDialog(
            unit = selectedUnitForDuel!!,
            audioService = effectiveAudioService,
            onRewardCoins = onRewardCoins,
            onDismiss = { selectedUnitForDuel = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F2FE), // Sky blue top
                        Color(0xFFFEF3C7), // Warm honeycomb amber middle
                        Color(0xFFF0FDF4)  // Fresh mint meadow bottom
                    )
                )
            )
            .testTag("honeycomb_map_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // TOP HEADER BAR WITH "ODAMA DÖN" ACTION
        item {
            HoneycombMapTopHeader(
                activeGrade = activeGrade,
                totalUnits = units.size,
                onBackToRoom = onBackToRoom
            )
        }

        // UNIT HONEYCOMB SECTIONS
        itemsIndexed(units) { index, unit ->
            val isUnitUnlocked = unit.lessons.any { unlockedLessonIds.contains(it.id) } || index == 0
            HoneycombUnitSection(
                unit = unit,
                unitIndex = index + 1,
                isUnitUnlocked = isUnitUnlocked,
                unlockedLessonIds = unlockedLessonIds,
                lessonProgressMap = lessonProgressMap,
                onStartLesson = onStartLesson,
                onOpenMnemonic = { selectedUnitForMnemonic = it },
                onOpenIntermediateStage = { targetUnit, stageType ->
                    selectedIntermediateStage = Pair(targetUnit, stageType)
                },
                onOpenTreasureChest = { selectedUnitForTreasureChest = it },
                onOpenBossBattle = { selectedUnitForBossBattle = it },
                onOpenSpeedDash = { selectedUnitForSpeedDash = it },
                onOpenSingAlong = { selectedUnitForSingAlong = it },
                onOpenDuel = { selectedUnitForDuel = it },
                onLockedClick = { lessonTitle ->
                    lockedLessonMessage = "🔒 \"$lessonTitle\" peteği henüz kilitli!\n\nKilidi açmak için lütfen önceki petekteki dersi tamamla! 🍯✨"
                }
            )
        }
    }
}

/**
 * Top Header Bar for the Honeycomb Map
 */
@Composable
private fun HoneycombMapTopHeader(
    activeGrade: GradeLevel,
    totalUnits: Int,
    onBackToRoom: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFDE68A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Return to Poko's Room button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BrandBlue,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onBackToRoom() }
                    .testTag("map_back_to_room_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏠", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Odama Dön",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🍯", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Petek Yol Haritası",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E)
                        )
                    )
                }
                Text(
                    text = "MEB ${activeGrade.title} • $totalUnits Ünite",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                )
            }
        }
    }
}

/**
 * HONEYCOMB UNIT SECTION
 * Includes unit header banner, progress bar, 7 pedagogical bridges, and interlocking hexagon nodes + 5 special arenas!
 */
@Composable
private fun HoneycombUnitSection(
    unit: UnitTopic,
    unitIndex: Int,
    isUnitUnlocked: Boolean,
    unlockedLessonIds: Set<String>,
    lessonProgressMap: Map<String, LessonProgressEntity>,
    onStartLesson: (Lesson) -> Unit,
    onOpenMnemonic: (UnitTopic) -> Unit,
    onOpenIntermediateStage: (UnitTopic, IntermediateStageType) -> Unit,
    onOpenTreasureChest: (UnitTopic) -> Unit,
    onOpenBossBattle: (UnitTopic) -> Unit,
    onOpenSpeedDash: (UnitTopic) -> Unit,
    onOpenSingAlong: (UnitTopic) -> Unit,
    onOpenDuel: (UnitTopic) -> Unit,
    onLockedClick: (lessonTitle: String) -> Unit
) {
    val totalLessons = unit.lessons.size
    val completedCount = unit.lessons.count { lessonProgressMap[it.id]?.isCompleted == true }
    val progress = if (totalLessons > 0 && isUnitUnlocked) completedCount.toFloat() / totalLessons else 0f

    val unitColor = if (isUnitUnlocked) Color(unit.colorHex) else Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // UNIT BANNER (Petek Kovanı Başlığı)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = unitColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isUnitUnlocked) 6.dp else 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = HexagonShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(46.dp)
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
                            text = if (isUnitUnlocked) "$completedCount / $totalLessons Petek" else "🔒 Kilitli",
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
                    trackColor = Color.White.copy(alpha = 0.35f)
                )

                if (isUnitUnlocked) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // 7 PEDAGOGICAL INTERMEDIATE STAGES (ARA KADEMELER STRIP)
                    Text(
                        text = "🌉 Ünite İçi 7 Pedagojik Ara Kademe & Köprü:",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF6366F1),
                                modifier = Modifier.clickable { onOpenIntermediateStage(unit, IntermediateStageType.COMIC_STORY) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "📖", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Çizgi Roman", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981),
                                modifier = Modifier.clickable { onOpenIntermediateStage(unit, IntermediateStageType.SENTENCE_LAB) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🧪", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Cümle Lab", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.clickable { onOpenIntermediateStage(unit, IntermediateStageType.SMART_REVIEW) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🔍", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Hata Dedektifi", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE11D48),
                                modifier = Modifier.clickable { onOpenIntermediateStage(unit, IntermediateStageType.PASSPORT_STAMP) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🛂", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Vize Damgası", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFA855F7),
                                modifier = Modifier.clickable { onOpenIntermediateStage(unit, IntermediateStageType.ROLEPLAY_SNAPSHOT) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🎙️", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Rol Canlandırma", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0284C7),
                                modifier = Modifier.clickable { onOpenIntermediateStage(unit, IntermediateStageType.TRACE_WRITE) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🖐️", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "İz Çiz & Yaz", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFD97706),
                                modifier = Modifier.clickable { onOpenIntermediateStage(unit, IntermediateStageType.CULTURE_FACT) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🇬🇧", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Biliyor Musun?", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMnemonic(unit) }
                            .testTag("unit_mnemonic_btn_${unit.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "💡", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Poko'nun Hafıza Şifreleri (Kelimeleri Kolay Ezberle)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // INTERLOCKING HONEYCOMB MESH (Petek Örgü Düğüm Dizilimi)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var stepIndex = 0

            // 1. LESSON HONEYCOMB NODES
            unit.lessons.forEachIndexed { idx, lesson ->
                val progressEntity = lessonProgressMap[lesson.id]
                val isCompleted = progressEntity?.isCompleted == true
                val stars = progressEntity?.starsEarned ?: 0
                val isUnlocked = unlockedLessonIds.contains(lesson.id)

                val hexOffsetX = when (stepIndex % 2) {
                    0 -> (-38).dp
                    else -> 38.dp
                }

                HoneycombNode(
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
                    modifier = Modifier.offset(x = hexOffsetX)
                )

                HoneycombConnectingConduit(
                    isUnlocked = isUnlocked,
                    fromLeftToRight = (stepIndex % 2 == 0)
                )
                stepIndex++
            }

            // 2. SPECIAL INTERACTIVE NODES FOR THE UNIT TRAIL (When unit is unlocked)
            if (isUnitUnlocked) {
                // Node A: 🎁 Mystery Treasure Chest Node
                val chestOffset = if (stepIndex % 2 == 0) (-38).dp else 38.dp
                HoneycombSpecialNode(
                    emoji = "🎁",
                    title = "Hazine Sandığı",
                    subtitle = "+50 Altın & Elmas",
                    glowColor = Color(0xFFF59E0B),
                    tag = "chest_${unit.id}",
                    onClick = { onOpenTreasureChest(unit) },
                    modifier = Modifier.offset(x = chestOffset)
                )
                HoneycombConnectingConduit(
                    isUnlocked = true,
                    fromLeftToRight = (stepIndex % 2 == 0)
                )
                stepIndex++

                // Node B: ⚡ 60s Speed Dash Node
                val dashOffset = if (stepIndex % 2 == 0) (-38).dp else 38.dp
                HoneycombSpecialNode(
                    emoji = "⚡",
                    title = "60 Sn Hız Koşusu",
                    subtitle = "Süreli Kelime Maratonu",
                    glowColor = Color(0xFF06B6D4),
                    tag = "dash_${unit.id}",
                    onClick = { onOpenSpeedDash(unit) },
                    modifier = Modifier.offset(x = dashOffset)
                )
                HoneycombConnectingConduit(
                    isUnlocked = true,
                    fromLeftToRight = (stepIndex % 2 == 0)
                )
                stepIndex++

                // Node C: 🎵 Sing-Along Karaoke Node
                val singOffset = if (stepIndex % 2 == 0) (-38).dp else 38.dp
                HoneycombSpecialNode(
                    emoji = "🎵",
                    title = "Şarkı & Ritim",
                    subtitle = "Poko ile Şarkı Söyle",
                    glowColor = Color(0xFFEC4899),
                    tag = "sing_${unit.id}",
                    onClick = { onOpenSingAlong(unit) },
                    modifier = Modifier.offset(x = singOffset)
                )
                HoneycombConnectingConduit(
                    isUnlocked = true,
                    fromLeftToRight = (stepIndex % 2 == 0)
                )
                stepIndex++

                // Node D: ⚔️ Two-Player Duel Node
                val duelOffset = if (stepIndex % 2 == 0) (-38).dp else 38.dp
                HoneycombSpecialNode(
                    emoji = "⚔️",
                    title = "Arkadaş Düellosu",
                    subtitle = "2 Kişilik Kapışma",
                    glowColor = Color(0xFFEA580C),
                    tag = "duel_${unit.id}",
                    onClick = { onOpenDuel(unit) },
                    modifier = Modifier.offset(x = duelOffset)
                )
                HoneycombConnectingConduit(
                    isUnlocked = true,
                    fromLeftToRight = (stepIndex % 2 == 0)
                )
                stepIndex++

                // Node E: 👾 Unit Boss Arena Node (Grand Finale of the Unit)
                val bossOffset = if (stepIndex % 2 == 0) (-38).dp else 38.dp
                HoneycombSpecialNode(
                    emoji = "👾",
                    title = "Bölüm Boss'u!",
                    subtitle = "Büyük Canavar Savaşı",
                    glowColor = Color(0xFF8B5CF6),
                    tag = "boss_${unit.id}",
                    isBoss = true,
                    onClick = { onOpenBossBattle(unit) },
                    modifier = Modifier.offset(x = bossOffset)
                )
            }
        }
    }
}

/**
 * HONEYCOMB NODE (Tekil Petek Hücresi)
 * 3D kabartmalı, parıltılı ve animasyonlu petek hücresi!
 */
@Composable
private fun HoneycombNode(
    lesson: Lesson,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    starsEarned: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hex_pulse")

    // Gentle pulse aura for active / unlocked node
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (!isCompleted && isUnlocked) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Bouncing icon for active node
    val iconBounce by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = if (!isCompleted && isUnlocked) 3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag("honeycomb_node_${lesson.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(86.dp),
            contentAlignment = Alignment.Center
        ) {
            // 3D Hexagon Canvas Rendering
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Base 3D extruded shadow (slightly shifted downwards)
                val shadowPath = createHexagonPath(w, h, offsetY = 6.dp.toPx())
                drawPath(
                    path = shadowPath,
                    color = if (isCompleted) Color(0xFF15803D)
                    else if (isUnlocked) Color(0xFF1D4ED8)
                    else Color(0xFF475569)
                )

                // Main Hexagon Tile
                val hexPath = createHexagonPath(w, h, offsetY = 0f)
                val hexBrush = when {
                    isCompleted -> Brush.verticalGradient(
                        listOf(Color(0xFF22C55E), Color(0xFF16A34A), Color(0xFF15803D))
                    )
                    isUnlocked -> Brush.verticalGradient(
                        listOf(Color(0xFF38BDF8), Color(0xFF2563EB), Color(0xFF1D4ED8))
                    )
                    else -> Brush.verticalGradient(
                        listOf(Color(0xFF94A3B8), Color(0xFF64748B))
                    )
                }

                drawPath(path = hexPath, brush = hexBrush)

                // Golden / White Honeycomb Rim Border
                val borderColor = when {
                    isCompleted -> SunGold
                    isUnlocked -> Color.White
                    else -> Color(0xFFCBD5E1)
                }

                drawPath(
                    path = hexPath,
                    color = borderColor,
                    style = Stroke(width = 3.5.dp.toPx())
                )

                // Top glossy specular highlight arc
                val highlightPath = Path().apply {
                    moveTo(w * 0.25f, h * 0.15f)
                    lineTo(w * 0.5f, h * 0.08f)
                    lineTo(w * 0.75f, h * 0.15f)
                }
                drawPath(
                    path = highlightPath,
                    color = Color.White.copy(alpha = 0.55f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Interior Content (Lock, Emoji & Stars)
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Kilitli Petek",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.offset(y = iconBounce.dp)
                ) {
                    Text(text = lesson.iconEmoji, fontSize = 28.sp)
                }
            }
        }

        // Honeycomb Cell Title Plaque & Stars
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isUnlocked) Color.White else Color(0xFFF1F5F9),
            shadowElevation = if (isUnlocked) 3.dp else 0.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isCompleted) SunGold else if (isUnlocked) BrandBlue else Color(0xFFCBD5E1)
            ),
            modifier = Modifier.padding(top = 4.dp)
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
                            fontWeight = FontWeight.Black,
                            color = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF94A3B8)
                        )
                    )
                }

                if (isCompleted && starsEarned > 0) {
                    StarRatingRow(starsEarned = starsEarned, size = 13.dp)
                } else if (isUnlocked) {
                    Text(
                        text = "BAŞLA ▶️",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BrandBlue,
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Connecting Honey Conduit between staggered honeycomb cells
 */
@Composable
private fun HoneycombConnectingConduit(
    isUnlocked: Boolean,
    fromLeftToRight: Boolean
) {
    Canvas(
        modifier = Modifier
            .width(100.dp)
            .height(28.dp)
            .padding(vertical = 2.dp)
    ) {
        val w = size.width
        val h = size.height

        val startX = if (fromLeftToRight) w * 0.25f else w * 0.75f
        val endX = if (fromLeftToRight) w * 0.75f else w * 0.25f

        // Glowing honey path curve
        val path = Path().apply {
            moveTo(startX, 0f)
            cubicTo(
                startX, h * 0.5f,
                endX, h * 0.5f,
                endX, h
            )
        }

        // Shadow path
        drawPath(
            path = path,
            color = if (isUnlocked) Color(0xFFD97706) else Color(0xFFCBD5E1),
            style = Stroke(width = 4.dp.toPx())
        )

        // Golden nectar highlight
        if (isUnlocked) {
            drawPath(
                path = path,
                color = SunGold,
                style = Stroke(width = 2.dp.toPx())
            )
            // Honey drop droplet
            drawCircle(
                color = SunOrange,
                radius = 3.5.dp.toPx(),
                center = Offset(w * 0.5f, h * 0.5f)
            )
        }
    }
}

/**
 * Helper to generate regular Pointy-Topped Hexagon Path
 */
private fun createHexagonPath(width: Float, height: Float, offsetY: Float = 0f): Path {
    val path = Path()
    val cx = width / 2f
    val cy = (height / 2f) + offsetY
    val radius = minOf(width, height) * 0.48f

    for (i in 0 until 6) {
        val angle = Math.toRadians((60 * i - 30).toDouble())
        val x = cx + radius * cos(angle).toFloat()
        val y = cy + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

/**
 * Compose Shape for regular pointy-topped Hexagon
 */
val HexagonShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = createHexagonPath(size.width, size.height, 0f)
        return Outline.Generic(path)
    }
}

/**
 * Friendly Locked Honeycomb Dialog with Poko
 */
@Composable
private fun LockedHoneycombDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("locked_honeycomb_dialog"),
            shape = RoundedCornerShape(26.dp),
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
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Text(
                        text = "🔒 Kilitli Petek Hücresi",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
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
                    text = "Hemen Tamamlayayım! 🚀",
                    onClick = onDismiss,
                    color = SunGold,
                    shadowColor = Color(0xFFB45309),
                    modifier = Modifier.fillMaxWidth(0.85f),
                    testTag = "locked_honeycomb_ok_btn"
                )
            }
        }
    }
}

/**
 * Dialog displaying Poko's Memory Codes & Mnemonics for all words in a Unit
 */
@Composable
private fun UnitMnemonicDialog(
    unit: UnitTopic,
    onDismiss: () -> Unit
) {
    val unitWords = remember(unit) {
        unit.lessons.flatMap { it.targetWords }.distinctBy { it.id }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("unit_mnemonic_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Hafıza Şifreleri Kitapçığı",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B)
                                )
                            )
                            Text(
                                text = "${unit.titleTr} (${unit.mebCode})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(unitWords) { word ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = word.emoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = word.english,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF0F172A),
                                                fontSize = 17.sp
                                            )
                                        )
                                        if (word.phonetic.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "[${word.phonetic}]",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = PlayfulPurple,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = BrandBlue.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = word.turkish,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = BrandBlue,
                                                fontSize = 12.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "💡 ${word.displayMnemonic}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E),
                                            fontSize = 11.5.sp,
                                            lineHeight = 14.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Kid3DButton(
                    text = "Anladım, Harika! 🌟",
                    onClick = onDismiss,
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "unit_mnemonic_close_btn"
                )
            }
        }
    }
}

/**
 * HONEYCOMB SPECIAL NODE
 * Used for Mystery Chest, Speed Dash, Boss Arena, Karaoke, and 2-Player Duel!
 */
@Composable
private fun HoneycombSpecialNode(
    emoji: String,
    title: String,
    subtitle: String,
    glowColor: Color,
    tag: String,
    isBoss: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "special_node_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isBoss) 1.12f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isBoss) 600 else 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "special_pulse"
    )

    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag("honeycomb_special_node_$tag"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(if (isBoss) 94.dp else 84.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // 3D shadow
                val shadowPath = createHexagonPath(w, h, offsetY = 6.dp.toPx())
                drawPath(
                    path = shadowPath,
                    color = glowColor.copy(alpha = 0.5f)
                )

                // Main Hexagon Tile
                val hexPath = createHexagonPath(w, h, offsetY = 0f)
                drawPath(
                    path = hexPath,
                    brush = Brush.verticalGradient(
                        listOf(glowColor.copy(alpha = 0.9f), glowColor, glowColor.copy(alpha = 0.7f))
                    )
                )

                // Golden Inner border
                drawPath(
                    path = hexPath,
                    color = Color.White.copy(alpha = 0.8f),
                    style = Stroke(width = if (isBoss) 3.5.dp.toPx() else 2.5.dp.toPx())
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = emoji,
                    fontSize = if (isBoss) 32.sp else 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, glowColor.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        fontSize = 10.5.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = glowColor,
                        fontSize = 8.5.sp
                    )
                )
            }
        }
    }
}

// =========================================================================
// 1. 🎁 MYSTERY TREASURE CHEST DIALOG
// =========================================================================
@Composable
private fun HoneycombTreasureChestDialog(
    unit: UnitTopic,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isOpened by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("treasure_chest_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎁 Gizli Hazine Sandığı",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309)
                    )
                )
                Text(
                    text = "${unit.titleTr} Gizli Hazinesi",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFEF08A), Color(0xFFFDE047), Color(0xFFF59E0B))
                            )
                        )
                        .clickable(enabled = !isOpened) {
                            isOpened = true
                            audioService.playCorrectSound()
                            onRewardCoins(50)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isOpened) "✨💎✨" else "🎁",
                        fontSize = if (isOpened) 42.sp else 54.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isOpened) {
                    Text(
                        text = "Sandığa dokun ve hazineni aç! 🗝️",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                } else {
                    AnimatedVisibility(visible = isOpened, enter = fadeIn() + scaleIn()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "TEBRİKLER! 🎉",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF16A34A)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙 +50 Altın", fontWeight = FontWeight.Black, color = Color(0xFFB45309), fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "💎 +5 Elmas", fontWeight = FontWeight.Black, color = Color(0xFF0284C7), fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Kid3DButton(
                    text = if (isOpened) "Ödülleri Topla! 🌟" else "Şimdi Aç! 🗝️",
                    onClick = {
                        if (!isOpened) {
                            isOpened = true
                            audioService.playCorrectSound()
                            onRewardCoins(50)
                        } else {
                            onDismiss()
                        }
                    },
                    color = SunGold,
                    shadowColor = Color(0xFFB45309),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "treasure_chest_action_btn"
                )
            }
        }
    }
}

// =========================================================================
// 2. 👾 UNIT BOSS BATTLE DIALOG
// =========================================================================
@Composable
private fun HoneycombBossBattleDialog(
    unit: UnitTopic,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val unitWords = remember(unit) {
        unit.lessons.flatMap { it.targetWords }.distinctBy { it.id }.take(6)
    }

    var bossHp by remember { mutableIntStateOf(100) }
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var isVictory by remember { mutableStateOf(false) }

    val currentTarget = remember(currentQuestionIdx, unitWords) {
        if (unitWords.isNotEmpty()) unitWords[currentQuestionIdx % unitWords.size] else null
    }

    val options = remember(currentTarget, unitWords) {
        if (currentTarget != null) {
            val others = unitWords.filter { it.id != currentTarget.id }.shuffled().take(2)
            (listOf(currentTarget) + others).shuffled()
        } else emptyList()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("boss_battle_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👾 ÜNİTE BOSS ARENASI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF7C3AED)
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEDE9FE)
                    ) {
                        Text(
                            text = "${unit.titleTr}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED)
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!isVictory) {
                    // Boss Avatar & Health Bar
                    Text(text = "🐉", fontSize = 54.sp)
                    Text(
                        text = "Kelime Ejderhası (Boss)",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "HP: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Red)
                        LinearProgressIndicator(
                            progress = { bossHp / 100f },
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = Color(0xFFEF4444),
                            trackColor = Color(0xFFFEE2E2)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "$bossHp%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFEF4444))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (currentTarget != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Soru: \"${currentTarget.english}\" ne demektir?",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3 Options
                        options.forEach { opt ->
                            Kid3DButton(
                                text = "${opt.emoji} ${opt.turkish}",
                                onClick = {
                                    if (opt.id == currentTarget.id) {
                                        audioService.playCorrectSound()
                                        val newHp = (bossHp - 25).coerceAtLeast(0)
                                        bossHp = newHp
                                        if (newHp == 0) {
                                            isVictory = true
                                            onRewardCoins(100)
                                        } else {
                                            currentQuestionIdx++
                                        }
                                    } else {
                                        audioService.playWrongSound()
                                    }
                                },
                                color = BrandBlue,
                                shadowColor = Color(0xFF1E40AF),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                testTag = "boss_option_${opt.id}"
                            )
                        }
                    }
                } else {
                    // VICTORY SCREEN
                    Text(text = "👑🏆🎉", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "BOSS YENİLDİ!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF16A34A)
                        )
                    )
                    Text(
                        text = "${unit.titleTr} Boss Arenasını başarıyla fethettin!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = "🪙 +100 Altın & 👑 Boss Fatihi Kupası!",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Kid3DButton(
                        text = "Zaferle Kapat! 🚀",
                        onClick = onDismiss,
                        color = LeafGreen,
                        shadowColor = Color(0xFF15803D),
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "boss_victory_close_btn"
                    )
                }
            }
        }
    }
}

// =========================================================================
// 3. ⚡ 60s SPEED DASH DIALOG
// =========================================================================
@Composable
private fun HoneycombSpeedDashDialog(
    unit: UnitTopic,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val unitWords = remember(unit) {
        unit.lessons.flatMap { it.targetWords }.distinctBy { it.id }
    }

    var timeLeft by remember { mutableIntStateOf(60) }
    var score by remember { mutableIntStateOf(0) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    LaunchedEffect(isFinished) {
        if (!isFinished) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isFinished = true
            audioService.playCorrectSound()
            onRewardCoins(score * 5)
        }
    }

    val currentTarget = remember(currentIdx, unitWords) {
        if (unitWords.isNotEmpty()) unitWords[currentIdx % unitWords.size] else null
    }

    val options = remember(currentTarget, unitWords) {
        if (currentTarget != null) {
            val others = unitWords.filter { it.id != currentTarget.id }.shuffled().take(2)
            (listOf(currentTarget) + others).shuffled()
        } else emptyList()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("speed_dash_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ 60 Sn Hız Koşusu",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0284C7)
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE0F2FE)
                    ) {
                        Text(
                            text = "⏱️ $timeLeft sn",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = if (timeLeft < 10) Color.Red else Color(0xFF0369A1)
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!isFinished) {
                    Text(
                        text = "Puan: $score ⭐",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentTarget != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = currentTarget.emoji, fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentTarget.english,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF15803D)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        options.forEach { opt ->
                            Kid3DButton(
                                text = opt.turkish,
                                onClick = {
                                    if (opt.id == currentTarget.id) {
                                        score += 10
                                        audioService.playCorrectSound()
                                    } else {
                                        audioService.playWrongSound()
                                    }
                                    currentIdx++
                                },
                                color = BrandBlue,
                                shadowColor = Color(0xFF1E40AF),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                testTag = "speed_dash_opt_${opt.id}"
                            )
                        }
                    }
                } else {
                    Text(text = "🏁⚡🎉", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SÜRE DOLDU!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0284C7)
                        )
                    )
                    Text(
                        text = "Toplam Skorun: $score Puan!",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Kid3DButton(
                        text = "Harika! Kapat 🚀",
                        onClick = onDismiss,
                        color = BrandBlue,
                        shadowColor = Color(0xFF1E40AF),
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "speed_dash_close_btn"
                    )
                }
            }
        }
    }
}

// =========================================================================
// 4. 🎵 SING-ALONG KARAOKE DIALOG
// =========================================================================
@Composable
private fun HoneycombSingAlongDialog(
    unit: UnitTopic,
    audioService: AudioService,
    speechService: SpeechRecognitionService,
    onRewardCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val unitWords = remember(unit) {
        unit.lessons.flatMap { it.targetWords }.distinctBy { it.id }.take(4)
    }

    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var isSangCorrectly by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("sing_along_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎵 Poko ile Şarkı & Karaoke",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFDB2777)
                    )
                )
                Text(
                    text = "${unit.titleTr} Şarkısı",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2F8)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBCFE8))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎤 Ritmik Şarkı Sözleri:", fontWeight = FontWeight.Bold, color = Color(0xFF9D174D), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        unitWords.forEach { w ->
                            Text(
                                text = "🎶 ${w.english} • ${w.turkish} [${w.phonetic}]",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF831843),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Kid3DButton(
                    text = "Dinle & Söyle 🔊",
                    onClick = {
                        val allWordsStr = unitWords.joinToString(", ") { it.english }
                        audioService.speakEnglish(allWordsStr)
                    },
                    color = Color(0xFFEC4899),
                    shadowColor = Color(0xFFBE185D),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Kid3DButton(
                    text = if (isListening) "Dinliyorum... 🎙️" else "Mikrofonla Eşlik Et 🎤",
                    onClick = {
                        val allWordsStr = unitWords.joinToString(" ") { it.english }
                        isListening = true
                        speechService.startListening(
                            target = allWordsStr,
                            onResult = { eval ->
                                isListening = false
                                recognizedText = eval.spokenText
                                isSangCorrectly = true
                                audioService.playCorrectSound()
                                onRewardCoins(40)
                            }
                        )
                    },
                    color = PlayfulPurple,
                    shadowColor = Color(0xFF6D28D9),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isSangCorrectly) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Harika söyledin! 🌟 +40 Altın", fontWeight = FontWeight.Black, color = Color(0xFF16A34A), fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Kid3DButton(
                    text = "Tamamla 🚀",
                    onClick = onDismiss,
                    color = BrandBlue,
                    shadowColor = Color(0xFF1E40AF),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// =========================================================================
// 5. ⚔️ TWO-PLAYER FRIEND DUEL DIALOG
// =========================================================================
@Composable
private fun HoneycombFriendDuelDialog(
    unit: UnitTopic,
    audioService: AudioService,
    onRewardCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val unitWords = remember(unit) {
        unit.lessons.flatMap { it.targetWords }.distinctBy { it.id }.take(6)
    }

    var p1Score by remember { mutableIntStateOf(0) }
    var p2Score by remember { mutableIntStateOf(0) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    val currentTarget = remember(currentIdx, unitWords) {
        if (unitWords.isNotEmpty()) unitWords[currentIdx % unitWords.size] else null
    }

    val options = remember(currentTarget, unitWords) {
        if (currentTarget != null) {
            val others = unitWords.filter { it.id != currentTarget.id }.shuffled().take(2)
            (listOf(currentTarget) + others).shuffled()
        } else emptyList()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("friend_duel_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚔️ 2 Kişilik Arkadaş Düellosu",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFEA580C)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFDBEAFE)) {
                        Text(text = "🔴 Oyuncu 1: $p1Score", fontWeight = FontWeight.Black, color = Color(0xFF1D4ED8), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFFEE2E2)) {
                        Text(text = "🔵 Oyuncu 2: $p2Score", fontWeight = FontWeight.Black, color = Color(0xFFDC2626), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!isGameOver && currentTarget != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = currentTarget.emoji, fontSize = 32.sp)
                            Text(text = "\"${currentTarget.english}\" nedir?", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFB45309))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "İlk doğru basan oyuncu puanı kapar!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(6.dp))

                    options.forEach { opt ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Kid3DButton(
                                text = "🔴 ${opt.turkish}",
                                onClick = {
                                    if (opt.id == currentTarget.id) {
                                        p1Score += 10
                                        audioService.playCorrectSound()
                                    } else {
                                        audioService.playWrongSound()
                                    }
                                    if (currentIdx >= 4) {
                                        isGameOver = true
                                        onRewardCoins(50)
                                    } else currentIdx++
                                },
                                color = BrandBlue,
                                shadowColor = Color(0xFF1E40AF),
                                modifier = Modifier.weight(1f)
                            )
                            Kid3DButton(
                                text = "🔵 ${opt.turkish}",
                                onClick = {
                                    if (opt.id == currentTarget.id) {
                                        p2Score += 10
                                        audioService.playCorrectSound()
                                    } else {
                                        audioService.playWrongSound()
                                    }
                                    if (currentIdx >= 4) {
                                        isGameOver = true
                                        onRewardCoins(50)
                                    } else currentIdx++
                                },
                                color = Color(0xFFEF4444),
                                shadowColor = Color(0xFFB91C1C),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    Text(text = "👑🏆🎉", fontSize = 46.sp)
                    val winner = if (p1Score > p2Score) "🔴 Oyuncu 1 Kazandı!" else if (p2Score > p1Score) "🔵 Oyuncu 2 Kazandı!" else "🤝 Berabere!"
                    Text(text = winner, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = Color(0xFF16A34A)))

                    Spacer(modifier = Modifier.height(14.dp))

                    Kid3DButton(
                        text = "Düelloyu Bitir 🚀",
                        onClick = onDismiss,
                        color = SunGold,
                        shadowColor = Color(0xFFB45309),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
