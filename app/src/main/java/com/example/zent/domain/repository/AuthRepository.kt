package com.example.zent.domain.repository

import com.example.zent.domain.model.User

interface AuthRepository {
    // Login com email e senha
    suspend fun login(email: String, password: String): Result<User>

    // Cadastro de novo usuário
    suspend fun register(name: String, email: String, password: String): Result<User>

    // Recuperação de senha
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    // Deslogar
    suspend fun logout()

    // Pegar o usuário que já está logado (para não pedir login toda vez que abrir o app)
    fun getCurrentUser(): User?
}