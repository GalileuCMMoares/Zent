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
import com.zent.app.presentation.navigation.Screen
import com.zent.app.presentation.navigation.bottomNavItems
import com.zent.app.presentation.profile.ProfileScreen
import com.zent.app.presentation.stats.StatsScreen
import com.zent.app.presentation.components.ZentTopBar // Importe a TopBar nova
import com.zent.app.presentation.create.CreateQuizScreen

val ZentGreenPrimary = Color(0xFF7E9F8F)
val ZentGreenLight = Color(0xFFE8F5E9)
val ZentGrayText = Color(0xFF6B7280)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // LÓGICA DE ESTADO DA TOP BAR
    // Define quais telas são consideradas "Principais" (tem botão Criar e fundo vidro)
    val mainScreens = listOf(
        Screen.Home.route,
        Screen.Library.route,
        Screen.Stats.route,
        Screen.Profile.route
    )

    val isMainScreen = currentRoute in mainScreens

    // Se for a tela CreateQuiz, a BottomBar deve sumir
    val showBottomBar = isMainScreen

    Box(modifier = Modifier.fillMaxSize()) {

        // Ajuste de Padding:
        // Na Home: Topo grande (110dp)
        // Na Create: Topo grande (110dp) também, pois a TopBar ainda existe lá!
        val topPadding = 110.dp
        val bottomPadding = if (showBottomBar) 100.dp else 0.dp

        val contentPadding = PaddingValues(top = topPadding, bottom = bottomPadding)

        // 1. CONTEÚDO
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // Telas Principais
            composable(Screen.Home.route) { HomeScreen(contentPadding) }
            composable(Screen.Library.route) { LibraryScreen(contentPadding) }
            composable(Screen.Stats.route) { StatsScreen(contentPadding) }
            composable(Screen.Profile.route) { ProfileScreen(contentPadding) }

            // Tela de Criação (Recebe o padding também!)
            composable(Screen.CreateQuiz.route) {
                CreateQuizScreen(contentPadding = contentPadding)
            }
        }

        // 2. TOP BAR ÚNICA (Reage ao estado)
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

        // 3. BOTTOM BAR (Condicional)
        if (showBottomBar) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                // ... (Código da NavigationBar igual ao anterior) ...
                // Cole aqui a sua NavigationBar que já estava funcionando
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    // ... itens ...
                    bottomNavItems.forEach { screen ->
                        // ... NavigationBarItem ...
                        // copie a lógica que já fizemos antes
                        val isSelected = navBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navController.navigate(screen.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                            icon = { Icon(screen.icon, null) },
                            label = { Text(screen.label) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = ZentGreenPrimary, selectedTextColor = ZentGreenPrimary, indicatorColor = ZentGreenLight, unselectedIconColor = ZentGrayText, unselectedTextColor = ZentGrayText)
                        )
                    }
                }
            }
        }
    }
}