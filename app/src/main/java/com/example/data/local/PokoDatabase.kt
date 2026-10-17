package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        LessonProgressEntity::class,
        WordProgressEntity::class,
        UnlockedCostumeEntity::class,
        UnlockedBadgeEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class PokoDatabase : RoomDatabase() {

    abstract fun pokoDao(): PokoDao

    companion object {
        @Volatile
        private var INSTANCE: PokoDatabase? = null

        fun getDatabase(context: Context): PokoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokoDatabase::class.java,
                    "poko_english_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
