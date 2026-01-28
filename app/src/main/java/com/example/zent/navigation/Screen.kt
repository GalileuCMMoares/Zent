package com.zent.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Library : Screen("library", "Biblioteca", Icons.Default.Book)
    object Stats : Screen("stats", "Estatísticas", Icons.Default.BarChart)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object CreateQuiz : Screen("create_quiz", "Criar Quiz", Icons.Default.Add)
}

// A lista da BottomBar continua a mesma (apenas as 4 principais)
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Library,
    Screen.Stats,
    Screen.Profile
)