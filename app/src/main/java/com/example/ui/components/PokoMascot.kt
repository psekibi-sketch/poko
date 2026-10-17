package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin

enum class PokoMood {
    HAPPY,
    CELEBRATING,
    THINKING,
    TALKING,
    DANCING,
    EXCITED,
    LISTENING
}

/**
 * 3D LEGO STAR WARS MINIFIGURE HERO (LORD VOKO / KYLO POKO / JEDI LUKE) ⚔️⭐
 *
 * Authentic LEGO blocky minifigure geometry with:
 * - Iconic Lego cylindrical head and flared helmet with top Lego stud
 * - Darth Vader / Kylo Ren dark battle helmet & triangular vocoder breather
 * - Trapezoidal Lego torso with detailed chest life-support computer box
 * - Animated billowing Sith / Jedi cape
 * - Lego C-clamp hands gripping an ANIMATED GLOWING LIGHTSABER (Işın Kılıcı)
 * - Pulsating laser plasma aura (Red, Blue, Green, Crossguard) with hum glow
 * - Dynamic lightsaber flourishing & swinging animations on speak and victory
 */
@Composable
fun PokoMascot(
    modifier: Modifier = Modifier,
    size: Dp = 130.dp,
    mood: PokoMood = PokoMood.HAPPY,
    costumeId: String = "costume_default",
    isJetFlying: Boolean = false,
    isWalking: Boolean = false,
    isWaving: Boolean = false,
    speechBubbleText: String? = null,
    speechBubbleSubtitle: String? = null,
    onSpeechClick: (() -> Unit)? = null,
    onMascotClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lego_starwars_anim")

    // Subtle gentle breathing / hover float
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = if (isJetFlying) -8f else -2.5f,
        targetValue = if (isJetFlying) 8f else 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isJetFlying) 850 else 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Lightsaber intense plasma pulse (humming energy beam)
    val saberGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "saber_pulse"
    )

    // Lightsaber swing / combat flourish angle
    val saberFlourishAngle by infiniteTransition.animateFloat(
        initialValue = if (mood == PokoMood.CELEBRATING || mood == PokoMood.DANCING) -18f else -5f,
        targetValue = if (mood == PokoMood.CELEBRATING || mood == PokoMood.DANCING) 28f else 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (mood == PokoMood.CELEBRATING || mood == PokoMood.DANCING) 260 else 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "saber_swing"
    )

    // Flowing Cape flutter in space wind
    val capeWave by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cape_wave"
    )

    // Eye blink animation
    val eyeBlinkScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(160, delayMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    // Walk cycle
    val walkCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (mood == PokoMood.DANCING) 400 else 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "walk"
    )

    val waddleY = if (isWalking || mood == PokoMood.DANCING) (sin(walkCycle * 2 * PI) * 12f).toFloat() else if (mood == PokoMood.THINKING) 12f else 0f
    val waddleZ = if (isWalking || mood == PokoMood.DANCING) (sin(walkCycle * 2 * PI) * 6f).toFloat() else if (mood == PokoMood.THINKING) -5f else 0f

    // Theme identification
    val isKylo = costumeId.contains("kylo", ignoreCase = true)
    val isJedi = costumeId.contains("jedi", ignoreCase = true) || costumeId.contains("luke", ignoreCase = true)
    val isYoda = costumeId.contains("yoda", ignoreCase = true)
    val isTrooper = costumeId.contains("trooper", ignoreCase = true)

    // Lightsaber Core & Aura Colors
    val saberAuraColor = when {
        isKylo -> Color(0xFFFF1744) // Crossguard Crimson Red
        isJedi -> Color(0xFF00B0FF) // Sapphire Blue
        isYoda -> Color(0xFF00E676) // Emerald Green
        isTrooper -> Color(0xFF38BDF8) // Laser Blaster Cyan
        else -> Color(0xFFFF0055) // Classic Darth Vader Sith Red
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Speech Bubble if present
        if (!speechBubbleText.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF818CF8)),
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .widthIn(max = 240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        enabled = onSpeechClick != null,
                        onClick = { onSpeechClick?.invoke() }
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = speechBubbleText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                fontSize = 13.sp
                            )
                        )
                        if (!speechBubbleSubtitle.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = speechBubbleSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4338CA),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    if (onSpeechClick != null) {
                        Spacer(modifier = Modifier.size(6.dp))
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Sesli Dinle",
                            tint = Color(0xFF3B6CF6),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(size)
                .offset(y = floatOffset.dp)
                .graphicsLayer {
                    cameraDistance = 18f
                    rotationY = waddleY
                    rotationZ = waddleZ
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onMascotClick?.invoke() }
                )
                .testTag("poko_mascot_avatar"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val cx = w / 2f
                val cy = h / 2f

                // ----------------------------------------------------
                // 0. RADIANT SITH / JEDI HOLOGRAPHIC AURA & RIM LIGHT ✨
                // ----------------------------------------------------
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            saberAuraColor.copy(alpha = 0.38f * saberGlowPulse),
                            Color(0xFFFF80AB).copy(alpha = 0.15f * saberGlowPulse),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = w * 0.58f
                    ),
                    topLeft = Offset(cx - w * 0.58f, cy - h * 0.58f),
                    size = Size(w * 1.16f, h * 1.16f)
                )

                // ----------------------------------------------------
                // 1. 3D PERSPECTIVE GROUND SHADOW (Starship Deck Hangar)
                // ----------------------------------------------------
                val shadowAlpha = if (isJetFlying) 0.20f else 0.40f
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Black.copy(alpha = shadowAlpha), Color.Transparent),
                        center = Offset(cx, h * 0.95f),
                        radius = w * 0.38f
                    ),
                    topLeft = Offset(cx - w * 0.38f, h * 0.90f),
                    size = Size(w * 0.76f, h * 0.10f)
                )

                // ----------------------------------------------------
                // 2. FLOWING SITH / JEDI CAPE (Pelerin)
                // ----------------------------------------------------
                val capeColor1 = if (isJedi) Color(0xFF5A2E0F) else Color(0xFF1E293B)
                val capeColor2 = if (isJedi) Color(0xFF8D4315) else Color(0xFF312E81)
                val capePath = Path().apply {
                    moveTo(cx - w * 0.24f, cy - h * 0.08f)
                    lineTo(cx + w * 0.24f, cy - h * 0.08f)
                    lineTo(cx + w * 0.38f + capeWave, cy + h * 0.44f)
                    cubicTo(
                        cx + w * 0.15f, cy + h * 0.47f,
                        cx - w * 0.15f, cy + h * 0.43f,
                        cx - w * 0.38f + capeWave * 0.5f, cy + h * 0.44f
                    )
                    close()
                }
                drawPath(
                    capePath,
                    brush = Brush.verticalGradient(listOf(capeColor1, capeColor2))
                )
                // Cape glowing energy rim
                drawPath(
                    capePath,
                    color = if (isJedi) Color(0xFFFDBA74).copy(alpha = 0.45f) else Color(0xFF818CF8).copy(alpha = 0.55f),
                    style = Stroke(width = 1.8.dp.toPx())
                )

                // ----------------------------------------------------
                // 3. LEGO MINIFIGURE LEGS & HIPS
                // ----------------------------------------------------
                val legColor = if (isTrooper) Color(0xFFF1F5F9) else if (isJedi) Color(0xFF44403C) else Color(0xFF27272A)
                val hipY = cy + h * 0.16f
                val legW = w * 0.14f
                val legH = h * 0.22f

                // Hip connector piece
                drawRoundRect(
                    color = legColor,
                    topLeft = Offset(cx - w * 0.18f, hipY),
                    size = Size(w * 0.36f, h * 0.06f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                // Hip metallic contour
                drawRoundRect(
                    color = Color(0xFFA1A1AA).copy(alpha = 0.6f),
                    topLeft = Offset(cx - w * 0.18f, hipY),
                    size = Size(w * 0.36f, h * 0.06f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 1.2.dp.toPx())
                )

                // Left Leg
                val leftLegOffset = if (isWalking) (sin(walkCycle * 2 * PI) * 4.0).toFloat() else 0f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(legColor, Color(0xFF18181B))
                    ),
                    topLeft = Offset(cx - w * 0.17f, hipY + h * 0.04f + leftLegOffset),
                    size = Size(legW, legH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                // Left Leg metallic outline
                drawRoundRect(
                    color = Color(0xFF71717A).copy(alpha = 0.5f),
                    topLeft = Offset(cx - w * 0.17f, hipY + h * 0.04f + leftLegOffset),
                    size = Size(legW, legH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                // Left Foot Toe with silver shine
                drawRoundRect(
                    color = Color(0xFF18181B),
                    topLeft = Offset(cx - w * 0.17f, hipY + h * 0.04f + legH - h * 0.04f + leftLegOffset),
                    size = Size(legW * 1.15f, h * 0.04f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawLine(
                    color = Color(0xFFA1A1AA),
                    start = Offset(cx - w * 0.17f, hipY + h * 0.04f + legH - h * 0.04f + leftLegOffset),
                    end = Offset(cx - w * 0.17f + legW * 1.15f, hipY + h * 0.04f + legH - h * 0.04f + leftLegOffset),
                    strokeWidth = 1.2.dp.toPx()
                )

                // Right Leg
                val rightLegOffset = if (isWalking) (-sin(walkCycle * 2 * PI) * 4.0).toFloat() else 0f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(legColor, Color(0xFF18181B))
                    ),
                    topLeft = Offset(cx + w * 0.03f, hipY + h * 0.04f + rightLegOffset),
                    size = Size(legW, legH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                // Right Leg metallic outline
                drawRoundRect(
                    color = Color(0xFF71717A).copy(alpha = 0.5f),
                    topLeft = Offset(cx + w * 0.03f, hipY + h * 0.04f + rightLegOffset),
                    size = Size(legW, legH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                // Right Foot Toe with silver shine
                drawRoundRect(
                    color = Color(0xFF18181B),
                    topLeft = Offset(cx + w * 0.03f, hipY + h * 0.04f + legH - h * 0.04f + rightLegOffset),
                    size = Size(legW * 1.15f, h * 0.04f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawLine(
                    color = Color(0xFFA1A1AA),
                    start = Offset(cx + w * 0.03f, hipY + h * 0.04f + legH - h * 0.04f + rightLegOffset),
                    end = Offset(cx + w * 0.03f + legW * 1.15f, hipY + h * 0.04f + legH - h * 0.04f + rightLegOffset),
                    strokeWidth = 1.2.dp.toPx()
                )

                // ----------------------------------------------------
                // 4. LEGO TRAPEZOID TORSO (Gövde)
                // ----------------------------------------------------
                val torsoTopY = cy - h * 0.12f
                val torsoBottomY = cy + h * 0.17f
                val torsoTopW = w * 0.36f
                val torsoBottomW = w * 0.42f

                val torsoColor = if (isTrooper) Color(0xFFFFFFFF) else if (isJedi) Color(0xFF573E29) else Color(0xFF27272A)
                val torsoPath = Path().apply {
                    moveTo(cx - torsoTopW / 2f, torsoTopY)
                    lineTo(cx + torsoTopW / 2f, torsoTopY)
                    lineTo(cx + torsoBottomW / 2f, torsoBottomY)
                    lineTo(cx - torsoBottomW / 2f, torsoBottomY)
                    close()
                }
                drawPath(
                    torsoPath,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF52525B), torsoColor, Color(0xFF18181B))
                    )
                )
                // Torso metallic gloss outline & silver shoulder pauldron stripes
                drawPath(
                    torsoPath,
                    color = Color(0xFFA1A1AA).copy(alpha = 0.55f),
                    style = Stroke(width = 2.dp.toPx())
                )
                // Silver Pauldron shoulder accents
                drawLine(Color(0xFFE4E4E7), Offset(cx - torsoTopW / 2f + 2.dp.toPx(), torsoTopY + 2.dp.toPx()), Offset(cx - torsoTopW / 4f, torsoTopY + 2.dp.toPx()), strokeWidth = 2.5.dp.toPx())
                drawLine(Color(0xFFE4E4E7), Offset(cx + torsoTopW / 4f, torsoTopY + 2.dp.toPx()), Offset(cx + torsoTopW / 2f - 2.dp.toPx(), torsoTopY + 2.dp.toPx()), strokeWidth = 2.5.dp.toPx())

                // Darth Vader Chest Computer Box / Life Support Panel (Pulsating LEDs & Silver Frame)
                if (!isJedi && !isTrooper) {
                    val boxW = w * 0.19f
                    val boxH = h * 0.11f
                    val boxX = cx - boxW / 2f
                    val boxY = torsoTopY + h * 0.055f

                    // Chrome Silver Border Box
                    drawRoundRect(
                        color = Color(0xFFE4E4E7),
                        topLeft = Offset(boxX, boxY),
                        size = Size(boxW, boxH),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Inner dark panel
                    drawRoundRect(
                        color = Color(0xFF18181B),
                        topLeft = Offset(boxX + 1.5.dp.toPx(), boxY + 1.5.dp.toPx()),
                        size = Size(boxW - 3.dp.toPx(), boxH - 3.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // Glowing Pulsing LED Indicators (Red, Cyan, Neon Green)
                    // Red LED with bloom
                    val ledRedCenter = Offset(boxX + boxW * 0.25f, boxY + boxH * 0.35f)
                    drawCircle(Color(0xFFFF1744).copy(alpha = 0.55f * saberGlowPulse), radius = 5.dp.toPx(), center = ledRedCenter)
                    drawCircle(Color(0xFFFF1744), radius = 2.8.dp.toPx(), center = ledRedCenter)
                    drawCircle(Color.White, radius = 1.dp.toPx(), center = ledRedCenter)

                    // Cyan LED with bloom
                    val ledCyanCenter = Offset(boxX + boxW * 0.50f, boxY + boxH * 0.35f)
                    drawCircle(Color(0xFF38BDF8).copy(alpha = 0.55f * saberGlowPulse), radius = 5.dp.toPx(), center = ledCyanCenter)
                    drawCircle(Color(0xFF38BDF8), radius = 2.8.dp.toPx(), center = ledCyanCenter)
                    drawCircle(Color.White, radius = 1.dp.toPx(), center = ledCyanCenter)

                    // Green LED with bloom
                    val ledGreenCenter = Offset(boxX + boxW * 0.75f, boxY + boxH * 0.35f)
                    drawCircle(Color(0xFF22C55E).copy(alpha = 0.55f * saberGlowPulse), radius = 5.dp.toPx(), center = ledGreenCenter)
                    drawCircle(Color(0xFF22C55E), radius = 2.8.dp.toPx(), center = ledGreenCenter)
                    drawCircle(Color.White, radius = 1.dp.toPx(), center = ledGreenCenter)

                    // Silver vertical grille lines
                    drawLine(Color(0xFFE4E4E7), Offset(boxX + boxW * 0.28f, boxY + boxH * 0.72f), Offset(boxX + boxW * 0.44f, boxY + boxH * 0.72f), strokeWidth = 2.2.dp.toPx())
                    drawLine(Color(0xFFFF1744), Offset(boxX + boxW * 0.56f, boxY + boxH * 0.72f), Offset(boxX + boxW * 0.72f, boxY + boxH * 0.72f), strokeWidth = 2.2.dp.toPx())

                    // Silver Imperial Belt Buckle with Red/Green buttons
                    drawRoundRect(
                        color = Color(0xFFE4E4E7),
                        topLeft = Offset(cx - w * 0.08f, torsoBottomY - h * 0.035f),
                        size = Size(w * 0.16f, h * 0.03f),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    drawCircle(Color(0xFFFF1744), radius = 1.8.dp.toPx(), center = Offset(cx - w * 0.04f, torsoBottomY - h * 0.02f))
                    drawCircle(Color(0xFF22C55E), radius = 1.8.dp.toPx(), center = Offset(cx + w * 0.04f, torsoBottomY - h * 0.02f))
                }

                // ----------------------------------------------------
                // 5. LEGO ARMS & C-SHAPED HANDS
                // ----------------------------------------------------
                val armColor = torsoColor
                val handColor = Color(0xFFFACC15) // Classic yellow Lego hand (or black glove for Vader)
                val finalHandColor = if (isTrooper) Color.Black else if (isJedi) handColor else Color(0xFF27272A)

                // Left Arm (bent naturally at rest)
                val leftArmPath = Path().apply {
                    moveTo(cx - torsoTopW / 2f, torsoTopY + h * 0.02f)
                    lineTo(cx - w * 0.26f, cy + h * 0.04f)
                    lineTo(cx - w * 0.20f, cy + h * 0.12f)
                    lineTo(cx - torsoTopW / 2f + w * 0.04f, torsoTopY + h * 0.06f)
                    close()
                }
                drawPath(leftArmPath, armColor)

                // Left C-Hand
                drawArc(
                    color = finalHandColor,
                    startAngle = 45f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(cx - w * 0.25f, cy + h * 0.11f),
                    size = Size(w * 0.07f, w * 0.07f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Right Arm (Raising and gripping the Lightsaber!)
                val rightArmPath = Path().apply {
                    moveTo(cx + torsoTopW / 2f, torsoTopY + h * 0.02f)
                    lineTo(cx + w * 0.28f, cy - h * 0.02f)
                    lineTo(cx + w * 0.24f, cy + h * 0.08f)
                    lineTo(cx + torsoTopW / 2f - w * 0.04f, torsoTopY + h * 0.06f)
                    close()
                }
                drawPath(rightArmPath, armColor)

                // Right C-Hand (Grip at handX, handY)
                val handX = cx + w * 0.26f
                val handY = cy + h * 0.04f
                drawArc(
                    color = finalHandColor,
                    startAngle = -45f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(handX - w * 0.035f, handY - w * 0.035f),
                    size = Size(w * 0.07f, w * 0.07f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // ----------------------------------------------------
                // 6. 3D GLOWING ANIMATED LIGHTSABER (IŞIN KILICI) ⚔️✨
                // ----------------------------------------------------
                // Chrome Metallic Hilt
                val hiltW = w * 0.035f
                val hiltH = h * 0.14f
                val hiltX = handX - hiltW / 2f
                val hiltY = handY - h * 0.04f

                // Silver Hilt body with black grips
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF94A3B8), Color(0xFFF1F5F9), Color(0xFF64748B))
                    ),
                    topLeft = Offset(hiltX, hiltY),
                    size = Size(hiltW, hiltH),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                // Black grip stripes
                drawLine(Color.Black, Offset(hiltX, hiltY + hiltH * 0.35f), Offset(hiltX + hiltW, hiltY + hiltH * 0.35f), strokeWidth = 1.5.dp.toPx())
                drawLine(Color.Black, Offset(hiltX, hiltY + hiltH * 0.55f), Offset(hiltX + hiltW, hiltY + hiltH * 0.55f), strokeWidth = 1.5.dp.toPx())
                // Red ignition button on hilt
                drawCircle(Color(0xFFEF4444), radius = 1.5.dp.toPx(), center = Offset(hiltX + hiltW * 0.5f, hiltY + hiltH * 0.25f))

                // Pulsating Laser Blade Extending Upwards!
                val bladeLen = h * 0.54f
                val bladeStartX = hiltX + hiltW / 2f
                val bladeStartY = hiltY
                val bladeEndX = bladeStartX + (saberFlourishAngle * 0.35f)
                val bladeEndY = bladeStartY - bladeLen

                // Outer Laser Plasma Glow Halo (High-intensity Star Wars Plasma Beam)
                drawLine(
                    color = saberAuraColor.copy(alpha = 0.45f * saberGlowPulse),
                    start = Offset(bladeStartX, bladeStartY),
                    end = Offset(bladeEndX, bladeEndY),
                    strokeWidth = 22.dp.toPx() * saberGlowPulse,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = saberAuraColor.copy(alpha = 0.85f * saberGlowPulse),
                    start = Offset(bladeStartX, bladeStartY),
                    end = Offset(bladeEndX, bladeEndY),
                    strokeWidth = 10.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Intense White Plasma Core
                drawLine(
                    color = Color.White,
                    start = Offset(bladeStartX, bladeStartY),
                    end = Offset(bladeEndX, bladeEndY),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Kylo Ren Crossguard Blades (Yan koruma lazerleri)
                if (isKylo) {
                    val guardY = hiltY + 2.dp.toPx()
                    // Left crossguard
                    drawLine(
                        color = saberAuraColor.copy(alpha = 0.8f),
                        start = Offset(bladeStartX, guardY),
                        end = Offset(bladeStartX - w * 0.09f, guardY),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(bladeStartX, guardY),
                        end = Offset(bladeStartX - w * 0.09f, guardY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Right crossguard
                    drawLine(
                        color = saberAuraColor.copy(alpha = 0.8f),
                        start = Offset(bladeStartX, guardY),
                        end = Offset(bladeStartX + w * 0.09f, guardY),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(bladeStartX, guardY),
                        end = Offset(bladeStartX + w * 0.09f, guardY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // ----------------------------------------------------
                // 7. LEGO MINIFIGURE HEAD & HELMET (DARTH VADER / KYLO REN)
                // ----------------------------------------------------
                val headW = w * 0.28f
                val headH = h * 0.24f
                val headY = cy - h * 0.32f

                // Iconic LEGO Top Stud (Lego miğfer üstündeki daire pim)
                val studW = w * 0.10f
                val studH = h * 0.04f
                drawRoundRect(
                    color = Color(0xFF3F3F46),
                    topLeft = Offset(cx - studW / 2f, headY - studH),
                    size = Size(studW, studH),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFFA1A1AA),
                    topLeft = Offset(cx - studW / 2f, headY - studH),
                    size = Size(studW, studH),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Darth Vader Flared Helmet Base (Miğferin omuzlara doğru genişleyen kavisli yapısı)
                val helmetPath = Path().apply {
                    moveTo(cx - headW * 0.40f, headY)
                    cubicTo(cx - headW * 0.48f, headY - h * 0.04f, cx + headW * 0.48f, headY - h * 0.04f, cx + headW * 0.40f, headY)
                    // Flaring down outward to shoulders
                    lineTo(cx + headW * 0.65f, headY + headH * 0.85f)
                    lineTo(cx + headW * 0.40f, headY + headH * 0.95f)
                    lineTo(cx, headY + headH * 0.88f)
                    lineTo(cx - headW * 0.40f, headY + headH * 0.95f)
                    lineTo(cx - headW * 0.65f, headY + headH * 0.85f)
                    close()
                }
                drawPath(
                    helmetPath,
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF71717A), Color(0xFF3F3F46), Color(0xFF27272A)),
                        center = Offset(cx - headW * 0.15f, headY + headH * 0.2f),
                        radius = headW * 0.70f
                    )
                )

                // Shining Silver Rim Stroke around entire flared helmet (Makes helmet stand out crisply!)
                drawPath(
                    helmetPath,
                    color = Color(0xFFE4E4E7).copy(alpha = 0.85f),
                    style = Stroke(width = 2.2.dp.toPx())
                )

                // Specular Gloss Crest Ridge (Parlak miğfer orta çizgisi)
                drawLine(
                    color = Color.White,
                    start = Offset(cx, headY - h * 0.02f),
                    end = Offset(cx, headY + headH * 0.38f),
                    strokeWidth = 2.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Kylo Ren Silver Brow Arcs or Vader Brow
                if (isKylo) {
                    val browY = headY + headH * 0.28f
                    drawLine(Color(0xFFE4E4E7), Offset(cx - headW * 0.35f, browY), Offset(cx + headW * 0.35f, browY), strokeWidth = 2.8.dp.toPx())
                    drawLine(Color(0xFFCBD5E1), Offset(cx - headW * 0.30f, browY + 3.dp.toPx()), Offset(cx + headW * 0.30f, browY + 3.dp.toPx()), strokeWidth = 1.8.dp.toPx())
                }

                // Eye Visor / Lenses with Blinking Animation (Glowing Ruby Sith Eyes)
                val eyeY = headY + headH * 0.34f
                val eyeW = headW * 0.24f
                val eyeH = (headH * 0.14f) * eyeBlinkScale

                val eyeGlowColor = if (isJedi) Color(0xFF38BDF8) else Color(0xFFFF1744)

                // Left Eye Lens with glow and glint
                drawOval(
                    color = Color.Black,
                    topLeft = Offset(cx - headW * 0.32f, eyeY),
                    size = Size(eyeW, eyeH)
                )
                drawOval(
                    color = eyeGlowColor.copy(alpha = 0.50f * saberGlowPulse),
                    topLeft = Offset(cx - headW * 0.34f, eyeY - 1.dp.toPx()),
                    size = Size(eyeW * 1.15f, eyeH * 1.15f)
                )
                drawOval(
                    color = eyeGlowColor,
                    topLeft = Offset(cx - headW * 0.30f, eyeY + 1.dp.toPx()),
                    size = Size(eyeW * 0.7f, eyeH * 0.7f)
                )
                drawCircle(Color.White, radius = 1.5.dp.toPx(), center = Offset(cx - headW * 0.24f, eyeY + eyeH * 0.35f))

                // Right Eye Lens with glow and glint
                drawOval(
                    color = Color.Black,
                    topLeft = Offset(cx + headW * 0.08f, eyeY),
                    size = Size(eyeW, eyeH)
                )
                drawOval(
                    color = eyeGlowColor.copy(alpha = 0.50f * saberGlowPulse),
                    topLeft = Offset(cx + headW * 0.06f, eyeY - 1.dp.toPx()),
                    size = Size(eyeW * 1.15f, eyeH * 1.15f)
                )
                drawOval(
                    color = eyeGlowColor,
                    topLeft = Offset(cx + headW * 0.10f, eyeY + 1.dp.toPx()),
                    size = Size(eyeW * 0.7f, eyeH * 0.7f)
                )
                drawCircle(Color.White, radius = 1.5.dp.toPx(), center = Offset(cx + headW * 0.16f, eyeY + eyeH * 0.35f))

                // Darth Vader Triangular Breathing Mask / Vocoder Grille (Silver Frame & Metallic Grille)
                val vocoderTopY = headY + headH * 0.52f
                val vocoderBottomY = headY + headH * 0.88f
                val vocoderPath = Path().apply {
                    moveTo(cx, vocoderTopY)
                    lineTo(cx + headW * 0.20f, vocoderBottomY)
                    lineTo(cx - headW * 0.20f, vocoderBottomY)
                    close()
                }
                drawPath(vocoderPath, Color(0xFF18181B))
                drawPath(vocoderPath, Color(0xFFF4F4F5), style = Stroke(width = 2.dp.toPx()))

                // Grille vent slots (Gümüş havalandırma kanalları)
                drawLine(Color(0xFFE4E4E7), Offset(cx - headW * 0.08f, vocoderTopY + headH * 0.14f), Offset(cx + headW * 0.08f, vocoderTopY + headH * 0.14f), strokeWidth = 2.dp.toPx())
                drawLine(Color(0xFFE4E4E7), Offset(cx - headW * 0.12f, vocoderTopY + headH * 0.22f), Offset(cx + headW * 0.12f, vocoderTopY + headH * 0.22f), strokeWidth = 2.dp.toPx())

                // Vocoder Silver Tusks (Vader'ın iki yanındaki parlak gümüş silindirler)
                drawCircle(Color(0xFFFFFFFF), radius = 2.5.dp.toPx(), center = Offset(cx - headW * 0.18f, vocoderBottomY))
                drawCircle(Color(0xFFFFFFFF), radius = 2.5.dp.toPx(), center = Offset(cx + headW * 0.18f, vocoderBottomY))
            }
        }
    }
}

/**
 * GALACTIC ASTRO-DROID COMPANION (R2-STYLE CHEERING DROID) 🤖✨
 * Beeps, rolls, and cheers happily for the child during exercises!
 */
@Composable
fun CompanionMascot(
    size: Dp = 72.dp,
    isCheering: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "droid_companion_anim")
    val rollTilt by infiniteTransition.animateFloat(
        initialValue = if (isCheering) -12f else -3f,
        targetValue = if (isCheering) 12f else 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCheering) 250 else 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "droid_tilt"
    )

    val eyeBlink by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "droid_eye"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = rollTilt
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            // Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(cx, h * 0.92f),
                    radius = w * 0.38f
                ),
                topLeft = Offset(cx - w * 0.38f, h * 0.88f),
                size = Size(w * 0.76f, h * 0.10f)
            )

            // Droid Body (White Cylinder)
            val bodyW = w * 0.54f
            val bodyH = h * 0.44f
            val bodyTop = cy - h * 0.05f
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFE2E8F0), Color.White, Color(0xFFCBD5E1))
                ),
                topLeft = Offset(cx - bodyW / 2f, bodyTop),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // Blue Tech Panels on body
            drawRect(Color(0xFF0284C7), Offset(cx - bodyW * 0.32f, bodyTop + bodyH * 0.2f), Size(bodyW * 0.24f, bodyH * 0.35f))
            drawRect(Color(0xFF0284C7), Offset(cx + bodyW * 0.08f, bodyTop + bodyH * 0.2f), Size(bodyW * 0.24f, bodyH * 0.35f))
            drawCircle(Color(0xFFEF4444), radius = 2.dp.toPx(), center = Offset(cx - bodyW * 0.1f, bodyTop + bodyH * 0.7f))
            drawCircle(Color(0xFF22C55E), radius = 2.dp.toPx(), center = Offset(cx + bodyW * 0.1f, bodyTop + bodyH * 0.7f))

            // Droid Dome Head (Silver & Blue hemisphere)
            val domeW = w * 0.52f
            val domeH = h * 0.30f
            val domeTop = cy - h * 0.32f
            val domePath = Path().apply {
                moveTo(cx - domeW / 2f, domeTop + domeH)
                cubicTo(
                    cx - domeW / 2f, domeTop,
                    cx + domeW / 2f, domeTop,
                    cx + domeW / 2f, domeTop + domeH
                )
                close()
            }
            drawPath(
                domePath,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF94A3B8), Color(0xFFF1F5F9), Color(0xFF64748B))
                )
            )

            // Blue horizontal dome stripe
            drawLine(
                color = Color(0xFF0284C7),
                start = Offset(cx - domeW * 0.44f, domeTop + domeH * 0.65f),
                end = Offset(cx + domeW * 0.44f, domeTop + domeH * 0.65f),
                strokeWidth = 3.dp.toPx()
            )

            // Primary Red/Cyan Eye Sensor (Blinks)
            drawCircle(Color.Black, radius = 5.dp.toPx(), center = Offset(cx, domeTop + domeH * 0.45f))
            drawCircle(Color(0xFF00F0FF).copy(alpha = eyeBlink), radius = 3.5.dp.toPx(), center = Offset(cx, domeTop + domeH * 0.45f))

            // Two Left and Right Droid Legs with Wheels
            val legColor = Color(0xFFCBD5E1)
            // Left leg
            drawRoundRect(legColor, Offset(cx - bodyW / 2f - w * 0.08f, cy - h * 0.05f), Size(w * 0.08f, h * 0.42f), CornerRadius(3.dp.toPx(), 3.dp.toPx()))
            // Right leg
            drawRoundRect(legColor, Offset(cx + bodyW / 2f, cy - h * 0.05f), Size(w * 0.08f, h * 0.42f), CornerRadius(3.dp.toPx(), 3.dp.toPx()))
        }
    }
}
