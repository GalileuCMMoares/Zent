package com.example.zent.domain.model

data class Topic(
    val id: String,
    val deckId: String,          // Liga este assunto à matéria (Deck)
    val title: String,           // Ex: "A Era Vargas"
    val sourceMaterial: String,  // O texto/PDF que a IA leu

    // Motor da Repetição Espaçada
    val nextReviewDate: Long,
    val intervalDays: Int,
    val easeFactor: Float,
    val repetitions: Int
)