package com.example.zent.data.remote

import com.google.firebase.firestore.DocumentId

data class QuestionRemote(
    @DocumentId val id: String = "",
    val topicId: String = "",         // Relaciona com o Topic
    val questionText: String = "",    // A pergunta em si
    val correctAnswer: String = "",   // A resposta correta
    val options: String = "",         // JSON string com as opções (ex: '["A", "B", "C"]')
    val isDeleted: Boolean = false
)