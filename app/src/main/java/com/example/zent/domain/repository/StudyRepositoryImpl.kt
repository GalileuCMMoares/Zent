package com.example.zent.data.repository

import com.example.zent.data.local.dao.DeckDao
import com.example.zent.data.local.dao.StudyDao
import com.example.zent.data.mapper.toDomain
import com.example.zent.data.mapper.toEntity
import com.example.zent.data.mapper.toRemote
import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Question
import com.example.zent.domain.model.Topic
import com.example.zent.domain.repository.StudyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class StudyRepositoryImpl(
    private val deckDao: DeckDao,
    private val studyDao: StudyDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : StudyRepository {

    override suspend fun createDeck(deck: Deck): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não está logado")
            val deckEntity = deck.toEntity()
            deckEntity.userId = userId
            deckDao.insertDeck(deckEntity)
            firestore.collection("users").document(userId).collection("decks").document(deck.id).set(deckEntity.toRemote()).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override fun getDecks(): Flow<List<Deck>> {
        val userId = auth.currentUser?.uid ?: ""
        return deckDao.getDecksByUser(userId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getDeckById(deckId: String): Flow<Deck?> {
        return deckDao.observeDeckById(deckId).map { it?.toDomain() }
    }

    override fun getTopicsByDeckId(deckId: String): Flow<List<Topic>> {
        return studyDao.getTopicsByDeck(deckId).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createTopic(topic: Topic): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não está logado")
            studyDao.insertTopic(topic.toEntity())
            firestore.collection("users").document(userId).collection("decks").document(topic.deckId).collection("topics").document(topic.id).set(topic.toRemote()).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- NOVAS IMPLEMENTAÇÕES ---

    override fun getTopicById(topicId: String): Flow<Topic?> {
        return studyDao.observeTopicById(topicId).map { it?.toDomain() }
    }

    override fun getQuestionsByTopicId(topicId: String): Flow<List<Question>> {
        return studyDao.observeQuestionsByTopic(topicId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getQuestionsByDeckId(deckId: String): Flow<List<Question>> {
        return studyDao.observeQuestionsByDeck(deckId).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createQuestions(questions: List<Question>): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não logado")
            questions.forEach { q ->
                studyDao.insertQuestion(q.toEntity())
                firestore.collection("users").document(userId).collection("decks").document(q.topicId) // Simplificando caminho
                    .collection("questions").document(q.id).set(q.toRemote()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun deleteQuestionsByTopicId(topicId: String): Result<Unit> {
        return try {
            studyDao.deleteQuestionsByTopicId(topicId)
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não logado")
            val questionsRef = firestore.collection("users").document(userId)
                .collection("decks").document(topicId)
                .collection("questions")
            val snapshot = questionsRef.get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateTopicSrsData(
        topicId: String, nextDate: Long, interval: Int, ease: Float, reps: Int
    ): Result<Unit> {
        return try {
            studyDao.updateTopicSrsData(topicId, nextDate, interval, ease, reps)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override fun getAllTopics(): Flow<List<Topic>> {
        val userId = auth.currentUser?.uid ?: ""
        return studyDao.getAllTopicsByUser(userId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getAllQuestions(): Flow<List<Question>> {
        val userId = auth.currentUser?.uid ?: ""
        return studyDao.getAllQuestionsByUser(userId).map { entities -> entities.map { it.toDomain() } }
    }
}
