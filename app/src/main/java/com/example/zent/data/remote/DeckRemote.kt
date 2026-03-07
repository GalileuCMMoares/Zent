package com.example.zent.data.remote

import com.google.firebase.firestore.DocumentId

data class DeckRemote(
    @DocumentId val id: String = "", // O Firestore preenche o ID do documento aqui
    val userId: String = "",
    val title: String = "",
    val description: String = "", // <- Adicionado para bater com o seu design!
    val colorHex: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val isDeleted: Boolean = false
)