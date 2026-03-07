package com.example.zent.viewmodel

import com.example.zent.domain.model.User

sealed class AuthState {
    object Idle : AuthState() // Parado, à espera que o utilizador faça algo
    object Loading : AuthState() // A carregar (mostrar um spinner/loading)
    data class Success(val user: User) : AuthState() // Sucesso no login/registo!
    data class Error(val message: String) : AuthState() // Ups, algo correu mal
}