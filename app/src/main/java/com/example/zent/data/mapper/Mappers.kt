package com.example.zent.data.mapper

import com.example.zent.data.local.DeckEntity
import com.example.zent.data.remote.DeckRemote
import com.example.zent.domain.model.Deck

fun DeckRemote.toEntity(): DeckEntity {
    return DeckEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        colorHex = this.colorHex,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt
    )
}

fun DeckEntity.toDomain(totalCards: Int = 0, toReviewCount: Int = 0): Deck {
    return Deck(
        id = this.id,
        title = this.title,
        colorHex = this.colorHex,
        totalCards = totalCards,
        toReviewCount = toReviewCount
    )
}