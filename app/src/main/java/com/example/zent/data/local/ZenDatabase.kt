package com.example.zent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.zent.data.local.dao.DeckDao
import com.example.zent.data.local.dao.StudyDao

@Database(
    entities = [DeckEntity::class, TopicEntity::class, QuestionEntity::class],
    version = 3,
    exportSchema = false
)
abstract class ZentDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao
    abstract fun studyDao(): StudyDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE questions ADD COLUMN difficulty TEXT NOT NULL DEFAULT 'MEDIUM'")
            }
        }
    }
}
