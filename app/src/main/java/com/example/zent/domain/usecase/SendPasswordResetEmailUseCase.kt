package com.example.zent.domain.usecase

import com.example.zent.domain.repository.AuthRepository

class SendPasswordResetEmailUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("Por favor, introduza o seu e-mail."))
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        if (!email.matches(emailRegex)) {
            return Result.failure(Exception("Introduza um e-mail válido."))
        }

        return repository.sendPasswordResetEmail(email)
    }
}