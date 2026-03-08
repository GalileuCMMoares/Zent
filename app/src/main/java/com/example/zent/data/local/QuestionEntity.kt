package com.example.zent.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val topicId: String,         // A qual Assunto este quiz pertence?
    val questionText: String,    // A pergunta gerada pela IA
    val correctAnswer: String,   // A resposta correta
    val options: String,         // Pode guardar um JSON com as alternativas (A, B, C, D) se for múltipla escolha
    val difficulty: String = "MEDIUM", // "EASY", "MEDIUM", "HARD"
    val isDeleted: Boolean = false
)