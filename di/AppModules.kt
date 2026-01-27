package com.example.zent.di

import org.koin.dsl.module

object AppModules {

    // 1. Módulo de DADOS
    val dataModule = module {
        // Banco de Dados (Singleton)
        // DAO (Singleton dependente do Banco)
    }

    // 2. Módulo de DOMÍNIO
    val domainModule = module {
        // factory { SpacedRepetitionCalculator() }
    }

    // 3. Módulo de APRESENTAÇÃO
    val presentationModule = module {
        // viewModel { HomeViewModel(get()) }
    }

    // A lista final que será carregada na Application
    val all = listOf(dataModule, domainModule, presentationModule)
}