package com.example.zent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zent.data.local.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Query("SELECT * FROM decks WHERE userId = :userId AND isDeleted = 0")
    fun getDecksByUser(userId: String): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :deckId AND isDeleted = 0")
    suspend fun getDeckById(deckId: String): DeckEntity?

    // NOVO: Busca uma matéria específica em tempo real (Flow) para a tela de Detalhes
    @Query("SELECT * FROM decks WHERE id = :deckId AND isDeleted = 0 LIMIT 1")
    fun observeDeckById(deckId: String): Flow<DeckEntity?>

    @Query("UPDATE decks SET isDeleted = 1, updatedAt = :timestamp WHERE id = :deckId")
    suspend fun softDeleteDeck(deckId: String, timestamp: Long)

    @Query("SELECT * FROM decks WHERE isDeleted = 1")
    suspend fun getDeletedDecksToSync(): List<DeckEntity>
}