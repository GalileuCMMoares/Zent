package com.example.zent.domain.repository

import com.example.zent.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // O .await() pausa a função aqui até o Firebase responder, sem travar o app!
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Mapeia o usuário do Firebase para o nosso modelo de Domínio
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    isPremium = false // Implementaremos a lógica de premium depois
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Erro desconhecido ao fazer login."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            // 1. Cria a conta no Firebase
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // 2. O Firebase não salva o nome na criação por e-mail, precisamos atualizar o perfil
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                firebaseUser.updateProfile(profileUpdates).await()

                // 3. Retorna o usuário criado
                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = firebaseUser.email ?: "",
                    isPremium = false
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Não foi possível criar a conta."))
            }
        } catch (e: Exception) {
            Result.failure(e) // Retorna o erro (ex: "E-mail já cadastrado", "Senha fraca")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                isPremium = false
            )
        } else {
            null
        }
    }
}