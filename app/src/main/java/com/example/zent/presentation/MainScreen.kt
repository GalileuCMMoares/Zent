package com.zent.app.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zent.presentation.HomeScreen
import com.example.zent.presentation.LibraryScreen
import com.example.zent.presentation.login.ForgotPasswordScreen
import com.example.zent.presentation.login.LoginScreen
import com.example.zent.presentation.login.RegisterScreen
import com.zent.app.presentation.navigation.Screen
import com.zent.app.presentation.navigation.bottomNavItems
import com.zent.app.presentation.profile.ProfileScreen
import com.zent.app.presentation.stats.StatsScreen
import com.zent.app.presentation.components.ZentTopBar // Importe a TopBar nova
import com.zent.app.presentation.create.CreateQuizScreen

val ZentGreenPrimary = Color(0xFF7E9F8F)
val ZentGreenLight = Color(0xFFE8F5E9)
val ZentGrayText = Color(0xFF6B7280)

const val ROUTE_LOGIN = "login"
const val ROUTE_REGISTER = "register"
const val ROUTE_FORGOT_PASSWORD = "forgot_password"

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // LÓGICA DE ESTADO DAS BARRAS
    val authScreens = listOf(ROUTE_LOGIN, ROUTE_REGISTER, ROUTE_FORGOT_PASSWORD)
    val isAuthScreen = currentRoute in authScreens

    val mainScreens = listOf(
        Screen.Home.route,
        Screen.Library.route,
        Screen.Stats.route,
        Screen.Profile.route
    )
    val isMainScreen = currentRoute in mainScreens

    // A BottomBar só aparece nas telas principais do app (Home, Library, Stats, Profile)
    val showBottomBar = isMainScreen
    // A TopBar aparece em todo o lado, EXCETO na Autenticação
    val showTopBar = !isAuthScreen

    Box(modifier = Modifier.fillMaxSize()) {

        // Ajuste de Padding:
        // Na Autenticação o padding é 0. Nas outras telas, as barras ocupam espaço.
        val topPadding = if (showTopBar) 110.dp else 0.dp
        val bottomPadding = if (showBottomBar) 100.dp else 0.dp

        val contentPadding = PaddingValues(top = topPadding, bottom = bottomPadding)

        // 1. CONTEÚDO
        NavHost(
            navController = navController,
            startDestination = ROUTE_LOGIN, // O app começa agora pelo Login!
            modifier = Modifier.fillMaxSize()
        ) {
            // --- TELAS DE AUTENTICAÇÃO ---
            composable(ROUTE_LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        // Navega para a Home
                        navController.navigate(Screen.Home.route) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(ROUTE_REGISTER)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(ROUTE_FORGOT_PASSWORD)
                    }
                )
            }

            composable(ROUTE_REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        // Ao registar com sucesso, também vai direto para a Home e limpa o histórico
                        navController.navigate(Screen.Home.route) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack() // Volta para a tela anterior (Login)
                    }
                )
            }

            composable(ROUTE_FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBackToLoginClick = {
                        navController.popBackStack() // Volta para o Login
                    }
                )
            }

            // --- TELAS DO APP (PÓS-LOGIN) ---
            composable(Screen.Home.route) { HomeScreen(contentPadding) }
            composable(Screen.Library.route) { LibraryScreen(contentPadding) }
            composable(Screen.Stats.route) { StatsScreen(contentPadding) }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    contentPadding = contentPadding,
                    onLogoutSuccess = {
                        // Volta para a tela de login e limpa o histórico
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(0) { inclusive = true } // Limpa toda a pilha de navegação
                        }
                    }
                )
            }
            composable(Screen.CreateQuiz.route) { CreateQuizScreen(contentPadding) }
        }

        // 2. TOP BAR ÚNICA (Condicional)
        if (showTopBar) {
            ZentTopBar(
                modifier = Modifier.align(Alignment.TopCenter),
                title = "Zent",
                isMainScreen = isMainScreen,
                onActionClick = {
                    navController.navigate(Screen.CreateQuiz.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 3. BOTTOM BAR (Condicional)
        if (showBottomBar) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = navBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(screen.icon, null) },
                            label = { Text(screen.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ZentGreenPrimary,
                                selectedTextColor = ZentGreenPrimary,
                                indicatorColor = ZentGreenLight,
                                unselectedIconColor = ZentGrayText,
                                unselectedTextColor = ZentGrayText
                            )
                        )
                    }
                }
            }
        }
    }
}