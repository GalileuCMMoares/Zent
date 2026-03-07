package com.example.zent.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey val id: String,
    val deckId: String,
    val question: String,
    val answer: String,
    val easeFactor: Float,
    val intervalDays: Int,
    val repetitions: Int,
    val nextReviewDate: Long,
    val isDeleted: Boolean,
    val updatedAt: Long
)