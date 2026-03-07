package com.example.zent.data.remote

import com.google.firebase.firestore.DocumentId

data class FlashcardRemote(
    @DocumentId val id: String = "",
    val deckId: String = "",
    val question: String = "",
    val answer: String = "",
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val nextReviewDate: Long = 0L,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
)
