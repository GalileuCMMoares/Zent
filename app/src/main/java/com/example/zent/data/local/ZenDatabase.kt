package com.example.zent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.zent.data.local.dao.DeckDao
import com.example.zent.data.local.dao.FlashcardDao

@Database(
    entities = [DeckEntity::class, FlashcardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ZentDatabase : RoomDatabase() {

    // O Room vai gerar o código dessas funções automaticamente graças ao KSP!
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao

}