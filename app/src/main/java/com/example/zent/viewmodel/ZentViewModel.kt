package com.example.zent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Topic // Import necessário
import com.example.zent.domain.repository.StudyRepository
import com.example.zent.domain.usecase.auth.AuthUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    // ==========================================
    // ESTADO DA TELA DE VISUALIZAÇÃO DA MATÉRIA
    // ==========================================

    private val _selectedDeck = MutableStateFlow<Deck?>(null)
    val selectedDeck: StateFlow<Deck?> = _selectedDeck.asStateFlow()

    private val _selectedDeckTopics = MutableStateFlow<List<Topic>>(emptyList())
    val selectedDeckTopics: StateFlow<List<Topic>> = _selectedDeckTopics.asStateFlow()

    fun loadDeckDetails(deckId: String) {
        viewModelScope.launch {
            studyRepository.getDeckById(deckId).collect { deck ->
                _selectedDeck.value = deck
            }
        }

        viewModelScope.launch {
            studyRepository.getTopicsByDeckId(deckId).collect { topics ->
                _selectedDeckTopics.value = topics
            }
        }
    }

    // ==========================================
    // FUNÇÕES DE ASSUNTOS (TOPICS) E IA
    // ==========================================

    fun generateTopicWithIA(deckId: String, name: String, description: String, material: String) {
        // Mostramos o loading (Passo 3 usa isso)
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                // Simula a IA processando o material por 3 segundos
                delay(3000)

                val newTopic = Topic(
                    id = UUID.randomUUID().toString(),
                    deckId = deckId,
                    title = name,
                    sourceMaterial = description, // Idealmente guardaremos o material aqui e a IA cria as Questions depois
                    nextReviewDate = 0L, // 0 = Hoje
                    intervalDays = 0,
                    easeFactor = 2.5f,
                    repetitions = 0
                )

                val result = studyRepository.createTopic(newTopic)

                result.fold(
                    onSuccess = {
                        _authState.value = AuthState.Idle
                        eventChannel.send(AuthEvent.ShowToast("Assunto e cartas gerados com sucesso!"))
                        eventChannel.send(AuthEvent.NavigateBack)
                    },
                    onFailure = { e ->
                        _authState.value = AuthState.Idle
                        eventChannel.send(AuthEvent.ShowToast("Assunto salvo localmente. (Erro nuvem: ${e.message})"))
                        eventChannel.send(AuthEvent.NavigateBack)
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Idle
                eventChannel.send(AuthEvent.ShowToast("Erro na IA: ${e.message}"))
                eventChannel.send(AuthEvent.NavigateBack)
            }
        }
    }

    // ==========================================
    // RESTANTE DO CÓDIGO (Auth & Decks)
    // ==========================================

    fun getCurrentUser() = authUseCases.getCurrentUser()

    fun checkIfUserIsLoggedIn() {
        val currentUser = authUseCases.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                eventChannel.send(AuthEvent.NavigateToHome)
            }
        }
    }

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

    fun logout() {
        viewModelScope.launch {
            authUseCases.logout()
            eventChannel.send(AuthEvent.NavigateToLogin)
        }
    }

    fun resetState() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    fun createDeck(name: String, description: String, colorHex: String) {
        if (name.isBlank()) {
            viewModelScope.launch {
                eventChannel.send(AuthEvent.ShowToast("O nome da matéria é obrigatório."))
            }
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val newDeck = Deck(
                id = UUID.randomUUID().toString(),
                title = name,
                description = description,
                colorHex = colorHex
            )

            val result = studyRepository.createDeck(newDeck)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle
                    eventChannel.send(AuthEvent.ShowToast("Matéria criada com sucesso!"))
                    eventChannel.send(AuthEvent.NavigateBack)
                },
                onFailure = { e ->
                    _authState.value = AuthState.Idle
                    eventChannel.send(AuthEvent.ShowToast("Matéria salva localmente. (Erro nuvem: ${e.message})"))
                    eventChannel.send(AuthEvent.NavigateBack)
                }
            )
        }
    }
}