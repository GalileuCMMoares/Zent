package com.example.zent.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Question
import com.example.zent.domain.model.Topic
import com.example.zent.domain.repository.StudyRepository
import com.example.zent.domain.usecase.FlashcardGenerator
import com.example.zent.domain.usecase.auth.AuthUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class AuthEvent {
    object NavigateToHome : AuthEvent()
    object NavigateToLogin : AuthEvent()
    object NavigateBack : AuthEvent()
    data class ShowToast(val message: String) : AuthEvent()
}

class ZentViewModel(
    private val authUseCases: AuthUseCases,
    private val studyRepository: StudyRepository
) : ViewModel() {

    val decks: StateFlow<List<Deck>> = studyRepository.getDecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val eventChannel = Channel<AuthEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val _selectedDeck = MutableStateFlow<Deck?>(null)
    val selectedDeck: StateFlow<Deck?> = _selectedDeck.asStateFlow()

    private val _selectedDeckTopics = MutableStateFlow<List<Topic>>(emptyList())
    val selectedDeckTopics: StateFlow<List<Topic>> = _selectedDeckTopics.asStateFlow()

    private val _selectedDeckQuestions = MutableStateFlow<List<Question>>(emptyList())
    val selectedDeckQuestions: StateFlow<List<Question>> = _selectedDeckQuestions.asStateFlow()

    fun loadDeckDetails(deckId: String) {
        viewModelScope.launch { studyRepository.getDeckById(deckId).collect { _selectedDeck.value = it } }
        viewModelScope.launch { studyRepository.getTopicsByDeckId(deckId).collect { _selectedDeckTopics.value = it } }
        viewModelScope.launch { studyRepository.getQuestionsByDeckId(deckId).collect { _selectedDeckQuestions.value = it } }
    }

    private val _selectedTopic = MutableStateFlow<Topic?>(null)
    val selectedTopic: StateFlow<Topic?> = _selectedTopic.asStateFlow()

    private val _selectedTopicQuestions = MutableStateFlow<List<Question>>(emptyList())
    val selectedTopicQuestions: StateFlow<List<Question>> = _selectedTopicQuestions.asStateFlow()

    fun loadTopicDetails(topicId: String) {
        viewModelScope.launch { studyRepository.getTopicById(topicId).collect { _selectedTopic.value = it } }
        viewModelScope.launch { studyRepository.getQuestionsByTopicId(topicId).collect { _selectedTopicQuestions.value = it } }
    }

    fun generateTopicWithIA(
        deckId: String, name: String, description: String, material: String, difficulty: String,
        imageUri: Uri? = null, pdfUri: Uri? = null, context: Context
    ) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                var bitmap: Bitmap? = null
                if (imageUri != null) {
                    bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source) { decoder, _, _ -> decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE }
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    }
                }

                var pdfBytes: ByteArray? = null
                if (pdfUri != null) {
                    pdfBytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(pdfUri)?.use { it.readBytes() }
                    }
                }

                val topicId = UUID.randomUUID().toString()

                // MÁGICA 1: Salva o nível escolar e o material completo para as próximas revisões!
                val safeMaterial = if (material.isNotBlank()) material.take(15000) else "Material visual/PDF anexo."
                val savedContext = "[NIVEL:$difficulty]\n$safeMaterial"

                val newTopic = Topic(
                    id = topicId, deckId = deckId, title = name, sourceMaterial = savedContext,
                    nextReviewDate = 0L, intervalDays = 0, easeFactor = 2.5f, repetitions = 0
                )

                val generator = FlashcardGenerator()
                val generatedCards = generator.generateCards(material, difficulty, bitmap, pdfBytes)

                if (generatedCards.isEmpty()) throw Exception("A IA não conseguiu extrair perguntas.")

                val questionsToSave = generatedCards.map { card ->
                    Question(id = UUID.randomUUID().toString(), topicId = topicId, questionText = card.question, correctAnswer = card.correctAnswer, options = card.options)
                }

                studyRepository.createTopic(newTopic)
                studyRepository.createQuestions(questionsToSave)

                _authState.value = AuthState.Idle
                eventChannel.send(AuthEvent.ShowToast("Sucesso! ${questionsToSave.size} cartas criadas."))
                eventChannel.send(AuthEvent.NavigateBack)

            } catch (e: Exception) {
                _authState.value = AuthState.Idle
                eventChannel.send(AuthEvent.ShowToast("Erro na IA: ${e.message}"))
                eventChannel.send(AuthEvent.NavigateBack)
            }
        }
    }

    fun finishQuizAndUpdateTopic(topic: Topic, correctAnswers: Int, totalQuestions: Int) {
        viewModelScope.launch {
            try {
                // Algoritmo de Repetição Espaçada atualizado
                val accuracy = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f

                var newEaseFactor = topic.easeFactor
                var newInterval = topic.intervalDays
                var newRepetitions = topic.repetitions

                if (accuracy >= 0.8f) { // Acertou +80% (Fácil)
                    newRepetitions += 1
                    newEaseFactor += 0.15f
                    newInterval = if (newRepetitions == 1) 1 else if (newRepetitions == 2) 4 else (newInterval * newEaseFactor).toInt()
                } else if (accuracy >= 0.6f) { // Acertou entre 60 e 80% (Médio)
                    newRepetitions += 1
                    newInterval = if (newRepetitions == 1) 1 else if (newRepetitions == 2) 3 else (newInterval * newEaseFactor).toInt()
                } else { // Errou muito (Difícil)
                    newRepetitions = 0
                    newInterval = 1
                    newEaseFactor = maxOf(1.3f, newEaseFactor - 0.2f)
                }

                val nextReview = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)

                // Atualiza o banco e o estado atual
                studyRepository.createTopic(topic.copy(nextReviewDate = nextReview, intervalDays = newInterval, easeFactor = newEaseFactor, repetitions = newRepetitions))
                _selectedTopic.value = _selectedTopic.value?.copy(nextReviewDate = nextReview, intervalDays = newInterval, easeFactor = newEaseFactor, repetitions = newRepetitions)

                // MÁGICA 2: GERA AS NOVAS CARTAS EM BACKGROUND PARA A PRÓXIMA SESSÃO
                launch(Dispatchers.IO) {
                    try {
                        val savedMaterial = topic.sourceMaterial
                        val level = if (savedMaterial.startsWith("[NIVEL:")) savedMaterial.substringAfter("[NIVEL:").substringBefore("]\n") else "Ensino Médio"
                        val contextText = if (savedMaterial.startsWith("[NIVEL:")) savedMaterial.substringAfter("]\n") else savedMaterial

                        val generator = FlashcardGenerator()
                        val newCards = generator.generateCards(contextText, level, null, null) // PDFs/Fotos não serão reutilizados de forma nativa por agora, focaremos no texto salvo

                        if (newCards.isNotEmpty()) {
                            val questionsToSave = newCards.map { card -> Question(id = UUID.randomUUID().toString(), topicId = topic.id, questionText = card.question, correctAnswer = card.correctAnswer, options = card.options) }
                            studyRepository.createQuestions(questionsToSave)
                        }
                    } catch (e: Exception) {
                        // Silencioso. Se falhar, o aluno reverá as antigas ou o app tentará gerar depois.
                    }
                }

            } catch (e: Exception) {
                eventChannel.send(AuthEvent.ShowToast("Erro ao salvar progresso."))
            }
        }
    }

    // Funções de Auth e afins...
    fun getCurrentUser() = authUseCases.getCurrentUser()
    fun checkIfUserIsLoggedIn() { if (getCurrentUser() != null) viewModelScope.launch { eventChannel.send(AuthEvent.NavigateToHome) } }
    fun login(email: String, password: String) {}
    fun register(name: String, email: String, password: String, confirmPassword: String) {}
    fun sendPasswordReset(email: String) {}
    fun logout() { viewModelScope.launch { authUseCases.logout(); eventChannel.send(AuthEvent.NavigateToLogin) } }
    fun resetState() { if (_authState.value is AuthState.Error) _authState.value = AuthState.Idle }

    fun createDeck(name: String, description: String, colorHex: String) {
        if (name.isBlank()) return
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val newDeck = Deck(id = UUID.randomUUID().toString(), title = name, description = description, colorHex = colorHex)
            studyRepository.createDeck(newDeck).onSuccess {
                _authState.value = AuthState.Idle
                eventChannel.send(AuthEvent.ShowToast("Matéria criada!"))
                eventChannel.send(AuthEvent.NavigateBack)
            }.onFailure { _authState.value = AuthState.Idle; eventChannel.send(AuthEvent.NavigateBack) }
        }
    }
}