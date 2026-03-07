package com.example.zent.di

import androidx.room.Room
import com.example.zent.data.local.ZentDatabase
import com.example.zent.data.repository.StudyRepositoryImpl
import com.example.zent.domain.repository.AuthRepository
import com.example.zent.domain.repository.AuthRepositoryImpl
import com.example.zent.domain.repository.StudyRepository
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
            )
                .fallbackToDestructiveMigration() // Evita crashes se mudarmos as tabelas durante o dev
                .build()
        }
        single { get<ZentDatabase>().deckDao() }
        single { get<ZentDatabase>().studyDao() } // NOVO: DAO de Assuntos e Quizzes

        // --- Firebase (Nuvem) ---
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }

        // --- Repositórios ---
        single<AuthRepository> { AuthRepositoryImpl(get()) }

        // NOVO: Repositório de Estudos (Salva no Room e no Firestore)
        single<StudyRepository> {
            StudyRepositoryImpl(deckDao = get(), firestore = get(), auth = get(), studyDao = get())
        }
    }

    // 2. Módulo de DOMÍNIO (Regras de Negócio / Casos de Uso)
    val domainModule = module {
        // --- Autenticação ---
        factory { LoginUseCase(get()) }
        factory { RegisterUseCase(get()) }
        factory { SendPasswordResetEmailUseCase(get()) }
        factory { GetCurrentUserUseCase(get()) }
        factory { LogoutUseCase(get()) }

        // Wrapper de Casos de Uso
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
        // ATUALIZADO: O ViewModel agora recebe os Casos de Uso de Auth E o Repositório de Estudos!
        viewModel {
            ZentViewModel(
                authUseCases = get(),
                studyRepository = get()
            )
        }
    }

    // A lista final que será carregada na Application
    val all = listOf(dataModule, domainModule, presentationModule)
}