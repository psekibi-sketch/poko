package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.ExtraGameFeaturesData
import com.example.data.model.LeagueTier
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange

@Composable
fun GalaxyLeagueScreen(
    userLeaguePoints: Int,
    userCoins: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTier = remember(userLeaguePoints) {
        when {
            userLeaguePoints >= 800 -> LeagueTier.LEGEND
            userLeaguePoints >= 500 -> LeagueTier.DIAMOND
            userLeaguePoints >= 300 -> LeagueTier.GOLD
            userLeaguePoints >= 150 -> LeagueTier.SILVER
            else -> LeagueTier.BRONZE
        }
    }

    val leaderboardUsers = remember(userLeaguePoints) {
        ExtraGameFeaturesData.generateLeaderboardUsers(userLeaguePoints)
    }

    val nextTierPoints = when (currentTier) {
        LeagueTier.BRONZE -> 150
        LeagueTier.SILVER -> 300
        LeagueTier.GOLD -> 500
        LeagueTier.DIAMOND -> 800
        LeagueTier.LEGEND -> 1000
    }

    val progressFraction = (userLeaguePoints.toFloat() / nextTierPoints.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF172554))
                )
            )
            .testTag("galaxy_league_screen"),
        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                }

                Text(
                    text = "🏆 Galaksi Şampiyonlar Ligi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.5.dp, SunGold)
                ) {
                    Text(
                        text = "⚡ $userLeaguePoints LP",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Active League Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(2.dp, Color(currentTier.badgeColorHex)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = currentTier.emoji, fontSize = 52.sp)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentTier.titleTr,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(currentTier.badgeColorHex)
                        )
                    )

                    Text(
                        text = "Haftalık Turnuva Sıralaması",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8)),
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    // League Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Üst Lig İlerlemesi", color = Color(0xFFE2E8F0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$userLeaguePoints / $nextTierPoints LP", color = Color(0xFFFDE047), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF00E676),
                        trackColor = Color(0xFF334155)
                    )
                }
            }
        }

        // Promotion Zone Info
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF10B981).copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color(0xFF34D399)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🚀", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "İlk 3 sıra haftanın sonunda Üst Lige terfi eder ve 200 Altın Kupa ödülü kazanır!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFA7F3D0),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Leaderboard List
        item {
            Text(
                text = "📊 Haftalık Lig Sıralaması",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )
        }

        items(leaderboardUsers) { user ->
            LeaderboardUserCard(user = user)
        }
    }
}

@Composable
private fun LeaderboardUserCard(user: com.example.data.model.LeaderboardUser) {
    val isTop3 = user.rank <= 3
    val rankBadgeColor = when (user.rank) {
        1 -> Color(0xFFF59E0B)
        2 -> Color(0xFF94A3B8)
        3 -> Color(0xFFB45309)
        else -> Color(0xFF475569)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isCurrentUser) Color(0xFF2563EB).copy(alpha = 0.85f) else Color(0xFF1E293B)
        ),
        border = BorderStroke(
            if (user.isCurrentUser) 2.dp else 1.dp,
            if (user.isCurrentUser) Color(0xFFFDE047) else Color(0xFF334155)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (user.isCurrentUser) 6.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank number
                Surface(
                    shape = CircleShape,
                    color = rankBadgeColor,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "#${user.rank}",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(text = user.avatarEmoji, fontSize = 26.sp)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (user.isCurrentUser) Color(0xFFFDE047) else Color.White
                        )
                    )
                    Text(
                        text = if (isTop3) "🚀 Terfi Bölgesi" else "Güvenli Bölge",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isTop3) Color(0xFF34D399) else Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A)
            ) {
                Text(
                    text = "${user.points} LP",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF67E8F9)
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
