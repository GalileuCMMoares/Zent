package com.zent.app.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.zent.app.presentation.navigation.Screen
import com.zent.app.presentation.navigation.bottomNavItems
import com.zent.app.presentation.profile.ProfileScreen
import com.zent.app.presentation.stats.StatsScreen

// Cores (importadas do seu tema)
val ZentGreenPrimary = Color(0xFF7E9F8F)
val ZentGreenLight = Color(0xFFE8F5E9)
val ZentGrayText = Color(0xFF6B7280)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // A Barra de Navegação agora vive AQUI, e não dentro de cada tela
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                // Observa qual tela está visível agora para pintar o ícone
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                // 1. Evita empilhar telas iguais se clicar várias vezes
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // 2. Evita recriar a tela se já estiver nela
                                launchSingleTop = true
                                // 3. Restaura o estado (scroll, etc) ao voltar
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = null) },
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
    ) { paddingValues ->
        // O NavHost é quem troca o conteúdo da tela
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues) // Importante: Respeita o espaço da BottomBar
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Library.route) {
                LibraryScreen()
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}