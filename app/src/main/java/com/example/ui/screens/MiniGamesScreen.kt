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
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.AudioService
import com.example.audio.SpeechEvaluation
import com.example.audio.SpeechRecognitionService
import com.example.audio.SpeechState
import com.example.data.curriculum.CurriculumData
import com.example.data.model.WordItem
import com.example.ui.components.Kid3DButton
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPink
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.rotate

enum class ActiveMiniGame {
    NONE,
    DAYS_OF_WEEK_ADVENTURE,
    SUPER_MARIO_RUN,
    MINECRAFT_PIXEL_CRAFT,
    SONIC_SPEED_DASH,
    DETECTIVE_SCAVENGER,
    STICKER_ALBUM,
    DANCE_FREEZE_PARTY,
    BEDTIME_STORIES,
    KARAOKE_STAGE,
    POKO_TOWN,
    INTERACTIVE_STORY,
    MAGIC_COLORING,
    TWO_PLAYER_DUEL,
    SPACE_DINO,
    WORD_GARDEN,
    WORD_TRAIN,
    VOICE_CHAMPION,
    BALLOON_RUSH,
    MEMORY_CARDS,
    SPEED_MATCH
}

@Composable
fun MiniGamesScreen(
    speechService: SpeechRecognitionService? = null,
    audioService: AudioService? = null,
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayPopFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val effectiveAudioService = remember(audioService) {
        audioService ?: AudioService(context)
    }
    val effectiveSpeechService = remember(speechService) {
        speechService ?: SpeechRecognitionService(context)
    }

    var activeGame by remember { mutableStateOf(ActiveMiniGame.NONE) }

    when (activeGame) {
        ActiveMiniGame.NONE -> {
            MiniGamesHub(
                onSelectGame = { activeGame = it },
                modifier = modifier
            )
        }
        ActiveMiniGame.DAYS_OF_WEEK_ADVENTURE -> {
            DaysOfWeekGameScreen(
                audioService = effectiveAudioService,
                speechService = effectiveSpeechService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.SUPER_MARIO_RUN -> {
            SuperMarioWordRunGame(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.MINECRAFT_PIXEL_CRAFT -> {
            MinecraftPixelCraftGameScreen(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.SONIC_SPEED_DASH -> {
            SonicSpeedDashGameScreen(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.DETECTIVE_SCAVENGER -> {
            DetectiveScavengerHuntScreen(
                audioService = effectiveAudioService,
                speechService = effectiveSpeechService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.STICKER_ALBUM -> {
            StickerAlbumScreen(
                audioService = effectiveAudioService,
                userCoins = 150,
                onRewardCoins = onRewardCoins,
                onSpendCoins = { true },
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.DANCE_FREEZE_PARTY -> {
            DanceFreezePartyScreen(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.BEDTIME_STORIES -> {
            BedtimeStoriesScreen(
                audioService = effectiveAudioService,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.KARAOKE_STAGE -> {
            PokoKaraokeScreen(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.POKO_TOWN -> {
            PokoTownScreen(
                audioService = effectiveAudioService,
                userCoins = 100,
                onBuyBuilding = { onRewardCoins(-it) },
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.INTERACTIVE_STORY -> {
            InteractiveStoryScreen(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.MAGIC_COLORING -> {
            MagicColoringScreen(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.TWO_PLAYER_DUEL -> {
            TwoPlayerDuelScreen(
                audioService = effectiveAudioService,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.SPACE_DINO -> {
            SpaceDinoGame(
                audioService = effectiveAudioService,
                onSpeakWord = onSpeakWord,
                onPlayCorrectFx = onPlayCorrectFx,
                onPlayWrongFx = onPlayWrongFx,
                onPlayFanfareFx = onPlayFanfareFx,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.WORD_GARDEN -> {
            WordGardenGame(
                audioService = effectiveAudioService,
                onSpeakWord = onSpeakWord,
                onPlayCorrectFx = onPlayCorrectFx,
                onPlayWrongFx = onPlayWrongFx,
                onPlayFanfareFx = onPlayFanfareFx,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.WORD_TRAIN -> {
            WordTrainGame(
                audioService = effectiveAudioService,
                onSpeakWord = onSpeakWord,
                onPlayCorrectFx = onPlayCorrectFx,
                onPlayWrongFx = onPlayWrongFx,
                onPlayFanfareFx = onPlayFanfareFx,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.VOICE_CHAMPION -> {
            VoiceChampionGame(
                speechService = effectiveSpeechService,
                audioService = effectiveAudioService,
                onSpeakWord = onSpeakWord,
                onPlayCorrectFx = onPlayCorrectFx,
                onPlayWrongFx = onPlayWrongFx,
                onPlayFanfareFx = onPlayFanfareFx,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.BALLOON_RUSH -> {
            BalloonRushGame(
                onSpeakWord = onSpeakWord,
                onPlayPopFx = onPlayPopFx,
                onPlayCorrectFx = onPlayCorrectFx,
                onPlayWrongFx = onPlayWrongFx,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.MEMORY_CARDS -> {
            MemoryCardGame(
                onSpeakWord = onSpeakWord,
                onPlayCorrectFx = onPlayCorrectFx,
                onPlayWrongFx = onPlayWrongFx,
                onPlayFanfareFx = onPlayFanfareFx,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
        ActiveMiniGame.SPEED_MATCH -> {
            SpeedMatchGame(
                onSpeakWord = onSpeakWord,
                onPlayCorrectFx = onPlayCorrectFx,
                onPlayWrongFx = onPlayWrongFx,
                onRewardCoins = onRewardCoins,
                onBack = { activeGame = ActiveMiniGame.NONE }
            )
        }
    }
}

@Composable
private fun MiniGamesHub(
    onSelectGame: (ActiveMiniGame) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FC)),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PlayfulPurple),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Oyun Alanı 🎮",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Eğlenceli mini oyunlarla İngilizce pratik yap ve ekstra altın kazan!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Text(text = "🕹️", fontSize = 48.sp)
                }
            }
        }

        // POPULAR HERO & THEMED GAMES (MARIO, MINECRAFT, SONIC, DETECTIVE, STICKER, DANCE)
        item {
            MiniGameCard(
                title = "Haftanın Günleri: Anlatım & Sınav 🗓️⭐",
                description = "Öğretmen Özel Ödevi! Önce 7 günü şarkılar ve hikayelerle öğren, ardından tren ve test sınavına gir!",
                rewardText = "+100 Altın & Karne",
                emoji = "🗓️",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF7C3AED))),
                onClick = { onSelectGame(ActiveMiniGame.DAYS_OF_WEEK_ADVENTURE) },
                testTag = "game_days_of_week_adventure"
            )
        }

        item {
            MiniGameCard(
                title = "Super Mario Kelime Koşusu 🍄⭐",
                description = "Mario ile zıpla, altın kutuları kır ve doğru İngilizce kelimeleri yakalayarak hız rekoru kır!",
                rewardText = "+60 Altın",
                emoji = "🍄",
                bgBrush = Brush.linearGradient(listOf(Color(0xFFDC2626), Color(0xFFF59E0B))),
                onClick = { onSelectGame(ActiveMiniGame.SUPER_MARIO_RUN) },
                testTag = "game_super_mario_run"
            )
        }

        item {
            MiniGameCard(
                title = "Minecraft Piksel Madencisi ⛏️💎",
                description = "Kelimeleri bilerek Elmas, Zümrüt ve Kızıltaş madeni kaz; kazandığın bloklarla kendi piksel şatonu yap!",
                rewardText = "+50 Altın",
                emoji = "⛏️",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF0D9488), Color(0xFF047857))),
                onClick = { onSelectGame(ActiveMiniGame.MINECRAFT_PIXEL_CRAFT) },
                testTag = "game_minecraft_craft"
            )
        }

        item {
            MiniGameCard(
                title = "Sonic Süper Hız Halkası 🦔💍",
                description = "Sonic ile dönerken doğru İngilizce altın halkalardan geç ve hızını 300 KM/H'ye çıkar!",
                rewardText = "+45 Altın",
                emoji = "🦔",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF2563EB))),
                onClick = { onSelectGame(ActiveMiniGame.SONIC_SPEED_DASH) },
                testTag = "game_sonic_dash"
            )
        }

        item {
            MiniGameCard(
                title = "Poko Dedektif Nesne Avı 🕵️‍♂️🔍",
                description = "Poko'nun odanda aramanı istediği gerçek nesneleri bul, sesli söyle ve Dedektif Madalyası kazan!",
                rewardText = "+50 Altın",
                emoji = "🕵️‍♂️",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF4338CA), Color(0xFF312E81))),
                onClick = { onSelectGame(ActiveMiniGame.DETECTIVE_SCAVENGER) },
                testTag = "game_detective_hunt"
            )
        }

        item {
            MiniGameCard(
                title = "Holografik Çıkartma Albümü 📖✨",
                description = "Sürpriz paketler aç, Star Wars, Mario ve Sevimli Hayvanlar çıkartmalarını albümüne yapıştır!",
                rewardText = "Koleksiyon",
                emoji = "📖",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF7E22CE), Color(0xFFBE185D))),
                onClick = { onSelectGame(ActiveMiniGame.STICKER_ALBUM) },
                testTag = "game_sticker_album"
            )
        }

        item {
            MiniGameCard(
                title = "Poko Dans & Freeze Partisi 🕺🎵",
                description = "Müzik eşliğinde zıpla, alkışla ve 'FREEZE' dendiğinde heykel gibi donarak ritmi yakala!",
                rewardText = "+40 Altın",
                emoji = "🕺",
                bgBrush = Brush.linearGradient(listOf(Color(0xFFDB2777), Color(0xFFF43F5E))),
                onClick = { onSelectGame(ActiveMiniGame.DANCE_FREEZE_PARTY) },
                testTag = "game_dance_party"
            )
        }

        item {
            MiniGameCard(
                title = "Poko Gece Uyku Masalları 🌙⭐",
                description = "Yatmadan önce huzurlu melodiler eşliğinde İngilizce ve Türkçe tatlı uyku hikayeleri dinle.",
                rewardText = "Huzurlu Uyku",
                emoji = "🌙",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))),
                onClick = { onSelectGame(ActiveMiniGame.BEDTIME_STORIES) },
                testTag = "game_bedtime_stories"
            )
        }

        item {
            MiniGameCard(
                title = "2 Kişilik Canlı Kelime Düellosu ⚔️",
                description = "Arkadaşınla veya kardeşinle aynı ekranda karşılıklı yarış, en hızlı doğru basan kazansın!",
                rewardText = "+50 Altın",
                emoji = "⚔️",
                bgBrush = Brush.linearGradient(listOf(Color(0xFFDC2626), Color(0xFF2563EB))),
                onClick = { onSelectGame(ActiveMiniGame.TWO_PLAYER_DUEL) },
                testTag = "game_two_player_duel"
            )
        }

        item {
            MiniGameCard(
                title = "Sihirli Kelime Boyama Kitabı 🎨",
                description = "İngilizce renk komutlarını dinle, sevimli resimleri rengarenk boya ve tablonu tamamla!",
                rewardText = "+40 Altın",
                emoji = "🎨",
                bgBrush = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFF10B981))),
                onClick = { onSelectGame(ActiveMiniGame.MAGIC_COLORING) },
                testTag = "game_magic_coloring"
            )
        }

        item {
            MiniGameCard(
                title = "Poko Karaoke & Şarkı Kulübü 🎤",
                description = "İngilizce şarkılar söyle, dans eden maskotla ritim tut ve telaffuz puanı topla!",
                rewardText = "+50 Altın",
                emoji = "🎤",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7))),
                onClick = { onSelectGame(ActiveMiniGame.KARAOKE_STAGE) },
                testTag = "game_karaoke_stage"
            )
        }

        item {
            MiniGameCard(
                title = "Poko Kasabası İnşaatı 🏰",
                description = "Kelime öğrendikçe açılan binalarla kendi 3D İngilizce kasabanı kur ve geliştir!",
                rewardText = "Şehir Kurucu",
                emoji = "🏰",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0D9488))),
                onClick = { onSelectGame(ActiveMiniGame.POKO_TOWN) },
                testTag = "game_poko_town"
            )
        }

        item {
            MiniGameCard(
                title = "İnteraktif Macera Hikayesi 📖",
                description = "Kendi tercihlerini seçerek hikayenin sonunu belirle, sesli İngilizce maceraya katıl!",
                rewardText = "+45 Altın",
                emoji = "📖",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF4F46E5))),
                onClick = { onSelectGame(ActiveMiniGame.INTERACTIVE_STORY) },
                testTag = "game_interactive_story"
            )
        }

        // Game: Minik Bahçıvan (Word Garden / Kelime Bahçesi)
        item {
            MiniGameCard(
                title = "Minik Bahçıvan 🌻",
                description = "Bahçıvan Poko'nun söylediği kelimenin tohumunu bulup toprağa ek, sula ve rengarenk bahçeni büyüt!",
                rewardText = "+50 Altın",
                emoji = "🌻",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF10B981))),
                onClick = { onSelectGame(ActiveMiniGame.WORD_GARDEN) },
                testTag = "game_word_garden"
            )
        }

        // Game: Poko'nun Kelime Treni (Wagon Word / Kelime Vagonu)
        item {
            MiniGameCard(
                title = "Poko'nun Kelime Treni 🚂",
                description = "İstasyon anonsunu dinle, raylardaki doğru kargoyu yakalayıp vagonlara yükle ve treni harekete geçir!",
                rewardText = "+50 Altın",
                emoji = "🚂",
                bgBrush = Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0F766E))),
                onClick = { onSelectGame(ActiveMiniGame.WORD_TRAIN) },
                testTag = "game_word_train"
            )
        }

        // Game 0: Voice Champion (NEW MICROPHONE PRONUNCIATION GAME)
        item {
            MiniGameCard(
                title = "Sesli Telaffuz Ustası 🎤",
                description = "Mikrofona konuşarak kelimeleri doğru telaffuz et, yıldızları ve altınları topla!",
                rewardText = "+60 Altın",
                emoji = "🎤",
                bgBrush = Brush.linearGradient(listOf(PlayfulPurple, Color(0xFF7C3AED))),
                onClick = { onSelectGame(ActiveMiniGame.VOICE_CHAMPION) },
                testTag = "game_voice_champion"
            )
        }

        // Game 1: Balloon Rush
        item {
            MiniGameCard(
                title = "Balon Patlatma Avı 🎈",
                description = "Uçan doğru İngilizce kelime balonlarını yakala ve patlat!",
                rewardText = "+30 Altın",
                emoji = "🎈",
                bgBrush = Brush.linearGradient(listOf(BrandBlue, Color(0xFF2563EB))),
                onClick = { onSelectGame(ActiveMiniGame.BALLOON_RUSH) },
                testTag = "game_balloon_rush"
            )
        }

        // Game 2: Memory Flip Cards
        item {
            MiniGameCard(
                title = "Hafıza Kartı Eşleştirme 🃏",
                description = "Kartları çevir, İngilizce kelimeleri görselleriyle eşleştir!",
                rewardText = "+40 Altın",
                emoji = "🃏",
                bgBrush = Brush.linearGradient(listOf(PlayfulPink, Color(0xFFDB2777))),
                onClick = { onSelectGame(ActiveMiniGame.MEMORY_CARDS) },
                testTag = "game_memory_cards"
            )
        }

        // Game 3: Speed Match
        item {
            MiniGameCard(
                title = "Hızlı Kelime Avı ⚡",
                description = "Zamana karşı yarış! Sesi dinle ve en hızlı şekilde doğru kartı bul!",
                rewardText = "+50 Altın",
                emoji = "⚡",
                bgBrush = Brush.linearGradient(listOf(SunOrange, Color(0xFFEA580C))),
                onClick = { onSelectGame(ActiveMiniGame.SPEED_MATCH) },
                testTag = "game_speed_match"
            )
        }
    }
}

@Composable
private fun MiniGameCard(
    title: String,
    description: String,
    rewardText: String,
    emoji: String,
    bgBrush: Brush,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(bgBrush, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "🪙 $rewardText",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(text = "▶", fontSize = 20.sp, color = BrandBlue, modifier = Modifier.padding(start = 6.dp))
        }
    }
}

// ----------------------------------------------------
// MINI GAME: BALLOON RUSH (Active Play)
// ----------------------------------------------------
@Composable
private fun BalloonRushGame(
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayPopFx: () -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit
) {
    val allWords = remember { CurriculumData.getAllWords().shuffled() }
    var score by remember { mutableIntStateOf(0) }
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var isGameOver by remember { mutableStateOf(false) }

    val currentTarget = allWords.getOrNull(currentWordIndex % allWords.size) ?: allWords.first()
    val options = remember(currentWordIndex) {
        val wrongOptions = allWords.filter { it.id != currentTarget.id }.shuffled().take(3)
        (listOf(currentTarget) + wrongOptions).shuffled()
    }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isGameOver = true
            onRewardCoins(score * 2)
        }
    }

    LaunchedEffect(currentWordIndex) {
        onSpeakWord(currentTarget.english, false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F9FF))
            .padding(16.dp)
    ) {
        // Game Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }

            Surface(shape = RoundedCornerShape(14.dp), color = Color.White, shadowElevation = 2.dp) {
                Text(
                    text = "⏱️ $timeLeft sn",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = if (timeLeft < 10) Color.Red else BrandBlue,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFFEF3C7), shadowElevation = 2.dp) {
                Text(
                    text = "⚡ Puan: $score",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isGameOver) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PokoMascot(size = 120.dp, mood = PokoMood.CELEBRATING)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Süre Doldu! 🏁",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Toplam Puan: $score • Kazanılan Altın: +${score * 2} 🪙",
                    style = MaterialTheme.typography.titleMedium.copy(color = BrandBlue, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Kid3DButton(
                    text = "Tekrar Oyna 🔄",
                    onClick = {
                        score = 0
                        timeLeft = 30
                        isGameOver = false
                        currentWordIndex = 0
                    },
                    color = LeafGreen
                )
            }
        } else {
            val isEnglishPrompt = (currentWordIndex % 2 == 1)
            // Target Prompt
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isEnglishPrompt) "İngilizce Kelimenin Türkçe Karşılığını Patlat:" else "Türkçe Kelimenin İngilizce Karşılığını Patlat:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isEnglishPrompt) PlayfulPurple else BrandBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isEnglishPrompt) "\"${currentTarget.english}\"" else "\"${currentTarget.turkish}\"",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isEnglishPrompt) PlayfulPurple else BrandBlue
                        )
                    )
                    if (isEnglishPrompt && currentTarget.phonetic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PlayfulPurple.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayfulPurple.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = "🗣️ Okunuşu: [ ${currentTarget.phonetic} ]",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PlayfulPurple,
                                    fontSize = 15.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Balloon Options
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(options) { opt ->
                    val balloonColor = when (opt.id.hashCode() % 4) {
                        0 -> Color(0xFF3B82F6)
                        1 -> Color(0xFFEC4899)
                        2 -> Color(0xFF10B981)
                        else -> Color(0xFFF59E0B)
                    }

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .shadow(6.dp, CircleShape)
                            .background(balloonColor, CircleShape)
                            .border(3.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            .clickable {
                                onPlayPopFx()
                                if (opt.id == currentTarget.id) {
                                    score += 10
                                    onPlayCorrectFx()
                                    currentWordIndex++
                                } else {
                                    onPlayWrongFx()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = opt.emoji, fontSize = 32.sp)
                            Text(
                                text = if (isEnglishPrompt) opt.turkish else opt.english,
                                style = MaterialTheme.typography.titleMedium.copy(
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

// ----------------------------------------------------
// MINI GAME: MEMORY CARDS (Active Play)
// ----------------------------------------------------
data class MemoryCard(
    val id: Int,
    val wordId: String,
    val displayText: String,
    val emoji: String,
    val isMatched: Boolean = false,
    val isFlipped: Boolean = false
)

@Composable
private fun MemoryCardGame(
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit
) {
    val sampleWords = remember { CurriculumData.getAllWords().shuffled().take(4) }
    val cards = remember {
        val list = mutableListOf<MemoryCard>()
        var idCounter = 0
        sampleWords.forEach { word ->
            // English card
            list.add(MemoryCard(id = idCounter++, wordId = word.id, displayText = word.english, emoji = "🇬🇧"))
            // Turkish/Emoji card
            list.add(MemoryCard(id = idCounter++, wordId = word.id, displayText = word.turkish, emoji = word.emoji))
        }
        list.shuffled()
    }

    val cardStates = remember { mutableStateListOf<MemoryCard>().apply { addAll(cards) } }
    var flippedIndices = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }
    var isVictory by remember { mutableStateOf(false) }

    fun onCardClick(index: Int) {
        if (flippedIndices.size >= 2 || cardStates[index].isFlipped || cardStates[index].isMatched) return

        cardStates[index] = cardStates[index].copy(isFlipped = true)
        flippedIndices.add(index)

        if (flippedIndices.size == 2) {
            moves++
            val first = cardStates[flippedIndices[0]]
            val second = cardStates[flippedIndices[1]]

            if (first.wordId == second.wordId) {
                // Match!
                onPlayCorrectFx()
                cardStates[flippedIndices[0]] = first.copy(isMatched = true)
                cardStates[flippedIndices[1]] = second.copy(isMatched = true)
                sampleWords.find { it.id == first.wordId }?.let { onSpeakWord(it.english, false) }
                flippedIndices.clear()

                if (cardStates.all { it.isMatched }) {
                    isVictory = true
                    onPlayFanfareFx()
                    onRewardCoins(40)
                }
            } else {
                // Not match -> Flip back
                onPlayWrongFx()
            }
        }
    }

    LaunchedEffect(flippedIndices.size) {
        if (flippedIndices.size == 2 && !cardStates[flippedIndices[0]].isMatched) {
            delay(1000)
            if (flippedIndices.size == 2) {
                cardStates[flippedIndices[0]] = cardStates[flippedIndices[0]].copy(isFlipped = false)
                cardStates[flippedIndices[1]] = cardStates[flippedIndices[1]].copy(isFlipped = false)
                flippedIndices.clear()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2F8))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }
            Text(
                text = "Hafıza Kartları 🃏",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
                Text(
                    text = "Hamle: $moves",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isVictory) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PokoMascot(size = 130.dp, mood = PokoMood.CELEBRATING, speechBubbleText = "Muhteşem Hafıza!")
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Tüm Eşleşmeleri Buldun! 🏆",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Kazanılan Ödül: +40 Altın 🪙",
                    style = MaterialTheme.typography.titleMedium.copy(color = BrandBlue, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Kid3DButton(text = "Geri Dön", onClick = onBack, color = LeafGreen)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(cardStates) { index, card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .clickable { onCardClick(index) }
                            .testTag("memory_card_$index"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (card.isFlipped || card.isMatched) Color.White else PlayfulPink
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (card.isMatched) LeafGreen else Color(0xFFF472B6)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (card.isFlipped || card.isMatched) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = card.emoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = card.displayText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF1E293B)
                                        )
                                    )
                                }
                            } else {
                                Text(text = "❓", fontSize = 34.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME: SPEED MATCH
// ----------------------------------------------------
@Composable
private fun SpeedMatchGame(
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit
) {
    val allWords = remember { CurriculumData.getAllWords().shuffled() }
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }

    val target = allWords[currentWordIndex % allWords.size]
    val options = remember(currentWordIndex) {
        val wrong = allWords.filter { it.id != target.id }.shuffled().take(3)
        (listOf(target) + wrong).shuffled()
    }

    val isEnglishQuestion = (currentWordIndex % 2 == 0)

    LaunchedEffect(currentWordIndex, isEnglishQuestion) {
        if (isEnglishQuestion) {
            onSpeakWord(target.english, false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }
            Text(
                text = "Hızlı Kelime Avı ⚡",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFEF3C7)) {
                Text(
                    text = "Puan: $score",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { onSpeakWord(target.english, false) },
                    modifier = Modifier
                        .size(64.dp)
                        .background(BrandBlue, CircleShape)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Dinle", tint = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isEnglishQuestion) target.english else target.turkish,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        fontSize = 22.sp
                    )
                )

                if (isEnglishQuestion && target.phonetic.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PlayfulPurple.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PlayfulPurple.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "🗣️ Okunuşu: [ ${target.phonetic} ]",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PlayfulPurple,
                                fontSize = 15.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isEnglishQuestion) "Doğru Türkçe karşılığına dokun!" else "Doğru İngilizce karşılığına dokun!",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options) { opt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .clickable {
                            if (opt.id == target.id) {
                                score += 15
                                streak++
                                onPlayCorrectFx()
                                onRewardCoins(5)
                                currentWordIndex++
                            } else {
                                streak = 0
                                onPlayWrongFx()
                            }
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFDE68A))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = opt.emoji, fontSize = 28.sp)
                        Text(
                            text = if (isEnglishQuestion) opt.turkish else opt.english,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// VOICE CHAMPION MINI-GAME (SPEECH-TO-TEXT PRONUNCIATION)
// ----------------------------------------------------
@Composable
private fun VoiceChampionGame(
    speechService: SpeechRecognitionService,
    audioService: AudioService,
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val words = remember { CurriculumData.getAllWords().shuffled() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var totalStars by remember { mutableIntStateOf(0) }

    val currentWord = words.getOrNull(currentIndex % words.size)

    val speechState by speechService.speechState.collectAsState()
    val recognizedText by speechService.recognizedText.collectAsState()
    val soundRms by speechService.soundRmsDb.collectAsState()
    val lastEvaluation by speechService.lastEvaluation.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted && currentWord != null) {
            speechService.startListening(currentWord.english)
        }
    }

    fun startAutoListening() {
        if (currentWord == null) return
        if (hasAudioPermission) {
            speechService.startListening(currentWord.english)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Automated Voice Guidance & Auto-Microphone flow on each new word
    LaunchedEffect(currentIndex) {
        currentWord?.let { word ->
            speechService.cancelListening()
            audioService.stopSpeech()
            delay(200)

            audioService.speakGuideThenEnglish(
                guideTr = "Hadi mikrofona '${word.english}' de veya Dinle'ye dokun!",
                targetEn = word.english,
                isSlow = false,
                onComplete = {
                    startAutoListening()
                }
            )
        }
    }

    // Evaluation results handler with verbal praise and retry
    LaunchedEffect(speechState, lastEvaluation) {
        val word = currentWord ?: return@LaunchedEffect

        if (speechState == SpeechState.SUCCESS && lastEvaluation != null) {
            val eval = lastEvaluation!!
            score += 100
            streak++
            totalStars += eval.stars
            onPlayCorrectFx()
            onRewardCoins(10)

            audioService.speakPraise("Harika!") {
                coroutineScope.launch {
                    delay(300)
                    currentIndex++
                }
            }
        } else if (speechState == SpeechState.TRY_AGAIN && lastEvaluation != null) {
            streak = 0
            onPlayWrongFx()
            audioService.speakRetry(
                retryTr = "Tekrar deneyelim!",
                targetEn = word.english,
                onComplete = {
                    startAutoListening()
                }
            )
        }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE || event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                speechService.cancelListening()
                audioService.stopSpeech()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            speechService.cancelListening()
            audioService.stopSpeech()
        }
    }

    // Determine Poko mascot mood
    val pokoMood = when {
        lastEvaluation?.isPassed == true -> PokoMood.CELEBRATING
        speechState == SpeechState.LISTENING -> PokoMood.TALKING
        speechState == SpeechState.TRY_AGAIN -> PokoMood.THINKING
        else -> PokoMood.HAPPY
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_mic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FC))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .testTag("voice_game_back_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color(0xFF64748B))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFEF3C7)) {
                    Text(
                        text = "⭐ $score Puan",
                        fontWeight = FontWeight.Bold,
                        color = SunGold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                if (streak > 1) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFEDD5)) {
                        Text(
                            text = "🔥 x$streak Seri",
                            fontWeight = FontWeight.Black,
                            color = SunOrange,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Poko Mascot & Speech Bubble
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PokoMascot(
                mood = pokoMood,
                size = 76.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (speechState) {
                        SpeechState.LISTENING -> "Seni dinliyorum! Hadi söyle! 👂"
                        SpeechState.PROCESSING -> "Sesini kontrol ediyorum... ⏳"
                        SpeechState.SUCCESS -> "Mükemmel söyledin! Harikasın! 🎉"
                        SpeechState.TRY_AGAIN -> "Tekrar dinle ve birlikte söyleyelim! 💪"
                        else -> "Hadi mikrofona söyle veya Dinle'ye dokun! 🎤🔊"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Word Hero Card
        if (currentWord != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_target_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = currentWord.emoji, fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentWord.english,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = BrandBlue,
                            fontSize = 28.sp
                        )
                    )
                    if (currentWord.phonetic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PlayfulPurple.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayfulPurple.copy(alpha = 0.3f)),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "🗣️ Okunuşu: [ ${currentWord.phonetic} ]",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PlayfulPurple,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }
                    }
                    Text(
                        text = currentWord.turkish,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Kid3DButton(
                            text = "Dinle",
                            emoji = "🔊",
                            onClick = {
                                audioService.speakEnglish(currentWord.english, false) {
                                    startAutoListening()
                                }
                            },
                            color = BrandBlue,
                            shadowColor = Color(0xFF1E40AF),
                            testTag = "voice_game_listen_btn"
                        )
                        Kid3DButton(
                            text = "Yavaş",
                            emoji = "🐢",
                            onClick = {
                                audioService.speakEnglish(currentWord.english, true) {
                                    startAutoListening()
                                }
                            },
                            color = SunGold,
                            shadowColor = Color(0xFFD97706),
                            testTag = "voice_game_slow_btn"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Microphone Pulsing Action Area
            val isListening = speechState == SpeechState.LISTENING
            val scale = if (isListening) pulseScale + (soundRms * 0.03f) else 1f

            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(scale)
                            .background(CandyRed.copy(alpha = 0.25f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .scale(scale * 0.9f)
                            .background(CandyRed.copy(alpha = 0.45f), CircleShape)
                    )
                }

                Surface(
                    onClick = {
                        if (isListening) {
                            speechService.stopListening()
                        } else {
                            startAutoListening()
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(8.dp, CircleShape)
                        .testTag("voice_game_mic_btn"),
                    shape = CircleShape,
                    color = if (isListening) CandyRed else PlayfulPurple,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mikrofon",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live result feedback
            when (speechState) {
                SpeechState.LISTENING -> {
                    Text(
                        text = if (recognizedText.isNotBlank()) "Duydum: \"$recognizedText\"" else "Seni dinliyorum... Şimdi söyle!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CandyRed
                        )
                    )
                }

                SpeechState.SUCCESS -> {
                    lastEvaluation?.let { eval ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFDCFCE7),
                            border = androidx.compose.foundation.BorderStroke(2.dp, LeafGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🎉 ${eval.feedbackMessage} (+100 Puan)",
                                    fontWeight = FontWeight.Black,
                                    color = LeafGreen
                                )
                            }
                        }
                    }
                }

                SpeechState.TRY_AGAIN -> {
                    lastEvaluation?.let { eval ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Duydum: \"${eval.spokenText}\" - Tekrar dinletiliyor...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C)
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Kid3DButton(
                                    text = "Tekrar Söyle",
                                    emoji = "🔄",
                                    onClick = { startAutoListening() },
                                    color = SunOrange,
                                    shadowColor = Color(0xFFC2410C)
                                )
                                Kid3DButton(
                                    text = "Sonraki Kelime",
                                    emoji = "➡️",
                                    onClick = { currentIndex++ },
                                    color = BrandBlue,
                                    shadowColor = Color(0xFF1E40AF)
                                )
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text = "Mikrofon otomatik açılacak, dinle ve söyle!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME: POKO'NUN KELİME TRENİ (WAGON WORD) 🚂
// ----------------------------------------------------
@Composable
private fun WordTrainGame(
    audioService: AudioService,
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit
) {
    val allCurriculumWords = remember { CurriculumData.getAllWords().shuffled() }
    var roundIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isVictory by remember { mutableStateOf(false) }

    val trainWagonCount = 4
    val roundWords = remember(roundIndex) {
        val startIndex = (roundIndex * trainWagonCount) % allCurriculumWords.size
        val selected = mutableListOf<WordItem>()
        for (i in 0 until trainWagonCount) {
            selected.add(allCurriculumWords[(startIndex + i) % allCurriculumWords.size])
        }
        selected
    }

    val loadedWagonWords = remember(roundIndex) { mutableStateListOf<WordItem>() }
    val activeTargetWord = roundWords.getOrNull(loadedWagonWords.size)
    var isDeparting by remember { mutableStateOf(false) }
    var shakingCrateId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Options for current wagon (1 correct + 3 decoys)
    val currentCrateOptions = remember(loadedWagonWords.size, roundIndex) {
        if (activeTargetWord != null) {
            val decoys = allCurriculumWords.filter { it.id != activeTargetWord.id }.shuffled().take(3)
            (listOf(activeTargetWord) + decoys).shuffled()
        } else {
            emptyList()
        }
    }

    val trainDepartureX by animateFloatAsState(
        targetValue = if (isDeparting) 800f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "train_depart"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "train_anim")
    val smokeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "smoke_y"
    )
    val pokoDanceFloat by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "poko_dance"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_crate"
    )

    // Voice announcement: Speaks English target clearly
    LaunchedEffect(activeTargetWord?.id) {
        if (activeTargetWord != null && !isDeparting) {
            audioService.stopSpeech()
            delay(250)
            audioService.speakEnglish(activeTargetWord.english, false)
        }
    }

    // Handle Victory and Departure
    LaunchedEffect(loadedWagonWords.size) {
        if (loadedWagonWords.size == trainWagonCount && !isDeparting) {
            isDeparting = true
            audioService.speakTurkish("Tüm vagonlar doldu! Çuf çuf! Tren kalkıyor!")
            onPlayFanfareFx()
            onRewardCoins(50)
            delay(2200)
            isVictory = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE0F2FE), Color(0xFFF8FAFC), Color(0xFFFEF3C7)))),
        contentPadding = PaddingValues(14.dp, 10.dp, 14.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Top Header Bar: Back Button, Title & Score
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color(0xFF0F172A))
                }

                Text(
                    text = "Kelime Treni 🚂",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        fontSize = 20.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A))
                ) {
                    Text(
                        text = "⭐ $score Puan",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        if (isVictory) {
            // 🎉 VICTORY SCREEN
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PokoMascot(
                        size = 130.dp,
                        mood = PokoMood.CELEBRATING,
                        speechBubbleText = "Çuf Çuf! Muhteşem Tren Şefi! 🚂🎉"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tüm Vagonlar Yüklendi! 🏆",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFBBF24))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "🪙", fontSize = 28.sp)
                            Column {
                                Text(text = "Kazanılan Ödül", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309)))
                                Text(
                                    text = "+50 Altın & +100 Puan",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFB45309)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Kid3DButton(
                            text = "Yeni Sefer 🔄",
                            onClick = {
                                isVictory = false
                                isDeparting = false
                                roundIndex++
                                loadedWagonWords.clear()
                            },
                            color = LeafGreen,
                            shadowColor = Color(0xFF15803D),
                            modifier = Modifier.weight(1f)
                        )

                        Kid3DButton(
                            text = "Oyunlara Dön 🎮",
                            onClick = onBack,
                            color = BrandBlue,
                            shadowColor = Color(0xFF1E40AF),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            // 2. Station Loudspeaker Announcer (NO TURKISH SPOILER!)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0284C7).copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.rotate(pokoDanceFloat)) {
                            PokoMascot(
                                size = 44.dp,
                                mood = if (isDeparting) PokoMood.CELEBRATING else PokoMood.HAPPY
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📢 İstasyon Anonsu:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0284C7),
                                    fontSize = 11.sp
                                )
                            )

                            if (activeTargetWord != null) {
                                Text(
                                    text = "Lütfen \"${activeTargetWord.english}\" vagonunu yükleyiniz!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A),
                                        fontSize = 14.sp
                                    )
                                )

                                if (activeTargetWord.phonetic.isNotBlank()) {
                                    Text(
                                        text = "🗣️ Okunuşu: [ ${activeTargetWord.phonetic} ]",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PlayfulPurple,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                activeTargetWord?.let {
                                    audioService.speakEnglish(it.english, false)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF0284C7), CircleShape)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Tekrar Dinle", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // 3. Compact Illustrated Train on Track (Locomotive + 4 Wagons All Visible)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Railway Tracks Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF0284C7))
                                )
                            )

                            // Ground grass
                            drawRect(
                                color = Color(0xFF047857),
                                topLeft = Offset(0f, h * 0.68f),
                                size = Size(w, h * 0.32f)
                            )

                            // Rails & Ties
                            val railTopY = h * 0.78f
                            val railBottomY = h * 0.86f
                            drawLine(Color(0xFF94A3B8), Offset(0f, railTopY), Offset(w, railTopY), strokeWidth = 2.5f)
                            drawLine(Color(0xFF64748B), Offset(0f, railBottomY), Offset(w, railBottomY), strokeWidth = 3f)

                            val tieCount = 14
                            val tieSpacing = w / tieCount
                            for (i in 0..tieCount) {
                                val x = i * tieSpacing
                                drawLine(Color(0xFF78350F), Offset(x, railTopY - 2f), Offset(x, railBottomY + 3f), strokeWidth = 3.5f)
                            }
                        }

                        // Compact Train Row
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = trainDepartureX.dp)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterStart),
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 🚂 Locomotive
                                item {
                                    Box(
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(68.dp),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = (-6).dp, y = (smokeOffset * 0.7f).dp)
                                        ) {
                                            Text(text = "💨", fontSize = 11.sp)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 6.dp, bottomStart = 5.dp, bottomEnd = 5.dp),
                                            color = Color(0xFFDC2626),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24)),
                                            modifier = Modifier
                                                .width(60.dp)
                                                .height(54.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(2.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = "🦉", fontSize = 12.sp)
                                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFFDE047), CircleShape))
                                                }
                                                Text(text = "POKO 🚂", fontWeight = FontWeight.Black, color = Color.White, fontSize = 7.sp)
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                    repeat(3) {
                                                        Box(modifier = Modifier.size(9.dp).background(Color(0xFF0F172A), CircleShape).border(1.dp, Color(0xFFF59E0B), CircleShape))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // 🚃 4 Wagons
                                itemsIndexed(roundWords) { index, wagonWord ->
                                    val isLoaded = index < loadedWagonWords.size
                                    val isCurrentTarget = index == loadedWagonWords.size
                                    val loadedItem = loadedWagonWords.getOrNull(index)

                                    val wagonBgColor = when (index % 4) {
                                        0 -> Color(0xFF0284C7)
                                        1 -> Color(0xFF7C3AED)
                                        2 -> Color(0xFF059669)
                                        else -> Color(0xFFD97706)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isLoaded) wagonBgColor else Color(0xFF1E293B).copy(alpha = 0.9f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isCurrentTarget) 2.dp else 1.dp,
                                            color = if (isCurrentTarget) Color(0xFFFDE047) else (if (isLoaded) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B))
                                        ),
                                        modifier = Modifier
                                            .width(48.dp)
                                            .height(54.dp)
                                            .scale(if (isCurrentTarget) pulseScale else 1f)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(2.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "V${index + 1}",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 7.sp
                                            )

                                            if (isLoaded && loadedItem != null) {
                                                Text(text = loadedItem.emoji, fontSize = 17.sp)
                                            } else if (isCurrentTarget) {
                                                Text(text = "⬇️", fontSize = 13.sp)
                                            } else {
                                                Text(text = "🔒", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                                            }

                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF0F172A), CircleShape).border(0.8.dp, Color(0xFF94A3B8), CircleShape))
                                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF0F172A), CircleShape).border(0.8.dp, Color(0xFF94A3B8), CircleShape))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Interactive Cargo Crates (NO ENGLISH SPOILER ON CARDS! Shows Emoji + Turkish)
            item {
                Text(
                    text = "📦 Doğru Anlamı Taşıyan Yük Sandığını Seç:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val rows = currentCrateOptions.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { crate ->
                                val isShaking = shakingCrateId == crate.id
                                val crateScale = if (isShaking) 0.92f else 1f

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(72.dp)
                                        .scale(crateScale)
                                        .clickable(enabled = !isDeparting) {
                                            if (activeTargetWord != null) {
                                                if (crate.id == activeTargetWord.id) {
                                                    onPlayCorrectFx()
                                                    loadedWagonWords.add(crate)
                                                    score += 25
                                                    audioService.speakPraise("Harika! ${crate.english} yüklendi!")
                                                } else {
                                                    onPlayWrongFx()
                                                    coroutineScope.launch {
                                                        shakingCrateId = crate.id
                                                        delay(400)
                                                        shakingCrateId = null
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("crate_${crate.english.lowercase()}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isShaking) CandyRed else Color(0xFF38BDF8)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = crate.emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        // ONLY SHOWS TURKISH MEANING (NO SPOILER DUPLICATION!)
                                        Text(
                                            text = crate.turkish,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF0F172A),
                                                fontSize = 13.sp
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME: MİNİK BAHÇIVAN (WORD GARDEN) 🌻
// ----------------------------------------------------
@Composable
private fun WordGardenGame(
    audioService: AudioService,
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit
) {
    val allCurriculumWords = remember { CurriculumData.getAllWords().shuffled() }
    var roundIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isVictory by remember { mutableStateOf(false) }

    val gardenPotsCount = 4
    val roundWords = remember(roundIndex) {
        val startIndex = (roundIndex * gardenPotsCount) % allCurriculumWords.size
        val selected = mutableListOf<WordItem>()
        for (i in 0 until gardenPotsCount) {
            selected.add(allCurriculumWords[(startIndex + i) % allCurriculumWords.size])
        }
        selected
    }

    val plantedWords = remember(roundIndex) { mutableStateListOf<WordItem>() }
    val activeTargetWord = roundWords.getOrNull(plantedWords.size)
    var shakingSeedId by remember { mutableStateOf<String?>(null) }
    var isWatering by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val currentSeedOptions = remember(plantedWords.size, roundIndex) {
        if (activeTargetWord != null) {
            val decoys = allCurriculumWords.filter { it.id != activeTargetWord.id }.shuffled().take(3)
            (listOf(activeTargetWord) + decoys).shuffled()
        } else {
            emptyList()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "garden_anim")
    val butterflyFloat by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "butterfly_y"
    )
    val plantPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pot_pulse"
    )

    LaunchedEffect(activeTargetWord?.id) {
        if (activeTargetWord != null) {
            audioService.stopSpeech()
            delay(250)
            audioService.speakEnglish(activeTargetWord.english, false)
        }
    }

    LaunchedEffect(plantedWords.size) {
        if (plantedWords.size == gardenPotsCount) {
            audioService.speakTurkish("Tüm bahçe rengarenk çiçek açtı! Harika bir bahçıvansın!")
            onPlayFanfareFx()
            onRewardCoins(50)
            delay(2000)
            isVictory = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFBAE6FD), Color(0xFFDCFCE7), Color(0xFFFEF3C7)))),
        contentPadding = PaddingValues(14.dp, 10.dp, 14.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color(0xFF0F172A))
                }

                Text(
                    text = "Minik Bahçıvan 🌻",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF065F46),
                        fontSize = 20.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A))
                ) {
                    Text(
                        text = "⭐ $score Puan",
                        fontWeight = FontWeight.Black,
                        color = SunGold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        if (isVictory) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🦋", fontSize = 30.sp, modifier = Modifier.offset(y = butterflyFloat.dp))
                        PokoMascot(
                            size = 130.dp,
                            mood = PokoMood.CELEBRATING,
                            speechBubbleText = "Bahçemiz Çiçek Açtı! 🌸🎉"
                        )
                        Text(text = "🐝", fontSize = 30.sp, modifier = Modifier.offset(y = (-butterflyFloat).dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Muhteşem Bahçıvan! 🌸🌻",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF065F46)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFBBF24))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "🪙", fontSize = 28.sp)
                            Column {
                                Text(text = "Kazanılan Ödül", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309)))
                                Text(
                                    text = "+50 Altın & +100 Puan",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFB45309)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Kid3DButton(
                            text = "Yeni Bahçe 🔄",
                            onClick = {
                                isVictory = false
                                roundIndex++
                                plantedWords.clear()
                            },
                            color = LeafGreen,
                            shadowColor = Color(0xFF15803D),
                            modifier = Modifier.weight(1f)
                        )

                        Kid3DButton(
                            text = "Oyunlara Dön 🎮",
                            onClick = onBack,
                            color = BrandBlue,
                            shadowColor = Color(0xFF1E40AF),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            // 2. Poko Guidance (NO TURKISH SPOILER!)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.offset(y = (butterflyFloat * 0.3f).dp)) {
                            PokoMascot(
                                size = 44.dp,
                                mood = if (isWatering) PokoMood.CELEBRATING else PokoMood.HAPPY
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🧑‍🌾 Bahçıvan Poko:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF059669),
                                    fontSize = 11.sp
                                )
                            )

                            if (activeTargetWord != null) {
                                Text(
                                    text = "Hadi bahçemize \"${activeTargetWord.english}\" tohumunu ekelim! 🌱",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A),
                                        fontSize = 14.sp
                                    )
                                )

                                if (activeTargetWord.phonetic.isNotBlank()) {
                                    Text(
                                        text = "🗣️ Okunuşu: [ ${activeTargetWord.phonetic} ]",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PlayfulPurple,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                activeTargetWord?.let {
                                    audioService.speakEnglish(it.english, false)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF059669), CircleShape)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Tekrar Dinle", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // 3. Compact Garden Bed
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF065F46)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF34D399)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            drawRect(
                                brush = Brush.verticalGradient(listOf(Color(0xFF7DD3FC), Color(0xFFA7F3D0)))
                            )
                            drawCircle(Color(0xFFFDE047), radius = 18.dp.toPx(), center = Offset(w * 0.90f, h * 0.20f))

                            val soilY = h * 0.60f
                            drawRect(Color(0xFF78350F), Offset(0f, soilY), Size(w, h * 0.40f))
                            drawRect(Color(0xFF10B981), Offset(0f, soilY - 3f), Size(w, 4f))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            roundWords.forEachIndexed { index, potWord ->
                                val isPlanted = index < plantedWords.size
                                val isCurrentPot = index == plantedWords.size
                                val plantedItem = plantedWords.getOrNull(index)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(52.dp)
                                        .scale(if (isCurrentPot) plantPulseScale else 1f)
                                ) {
                                    Box(
                                        modifier = Modifier.size(30.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isPlanted && plantedItem != null) {
                                            Text(text = plantedItem.emoji, fontSize = 20.sp)
                                        } else if (isCurrentPot) {
                                            Text(text = if (isWatering) "💦" else "🌱", fontSize = 16.sp)
                                        } else {
                                            Text(text = "🪴", fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f))
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp, bottomStart = 6.dp, bottomEnd = 6.dp),
                                        color = if (isPlanted) Color(0xFFD97706) else (if (isCurrentPot) Color(0xFFB45309) else Color(0xFF57534E)),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isCurrentPot) 1.5.dp else 1.dp,
                                            color = if (isCurrentPot) Color(0xFFFDE047) else Color(0xFF78350F)
                                        ),
                                        modifier = Modifier
                                            .width(46.dp)
                                            .height(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "Saksı ${index + 1}",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 7.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Interactive Seed Packets (NO ENGLISH SPOILER ON CARDS! Shows Emoji + Turkish)
            item {
                Text(
                    text = "🌱 Doğru Anlamı Taşıyan Tohum Paketini Seç:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val rows = currentSeedOptions.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { seed ->
                                val isShaking = shakingSeedId == seed.id
                                val seedScale = if (isShaking) 0.92f else 1f

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(72.dp)
                                        .scale(seedScale)
                                        .clickable(enabled = plantedWords.size < gardenPotsCount && !isWatering) {
                                            if (activeTargetWord != null) {
                                                if (seed.id == activeTargetWord.id) {
                                                    isWatering = true
                                                    onPlayCorrectFx()
                                                    plantedWords.add(seed)
                                                    score += 25
                                                    audioService.speakPraise("Harika! ${seed.english} filizlendi ve büyüdü!")
                                                    coroutineScope.launch {
                                                        delay(700)
                                                        isWatering = false
                                                    }
                                                } else {
                                                    onPlayWrongFx()
                                                    coroutineScope.launch {
                                                        shakingSeedId = seed.id
                                                        delay(400)
                                                        shakingSeedId = null
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("seed_${seed.english.lowercase()}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isShaking) CandyRed else Color(0xFF10B981)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = seed.emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        // ONLY SHOWS TURKISH MEANING (NO SPOILER DUPLICATION!)
                                        Text(
                                            text = seed.turkish,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF0F172A),
                                                fontSize = 13.sp
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME: GALAKTİK DİNOZOR KELİME SAVAŞI (SPACE DINO WORD BATTLE) 🦖⚔️
// ----------------------------------------------------
private data class DinoHero(
    val id: String,
    val name: String,
    val title: String,
    val emoji: String,
    val weaponEmoji: String,
    val weaponName: String,
    val saberColor: Color,
    val bgGradient: List<Color>
)

private data class AlienBoss(
    val name: String,
    val title: String,
    val emoji: String,
    val maxHp: Int = 100,
    val defeatQuote: String
)

@Composable
private fun SpaceDinoGame(
    audioService: AudioService,
    onSpeakWord: (String, Boolean) -> Unit,
    onPlayCorrectFx: () -> Unit,
    onPlayWrongFx: () -> Unit,
    onPlayFanfareFx: () -> Unit,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit
) {
    val allCurriculumWords = remember { CurriculumData.getAllWords().shuffled() }
    var roundIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isVictory by remember { mutableStateOf(false) }

    // Dino Hero Roster (Star Wars Inspired)
    val heroes = remember {
        listOf(
            DinoHero("rex", "Rex Skywalker", "Yeşil Işın Kılıcı Ustası", "🦖", "⚔️", "Yeşil Işın Kılıcı", Color(0xFF22C55E), listOf(Color(0xFF059669), Color(0xFF10B981))),
            DinoHero("bronto", "Bronto Solo", "Mavi Lazer Blaster Pilotu", "🦕", "🔫", "Mavi Lazer Blaster", Color(0xFF38BDF8), listOf(Color(0xFF0284C7), Color(0xFF38BDF8))),
            DinoHero("poko_jedi", "Astro-Poko Jedi", "Mor Güç Kılıcı Şövalyesi", "🦊", "🔮", "Mor Güç Kılıcı", Color(0xFFA855F7), listOf(Color(0xFF7C3AED), Color(0xFFA855F7))),
            DinoHero("stego", "Stego Kenobi", "Altın Işın Kalkanı Muhafızı", "🦏", "🛡️", "Altın Işın Kılıcı", Color(0xFFFACC15), listOf(Color(0xFFCA8A04), Color(0xFFFDE047)))
        )
    }
    var selectedHeroIndex by remember { mutableIntStateOf(0) }
    val currentHero = heroes[selectedHeroIndex]

    // Alien Bosses per round
    val alienBosses = remember {
        listOf(
            AlienBoss("Lord Cyclops", "Tek Gözlü Komik Canavar", "👾", 100, "Aman tanrım, kelimelerin çok güçlü!"),
            AlienBoss("Blobba the Hutt", "Kozmik Jöle Dev", "🐙", 100, "Jölem eridi, pes ediyorum!"),
            AlienBoss("Dark Vader-Bot", "Teneke Galaksi Robotu", "🤖", 100, "Bip-bup! Işın kılıcın devrelerimi yaktı!"),
            AlienBoss("Cosmo Emperor", "Minik Yıldızlar Kralı", "👽", 100, "Tüm evrenin en zeki dinozoru sensin!")
        )
    }
    val currentBoss = alienBosses[roundIndex % alienBosses.size]

    val missionTargetsCount = 4
    val roundWords = remember(roundIndex) {
        val startIndex = (roundIndex * missionTargetsCount) % allCurriculumWords.size
        val selected = mutableListOf<WordItem>()
        for (i in 0 until missionTargetsCount) {
            selected.add(allCurriculumWords[(startIndex + i) % allCurriculumWords.size])
        }
        selected
    }

    val defeatedSteps = remember(roundIndex) { mutableStateListOf<WordItem>() }
    val activeTargetWord = roundWords.getOrNull(defeatedSteps.size)
    val bossCurrentHp = (100 - (defeatedSteps.size * 25)).coerceAtLeast(0)

    // Animation & Combat States
    var isHeroAttacking by remember { mutableStateOf(false) }
    var isEnemyFlinching by remember { mutableStateOf(false) }
    var isEnemyCounterAttacking by remember { mutableStateOf(false) }
    var shakingCardId by remember { mutableStateOf<String?>(null) }
    var lastHitDamageText by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val currentOptions = remember(defeatedSteps.size, roundIndex) {
        if (activeTargetWord != null) {
            val decoys = allCurriculumWords.filter { it.id != activeTargetWord.id }.shuffled().take(3)
            (listOf(activeTargetWord) + decoys).shuffled()
        } else {
            emptyList()
        }
    }

    // Continuous visual animations
    val infiniteTransition = rememberInfiniteTransition(label = "space_battle_anim")
    val heroBreathing by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_breathe"
    )
    val enemyHover by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(950, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "enemy_hover"
    )
    val saberGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "saber_glow"
    )
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    LaunchedEffect(activeTargetWord?.id) {
        if (activeTargetWord != null) {
            audioService.stopSpeech()
            delay(250)
            audioService.speakEnglish(activeTargetWord.english, false)
        }
    }

    LaunchedEffect(defeatedSteps.size) {
        if (defeatedSteps.size == missionTargetsCount) {
            delay(500)
            audioService.speakTurkish("${currentBoss.name} yenildi! Galaksi ${currentHero.name} sayesinde kurtuldu!")
            onPlayFanfareFx()
            onRewardCoins(60)
            delay(1500)
            isVictory = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF090D16),
                        Color(0xFF131127),
                        Color(0xFF1E1B4B),
                        Color(0xFF2E1065)
                    )
                )
            ),
        contentPadding = PaddingValues(12.dp, 8.dp, 12.dp, 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Header Bar with Back Button, Title, and Score
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kelime Savaşı 🦖⚔️",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047),
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "Bölüm ${roundIndex + 1}: ${currentBoss.name}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFC7D2FE),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFDE047),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCA8A04))
                ) {
                    Text(
                        text = "⭐ $score P",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (isVictory) {
            // 🎉 VICTORY & BOSS DEFEATED CELEBRATION
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💥", fontSize = 28.sp)
                        Text(
                            text = currentHero.emoji,
                            fontSize = 58.sp,
                            modifier = Modifier.scale(saberGlow)
                        )
                        Text(text = "🏆", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${currentBoss.name} Bozguna Uğradı! 🌟",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "\"${currentBoss.defeatQuote}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )

                    // Unlocked Star Wars Dino Jedi Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1B4B),
                        border = androidx.compose.foundation.BorderStroke(2.dp, currentHero.saberColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎖️", fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "Yeni Galaktik Ünvan!",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC7D2FE), fontSize = 10.sp)
                                )
                                Text(
                                    text = "${currentHero.name} - Galaksi Şampiyonu",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFBBF24))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🪙", fontSize = 24.sp)
                            Column {
                                Text(text = "Kazanılan Ödül", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309), fontSize = 10.sp))
                                Text(
                                    text = "+60 Altın & +100 Zafer Puanı",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFB45309),
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Kid3DButton(
                            text = "Sonraki Canavar 👾",
                            onClick = {
                                isVictory = false
                                roundIndex++
                                defeatedSteps.clear()
                            },
                            color = LeafGreen,
                            shadowColor = Color(0xFF15803D),
                            modifier = Modifier.weight(1f)
                        )

                        Kid3DButton(
                            text = "Menüye Dön 🎮",
                            onClick = onBack,
                            color = BrandBlue,
                            shadowColor = Color(0xFF1E40AF),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            // 2. Star Wars Dino Hero Character Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚔️ Savaşçını Seç:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                heroes.forEachIndexed { idx, hero ->
                                    val isSelected = selectedHeroIndex == idx
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) hero.saberColor else Color(0xFF334155),
                                        modifier = Modifier.clickable { selectedHeroIndex = idx }
                                    ) {
                                        Text(
                                            text = "${hero.emoji} ${hero.name.split(" ")[0]}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Dynamic 3D Battle Arena: Dino Hero vs Alien Boss with Animated Lightsabers & HP Bars!
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0F19)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, currentHero.saberColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background Stars & Laser Beams
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Stars
                            listOf(
                                Offset(w * 0.15f, h * 0.2f),
                                Offset(w * 0.35f, h * 0.75f),
                                Offset(w * 0.5f, h * 0.15f),
                                Offset(w * 0.7f, h * 0.8f),
                                Offset(w * 0.85f, h * 0.3f)
                            ).forEach { pos ->
                                drawCircle(Color.White.copy(alpha = starTwinkle), radius = 2.5f, center = pos)
                            }

                            // Attack Slash / Laser Trail
                            if (isHeroAttacking) {
                                drawLine(
                                    color = currentHero.saberColor,
                                    start = Offset(w * 0.25f, h * 0.5f),
                                    end = Offset(w * 0.75f, h * 0.5f),
                                    strokeWidth = 8f,
                                    cap = StrokeCap.Round
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 16f,
                                    center = Offset(w * 0.72f, h * 0.5f)
                                )
                            }
                        }

                        // Arena Content: Hero on Left, VS in Center, Boss on Right
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // --- LEFT: HERO DINO ---
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(90.dp)
                                    .offset(
                                        x = if (isHeroAttacking) 35.dp else 0.dp,
                                        y = heroBreathing.dp
                                    )
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = currentHero.saberColor.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, currentHero.saberColor)
                                ) {
                                    Text(
                                        text = currentHero.name,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Text(
                                        text = currentHero.emoji,
                                        fontSize = 38.sp,
                                        modifier = Modifier.scale(if (isHeroAttacking) 1.25f else 1f)
                                    )
                                    // Glowing Lightsaber/Weapon
                                    Text(
                                        text = currentHero.weaponEmoji,
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .offset(x = 6.dp, y = (-2).dp)
                                            .scale(saberGlow)
                                    )
                                }

                                Text(
                                    text = "⚡ KAHRAMAN",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4ADE80),
                                    fontSize = 7.sp
                                )
                            }

                            // --- CENTER: BATTLE STATUS & DAMAGE POPUP ---
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (lastHitDamageText != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (lastHitDamageText!!.contains("HASAR")) Color(0xFFEF4444) else Color(0xFF3B82F6)
                                    ) {
                                        Text(
                                            text = lastHitDamageText!!,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "⚔️ VS ⚔️",
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFDE047),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${defeatedSteps.size}/$missionTargetsCount Darbe",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 8.sp
                                    )
                                }
                            }

                            // --- RIGHT: ALIEN BOSS ---
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(95.dp)
                                    .offset(
                                        x = if (isEnemyFlinching) 14.dp else (if (isEnemyCounterAttacking) (-12).dp else 0.dp),
                                        y = enemyHover.dp
                                    )
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                                ) {
                                    Text(
                                        text = currentBoss.name,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 8.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = if (isEnemyFlinching) "💥" else currentBoss.emoji,
                                    fontSize = 38.sp,
                                    modifier = Modifier.scale(if (isEnemyFlinching) 0.85f else 1f)
                                )

                                // Enemy HP Bar
                                Column(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    LinearProgressIndicator(
                                        progress = { bossCurrentHp / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = if (bossCurrentHp > 50) Color(0xFF22C55E) else (if (bossCurrentHp > 25) Color(0xFFF59E0B) else Color(0xFFEF4444)),
                                        trackColor = Color(0xFF334155)
                                    )
                                    Text(
                                        text = "HP: $bossCurrentHp/100",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 7.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Voice Attack Guidance (NO TURKISH SPOILER!)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF818CF8).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎯", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Saldırı Hedefi (Dinle ve Vur):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFDE047),
                                    fontSize = 10.sp
                                )
                            )
                            if (activeTargetWord != null) {
                                Text(
                                    text = "\"${activeTargetWord.english}\"",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                )
                                if (activeTargetWord.phonetic.isNotBlank()) {
                                    Text(
                                        text = "🗣️ Okunuşu: [ ${activeTargetWord.phonetic} ]",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC7D2FE),
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                activeTargetWord?.let {
                                    audioService.speakEnglish(it.english, false)
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF6366F1), CircleShape)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Tekrar Dinle", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // 5. Battle Action Target Cards (NO SPOILERS! Shows Emoji + Turkish only)
            item {
                Text(
                    text = "⚡ Doğru Anlamı Seçip Işın Kılıcıyla Saldır:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFDE047),
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val rows = currentOptions.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { optionWord ->
                                val isShaking = shakingCardId == optionWord.id
                                val cardScale = if (isShaking) 0.92f else 1f

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(68.dp)
                                        .scale(cardScale)
                                        .clickable(enabled = defeatedSteps.size < missionTargetsCount && !isHeroAttacking) {
                                            if (activeTargetWord != null) {
                                                if (optionWord.id == activeTargetWord.id) {
                                                    isHeroAttacking = true
                                                    isEnemyFlinching = true
                                                    lastHitDamageText = "💥 -25 HP HASAR!"
                                                    onPlayCorrectFx()
                                                    defeatedSteps.add(optionWord)
                                                    score += 25
                                                    audioService.speakPraise("${currentHero.weaponName} İsabeti! ${optionWord.english} vuruldu!")
                                                    coroutineScope.launch {
                                                        delay(700)
                                                        isHeroAttacking = false
                                                        isEnemyFlinching = false
                                                        lastHitDamageText = null
                                                    }
                                                } else {
                                                    onPlayWrongFx()
                                                    isEnemyCounterAttacking = true
                                                    lastHitDamageText = "🛡️ Iska! Tekrar Dene!"
                                                    coroutineScope.launch {
                                                        shakingCardId = optionWord.id
                                                        delay(500)
                                                        shakingCardId = null
                                                        isEnemyCounterAttacking = false
                                                        lastHitDamageText = null
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("battle_card_${optionWord.english.lowercase()}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isShaking) CandyRed else Color(0xFF818CF8)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(3.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = optionWord.emoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(1.dp))
                                        // ONLY SHOWS TURKISH MEANING (NO ENGLISH SPOILER ON CARD!)
                                        Text(
                                            text = optionWord.turkish,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

