package com.example.zent.domain.usecase

import com.example.zent.domain.model.User
import com.example.zent.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {

        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return Result.failure(Exception("Por favor, preencha todos os campos."))
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        if (!email.matches(emailRegex)) {
            return Result.failure(Exception("Introduza um e-mail válido."))
        }

        if (password.length < 6) {
            return Result.failure(Exception("A palavra-passe deve ter pelo menos 6 caracteres."))
        }

        if (password != confirmPassword) {
            return Result.failure(Exception("As palavras-passe não coincidem."))
        }

        // Tudo validado! Passamos apenas o necessário para o repositório
        return repository.register(name, email, password)
    }
}