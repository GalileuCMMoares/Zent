package com.example.zent.domain.model

data class Flashcard(
    val id: String,
    val deckId: String,
    val question: String,
    val answer: String, // Ou lista de opções, se for múltipla escolha
    val nextReviewDate: Long // Quando essa carta deve aparecer de novo
)