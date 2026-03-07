package com.example.zent.di

import androidx.room.Room
import com.example.zent.data.local.ZentDatabase
import com.example.zent.domain.repository.AuthRepository
import com.example.zent.domain.repository.AuthRepositoryImpl
import com.example.zent.domain.usecase.GetCurrentUserUseCase
import com.example.zent.domain.usecase.LoginUseCase
import com.example.zent.domain.usecase.LogoutUseCase
import com.example.zent.domain.usecase.RegisterUseCase
import com.example.zent.domain.usecase.SendPasswordResetEmailUseCase
import com.example.zent.domain.usecase.auth.AuthUseCases
import com.example.zent.viewmodel.ZentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object AppModules {

    // 1. Módulo de DADOS (Local, Remoto e Repositórios)
    val dataModule = module {
        // --- Banco de Dados Local (Room) ---
        single {
            Room.databaseBuilder(
                androidContext(),
                ZentDatabase::class.java,
                "zent_database"
            ).build()
        }
        single { get<ZentDatabase>().deckDao() }
        single { get<ZentDatabase>().flashcardDao() }

        // --- Firebase (Nuvem) ---
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }

        // --- Repositórios ---
        // O Koin entende que quando alguém pedir a interface AuthRepository,
        // ele deve entregar a implementação AuthRepositoryImpl (que precisa do FirebaseAuth)
        single<AuthRepository> { AuthRepositoryImpl(get()) }
    }

    // 2. Módulo de DOMÍNIO (Regras de Negócio / Casos de Uso)
    val domainModule = module {
        // factory { SpacedRepetitionCalculator() } // Mantive o seu exemplo!

        // --- Autenticação ---
        factory { LoginUseCase(get()) }
        factory { RegisterUseCase(get()) }
        factory { SendPasswordResetEmailUseCase(get()) }
        factory { GetCurrentUserUseCase(get()) }
        factory { LogoutUseCase(get()) }

        // Wrapper de Casos de Uso para facilitar a vida do ViewModel
        factory {
            AuthUseCases(
                login = get(),
                register = get(),
                sendPasswordReset = get(),
                getCurrentUser = get(),
                logout = get()
            )
        }
    }

    // 3. Módulo de APRESENTAÇÃO (ViewModels)
    val presentationModule = module {
        viewModel { ZentViewModel(authUseCases = get()) }
    }

    // A lista final que será carregada na Application
    val all = listOf(dataModule, domainModule, presentationModule)
}