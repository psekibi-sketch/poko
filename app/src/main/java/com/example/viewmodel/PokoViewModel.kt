package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.data.curriculum.CurriculumData
import com.example.data.local.LessonProgressEntity
import com.example.data.local.PokoDatabase
import com.example.data.local.UserProfileEntity
import com.example.data.local.WordProgressEntity
import com.example.data.model.BadgeItem
import com.example.data.model.GradeLevel
import com.example.data.model.Lesson
import com.example.data.model.PokoCostume
import com.example.data.model.UnitTopic
import com.example.data.model.WordItem
import com.example.data.repository.PokoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val emoji: String) {
    ROOM("Odam", "🏠"),
    MAP("Petek Harita", "🗺️"),
    GAMES("Oyunlar", "🎮"),
    WORDS("Kelimeler", "📚"),
    SHOP("Dolap", "👕"),
    PARENT("Veli", "📊")
}

class PokoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PokoDatabase.getDatabase(application)
    private val repository = PokoRepository(db.pokoDao())
    val audioService = AudioService(application)
    val speechService = SpeechRecognitionService(application)

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    val lessonProgress: StateFlow<List<LessonProgressEntity>> = repository.lessonProgressList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wordProgress: StateFlow<List<WordProgressEntity>> = repository.wordProgressList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val costumes: StateFlow<List<PokoCostume>> = repository.getCostumes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<BadgeItem>> = repository.getBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeGrade = MutableStateFlow(GradeLevel.GRADE_1)
    val activeGrade: StateFlow<GradeLevel> = _activeGrade.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.ROOM)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activePlayingLesson = MutableStateFlow<Lesson?>(null)
    val activePlayingLesson: StateFlow<Lesson?> = _activePlayingLesson.asStateFlow()

    private val _isSoundFxEnabled = MutableStateFlow(true)
    val isSoundFxEnabled: StateFlow<Boolean> = _isSoundFxEnabled.asStateFlow()

    private val _speechRate = MutableStateFlow(0.85f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    val unitsForCurrentGrade: StateFlow<List<UnitTopic>> = combine(_activeGrade, lessonProgress) { grade, _ ->
        CurriculumData.getUnitsForGrade(grade)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initDefaultProfileIfNeeded()
        }
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setGrade(grade: GradeLevel) {
        _activeGrade.value = grade
        viewModelScope.launch {
            repository.setGradeLevel(grade)
        }
    }

    fun startLesson(lesson: Lesson) {
        _activePlayingLesson.value = lesson
    }

    fun closeLesson() {
        _activePlayingLesson.value = null
    }

    fun completeLesson(
        lesson: Lesson,
        stars: Int,
        score: Int,
        correctCount: Int,
        totalCount: Int,
        isPerfectScore: Boolean = true
    ) {
        viewModelScope.launch {
            repository.completeLesson(
                lessonId = lesson.id,
                starsEarned = stars,
                score = score,
                learnedWords = lesson.targetWords,
                correctCount = correctCount,
                totalCount = totalCount,
                isPerfectScore = isPerfectScore
            )
            // Seamless automatic transition: find the next lesson in current grade, or advance to next grade
            val currentGrade = _activeGrade.value
            val allUnits = CurriculumData.getUnitsForGrade(currentGrade)
            val allLessons = allUnits.flatMap { it.lessons }
            val currentIdx = allLessons.indexOfFirst { it.id == lesson.id }
            val nextLesson = if (currentIdx != -1 && currentIdx + 1 < allLessons.size) {
                allLessons[currentIdx + 1]
            } else null

            if (nextLesson != null) {
                _activePlayingLesson.value = nextLesson
            } else {
                // Current grade is finished! Seamlessly advance to the next grade!
                val nextGrade = when (currentGrade) {
                    GradeLevel.GRADE_1 -> GradeLevel.GRADE_2
                    GradeLevel.GRADE_2 -> GradeLevel.GRADE_3
                    GradeLevel.GRADE_3 -> GradeLevel.GRADE_4
                    GradeLevel.GRADE_4 -> null
                }
                if (nextGrade != null) {
                    setGrade(nextGrade)
                    val nextUnits = CurriculumData.getUnitsForGrade(nextGrade)
                    val firstLessonOfNextGrade = nextUnits.flatMap { it.lessons }.firstOrNull()
                    _activePlayingLesson.value = firstLessonOfNextGrade
                } else {
                    _activePlayingLesson.value = null
                }
            }
        }
    }

    fun speak(text: String, isSlow: Boolean = false, onDone: (() -> Unit)? = null) {
        audioService.speakEnglish(text, isSlow, onDone)
    }

    fun speakTurkish(text: String, isSlow: Boolean = false, onDone: (() -> Unit)? = null) {
        audioService.speakTurkish(text, isSlow, onDone)
    }

    fun speakGuideThenEnglish(guideTr: String, targetEn: String, isSlow: Boolean = false, onComplete: (() -> Unit)? = null) {
        audioService.speakGuideThenEnglish(guideTr, targetEn, isSlow, onComplete)
    }

    fun speakPraise(phrase: String = "Harika!", onComplete: (() -> Unit)? = null) {
        audioService.speakPraise(phrase, onComplete)
    }

    fun speakRetry(guideTr: String = "Tekrar deneyelim!", targetEn: String, onComplete: (() -> Unit)? = null) {
        audioService.speakRetry(guideTr, targetEn, onComplete)
    }

    fun stopSpeech() {
        audioService.stopSpeech()
    }

    fun playCorrectFx() = audioService.playCorrectSound()
    fun playWrongFx() = audioService.playWrongSound()
    fun playPopFx() = audioService.playPopSound()
    fun playFanfareFx() = audioService.playFanfareSound()
    fun playCoinFx() = audioService.playCoinSound()

    fun rewardCoins(amount: Int) {
        viewModelScope.launch {
            val current = userProfile.value
            db.pokoDao().insertOrUpdateProfile(current.copy(coins = current.coins + amount))
            playCoinFx()
        }
    }

    fun toggleFavoriteWord(wordId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteWord(wordId, isFavorite)
        }
    }

    fun buyCostume(costume: PokoCostume) {
        viewModelScope.launch {
            val success = repository.buyCostume(costume)
            if (success) {
                playCoinFx()
            }
        }
    }

    fun equipItem(item: PokoCostume) {
        viewModelScope.launch {
            repository.equipItem(item)
            playPopFx()
        }
    }

    fun unequipItem(item: PokoCostume) {
        viewModelScope.launch {
            repository.unequipItem(item)
            playPopFx()
        }
    }

    fun equipCostume(costumeId: String) {
        viewModelScope.launch {
            repository.equipCostume(costumeId)
        }
    }

    fun resetGradeProgress(grade: GradeLevel) {
        viewModelScope.launch {
            repository.resetGradeProgress(grade)
            _activeGrade.value = grade
            playFanfareFx()
        }
    }

    fun toggleSoundFx() {
        val newState = !_isSoundFxEnabled.value
        _isSoundFxEnabled.value = newState
        audioService.isSoundFxEnabled = newState
    }

    fun updateSpeechRate(newRate: Float) {
        _speechRate.value = newRate
        audioService.updateSpeechRate(newRate)
    }

    fun saveLessonResumeProgress(lessonId: String, currentExerciseIndex: Int) {
        viewModelScope.launch {
            repository.saveLessonResumeProgress(lessonId, currentExerciseIndex)
        }
    }

    suspend fun exportBackupJson(): String {
        val backupData = repository.createBackupData()
        return backupData.toJsonString()
    }

    suspend fun restoreBackupJson(jsonString: String): Result<Unit> {
        return try {
            val backupData = com.example.data.model.PokoBackupData.fromJsonString(jsonString)
            repository.restoreBackup(backupData)
            val restoredGrade = when (backupData.userProfile.activeGradeLevel) {
                "GRADE_2" -> GradeLevel.GRADE_2
                "GRADE_3" -> GradeLevel.GRADE_3
                "GRADE_4" -> GradeLevel.GRADE_4
                else -> GradeLevel.GRADE_1
            }
            _activeGrade.value = restoredGrade
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _activeGrade.value = GradeLevel.GRADE_1
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioService.shutdown()
        speechService.shutdown()
    }
}
