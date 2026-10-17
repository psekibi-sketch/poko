package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val starsEarned: Int,
    val isCompleted: Boolean,
    val highScore: Int,
    val lastExerciseIndex: Int = 0,
    val completedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "word_progress")
data class WordProgressEntity(
    @PrimaryKey val wordId: String,
    val isLearned: Boolean,
    val masteryCount: Int = 0,
    val mistakesCount: Int = 0,
    val isFavorite: Boolean = false,
    val lastPracticedMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 40,
    val totalStars: Int = 0,
    val streakDays: Int = 1,
    val lastActiveDateMillis: Long = System.currentTimeMillis(),
    val activeGradeLevel: String = "GRADE_1",
    val equippedCostumeId: String = "costume_default",
    val equippedWeaponId: String = "weapon_red_saber",
    val equippedHelmetId: String = "helmet_vader",
    val equippedDecorId: String = "decor_holotable",
    val equippedThemeId: String = "theme_starship",
    val totalTimeSeconds: Long = 0L,
    val totalCorrectAnswers: Int = 0,
    val totalExercisesPlayed: Int = 0,
    // Pet / Sanal Bebek
    val petType: String = "DRAGON",
    val petName: String = "Drako",
    val petLevel: Int = 1,
    val petExp: Int = 20,
    val petHunger: Int = 85,
    val starFruits: Int = 8,
    // Daily Spin & Chests
    val lastSpinDateMillis: Long = 0L,
    val mysteryChests: Int = 1,
    // Galaxy League
    val leagueTier: String = "BRONZE",
    val leaguePoints: Int = 140,
    // Cards
    val unlockedCardIds: String = "card_vader,card_dragon,card_star"
)

@Entity(tableName = "unlocked_costumes")
data class UnlockedCostumeEntity(
    @PrimaryKey val costumeId: String,
    val unlockedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "unlocked_badges")
data class UnlockedBadgeEntity(
    @PrimaryKey val badgeId: String,
    val unlockedAtMillis: Long = System.currentTimeMillis()
)
