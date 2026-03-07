package com.example.zent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zent.data.local.QuestionEntity
import com.example.zent.data.local.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {

    // --- ASSUNTOS (TOPICS) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    // Traz todos os assuntos de uma matéria para listar na tela
    @Query("SELECT * FROM topics WHERE deckId = :deckId AND isDeleted = 0")
    fun getTopicsByDeck(deckId: String): Flow<List<TopicEntity>>

    // A MÁGICA: Busca os Assuntos que estão na hora de revisar (Data de revisão <= Hoje)
    @Query("SELECT * FROM topics WHERE deckId = :deckId AND nextReviewDate <= :currentTimestamp AND isDeleted = 0")
    suspend fun getTopicsToReview(deckId: String, currentTimestamp: Long): List<TopicEntity>

    // Atualiza os dados do algoritmo após o aluno terminar o Quiz
    @Query("UPDATE topics SET nextReviewDate = :nextDate, intervalDays = :interval, easeFactor = :ease, repetitions = :reps WHERE id = :topicId")
    suspend fun updateTopicSrsData(topicId: String, nextDate: Long, interval: Int, ease: Float, reps: Int)


    // --- QUESTÕES DO QUIZ ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    // Quando o aluno clicar no Assunto, puxamos as questões para montar a tela do Quiz
    @Query("SELECT * FROM questions WHERE topicId = :topicId AND isDeleted = 0")
    suspend fun getQuestionsForTopic(topicId: String): List<QuestionEntity>
}