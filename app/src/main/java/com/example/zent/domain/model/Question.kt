package com.example.zent.domain.model

data class Question(
    val id: String,
    val topicId: String,         // Liga esta pergunta ao Assunto (Topic)
    val questionText: String,    // Pergunta
    val correctAnswer: String,   // Resposta
    val options: String          // Opções alternativas (A, B, C, D) em JSON
)