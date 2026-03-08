package com.example.zent.domain.repository

import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Question
import com.example.zent.domain.model.Topic // Não esqueça deste import
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    suspend fun createDeck(deck: Deck): Result<Unit>
    fun getDecks(): Flow<List<Deck>>

    fun getDeckById(deckId: String): Flow<Deck?>
    fun getTopicsByDeckId(deckId: String): Flow<List<Topic>>
    suspend fun createTopic(topic: Topic): Result<Unit>

    // NOVO: Funções de Busca Específica
    fun getTopicById(topicId: String): Flow<Topic?>
    fun getQuestionsByTopicId(topicId: String): Flow<List<Question>>
    fun getQuestionsByDeckId(deckId: String): Flow<List<Question>>
    suspend fun createQuestions(questions: List<Question>): Result<Unit>
    suspend fun deleteQuestionsByTopicId(topicId: String): Result<Unit>
    suspend fun updateTopicSrsData(topicId: String, nextDate: Long, interval: Int, ease: Float, reps: Int): Result<Unit>

    // Estatísticas globais
    fun getAllTopics(): Flow<List<Topic>>
    fun getAllQuestions(): Flow<List<Question>>
}