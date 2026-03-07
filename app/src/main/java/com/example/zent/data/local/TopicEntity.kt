package com.example.zent.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val deckId: String,          // A qual matéria pertence?
    val title: String,           // Ex: "A Era Vargas"
    val sourceMaterial: String,  // O texto ou link do PDF que a IA usou

    // --- O MOTOR DO TEMPO (SRS) FICA AQUI ---
    val nextReviewDate: Long,    // Quando o aluno precisa refazer o quiz deste assunto?
    val intervalDays: Int = 0,   // Intervalo atual em dias
    val easeFactor: Float = 2.5f,
    val repetitions: Int = 0,

    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false
)