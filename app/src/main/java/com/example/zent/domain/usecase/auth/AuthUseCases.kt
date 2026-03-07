package com.example.zent.domain.usecase.auth

import com.example.zent.domain.usecase.GetCurrentUserUseCase
import com.example.zent.domain.usecase.LoginUseCase
import com.example.zent.domain.usecase.LogoutUseCase
import com.example.zent.domain.usecase.RegisterUseCase
import com.example.zent.domain.usecase.SendPasswordResetEmailUseCase

data class AuthUseCases(
    val login: LoginUseCase,
    val register: RegisterUseCase,
    val sendPasswordReset: SendPasswordResetEmailUseCase,
    val getCurrentUser: GetCurrentUserUseCase,
    val logout: LogoutUseCase
)