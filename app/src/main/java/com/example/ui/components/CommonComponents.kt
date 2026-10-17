package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GradeLevel
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

@Composable
fun KidTopBar(
    activeGrade: GradeLevel,
    coins: Int,
    stars: Int,
    streakDays: Int,
    isSoundOn: Boolean,
    onGradeChange: (GradeLevel) -> Unit,
    onSoundToggle: () -> Unit,
    onStreakClick: () -> Unit,
    onOpenParentReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        color = Color.White,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Grade Switcher Pill (1, 2, 3, 4. Sınıf)
            GradeSwitcherPill(
                activeGrade = activeGrade,
                onGradeChange = onGradeChange
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Streak Counter
                StatChip(
                    icon = "🔥",
                    value = "$streakDays",
                    bgBrush = Brush.horizontalGradient(listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA))),
                    borderColor = Color(0xFFFB923C),
                    onClick = onStreakClick,
                    testTag = "streak_chip"
                )

                // Stars Counter
                StatChip(
                    icon = "⭐",
                    value = "$stars",
                    bgBrush = Brush.horizontalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))),
                    borderColor = Color(0xFFF59E0B),
                    testTag = "stars_chip"
                )

                // Coins Counter
                StatChip(
                    icon = "🪙",
                    value = "$coins",
                    bgBrush = Brush.horizontalGradient(listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))),
                    borderColor = Color(0xFF38BDF8),
                    testTag = "coins_chip"
                )

                // Sound FX Toggle
                IconButton(
                    onClick = onSoundToggle,
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                        .testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isSoundOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Ses Aç/Kapat",
                        tint = if (isSoundOn) BrandBlue else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Dedicated Parent Access Button 👨‍👩‍👧
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3E8FF),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFC084FC)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenParentReport)
                        .testTag("parent_top_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = "👨‍👩‍👧", fontSize = 13.sp)
                        Text(
                            text = "Veli",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF7E22CE),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradeSwitcherPill(
    activeGrade: GradeLevel,
    onGradeChange: (GradeLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color(0xFFF1F5F9), RoundedCornerShape(18.dp))
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        GradeLevel.entries.forEach { grade ->
            val isSelected = activeGrade == grade
            val label = when (grade) {
                GradeLevel.GRADE_1 -> "1.S"
                GradeLevel.GRADE_2 -> "2.S"
                GradeLevel.GRADE_3 -> "3.S"
                GradeLevel.GRADE_4 -> "4.S"
            }
            GradeOptionTab(
                title = label,
                isSelected = isSelected,
                onClick = { onGradeChange(grade) },
                testTag = "grade_${grade.name.lowercase()}_tab"
            )
        }
    }
}

@Composable
private fun GradeOptionTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(BrandBlue, Color(0xFF2563EB)))
    } else {
        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF64748B)
            )
        )
    }
}

@Composable
fun StatChip(
    icon: String,
    value: String,
    bgBrush: Brush,
    borderColor: Color,
    onClick: (() -> Unit)? = null,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 7.dp, vertical = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(text = icon, fontSize = 13.sp)
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
        )
    }
}

@Composable
fun Kid3DButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = BrandBlue,
    shadowColor: Color = Color(0xFF1E40AF),
    textColor: Color = Color.White,
    icon: ImageVector? = null,
    emoji: String? = null,
    enabled: Boolean = true,
    paddingVertical: Dp = 16.dp,
    fontSize: TextUnit = 18.sp,
    testTag: String = "kid_3d_button"
) {
    var isPressed by remember { mutableStateOf(false) }
    val offsetY by animateFloatAsState(
        targetValue = if (isPressed) 5f else 0f,
        animationSpec = spring(),
        label = "btn_offset"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(),
        label = "btn_scale"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .scale(buttonScale)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onClick()
                        }
                    )
                }
            }
    ) {
        // Shadow base layer (3D depth block)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 6.dp)
                .background(if (enabled) shadowColor else Color(0xFFCBD5E1), RoundedCornerShape(22.dp))
        )

        val btnBrush = if (enabled) {
            Brush.verticalGradient(
                listOf(
                    color,
                    color.copy(alpha = 0.95f)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF94A3B8),
                    Color(0xFF94A3B8)
                )
            )
        }

        // Top interactive layer with glossy bevel highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY.dp)
                .background(btnBrush, RoundedCornerShape(22.dp))
                .border(
                    width = 2.dp,
                    color = if (enabled) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 16.dp, vertical = paddingVertical),
            contentAlignment = Alignment.Center
        ) {
            // Subtle top glossy reflection
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (emoji != null) {
                    Text(
                        text = emoji,
                        fontSize = (fontSize.value + 4).sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier
                            .size((fontSize.value + 6).dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        fontSize = fontSize
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StarRatingRow(
    starsEarned: Int,
    maxStars: Int = 3,
    size: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isGold = i <= starsEarned
            Text(
                text = if (isGold) "⭐" else "▫️",
                fontSize = (size.value * 0.85f).sp
            )
        }
    }
}
