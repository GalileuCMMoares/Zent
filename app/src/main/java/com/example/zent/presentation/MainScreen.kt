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
// Ajuste os imports abaixo conforme seu pacote real
import com.example.zent.presentation.HomeScreen
import com.example.zent.presentation.LibraryScreen
import com.zent.app.presentation.navigation.Screen
import com.zent.app.presentation.navigation.bottomNavItems
import com.zent.app.presentation.profile.ProfileScreen
import com.zent.app.presentation.stats.StatsScreen
import com.zent.app.presentation.components.ZentTopBar // Importe a TopBar nova

val ZentGreenPrimary = Color(0xFF7E9F8F)
val ZentGreenLight = Color(0xFFE8F5E9)
val ZentGrayText = Color(0xFF6B7280)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Usamos Box para permitir sobreposição (Layers)
    Box(modifier = Modifier.fillMaxSize()) {

        // Definição dos espaçamentos para o conteúdo não ficar escondido
        // Topo: 100dp (TopBar) + um pouco extra
        // Baixo: 80dp (NavBar) + um pouco extra
        val contentPadding = PaddingValues(top = 110.dp, bottom = 100.dp)

        // CAMADA 1: CONTEÚDO (Fundo)
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {
                HomeScreen(contentPadding = contentPadding)
            }
            composable(Screen.Library.route) {
                LibraryScreen(contentPadding = contentPadding)
            }
            composable(Screen.Stats.route) {
                StatsScreen(contentPadding = contentPadding)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(contentPadding = contentPadding)
            }
        }

        // CAMADA 2: TOP BAR (Fixa no topo)
        ZentTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            onActionClick = { /* Ação Global */ }
        )

        // CAMADA 3: BOTTOM BAR (Fixa embaixo)
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
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
    }
}