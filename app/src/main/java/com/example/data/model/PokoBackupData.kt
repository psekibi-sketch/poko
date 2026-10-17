package com.example.data.model

import com.example.data.local.LessonProgressEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WordProgressEntity
import org.json.JSONArray
import org.json.JSONObject

data class PokoBackupData(
    val version: Int = 1,
    val exportedAtMillis: Long = System.currentTimeMillis(),
    val userProfile: UserProfileEntity,
    val lessonProgress: List<LessonProgressEntity>,
    val wordProgress: List<WordProgressEntity>,
    val unlockedCostumes: List<String>,
    val unlockedBadges: List<String>
) {
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("version", version)
        json.put("exportedAtMillis", exportedAtMillis)

        // Profile
        val profileObj = JSONObject().apply {
            put("coins", userProfile.coins)
            put("totalStars", userProfile.totalStars)
            put("streakDays", userProfile.streakDays)
            put("activeGradeLevel", userProfile.activeGradeLevel)
            put("equippedCostumeId", userProfile.equippedCostumeId)
            put("totalCorrectAnswers", userProfile.totalCorrectAnswers)
            put("totalExercisesPlayed", userProfile.totalExercisesPlayed)
        }
        json.put("userProfile", profileObj)

        // Lesson progress
        val lessonsArr = JSONArray()
        lessonProgress.forEach { lp ->
            val obj = JSONObject().apply {
                put("lessonId", lp.lessonId)
                put("starsEarned", lp.starsEarned)
                put("isCompleted", lp.isCompleted)
                put("highScore", lp.highScore)
                put("lastExerciseIndex", lp.lastExerciseIndex)
                put("completedAtMillis", lp.completedAtMillis)
            }
            lessonsArr.put(obj)
        }
        json.put("lessonProgress", lessonsArr)

        // Word progress
        val wordsArr = JSONArray()
        wordProgress.forEach { wp ->
            val obj = JSONObject().apply {
                put("wordId", wp.wordId)
                put("isLearned", wp.isLearned)
                put("masteryCount", wp.masteryCount)
                put("isFavorite", wp.isFavorite)
                put("lastPracticedMillis", wp.lastPracticedMillis)
            }
            wordsArr.put(obj)
        }
        json.put("wordProgress", wordsArr)

        // Costumes
        val costumesArr = JSONArray()
        unlockedCostumes.forEach { costumesArr.put(it) }
        json.put("unlockedCostumes", costumesArr)

        // Badges
        val badgesArr = JSONArray()
        unlockedBadges.forEach { badgesArr.put(it) }
        json.put("unlockedBadges", badgesArr)

        return json.toString(2)
    }

    companion object {
        fun fromJsonString(jsonString: String): PokoBackupData {
            val json = JSONObject(jsonString.trim())
            val version = json.optInt("version", 1)
            val exportedAtMillis = json.optLong("exportedAtMillis", System.currentTimeMillis())

            val profileObj = json.getJSONObject("userProfile")
            val profile = UserProfileEntity(
                id = 1,
                coins = profileObj.optInt("coins", 50),
                totalStars = profileObj.optInt("totalStars", 0),
                streakDays = profileObj.optInt("streakDays", 1),
                activeGradeLevel = profileObj.optString("activeGradeLevel", "GRADE_1"),
                equippedCostumeId = profileObj.optString("equippedCostumeId", "costume_default"),
                totalCorrectAnswers = profileObj.optInt("totalCorrectAnswers", 0),
                totalExercisesPlayed = profileObj.optInt("totalExercisesPlayed", 0)
            )

            val lessonsList = mutableListOf<LessonProgressEntity>()
            val lessonsArray = json.optJSONArray("lessonProgress")
            if (lessonsArray != null) {
                for (i in 0 until lessonsArray.length()) {
                    val lpObj = lessonsArray.getJSONObject(i)
                    lessonsList.add(
                        LessonProgressEntity(
                            lessonId = lpObj.getString("lessonId"),
                            starsEarned = lpObj.optInt("starsEarned", 0),
                            isCompleted = lpObj.optBoolean("isCompleted", false),
                            highScore = lpObj.optInt("highScore", 0),
                            lastExerciseIndex = lpObj.optInt("lastExerciseIndex", 0),
                            completedAtMillis = lpObj.optLong("completedAtMillis", System.currentTimeMillis())
                        )
                    )
                }
            }

            val wordsList = mutableListOf<WordProgressEntity>()
            val wordsArray = json.optJSONArray("wordProgress")
            if (wordsArray != null) {
                for (i in 0 until wordsArray.length()) {
                    val wpObj = wordsArray.getJSONObject(i)
                    wordsList.add(
                        WordProgressEntity(
                            wordId = wpObj.getString("wordId"),
                            isLearned = wpObj.optBoolean("isLearned", true),
                            masteryCount = wpObj.optInt("masteryCount", 1),
                            isFavorite = wpObj.optBoolean("isFavorite", false),
                            lastPracticedMillis = wpObj.optLong("lastPracticedMillis", System.currentTimeMillis())
                        )
                    )
                }
            }

            val costumesList = mutableListOf<String>()
            val costumesArray = json.optJSONArray("unlockedCostumes")
            if (costumesArray != null) {
                for (i in 0 until costumesArray.length()) {
                    costumesList.add(costumesArray.getString(i))
                }
            }

            val badgesList = mutableListOf<String>()
            val badgesArray = json.optJSONArray("unlockedBadges")
            if (badgesArray != null) {
                for (i in 0 until badgesArray.length()) {
                    badgesList.add(badgesArray.getString(i))
                }
            }

            return PokoBackupData(
                version = version,
                exportedAtMillis = exportedAtMillis,
                userProfile = profile,
                lessonProgress = lessonsList,
                wordProgress = wordsList,
                unlockedCostumes = costumesList,
                unlockedBadges = badgesList
            )
        }
    }
}
