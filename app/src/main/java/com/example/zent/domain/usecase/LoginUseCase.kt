package com.example.zent.domain.usecase

import com.example.zent.domain.model.User
import com.example.zent.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // 1. Validações Locais
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Por favor, preencha todos os campos."))
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        if (!email.matches(emailRegex)) {
            return Result.failure(Exception("Introduza um e-mail válido."))
        }

        // 2. Chama o Repositório
        return repository.login(email, password)
    }
}