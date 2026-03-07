package com.example.zent.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val colorHex: String,
    val isDeleted: Boolean, // Importante para o Offline-First
    val updatedAt: Long
)