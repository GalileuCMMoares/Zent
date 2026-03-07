package com.example.zent.data.repository

import com.example.zent.data.local.DeckEntity
import com.example.zent.data.local.dao.DeckDao
import com.example.zent.data.local.dao.StudyDao // Import do novo DAO
import com.example.zent.data.mapper.toDomain
import com.example.zent.data.mapper.toEntity
import com.example.zent.data.mapper.toRemote
import com.example.zent.domain.model.Deck
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

            val deckEntity = DeckEntity(
                id = deck.id,
                userId = userId,
                title = deck.title,
                description = deck.description,
                colorHex = deck.colorHex,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            deckDao.insertDeck(deckEntity)

            val deckRemote = deckEntity.toRemote()

            firestore.collection("users")
                .document(userId)
                .collection("decks")
                .document(deck.id)
                .set(deckRemote)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDecks(): Flow<List<Deck>> {
        val userId = auth.currentUser?.uid ?: ""
        return deckDao.getDecksByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDeckById(deckId: String): Flow<Deck?> {
        return deckDao.observeDeckById(deckId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getTopicsByDeckId(deckId: String): Flow<List<Topic>> {
        return studyDao.getTopicsByDeck(deckId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // NOVO: Salva o assunto (Topic) localmente e na nuvem
    override suspend fun createTopic(topic: Topic): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não está logado")

            // 1. Salva no SQLite (Room)
            val topicEntity = topic.toEntity()
            studyDao.insertTopic(topicEntity)

            // 2. Salva no Firebase
            val topicRemote = topic.toRemote()
            firestore.collection("users")
                .document(userId)
                .collection("decks")
                .document(topic.deckId)
                .collection("topics") // Subpasta de assuntos dentro da matéria
                .document(topic.id)
                .set(topicRemote)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}