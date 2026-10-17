package com.example.data.repository

import com.example.data.curriculum.CurriculumData
import com.example.data.local.LessonProgressEntity
import com.example.data.local.PokoDao
import com.example.data.local.UnlockedBadgeEntity
import com.example.data.local.UnlockedCostumeEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WordProgressEntity
import com.example.data.model.BadgeItem
import com.example.data.model.GradeLevel
import com.example.data.model.Lesson
import com.example.data.model.PokoCostume
import com.example.data.model.UnitTopic
import com.example.data.model.WordItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

import com.example.data.model.CostumeCategory

class PokoRepository(private val dao: PokoDao) {

    val userProfile: Flow<UserProfileEntity> = dao.getUserProfile().map {
        it ?: UserProfileEntity()
    }

    val lessonProgressList: Flow<List<LessonProgressEntity>> = dao.getAllLessonProgress()
    val wordProgressList: Flow<List<WordProgressEntity>> = dao.getAllWordProgress()
    val unlockedCostumeIds: Flow<List<String>> = dao.getUnlockedCostumeIds()
    val unlockedBadgeIds: Flow<List<String>> = dao.getUnlockedBadgeIds()

    suspend fun initDefaultProfileIfNeeded() {
        val current = dao.getUserProfile().firstOrNull()
        if (current == null) {
            dao.insertOrUpdateProfile(
                UserProfileEntity(
                    coins = 50,
                    totalStars = 0,
                    streakDays = 1,
                    activeGradeLevel = "GRADE_1",
                    equippedCostumeId = "costume_default",
                    equippedWeaponId = "weapon_red_saber",
                    equippedHelmetId = "helmet_vader",
                    equippedDecorId = "decor_holotable",
                    equippedThemeId = "theme_starship"
                )
            )
            dao.unlockCostume(UnlockedCostumeEntity("costume_default"))
            dao.unlockCostume(UnlockedCostumeEntity("weapon_red_saber"))
            dao.unlockCostume(UnlockedCostumeEntity("helmet_vader"))
            dao.unlockCostume(UnlockedCostumeEntity("decor_holotable"))
            dao.unlockCostume(UnlockedCostumeEntity("theme_starship"))
        }
    }

    fun getUnitsWithProgress(grade: GradeLevel): Flow<List<UnitTopic>> {
        return combine(lessonProgressList, userProfile) { progressList, profile ->
            val progressMap = progressList.associateBy { it.lessonId }
            val rawUnits = CurriculumData.getUnitsForGrade(grade)

            var isFirstLesson = true
            var previousLessonCompleted = true

            rawUnits.map { unit ->
                val updatedLessons = unit.lessons.map { lesson ->
                    val progress = progressMap[lesson.id]
                    val isCompleted = progress?.isCompleted == true
                    val stars = progress?.starsEarned ?: 0

                    // Unlock first lesson always, subsequent unlocked if previous completed
                    val isUnlocked = isFirstLesson || previousLessonCompleted || isCompleted
                    isFirstLesson = false
                    previousLessonCompleted = isCompleted

                    lesson.copy()
                }
                unit.copy(lessons = updatedLessons)
            }
        }
    }

    fun getCostumes(): Flow<List<PokoCostume>> {
        return combine(unlockedCostumeIds, userProfile) { unlockedIds, profile ->
            val defaultUnlocked = setOf("costume_default", "weapon_red_saber", "helmet_vader", "decor_holotable", "theme_starship")
            val unlockedSet = unlockedIds.toSet() + defaultUnlocked
            CurriculumData.initialCostumes.map { costume ->
                val isEquipped = when (costume.category) {
                    CostumeCategory.COSTUME -> profile.equippedCostumeId == costume.id
                    CostumeCategory.LIGHTSABER -> profile.equippedWeaponId == costume.id
                    CostumeCategory.HELMET -> profile.equippedHelmetId == costume.id
                    CostumeCategory.ROOM_DECOR -> profile.equippedDecorId == costume.id
                    CostumeCategory.ROOM_THEME -> profile.equippedThemeId == costume.id
                }
                costume.copy(
                    isUnlocked = unlockedSet.contains(costume.id),
                    isEquipped = isEquipped
                )
            }
        }
    }

    fun getBadges(): Flow<List<BadgeItem>> {
        return combine(unlockedBadgeIds, userProfile, wordProgressList) { unlockedIds, profile, wordProgress ->
            val unlockedSet = unlockedIds.toSet()
            val learnedWordsCount = wordProgress.count { it.isLearned }

            CurriculumData.availableBadges.map { badge ->
                val currentProg = when (badge.id) {
                    "badge_first_step" -> if (profile.totalStars > 0) 1 else 0
                    "badge_star_collector" -> profile.totalStars
                    "badge_word_master" -> learnedWordsCount
                    "badge_streak_3" -> profile.streakDays
                    "badge_balloon_pop" -> profile.totalCorrectAnswers
                    "badge_fashion_poko" -> if (unlockedIds.isNotEmpty()) 1 else 0
                    else -> 0
                }
                val isUnlocked = unlockedSet.contains(badge.id) || currentProg >= badge.requiredValue
                badge.copy(
                    isUnlocked = isUnlocked,
                    progress = currentProg
                )
            }
        }
    }

    suspend fun completeLesson(
        lessonId: String,
        starsEarned: Int,
        score: Int,
        learnedWords: List<WordItem>,
        correctCount: Int,
        totalCount: Int,
        isPerfectScore: Boolean = true
    ) {
        val currentProgress = dao.getLessonProgress(lessonId)
        val prevStars = currentProgress?.starsEarned ?: 0
        val prevCompleted = currentProgress?.isCompleted ?: false
        val starDiff = if (starsEarned > prevStars) starsEarned - prevStars else 0

        val earnedCoins = (starsEarned * 10) + if (isPerfectScore) 25 else 10
        val isNowCompleted = true

        dao.saveLessonProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                starsEarned = maxOf(starsEarned, prevStars),
                isCompleted = isNowCompleted,
                highScore = maxOf(score, currentProgress?.highScore ?: 0)
            )
        )

        // Save Word progress
        learnedWords.forEach { word ->
            val wordProg = dao.getWordProgress(word.id)
            val currentMastery = (wordProg?.masteryCount ?: 0) + 1
            dao.saveWordProgress(
                WordProgressEntity(
                    wordId = word.id,
                    isLearned = true,
                    masteryCount = currentMastery,
                    isFavorite = wordProg?.isFavorite ?: false
                )
            )
        }

        // Update profile
        val currentProfile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val newStars = currentProfile.totalStars + starDiff
        val newCoins = currentProfile.coins + earnedCoins
        val newCorrect = currentProfile.totalCorrectAnswers + correctCount
        val newTotal = currentProfile.totalExercisesPlayed + totalCount

        dao.insertOrUpdateProfile(
            currentProfile.copy(
                coins = newCoins,
                totalStars = newStars,
                totalCorrectAnswers = newCorrect,
                totalExercisesPlayed = newTotal
            )
        )

        // Check badge unlocks
        if (starsEarned >= 1) dao.unlockBadge(UnlockedBadgeEntity("badge_first_step"))
        if (newStars >= 15) dao.unlockBadge(UnlockedBadgeEntity("badge_star_collector"))
    }

    suspend fun buyCostume(costume: PokoCostume): Boolean {
        val profile = dao.getUserProfile().firstOrNull() ?: return false
        if (profile.coins >= costume.price) {
            val updatedProfile = when (costume.category) {
                CostumeCategory.COSTUME -> profile.copy(coins = profile.coins - costume.price, equippedCostumeId = costume.id)
                CostumeCategory.LIGHTSABER -> profile.copy(coins = profile.coins - costume.price, equippedWeaponId = costume.id)
                CostumeCategory.HELMET -> profile.copy(coins = profile.coins - costume.price, equippedHelmetId = costume.id)
                CostumeCategory.ROOM_DECOR -> profile.copy(coins = profile.coins - costume.price, equippedDecorId = costume.id)
                CostumeCategory.ROOM_THEME -> profile.copy(coins = profile.coins - costume.price, equippedThemeId = costume.id)
            }
            dao.insertOrUpdateProfile(updatedProfile)
            dao.unlockCostume(UnlockedCostumeEntity(costume.id))
            dao.unlockBadge(UnlockedBadgeEntity("badge_fashion_poko"))
            return true
        }
        return false
    }

    suspend fun equipItem(item: PokoCostume) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        val updatedProfile = when (item.category) {
            CostumeCategory.COSTUME -> profile.copy(equippedCostumeId = item.id)
            CostumeCategory.LIGHTSABER -> profile.copy(equippedWeaponId = item.id)
            CostumeCategory.HELMET -> profile.copy(equippedHelmetId = item.id)
            CostumeCategory.ROOM_DECOR -> profile.copy(equippedDecorId = item.id)
            CostumeCategory.ROOM_THEME -> profile.copy(equippedThemeId = item.id)
        }
        dao.insertOrUpdateProfile(updatedProfile)
    }

    suspend fun unequipItem(item: PokoCostume) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        val updatedProfile = when (item.category) {
            CostumeCategory.COSTUME -> profile.copy(equippedCostumeId = "costume_default")
            CostumeCategory.LIGHTSABER -> profile.copy(equippedWeaponId = "weapon_red_saber")
            CostumeCategory.HELMET -> profile.copy(equippedHelmetId = "helmet_vader")
            CostumeCategory.ROOM_DECOR -> profile.copy(equippedDecorId = "decor_holotable")
            CostumeCategory.ROOM_THEME -> profile.copy(equippedThemeId = "theme_starship")
        }
        dao.insertOrUpdateProfile(updatedProfile)
    }

    suspend fun equipCostume(costumeId: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        dao.insertOrUpdateProfile(profile.copy(equippedCostumeId = costumeId))
    }

    suspend fun setGradeLevel(grade: GradeLevel) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        dao.insertOrUpdateProfile(profile.copy(activeGradeLevel = grade.name))
    }

    suspend fun resetGradeProgress(grade: GradeLevel) {
        val units = CurriculumData.getUnitsForGrade(grade)
        val lessonIds = units.flatMap { it.lessons }.map { it.id }
        val wordIds = units.flatMap { unit -> unit.lessons.flatMap { it.targetWords } }.map { it.id }
        if (lessonIds.isNotEmpty()) {
            dao.deleteLessonsByIds(lessonIds)
        }
        if (wordIds.isNotEmpty()) {
            dao.deleteWordsByIds(wordIds)
        }
    }

    suspend fun toggleFavoriteWord(wordId: String, isFavorite: Boolean) {
        val existing = dao.getWordProgress(wordId)
        if (existing != null) {
            dao.saveWordProgress(existing.copy(isFavorite = isFavorite))
        } else {
            dao.saveWordProgress(
                WordProgressEntity(
                    wordId = wordId,
                    isLearned = false,
                    isFavorite = isFavorite
                )
            )
        }
    }

    suspend fun saveLessonResumeProgress(lessonId: String, currentExerciseIndex: Int) {
        val existing = dao.getLessonProgress(lessonId)
        if (existing != null) {
            dao.saveLessonProgress(
                existing.copy(
                    lastExerciseIndex = currentExerciseIndex,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
        } else {
            dao.saveLessonProgress(
                LessonProgressEntity(
                    lessonId = lessonId,
                    starsEarned = 0,
                    isCompleted = false,
                    highScore = 0,
                    lastExerciseIndex = currentExerciseIndex,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun createBackupData(): com.example.data.model.PokoBackupData {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val lessons = dao.getAllLessonProgress().firstOrNull() ?: emptyList()
        val words = dao.getAllWordProgress().firstOrNull() ?: emptyList()
        val costumes = dao.getUnlockedCostumeIds().firstOrNull() ?: emptyList()
        val badges = dao.getUnlockedBadgeIds().firstOrNull() ?: emptyList()

        return com.example.data.model.PokoBackupData(
            userProfile = profile,
            lessonProgress = lessons,
            wordProgress = words,
            unlockedCostumes = costumes,
            unlockedBadges = badges
        )
    }

    suspend fun restoreBackup(backup: com.example.data.model.PokoBackupData) {
        dao.insertOrUpdateProfile(backup.userProfile)
        dao.insertAllLessonProgress(backup.lessonProgress)
        dao.insertAllWordProgress(backup.wordProgress)
        backup.unlockedCostumes.forEach { dao.unlockCostume(UnlockedCostumeEntity(it)) }
        backup.unlockedBadges.forEach { dao.unlockBadge(UnlockedBadgeEntity(it)) }
    }

    suspend fun resetAllProgress() {
        dao.clearLessonProgress()
        dao.clearWordProgress()
        dao.clearUnlockedCostumes()
        dao.clearUnlockedBadges()
        dao.insertOrUpdateProfile(
            UserProfileEntity(
                coins = 50,
                totalStars = 0,
                streakDays = 1,
                activeGradeLevel = "GRADE_1",
                equippedCostumeId = "costume_default"
            )
        )
        dao.unlockCostume(UnlockedCostumeEntity("costume_default"))
    }
}
