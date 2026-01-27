package com.example.zent.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- CORES DA UI (Extraídas da imagem) ---
val ZentBackground = Color(0xFFF9FAFB) // Branco gelo do fundo
val ZentGreenPrimary = Color(0xFF7E9F8F) // Verde do card principal
val ZentTextDark = Color(0xFF1F2937)     // Preto suave
val ZentPurple = Color(0xFFA89BC6)       // Roxo do logo e ícones
val ZentPurpleLight = Color(0xFFF3E8FF)  // Fundo ícone roxo
val ZentGreenLight = Color(0xFFE8F5E9)   // Fundo ícone verde
val ZentGrayText = Color(0xFF6B7280)     // Cinza textos secundários

@Composable
fun HomeScreen() {
    Scaffold(
        containerColor = ZentBackground,
        bottomBar = { ZentBottomNavigation() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp) // Margem lateral geral
                .verticalScroll(rememberScrollState()) // Scroll vertical
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TopBarSection()

            Spacer(modifier = Modifier.height(24.dp))
            HeroSection()

            Spacer(modifier = Modifier.height(32.dp))
            RecentsSection()

            Spacer(modifier = Modifier.height(24.dp))
            StatsSection()

            Spacer(modifier = Modifier.height(32.dp)) // Espaço final
        }
    }
}

// --- 1. TOPO (Logo e Botão Criar) ---
@Composable
fun TopBarSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Zent",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ZentPurple
        )

        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
            onClick = { /* Ação de criar */ }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = ZentTextDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Criar",
                    fontWeight = FontWeight.SemiBold,
                    color = ZentTextDark
                )
            }
        }
    }
}

// --- 2. HERO CARD (Revisão de Hoje) ---
@Composable
fun HeroSection() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ZentGreenPrimary),
        modifier = Modifier.fillMaxWidth().height(280.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra suave
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabeçalho do Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome, // Ícone de brilho
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Revisão de Hoje",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Tag "7 dias"
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("7 dias", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Números Grandes
            Column {
                Text(
                    text = "15",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 64.sp
                )
                Text(
                    text = "cartas pendentes",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Botão Branco
            Button(
                onClick = { /* Começar estudo */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    text = "Começar Sessão de Estudo",
                    color = ZentGreenPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- 3. MATÉRIAS RECENTES (Lista Horizontal) ---
@Composable
fun RecentsSection() {
    Column {
        Text(
            text = "Matérias Recentes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ZentGrayText
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(getMockSubjects()) { subject ->
                SubjectCard(subject)
            }
        }
    }
}

@Composable
fun SubjectCard(subject: SubjectMock) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(160.dp).height(180.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Ícone
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(subject.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Book, // Ícone genérico de livro
                    contentDescription = null,
                    tint = subject.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = subject.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ZentTextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subject.subtitle,
                    fontSize = 12.sp,
                    color = ZentGrayText
                )
            }

            LinearProgressIndicator(
                progress = { subject.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = ZentGreenPrimary,
                trackColor = Color(0xFFE5E7EB),
            )
        }
    }
}

// --- 4. ESTATÍSTICAS (Cards inferiores) ---
@Composable
fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Total Estudado",
            value = "135",
            subLabel = "cartas esta semana"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Taxa de Acerto",
            value = "87%",
            subLabel = "últimos 7 dias"
        )
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, value: String, subLabel: String) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, fontSize = 14.sp, color = ZentGrayText)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subLabel, fontSize = 12.sp, color = ZentGrayText)
        }
    }
}

// --- 5. BOTTOM NAVIGATION ---
@Composable
fun ZentBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZentGreenPrimary,
                selectedTextColor = ZentGreenPrimary,
                indicatorColor = ZentGreenLight
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Book, contentDescription = null) },
            label = { Text("Biblioteca") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = ZentGrayText,
                unselectedTextColor = ZentGrayText
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Estatísticas") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = ZentGrayText,
                unselectedTextColor = ZentGrayText
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = ZentGrayText,
                unselectedTextColor = ZentGrayText
            )
        )
    }
}

// --- MOCK DATA ---
data class SubjectMock(
    val title: String,
    val subtitle: String,
    val progress: Float,
    val iconColor: Color,
    val iconBgColor: Color
)

fun getMockSubjects() = listOf(
    SubjectMock("História", "8 de 45 cartas", 0.2f, ZentGreenPrimary, ZentGreenLight),
    SubjectMock("Inglês", "5 de 67 cartas", 0.1f, ZentGreenPrimary, ZentGreenLight),
    SubjectMock("Kotlin", "2 de 23 cartas", 0.15f, ZentPurple, ZentPurpleLight),
)

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}