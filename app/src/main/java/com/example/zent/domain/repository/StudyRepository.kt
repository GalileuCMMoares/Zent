package com.example.zent.domain.repository

import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Topic // Não esqueça deste import
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    suspend fun createDeck(deck: Deck): Result<Unit>
    fun getDecks(): Flow<List<Deck>>

    fun getDeckById(deckId: String): Flow<Deck?>
    fun getTopicsByDeckId(deckId: String): Flow<List<Topic>>

    // NOVO: Função para salvar o Assunto
    suspend fun createTopic(topic: Topic): Result<Unit>
}