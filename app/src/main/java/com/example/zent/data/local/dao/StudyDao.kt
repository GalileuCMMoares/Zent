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

    @Query("SELECT * FROM topics WHERE deckId = :deckId AND isDeleted = 0")
    fun getTopicsByDeck(deckId: String): Flow<List<TopicEntity>>

    // NOVO: Busca apenas um assunto específico (Para a tela de Detalhes do Assunto)
    @Query("SELECT * FROM topics WHERE id = :topicId LIMIT 1")
    fun observeTopicById(topicId: String): Flow<TopicEntity?>

    @Query("SELECT * FROM topics WHERE deckId = :deckId AND nextReviewDate <= :currentTimestamp AND isDeleted = 0")
    suspend fun getTopicsToReview(deckId: String, currentTimestamp: Long): List<TopicEntity>

    @Query("UPDATE topics SET nextReviewDate = :nextDate, intervalDays = :interval, easeFactor = :ease, repetitions = :reps WHERE id = :topicId")
    suspend fun updateTopicSrsData(topicId: String, nextDate: Long, interval: Int, ease: Float, reps: Int)


    // --- QUESTÕES DO QUIZ ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    // NOVO: Busca as questões de um Assunto em tempo real
    @Query("SELECT * FROM questions WHERE topicId = :topicId AND isDeleted = 0")
    fun observeQuestionsByTopic(topicId: String): Flow<List<QuestionEntity>>

    // NOVO: Busca TODAS as questões de uma Matéria (Deck) para calcularmos as estatísticas reais
    @Query("SELECT questions.* FROM questions INNER JOIN topics ON questions.topicId = topics.id WHERE topics.deckId = :deckId AND questions.isDeleted = 0")
    fun observeQuestionsByDeck(deckId: String): Flow<List<QuestionEntity>>
}