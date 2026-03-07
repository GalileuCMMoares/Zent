package com.example.zent.data.remote

import com.google.firebase.firestore.DocumentId

data class DeckRemote(
    @DocumentId val id: String = "", // O Firestore preenche o ID do documento aqui
    val userId: String = "",
    val title: String = "",
    val colorHex: String = "",
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
)