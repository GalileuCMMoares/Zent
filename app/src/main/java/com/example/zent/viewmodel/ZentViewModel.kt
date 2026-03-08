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
import com.example.zent.domain.util.QuestionDifficulty
import com.example.zent.domain.util.QuestionResult
import com.example.zent.domain.util.SpacedRepetitionAlgorithm
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

    // Estatísticas globais: todos os assuntos e questões do usuário
    val allTopics: StateFlow<List<Topic>> = studyRepository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<Question>> = studyRepository.getAllQuestions()
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

    private val _isGeneratingCards = MutableStateFlow(false)
    val isGeneratingCards: StateFlow<Boolean> = _isGeneratingCards.asStateFlow()

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
                    Question(
                        id = UUID.randomUUID().toString(),
                        topicId = topicId,
                        questionText = card.question,
                        correctAnswer = card.correctAnswer,
                        options = card.options,
                        difficulty = card.difficulty
                    )
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

    fun finishQuizAndUpdateTopic(
        topic: Topic,
        questionResults: List<Pair<String, Boolean>> // Lista de (difficulty, wasCorrect)
    ) {
        viewModelScope.launch {
            try {
                // 1. Converte os resultados brutos para objetos do domínio
                val results = questionResults.map { (difficulty, wasCorrect) ->
                    QuestionResult(
                        difficulty = QuestionDifficulty.fromString(difficulty),
                        wasCorrect = wasCorrect
                    )
                }

                // 2. Executa o algoritmo de repetição espaçada com acurácia ponderada
                val srsResult = SpacedRepetitionAlgorithm.processQuizResults(
                    results = results,
                    currentInterval = topic.intervalDays,
                    currentEaseFactor = topic.easeFactor,
                    currentRepetitions = topic.repetitions
                )

                // 3. Atualiza os dados SRS do tópico no banco
                studyRepository.updateTopicSrsData(
                    topicId = topic.id,
                    nextDate = srsResult.nextReviewDate,
                    interval = srsResult.intervalDays,
                    ease = srsResult.easeFactor,
                    reps = srsResult.repetitions
                )

                // 4. Atualiza o estado local
                _selectedTopic.value = _selectedTopic.value?.copy(
                    nextReviewDate = srsResult.nextReviewDate,
                    intervalDays = srsResult.intervalDays,
                    easeFactor = srsResult.easeFactor,
                    repetitions = srsResult.repetitions
                )

            } catch (e: Exception) {
                eventChannel.send(AuthEvent.ShowToast("Erro ao salvar progresso."))
            }
        }
    }

    /**
     * Chamado quando o aluno clica em "Revisar Assunto" e a data de revisão já chegou.
     * Deleta as cartas antigas, gera novas com a IA e navega para o quiz.
     */
    fun startReviewSession(topic: Topic, onReady: () -> Unit) {
        _isGeneratingCards.value = true

        viewModelScope.launch {
            try {
                // 1. Deleta as cartas antigas
                studyRepository.deleteQuestionsByTopicId(topic.id)

                // 2. Gera novas cartas com a IA
                val savedMaterial = topic.sourceMaterial
                val level = if (savedMaterial.startsWith("[NIVEL:"))
                    savedMaterial.substringAfter("[NIVEL:").substringBefore("]\n")
                else "Ensino Médio"
                val contextText = if (savedMaterial.startsWith("[NIVEL:"))
                    savedMaterial.substringAfter("]\n")
                else savedMaterial

                val generator = FlashcardGenerator()
                val newCards = withContext(Dispatchers.IO) {
                    generator.generateCards(contextText, level, null, null)
                }

                if (newCards.isEmpty()) throw Exception("A IA não conseguiu gerar novas questões.")

                val questionsToSave = newCards.map { card ->
                    Question(
                        id = UUID.randomUUID().toString(),
                        topicId = topic.id,
                        questionText = card.question,
                        correctAnswer = card.correctAnswer,
                        options = card.options,
                        difficulty = card.difficulty
                    )
                }
                studyRepository.createQuestions(questionsToSave)

                _isGeneratingCards.value = false
                onReady()

            } catch (e: Exception) {
                _isGeneratingCards.value = false
                eventChannel.send(AuthEvent.ShowToast("Erro ao gerar novas cartas: ${e.message}"))
            }
        }
    }

    // Funções de Auth e afins...
    fun getCurrentUser() = authUseCases.getCurrentUser()
    fun checkIfUserIsLoggedIn() { if (getCurrentUser() != null) viewModelScope.launch { eventChannel.send(AuthEvent.NavigateToHome) } }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authUseCases.login(email, password)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle
                    eventChannel.send(AuthEvent.NavigateToHome)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Erro desconhecido")
                }
            )
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authUseCases.register(name, email, password, confirmPassword)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle
                    eventChannel.send(AuthEvent.NavigateToHome)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Erro desconhecido")
                }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authUseCases.sendPasswordReset.invoke(email)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle
                    eventChannel.send(AuthEvent.ShowToast("Link de recuperação enviado com sucesso!"))
                    eventChannel.send(AuthEvent.NavigateToLogin)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Erro desconhecido")
                }
            )
        }
    }

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
