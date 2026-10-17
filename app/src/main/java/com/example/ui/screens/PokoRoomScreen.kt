package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.CurriculumData
import com.example.data.local.LessonProgressEntity
import com.example.data.model.GradeLevel
import com.example.data.model.Lesson
import com.example.data.model.UnitTopic
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import com.example.viewmodel.MainTab

/**
 * POKO'S ROOM (POKO'NUN ODASI) - 3D Çocuk Dostu Oda Konsepti
 *
 * Canlı, neşeli, 3 boyutlu derinlik hissi veren çocuk odası atmosferi:
 * - Güneşli, gökkuşağı ve bulut animasyonlu kemer pencere
 * - Tavan süsleme bayrakları (bunting garland)
 * - Sıcak ahşap parke zemin ve yıldızlı renkli peluş halı
 * - Ortada canlı, konuşan Poko baykuş maskotu
 * - Etkileşimli 3D mobilya istasyonları:
 *   1. Çalışma Masası -> Petek Ders Haritası (Honeycomb Map)
 *   2. Oyun Konsolu / Sandığı -> Mini Oyunlar
 *   3. Kelime Kitaplığı -> Kelime Bankası
 *   4. Kostüm Dolabı -> Avatar & Giydirme
 *   5. Başarı Panosu -> Gelişim Raporu
 */
@Composable
fun PokoRoomScreen(
    units: List<UnitTopic>,
    lessonProgressMap: Map<String, LessonProgressEntity>,
    activeGrade: GradeLevel,
    equippedCostumeId: String = "costume_default",
    equippedWeaponId: String = "weapon_red_saber",
    equippedHelmetId: String = "helmet_vader",
    equippedDecorId: String = "decor_holotable",
    equippedThemeId: String = "theme_starship",
    totalStars: Int,
    coins: Int,
    streakDays: Int,
    onNavigateToTab: (MainTab) -> Unit,
    onStartLesson: (Lesson) -> Unit,
    onMascotSpeak: () -> Unit,
    onOpenBuddyTalk: () -> Unit = {},
    onOpenDaysOfWeekGame: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Current grade lessons
    val allLessonsCurrentGrade = remember(units) { units.flatMap { it.lessons } }
    
    // 1. Check if there's any uncompleted lesson in the current active grade
    val nextLessonInCurrentGrade = remember(allLessonsCurrentGrade, lessonProgressMap) {
        allLessonsCurrentGrade.firstOrNull { lesson ->
            val prog = lessonProgressMap[lesson.id]
            prog == null || !prog.isCompleted
        }
    }

    // 2. If current grade is 100% finished, find the first uncompleted lesson in subsequent grades (Grade 1 -> Grade 2 -> Grade 3 -> Grade 4)
    val nextLesson = remember(nextLessonInCurrentGrade, lessonProgressMap, activeGrade) {
        if (nextLessonInCurrentGrade != null) {
            nextLessonInCurrentGrade
        } else {
            val allGrades = listOf(GradeLevel.GRADE_1, GradeLevel.GRADE_2, GradeLevel.GRADE_3, GradeLevel.GRADE_4)
            val currentIdx = allGrades.indexOf(activeGrade)
            val orderedGradesToCheck = allGrades.drop(currentIdx + 1) + allGrades.take(currentIdx)
            orderedGradesToCheck.firstNotNullOfOrNull { grade ->
                val gradeLessons = CurriculumData.getUnitsForGrade(grade).flatMap { it.lessons }
                gradeLessons.firstOrNull { lesson ->
                    val prog = lessonProgressMap[lesson.id]
                    prog == null || !prog.isCompleted
                }
            } ?: allLessonsCurrentGrade.firstOrNull()
        }
    }

    val completedLessonsCount = remember(allLessonsCurrentGrade, lessonProgressMap) {
        allLessonsCurrentGrade.count { lessonProgressMap[it.id]?.isCompleted == true }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2FE))
            .testTag("poko_room_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            // 3D ROOM MAIN STAGE (Starship Bridge, Droid, Holotable & Poko)
            Room3DStage(
                activeGrade = activeGrade,
                equippedCostumeId = equippedCostumeId,
                equippedDecorId = equippedDecorId,
                equippedThemeId = equippedThemeId,
                totalStars = totalStars,
                coins = coins,
                streakDays = streakDays,
                onMascotClick = onMascotSpeak,
                onOpenBuddyTalk = onOpenBuddyTalk
            )
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))

            // PRIMARY HERO CTA: "Maceraya Başla / Devam Et" (Always points to the next uncompleted lesson)
            if (nextLesson != null) {
                PrimaryAdventureHeroCard(
                    nextLesson = nextLesson,
                    activeGrade = activeGrade,
                    completedCount = completedLessonsCount,
                    totalCount = allLessonsCurrentGrade.size,
                    onOpenMap = { onNavigateToTab(MainTab.MAP) },
                    onStartDirectLesson = { onStartLesson(nextLesson) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // INTERACTIVE ROOM STATIONS GRID (3D Odadaki Mobilya & İstasyonlar)
            Text(
                text = "🏠 Poko'nun Odasındaki İstasyonlar",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            RoomStationsSection(
                onNavigateToTab = onNavigateToTab,
                onOpenBuddyTalk = onOpenBuddyTalk,
                onOpenDaysOfWeekGame = onOpenDaysOfWeekGame
            )
        }
    }
}

/**
 * 3D STAR WARS IMPERIAL STARSHIP BRIDGE & GALACTIC COCKPIT CANVAS 🚀⭐
 * Displays:
 * - Panoramic space bridge viewport showing hyperspace lightspeed streaks, twin suns and starships
 * - Dark titanium spaceship bulkheads with glowing cyan LED conduits
 * - Metallic starship deck grating with perspective glowing runway light lines
 * - Center Holographic projection platform (Holo-Deck)
 * - 3D LEGO Star Wars Minifigure Hero with glowing Lightsaber
 */
@Composable
private fun Room3DStage(
    activeGrade: GradeLevel,
    equippedCostumeId: String,
    equippedDecorId: String = "decor_holotable",
    equippedThemeId: String = "theme_starship",
    totalStars: Int,
    coins: Int,
    streakDays: Int,
    onMascotClick: () -> Unit,
    onOpenBuddyTalk: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "starship_bridge_anim")

    // Hyperspace lightspeed star streak movement
    val hyperSpeedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hyper_speed"
    )

    // Hologram & console pulse
    val holoPulse by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "holo_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        // 1. Starship Bridge Cockpit Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val deckHeight = h * 0.55f

            // A. Deep Space Viewport (Upper Half)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712), // Deep black void
                        Color(0xFF090D1A), // Space blue
                        Color(0xFF1E1B4B)  // Nebula indigo
                    ),
                    startY = 0f,
                    endY = deckHeight
                ),
                size = Size(w, deckHeight)
            )

            // Hyperspace star streaks emitting from center
            val vpCenterX = w * 0.5f
            val vpCenterY = deckHeight * 0.45f
            val starRays = 18
            for (i in 0 until starRays) {
                val angle = (i * (360f / starRays) + hyperSpeedOffset * 40f) * (PI / 180f)
                val innerR = 15.dp.toPx() + (hyperSpeedOffset * 25.dp.toPx())
                val outerR = innerR + 45.dp.toPx() + (hyperSpeedOffset * 50.dp.toPx())
                val startX = (vpCenterX + cos(angle) * innerR).toFloat()
                val startY = (vpCenterY + sin(angle) * innerR).toFloat()
                val endX = (vpCenterX + cos(angle) * outerR).toFloat()
                val endY = (vpCenterY + sin(angle) * outerR).toFloat()

                drawLine(
                    color = Color(0xFF67E8F9).copy(alpha = 0.7f * holoPulse),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = (1.5f + (hyperSpeedOffset * 1.5f)).dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Distant Glowing Alien Planet (Tatooine / Mustafar / Coruscant)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF1E1B4B)),
                    center = Offset(w * 0.18f, deckHeight * 0.35f),
                    radius = 35.dp.toPx()
                ),
                radius = 35.dp.toPx(),
                center = Offset(w * 0.18f, deckHeight * 0.35f)
            )
            // Planet atmospheric rim glow
            drawCircle(
                color = Color(0xFF7DD3FC).copy(alpha = 0.5f),
                radius = 36.dp.toPx(),
                center = Offset(w * 0.18f, deckHeight * 0.35f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Distant Star Destroyer Silhouette
            val sdX = w * 0.80f
            val sdY = deckHeight * 0.30f
            val sdPath = Path().apply {
                moveTo(sdX, sdY - 10.dp.toPx())
                lineTo(sdX + 32.dp.toPx(), sdY + 6.dp.toPx())
                lineTo(sdX - 32.dp.toPx(), sdY + 6.dp.toPx())
                close()
            }
            drawPath(sdPath, Color(0xFF334155).copy(alpha = 0.8f))

            // Cockpit Angled Struts / Steel Viewport Beams (Imperial Bridge Frame)
            val strutColor = Color(0xFF1E293B)
            val strutHighlight = Color(0xFF38BDF8)
            // Left angled strut
            val leftStrutPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.15f, 0f)
                lineTo(w * 0.28f, deckHeight)
                lineTo(0f, deckHeight)
                close()
            }
            drawPath(leftStrutPath, strutColor)
            drawLine(strutHighlight.copy(alpha = 0.6f), Offset(w * 0.15f, 0f), Offset(w * 0.28f, deckHeight), strokeWidth = 2.dp.toPx())

            // Right angled strut
            val rightStrutPath = Path().apply {
                moveTo(w, 0f)
                lineTo(w * 0.85f, 0f)
                lineTo(w * 0.72f, deckHeight)
                lineTo(w, deckHeight)
                close()
            }
            drawPath(rightStrutPath, strutColor)
            drawLine(strutHighlight.copy(alpha = 0.6f), Offset(w * 0.85f, 0f), Offset(w * 0.72f, deckHeight), strokeWidth = 2.dp.toPx())

            // Horizontal Viewport Bridge Sill with LED indicator lights
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF334155), Color(0xFF0F172A))
                ),
                topLeft = Offset(0f, deckHeight - 12.dp.toPx()),
                size = Size(w, 14.dp.toPx())
            )
            // Neon Cyan Guide line along sill
            drawLine(Color(0xFF00F0FF), Offset(0f, deckHeight - 12.dp.toPx()), Offset(w, deckHeight - 12.dp.toPx()), strokeWidth = 2.dp.toPx())

            // Console blinking LEDs along sill
            for (led in 0..12) {
                val ledX = w * (led / 12f)
                val ledColor = when (led % 3) {
                    0 -> Color(0xFFEF4444)
                    1 -> Color(0xFF22C55E)
                    else -> Color(0xFF38BDF8)
                }
                drawCircle(ledColor, radius = 2.dp.toPx(), center = Offset(ledX, deckHeight - 5.dp.toPx()))
            }

            // B. Starship Bridge Deck (Lower Half Metallic Grating & Hologram Pad)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Titanium dark deck
                        Color(0xFF020617), // Deep space black deck
                        Color(0xFF020617)
                    ),
                    startY = deckHeight,
                    endY = h
                ),
                topLeft = Offset(0f, deckHeight),
                size = Size(w, h - deckHeight)
            )

            // Perspective glowing runway light lines (Star Wars hangar style)
            val runwayLines = 8
            for (i in 0..runwayLines) {
                val startDeckX = w * 0.5f + (i - runwayLines / 2f) * (w * 0.08f)
                val endDeckX = w * 0.5f + (i - runwayLines / 2f) * (w * 0.22f)
                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = 0.35f),
                    start = Offset(startDeckX, deckHeight),
                    end = Offset(endDeckX, h),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Center Circular 3D Holo-Deck Platform (Hologram Yansıtıcı Disk)
            val holoCenter = Offset(w * 0.5f, deckHeight + (h - deckHeight) * 0.55f)
            val holoRadiusX = w * 0.42f
            val holoRadiusY = (h - deckHeight) * 0.40f

            // Outer metallic rim
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF090D16)),
                    center = holoCenter,
                    radius = holoRadiusX
                ),
                topLeft = Offset(holoCenter.x - holoRadiusX, holoCenter.y - holoRadiusY),
                size = Size(holoRadiusX * 2, holoRadiusY * 2)
            )
            // Outer neon blue halo
            drawOval(
                color = Color(0xFF00F0FF).copy(alpha = 0.85f * holoPulse),
                topLeft = Offset(holoCenter.x - holoRadiusX, holoCenter.y - holoRadiusY),
                size = Size(holoRadiusX * 2, holoRadiusY * 2),
                style = Stroke(width = 2.5.dp.toPx())
            )
            // Inner cyan grid ring
            drawOval(
                color = Color(0xFF38BDF8).copy(alpha = 0.6f),
                topLeft = Offset(holoCenter.x - holoRadiusX * 0.70f, holoCenter.y - holoRadiusY * 0.70f),
                size = Size(holoRadiusX * 1.4f, holoRadiusY * 1.4f),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Concentric core holo glow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.45f * holoPulse), Color.Transparent),
                    center = holoCenter,
                    radius = holoRadiusX * 0.50f
                ),
                topLeft = Offset(holoCenter.x - holoRadiusX * 0.50f, holoCenter.y - holoRadiusY * 0.50f),
                size = Size(holoRadiusX, holoRadiusY)
            )
        }

        // 2. Ceiling Imperial Bridge Status Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🚀", fontSize = 14.sp)
                    Text(
                        text = "YILDIZ GEMİSİ KÖPRÜSÜ",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF67E8F9),
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🔥", fontSize = 14.sp)
                    Text(
                        text = "$streakDays Gün Serisi",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFBBF24),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // 3. 3D LEGO Star Wars Minifigure Hero Standing on the Holo-Deck in Center!
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PokoMascot(
                    size = 145.dp,
                    mood = PokoMood.HAPPY,
                    costumeId = equippedCostumeId,
                    speechBubbleText = "May the Force be with you! ⚔️✨",
                    speechBubbleSubtitle = "Güç seninle olsun genç kahraman! Hadi başlayalım!",
                    onSpeechClick = onMascotClick,
                    onMascotClick = onMascotClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Interactive "Tap Hero" & Glowing Buddy Talk Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.9f),
                        shadowElevation = 3.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.6f)),
                        modifier = Modifier.clickable { onMascotClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔊", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Dinle",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF67E8F9)
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDC2626),
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFEF08A)),
                        modifier = Modifier
                            .clickable(onClick = onOpenBuddyTalk)
                            .testTag("room_open_buddy_talk_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "⚔️", fontSize = 13.sp)
                            Text(
                                text = "Galaktik Sohbet (Buddy)!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * PRIMARY HERO CARD - "Maceraya Başla / Petek Harita"
 * The main gateway to the Honeycomb Map & Active Lesson!
 */
@Composable
private fun PrimaryAdventureHeroCard(
    nextLesson: Lesson,
    activeGrade: GradeLevel,
    completedCount: Int,
    totalCount: Int,
    onOpenMap: () -> Unit,
    onStartDirectLesson: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val isAdvanceToNextGrade = nextLesson.grade != activeGrade
    val isAllCompleted = completedCount >= totalCount && isAdvanceToNextGrade

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .scale(glowScale),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = if (isAdvanceToNextGrade) {
                            listOf(Color(0xFF7C3AED), Color(0xFF2563EB), Color(0xFF0284C7))
                        } else {
                            listOf(Color(0xFF0284C7), Color(0xFF2563EB), Color(0xFF4F46E5))
                        }
                    )
                )
                .border(
                    width = 3.dp,
                    color = Color(0xFFFDE047),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = if (isAdvanceToNextGrade) "🚀 YENİ KADEME AÇILDI: ${nextLesson.grade.title}" else "🗺️ ${activeGrade.title} MACERASI",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SunGold,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "$completedCount / $totalCount Ders",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78350F)
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(54.dp),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = nextLesson.iconEmoji, fontSize = 30.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAdvanceToNextGrade) "🎉 Önceki Kademe Tamamlandı! Sıradaki:" else "Sıradaki Aşama:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                        Text(
                            text = nextLesson.titleTr,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        Text(
                            text = nextLesson.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            ),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Kid3DButton(
                        text = "Haritaya Git 🗺️",
                        onClick = onOpenMap,
                        color = Color(0xFF059669),
                        shadowColor = Color(0xFF065F46),
                        modifier = Modifier.weight(1f),
                        testTag = "room_go_to_map_btn"
                    )

                    Kid3DButton(
                        text = if (isAdvanceToNextGrade) "${nextLesson.grade.title}'e Başla ▶️" else "Derse Başla ▶️",
                        onClick = onStartDirectLesson,
                        color = SunGold,
                        shadowColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1.2f),
                        testTag = "room_start_lesson_btn"
                    )
                }
            }
        }
    }
}

/**
 * 3D ROOM INTERACTIVE STATIONS
 * Beautiful cards representing the interactive zones inside Poko's room:
 * - Honeycomb Map Station (Çalışma Masası)
 * - Mini Games Arcade (Oyun Sandığı)
 * - Word Library (Kelime Kitaplığı)
 * - Avatar Wardrobe (Kostüm Dolabı)
 * - Trophy Board (Gelişim Raporu)
 */
@Composable
private fun RoomStationsSection(
    onNavigateToTab: (MainTab) -> Unit,
    onOpenBuddyTalk: () -> Unit = {},
    onOpenDaysOfWeekGame: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Teacher's Assignment: Haftanın Günleri (Days of the Week)
        RoomStationCard(
            title = "Haftanın Günleri: Öğren & Sınav Ol! 🗓️⭐",
            subtitle = "Öğretmen Özel Ödevi: 7 günü önce şarkıyla öğren, sonra sınav ol!",
            emoji = "🗓️",
            badge = "ÖĞRETMEN ÖDEVİ",
            bgGradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
            accentColor = Color(0xFFFDE047),
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenDaysOfWeekGame,
            testTag = "station_days_of_week"
        )

        // Featured Station: Poko Buddy Canlı Sohbet Odası
        RoomStationCard(
            title = "Poko Buddy Sohbeti 🎙️",
            subtitle = "Canlı ve neşeli İngilizce konuşma arkadaşın",
            emoji = "🦊",
            badge = "BUDDY TALK",
            bgGradient = listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)),
            accentColor = Color(0xFFFDE047),
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenBuddyTalk,
            testTag = "station_buddy_talk"
        )

        // Row 1: Petek Harita & Mini Oyunlar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoomStationCard(
                title = "Petek Harita",
                subtitle = "Üniteler & Kademeler",
                emoji = "🗺️",
                badge = "DERSLER",
                bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
                accentColor = Color(0xFF38BDF8),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(MainTab.MAP) },
                testTag = "station_honeycomb_map"
            )

            RoomStationCard(
                title = "Oyun Sandığı",
                subtitle = "Eğlenceli Mini Oyunlar",
                emoji = "🎮",
                badge = "OYUNLAR",
                bgGradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                accentColor = Color(0xFFA78BFA),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(MainTab.GAMES) },
                testTag = "station_mini_games"
            )
        }

        // Row 2: Kelime Kitaplığı & Kostüm Dolabı
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoomStationCard(
                title = "Kelime Kitaplığı",
                subtitle = "Sesli Kelime Bankası",
                emoji = "📚",
                badge = "SÖZLÜK",
                bgGradient = listOf(Color(0xFF10B981), Color(0xFF047857)),
                accentColor = Color(0xFF34D399),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(MainTab.WORDS) },
                testTag = "station_word_bank"
            )

            RoomStationCard(
                title = "Poko Dolabı",
                subtitle = "Kıyafetler & Şapkalar",
                emoji = "👕",
                badge = "KOSTÜM",
                bgGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                accentColor = Color(0xFFFBBF24),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(MainTab.SHOP) },
                testTag = "station_costume_shop"
            )
        }

        // Full Width: Gelişim, Veli Panosu ve Ders Sıfırlama
        RoomStationCard(
            title = "👨‍👩‍👧 Veli Panosu & Dersleri Sıfırla",
            subtitle = "İlerleme raporu, ses ayarları ve kademe derslerini sıfırlama alanı",
            emoji = "📊",
            badge = "EBEVEYN ALANI",
            bgGradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
            accentColor = Color(0xFFFDE047),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigateToTab(MainTab.PARENT) },
            testTag = "station_parent_report"
        )
    }
}

@Composable
private fun RoomStationCard(
    title: String,
    subtitle: String,
    emoji: String,
    badge: String,
    bgGradient: List<Color>,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val darkBaseColor = bgGradient.lastOrNull() ?: Color(0xFF1E1B4B)

    Box(
        modifier = modifier
            .testTag(testTag)
    ) {
        // 3D Physical base shadow block
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 5.dp)
                .background(darkBaseColor.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
        )

        // Top interactive card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(2.dp, accentColor.copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Brush.verticalGradient(bgGradient))
            ) {
                // Specular gloss highlight top arc
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 26.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 9.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.90f),
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        ),
                        maxLines = 2
                    )
                }
            }
        }
    }
}
