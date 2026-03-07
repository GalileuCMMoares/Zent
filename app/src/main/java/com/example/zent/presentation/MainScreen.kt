package com.zent.app.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
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
import com.example.zent.presentation.CreateDeckScreen
import com.example.zent.presentation.HomeScreen
import com.example.zent.presentation.LibraryScreen
import com.example.zent.presentation.login.ForgotPasswordScreen
import com.example.zent.presentation.login.LoginScreen
import com.example.zent.presentation.login.RegisterScreen
import com.zent.app.presentation.components.ZentTopBar
import com.zent.app.presentation.navigation.Screen
import com.zent.app.presentation.navigation.bottomNavItems
import com.zent.app.presentation.profile.ProfileScreen
import com.zent.app.presentation.stats.StatsScreen

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

    // Identifica se estamos na tela de criar matéria
    val isCreateScreen = currentRoute == Screen.CreateQuiz.route

    // A BottomBar só aparece nas telas principais do app (Home, Library, Stats, Profile)
    val showBottomBar = isMainScreen

    // A TopBar Global aparece nas telas principais, mas NÃO na Autenticação e NÃO na de Criação (pois ela tem a própria)
    val showTopBar = !isAuthScreen && !isCreateScreen

    Box(modifier = Modifier.fillMaxSize()) {

        // Ajuste de Padding:
        // Na Autenticação e Criação, o padding global é 0. Nas outras telas, as barras ocupam espaço.
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
                        navController.navigate(Screen.Home.route) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(ROUTE_FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBackToLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            // --- TELAS DO APP (PÓS-LOGIN) ---
            composable(Screen.Home.route) { HomeScreen(contentPadding) }
            composable(Screen.Library.route) {
                LibraryScreen(
                    contentPadding = contentPadding,
                    onNavigateToCreateDeck = {
                        navController.navigate(Screen.CreateQuiz.route) // Navega para a tela de nova matéria!
                    }
                )
            }
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

            // --- TELA DE CRIAR MATÉRIA ---
            composable(Screen.CreateQuiz.route) {
                CreateDeckScreen(
                    onBackClick = {
                        navController.popBackStack() // Volta para a tela anterior
                    }
                )
            }
        }

        // 2. TOP BAR ÚNICA GLOBLAL (Condicional)
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