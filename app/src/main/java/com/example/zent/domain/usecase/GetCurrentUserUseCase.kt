package com.example.zent.domain.usecase

import com.example.zent.domain.model.User
import com.example.zent.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}