package com.example.zent.data.mapper

import com.example.zent.data.local.DeckEntity
import com.example.zent.data.local.QuestionEntity
import com.example.zent.data.local.TopicEntity
import com.example.zent.data.remote.DeckRemote
import com.example.zent.data.remote.QuestionRemote
import com.example.zent.data.remote.TopicRemote
import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Question
import com.example.zent.domain.model.Topic

// ==========================================
// DECK (MATÉRIA) MAPPERS
// ==========================================

fun DeckRemote.toEntity(): DeckEntity {
    return DeckEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        description = this.description,
        colorHex = this.colorHex,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

fun DeckEntity.toRemote(): DeckRemote {
    return DeckRemote(
        id = this.id,
        userId = this.userId,
        title = this.title,
        description = this.description,
        colorHex = this.colorHex,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

fun DeckEntity.toDomain(totalCards: Int = 0, toReviewCount: Int = 0): Deck {
    return Deck(
        id = this.id,
        title = this.title,
        description = this.description,
        colorHex = this.colorHex,
        totalCards = totalCards,
        toReviewCount = toReviewCount
    )
}

// NOVO: Do Domínio (Tela) para o Banco Local (SQLite)
fun Deck.toEntity(): DeckEntity {
    return DeckEntity(
        id = this.id,
        userId = "", // O repositório vai preencher com o ID do usuário
        title = this.title,
        description = this.description,
        colorHex = this.colorHex,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        isDeleted = false
    )
}

// NOVO: Do Domínio (Tela) para a Nuvem (Firebase)
fun Deck.toRemote(): DeckRemote {
    return DeckRemote(
        id = this.id,
        userId = "", // O repositório vai preencher com o ID do usuário
        title = this.title,
        description = this.description,
        colorHex = this.colorHex,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        isDeleted = false
    )
}

// ==========================================
// TOPIC (ASSUNTO) MAPPERS
// ==========================================

fun TopicRemote.toEntity(): TopicEntity {
    return TopicEntity(
        id = this.id,
        deckId = this.deckId,
        title = this.title,
        sourceMaterial = this.sourceMaterial,
        nextReviewDate = this.nextReviewDate,
        intervalDays = this.intervalDays,
        easeFactor = this.easeFactor,
        repetitions = this.repetitions,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

fun TopicEntity.toRemote(): TopicRemote {
    return TopicRemote(
        id = this.id,
        deckId = this.deckId,
        title = this.title,
        sourceMaterial = this.sourceMaterial,
        nextReviewDate = this.nextReviewDate,
        intervalDays = this.intervalDays,
        easeFactor = this.easeFactor,
        repetitions = this.repetitions,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

fun TopicEntity.toDomain(): Topic {
    return Topic(
        id = this.id,
        deckId = this.deckId,
        title = this.title,
        sourceMaterial = this.sourceMaterial,
        nextReviewDate = this.nextReviewDate,
        intervalDays = this.intervalDays,
        easeFactor = this.easeFactor,
        repetitions = this.repetitions
    )
}

// Do Domínio (Tela) para o Banco Local (SQLite)
fun Topic.toEntity(): TopicEntity {
    return TopicEntity(
        id = this.id,
        deckId = this.deckId,
        title = this.title,
        sourceMaterial = this.sourceMaterial,
        nextReviewDate = this.nextReviewDate,
        intervalDays = this.intervalDays,
        easeFactor = this.easeFactor,
        repetitions = this.repetitions,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        isDeleted = false
    )
}

// Do Domínio (Tela) para a Nuvem (Firebase)
fun Topic.toRemote(): TopicRemote {
    return TopicRemote(
        id = this.id,
        deckId = this.deckId,
        title = this.title,
        sourceMaterial = this.sourceMaterial,
        nextReviewDate = this.nextReviewDate,
        intervalDays = this.intervalDays,
        easeFactor = this.easeFactor,
        repetitions = this.repetitions,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        isDeleted = false
    )
}

// ==========================================
// QUESTION (QUESTÕES DA IA) MAPPERS
// ==========================================

fun QuestionRemote.toEntity(): QuestionEntity {
    return QuestionEntity(
        id = this.id,
        topicId = this.topicId,
        questionText = this.questionText,
        correctAnswer = this.correctAnswer,
        options = this.options,
        isDeleted = this.isDeleted
    )
}

fun QuestionEntity.toRemote(): QuestionRemote {
    return QuestionRemote(
        id = this.id,
        topicId = this.topicId,
        questionText = this.questionText,
        correctAnswer = this.correctAnswer,
        options = this.options,
        isDeleted = this.isDeleted
    )
}

fun QuestionEntity.toDomain(): Question {
    return Question(
        id = this.id,
        topicId = this.topicId,
        questionText = this.questionText,
        correctAnswer = this.correctAnswer,
        options = this.options
    )
}

// Do Domínio (Tela) para o Banco Local (SQLite)
fun Question.toEntity(): QuestionEntity {
    return QuestionEntity(
        id = this.id,
        topicId = this.topicId,
        questionText = this.questionText,
        correctAnswer = this.correctAnswer,
        options = this.options,
        isDeleted = false
    )
}

// Do Domínio (Tela) para a Nuvem (Firebase)
fun Question.toRemote(): QuestionRemote {
    return QuestionRemote(
        id = this.id,
        topicId = this.topicId,
        questionText = this.questionText,
        correctAnswer = this.correctAnswer,
        options = this.options,
        isDeleted = false
    )
}