package com.example.zent.data.remote

import com.google.firebase.firestore.DocumentId

data class TopicRemote(
    @DocumentId val id: String = "",
    val deckId: String = "",          // Relaciona com o Deck
    val title: String = "",           // Título do Assunto gerado ou digitado
    val sourceMaterial: String = "",  // O texto base usado pela IA

    // --- DADOS DA CURVA DE ESQUECIMENTO (SRS) ---
    val nextReviewDate: Long = 0L,
    val intervalDays: Int = 0,
    val easeFactor: Float = 2.5f,
    val repetitions: Int = 0,

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val isDeleted: Boolean = false
)
