package com.example.zent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zent.data.local.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    // Salva um baralho novo ou atualiza um existente (REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    // Busca todos os baralhos de um usuário que NÃO foram deletados
    // Usamos Flow para que a tela atualize sozinha se um baralho novo for adicionado!
    @Query("SELECT * FROM decks WHERE userId = :userId AND isDeleted = 0")
    fun getDecksByUser(userId: String): Flow<List<DeckEntity>>

    // Busca um baralho específico pelo ID
    @Query("SELECT * FROM decks WHERE id = :deckId AND isDeleted = 0")
    suspend fun getDeckById(deckId: String): DeckEntity?

    // Soft Delete: Apenas marca como deletado e atualiza o horário
    @Query("UPDATE decks SET isDeleted = 1, updatedAt = :timestamp WHERE id = :deckId")
    suspend fun softDeleteDeck(deckId: String, timestamp: Long)

    // Busca os baralhos que precisam subir pro Firebase (os deletados ou atualizados recentemente)
    // (Usaremos isso mais para frente na sincronização)
    @Query("SELECT * FROM decks WHERE isDeleted = 1")
    suspend fun getDeletedDecksToSync(): List<DeckEntity>
}