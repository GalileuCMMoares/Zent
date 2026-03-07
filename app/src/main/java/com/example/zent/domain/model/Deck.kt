package com.example.zent.domain.model

data class Deck(
    val id: String,
    val title: String,
    val colorHex: String,
    val totalCards: Int = 0, // Calculado localmente para mostrar na tela
    val toReviewCount: Int = 0 // Cartas que precisam ser revisadas hoje
)
