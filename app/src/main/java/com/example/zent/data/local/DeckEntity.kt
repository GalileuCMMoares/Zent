package com.example.zent.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: String,
    var userId: String,
    val title: String,
    val description: String,
    val colorHex: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false
)