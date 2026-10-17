package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.GradeLevel
import com.example.ui.components.KidTopBar
import com.example.ui.screens.AvatarShopScreen
import com.example.ui.screens.DaysOfWeekGameScreen
import com.example.ui.screens.HoneycombMapScreen
import com.example.ui.screens.LessonActivityScreen
import com.example.ui.screens.MiniGamesScreen
import com.example.ui.screens.ParentReportScreen
import com.example.ui.screens.PokoBuddyTalkScreen
import com.example.ui.screens.PokoRoomScreen
import com.example.ui.screens.WordBankScreen
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainTab
import com.example.viewmodel.PokoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PokoAppRoot()
            }
        }
    }
}

@Composable
fun PokoAppRoot(
    viewModel: PokoViewModel = viewModel()
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE || event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                viewModel.stopSpeech()
                viewModel.speechService.cancelListening()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopSpeech()
            viewModel.speechService.cancelListening()
        }
    }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val lessonProgress by viewModel.lessonProgress.collectAsStateWithLifecycle()
    val wordProgress by viewModel.wordProgress.collectAsStateWithLifecycle()
    val costumes by viewModel.costumes.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()
    val activeGrade by viewModel.activeGrade.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activePlayingLesson by viewModel.activePlayingLesson.collectAsStateWithLifecycle()
    val units by viewModel.unitsForCurrentGrade.collectAsStateWithLifecycle()
    val isSoundOn by viewModel.isSoundFxEnabled.collectAsStateWithLifecycle()
    val speechRate by viewModel.speechRate.collectAsStateWithLifecycle()

    val lessonProgressMap = lessonProgress.associateBy { it.lessonId }
    var isBuddyTalkOpen by remember { mutableStateOf(false) }
    var isDaysOfWeekGameOpen by remember { mutableStateOf(false) }

    // If Days of the Week Game is open, show the immersive full-screen learning & quiz view
    if (isDaysOfWeekGameOpen) {
        BackHandler {
            isDaysOfWeekGameOpen = false
        }
        DaysOfWeekGameScreen(
            audioService = viewModel.audioService,
            speechService = viewModel.speechService,
            onRewardCoins = { viewModel.rewardCoins(it) },
            onBack = { isDaysOfWeekGameOpen = false },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        )
        return
    }

    // If Buddy Talk mode is open, show the immersive full-screen Buddy Talk conversation
    if (isBuddyTalkOpen) {
        BackHandler {
            isBuddyTalkOpen = false
        }
        PokoBuddyTalkScreen(
            audioService = viewModel.audioService,
            speechService = viewModel.speechService,
            userCoins = userProfile.coins,
            equippedCostumeId = userProfile.equippedCostumeId,
            onRewardCoins = { viewModel.rewardCoins(it) },
            onBack = { isBuddyTalkOpen = false },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        )
        return
    }

    // If an active lesson is open, show the immersive full-screen lesson view
    val currentPlayingLesson = activePlayingLesson
    if (currentPlayingLesson != null) {
        BackHandler {
            viewModel.closeLesson()
        }

        val currentLessonProgress = lessonProgressMap[currentPlayingLesson.id]
        val savedIndex = if (currentLessonProgress?.isCompleted == true && currentLessonProgress.starsEarned >= 3) {
            0
        } else {
            currentLessonProgress?.lastExerciseIndex ?: 0
        }

        androidx.compose.runtime.key(currentPlayingLesson.id) {
            LessonActivityScreen(
                lesson = currentPlayingLesson,
                initialExerciseIndex = savedIndex,
                equippedCostumeId = userProfile.equippedCostumeId,
                audioService = viewModel.audioService,
                speechService = viewModel.speechService,
                onSpeakWord = { word, isSlow -> viewModel.speak(word, isSlow) },
                onPlayCorrectFx = { viewModel.playCorrectFx() },
                onPlayWrongFx = { viewModel.playWrongFx() },
                onPlayPopFx = { viewModel.playPopFx() },
                onPlayFanfareFx = { viewModel.playFanfareFx() },
                onProgressUpdate = { index ->
                    viewModel.saveLessonResumeProgress(currentPlayingLesson.id, index)
                },
                onFinishLesson = { stars, score, correctCount, totalCount, isPerfectScore ->
                    viewModel.completeLesson(currentPlayingLesson, stars, score, correctCount, totalCount, isPerfectScore)
                },
                onClose = { viewModel.closeLesson() },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            )
        }
        return
    }

    // Secondary screen back handler: returns to Poko's Room
    BackHandler(enabled = currentTab != MainTab.ROOM) {
        viewModel.selectTab(MainTab.ROOM)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            KidTopBar(
                activeGrade = activeGrade,
                coins = userProfile.coins,
                stars = userProfile.totalStars,
                streakDays = userProfile.streakDays,
                isSoundOn = isSoundOn,
                onGradeChange = { viewModel.setGrade(it) },
                onSoundToggle = { viewModel.toggleSoundFx() },
                onStreakClick = { viewModel.speak("You have a ${userProfile.streakDays} day streak! Keep learning!", false) },
                onOpenParentReport = { viewModel.selectTab(MainTab.PARENT) },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("main_bottom_nav"),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                shadowElevation = 10.dp,
                border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        val tabScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1.0f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "tab_scale_${tab.name}"
                        )

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) BrandBlue.copy(alpha = 0.12f) else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.5.dp, BrandBlue.copy(alpha = 0.35f)) else null,
                            modifier = Modifier
                                .scale(tabScale)
                                .clickable {
                                    if (!isSelected) {
                                        viewModel.audioService.playPopSound()
                                        viewModel.selectTab(tab)
                                    }
                                }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .testTag("nav_tab_${tab.name.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = tab.emoji,
                                    fontSize = if (isSelected) 22.sp else 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) BrandBlue else Color(0xFF64748B),
                                    fontSize = if (isSelected) 11.sp else 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_switch"
            ) { targetTab ->
                when (targetTab) {
                    MainTab.ROOM -> {
                        PokoRoomScreen(
                            units = units,
                            lessonProgressMap = lessonProgressMap,
                            activeGrade = activeGrade,
                            equippedCostumeId = userProfile.equippedCostumeId,
                            equippedWeaponId = userProfile.equippedWeaponId,
                            equippedHelmetId = userProfile.equippedHelmetId,
                            equippedDecorId = userProfile.equippedDecorId,
                            equippedThemeId = userProfile.equippedThemeId,
                            totalStars = userProfile.totalStars,
                            coins = userProfile.coins,
                            streakDays = userProfile.streakDays,
                            onNavigateToTab = { viewModel.selectTab(it) },
                            onStartLesson = { viewModel.startLesson(it) },
                            onMascotSpeak = {
                                viewModel.speak("Hello! Welcome to Poko's room! Let's learn English together!", false)
                            },
                            onOpenBuddyTalk = { isBuddyTalkOpen = true },
                            onOpenDaysOfWeekGame = { isDaysOfWeekGameOpen = true }
                        )
                    }

                    MainTab.MAP -> {
                        HoneycombMapScreen(
                            units = units,
                            lessonProgressMap = lessonProgressMap,
                            activeGrade = activeGrade,
                            audioService = viewModel.audioService,
                            speechService = viewModel.speechService,
                            onRewardCoins = { viewModel.rewardCoins(it) },
                            onStartLesson = { viewModel.startLesson(it) },
                            onBackToRoom = { viewModel.selectTab(MainTab.ROOM) }
                        )
                    }

                    MainTab.GAMES -> {
                        MiniGamesScreen(
                            speechService = viewModel.speechService,
                            audioService = viewModel.audioService,
                            onSpeakWord = { word, isSlow -> viewModel.speak(word, isSlow) },
                            onPlayCorrectFx = { viewModel.playCorrectFx() },
                            onPlayWrongFx = { viewModel.playWrongFx() },
                            onPlayPopFx = { viewModel.playPopFx() },
                            onPlayFanfareFx = { viewModel.playFanfareFx() },
                            onRewardCoins = { viewModel.rewardCoins(it) }
                        )
                    }

                    MainTab.WORDS -> {
                        WordBankScreen(
                            wordProgressList = wordProgress,
                            speechService = viewModel.speechService,
                            audioService = viewModel.audioService,
                            onSpeakWord = { word, isSlow -> viewModel.speak(word, isSlow) },
                            onToggleFavorite = { wordId, isFav ->
                                viewModel.toggleFavoriteWord(wordId, isFav)
                            }
                        )
                    }

                    MainTab.SHOP -> {
                        AvatarShopScreen(
                            userCoins = userProfile.coins,
                            costumes = costumes,
                            badges = badges,
                            equippedCostumeId = userProfile.equippedCostumeId,
                            equippedWeaponId = userProfile.equippedWeaponId,
                            equippedHelmetId = userProfile.equippedHelmetId,
                            equippedDecorId = userProfile.equippedDecorId,
                            equippedThemeId = userProfile.equippedThemeId,
                            onEquipItem = { viewModel.equipItem(it) },
                            onUnequipItem = { viewModel.unequipItem(it) },
                            onBuyCostume = { viewModel.buyCostume(it) }
                        )
                    }

                    MainTab.PARENT -> {
                        ParentReportScreen(
                            userProfile = userProfile,
                            lessonProgress = lessonProgress,
                            wordProgress = wordProgress,
                            isSoundOn = isSoundOn,
                            speechRate = speechRate,
                            onSoundToggle = { viewModel.toggleSoundFx() },
                            onSpeechRateChange = { viewModel.updateSpeechRate(it) },
                            onTestVoice = {
                                viewModel.speak("Hello! This is English Text To Speech test. Let's learn English together!", false)
                            },
                            onResetProgress = { viewModel.resetAllProgress() },
                            onResetGradeProgress = { grade -> viewModel.resetGradeProgress(grade) },
                            onExportBackup = { viewModel.exportBackupJson() },
                            onRestoreBackup = { jsonStr -> viewModel.restoreBackupJson(jsonStr) }
                        )
                    }
                }
            }
        }
    }
}
