package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PokoDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // Lesson Progress
    @Query("SELECT * FROM user_progress")
    fun getAllLessonProgress(): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE lessonId = :lessonId")
    suspend fun getLessonProgress(lessonId: String): LessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessonProgress(progress: LessonProgressEntity)

    @Query("UPDATE user_progress SET lastExerciseIndex = :lastIndex WHERE lessonId = :lessonId")
    suspend fun updateLessonResumeIndex(lessonId: String, lastIndex: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLessonProgress(list: List<LessonProgressEntity>)

    // Word Progress
    @Query("SELECT * FROM word_progress")
    fun getAllWordProgress(): Flow<List<WordProgressEntity>>

    @Query("SELECT * FROM word_progress WHERE wordId = :wordId")
    suspend fun getWordProgress(wordId: String): WordProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWordProgress(wordProgress: WordProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWordProgress(list: List<WordProgressEntity>)

    @Query("UPDATE word_progress SET isFavorite = :isFavorite WHERE wordId = :wordId")
    suspend fun toggleFavoriteWord(wordId: String, isFavorite: Boolean)

    // Costumes
    @Query("SELECT costumeId FROM unlocked_costumes")
    fun getUnlockedCostumeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockCostume(costume: UnlockedCostumeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCostumes(list: List<UnlockedCostumeEntity>)

    // Badges
    @Query("SELECT badgeId FROM unlocked_badges")
    fun getUnlockedBadgeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockBadge(badge: UnlockedBadgeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBadges(list: List<UnlockedBadgeEntity>)

    // Reset Progress (for Parents)
    @Query("DELETE FROM user_progress")
    suspend fun clearLessonProgress()

    @Query("DELETE FROM user_progress WHERE lessonId IN (:lessonIds)")
    suspend fun deleteLessonsByIds(lessonIds: List<String>)

    @Query("DELETE FROM word_progress")
    suspend fun clearWordProgress()

    @Query("DELETE FROM word_progress WHERE wordId IN (:wordIds)")
    suspend fun deleteWordsByIds(wordIds: List<String>)

    @Query("DELETE FROM unlocked_costumes")
    suspend fun clearUnlockedCostumes()

    @Query("DELETE FROM unlocked_badges")
    suspend fun clearUnlockedBadges()
}
