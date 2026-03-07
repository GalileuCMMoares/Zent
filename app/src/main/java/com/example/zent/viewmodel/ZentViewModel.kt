package com.example.zent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zent.domain.usecase.auth.AuthUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class AuthEvent {
    object NavigateToHome : AuthEvent()
    object NavigateToLogin : AuthEvent()
    data class ShowToast(val message: String) : AuthEvent()
}

class ZentViewModel(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    // ESTADO: Serve apenas para coisas visuais que ficam na tela (Loading, Erros no texto)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // EVENTOS: Ações de "disparo único" (Navegar de tela, Toast)
    // O Channel.BUFFERED garante que o evento do init não se perde enquanto a tela carrega
    private val eventChannel = Channel<AuthEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()
    fun getCurrentUser() = authUseCases.getCurrentUser()

    init {
        checkIfUserIsLoggedIn()
    }

    // Verifica se já existe um utilizador logado quando o app abre
    private fun checkIfUserIsLoggedIn() {
        val currentUser = authUseCases.getCurrentUser()
        if (currentUser != null) {
            // Em vez de mudar o state, disparamos o evento para ir direto à Home
            viewModelScope.launch {
                eventChannel.send(AuthEvent.NavigateToHome)
            }
        }
    }

    // Função que será chamada pelo botão "Entrar" da LoginScreen
    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading // Avisa a tela para mostrar o Loading

        viewModelScope.launch {
            val result = authUseCases.login(email, password)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle // Tira o loading da tela
                    eventChannel.send(AuthEvent.NavigateToHome) // Dispara a navegação!
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Erro desconhecido")
                }
            )
        }
    }

    // Função que será chamada pelo botão "Criar conta" da RegisterScreen
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authUseCases.register(name, email, password, confirmPassword)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle // Tira o loading
                    eventChannel.send(AuthEvent.NavigateToHome) // Navega
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Erro desconhecido")
                }
            )
        }
    }

    // Função para o ecrã "Esqueceu a senha?"
    fun sendPasswordReset(email: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authUseCases.sendPasswordReset.invoke(email)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle
                    // Avisa a tela para mostrar o Toast e voltar ao Login
                    eventChannel.send(AuthEvent.ShowToast("Link de recuperação enviado com sucesso!"))
                    eventChannel.send(AuthEvent.NavigateToLogin)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Erro desconhecido")
                }
            )
        }
    }

    // Função de Sair da Conta (Logout)
    fun logout() {
        viewModelScope.launch {
            authUseCases.logout()
            eventChannel.send(AuthEvent.NavigateToLogin)
        }
    }

    // Útil para limpar mensagens de erro quando o utilizador começa a escrever de novo
    fun resetState() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}