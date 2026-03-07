package com.example.zent.domain.model

data class Deck(
    val id: String,
    val title: String,
    val description: String, // <- Novo campo adicionado para a UI!
    val colorHex: String,
    val totalCards: Int = 0, // Pode representar a soma de questões ou assuntos
    val toReviewCount: Int = 0 // Assuntos que precisam ser revisados hoje
)
