package com.example.zent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zent.data.local.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>) // Inserir vários de uma vez (útil ao baixar do Firebase)

    // Traz todos os cartões de um baralho
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND isDeleted = 0")
    fun getCardsByDeck(deckId: String): Flow<List<FlashcardEntity>>

    // A MÁGICA DA REVISÃO: Traz apenas os cartões que estão na hora de estudar
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND nextReviewDate <= :currentTimestamp AND isDeleted = 0")
    suspend fun getCardsToReview(deckId: String, currentTimestamp: Long): List<FlashcardEntity>

    // Soft Delete
    @Query("UPDATE flashcards SET isDeleted = 1, updatedAt = :timestamp WHERE id = :cardId")
    suspend fun softDeleteFlashcard(cardId: String, timestamp: Long)

    // Deleta os cartões associados a um baralho que acabou de ser deletado
    @Query("UPDATE flashcards SET isDeleted = 1, updatedAt = :timestamp WHERE deckId = :deckId")
    suspend fun softDeleteCardsByDeck(deckId: String, timestamp: Long)
}