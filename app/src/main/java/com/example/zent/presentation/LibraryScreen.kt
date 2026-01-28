package com.example.zent.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- REUTILIZANDO CORES (Mesmas da Home) --- // Verde Sálvia
val ZentGreenDarker = Color(0xFF6B8A7A)  // Um pouco mais escuro para botões sólidos
val ZentGrayLight = Color(0xFFE5E7EB)    // Cinza bem claro para barras de fundo

@Composable
fun LibraryScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        // APLICA O PADDING DO MAIN SCREEN DIRETO NA LISTA
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Removido: item { LibraryTopBar() }

        // Cabeçalho "Biblioteca"
        item {
            // Pequeno ajuste visual se necessário
            LibraryHeaderSection()
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LibraryStatsRow()
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(getLibraryMockData()) { deck ->
            DeckCardItem(deck)
        }


    }
}

// --- COMPONENTES DA TELA ---

@Composable
fun LibraryTopBar() {
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
        // Botão "Criar" (Outline)
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, ZentGrayLight),
            onClick = { /* Ação */ }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp), tint = ZentTextDark)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Criar", fontWeight = FontWeight.SemiBold, color = ZentTextDark)
            }
        }
    }
}

@Composable
fun LibraryHeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Biblioteca",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ZentTextDark
            )
            Text(
                text = "5 baralhos",
                fontSize = 14.sp,
                color = ZentGrayText
            )
        }

        // Botão "+ Novo" (Sólido Verde)
        Button(
            onClick = { /* Novo Baralho */ },
            colors = ButtonDefaults.buttonColors(containerColor = ZentGreenDarker),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Novo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun LibraryStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card Esquerdo (Total de Cartas)
        LibraryMiniStat(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Book,
            value = "225",
            label = "Total de Cartas",
            iconBg = ZentGreenLight,
            iconColor = ZentGreenPrimary
        )
        // Card Direito (Dominadas)
        LibraryMiniStat(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.TrendingUp,
            value = "143",
            label = "Dominadas",
            iconBg = ZentPurpleLight,
            iconColor = ZentPurple
        )
    }
}

@Composable
fun LibraryMiniStat(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    iconBg: Color,
    iconColor: Color
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp) // Flat, apenas border se quiser
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ZentTextDark)
                Text(text = label, fontSize = 11.sp, color = ZentGrayText, lineHeight = 12.sp)
            }
        }
    }
}

@Composable
fun DeckCardItem(deck: DeckMock) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Linha Superior (Ícone + Textos + Menu)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Ícone do Baralho
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)) // Um pouco quadrado
                        .background(deck.colorBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Book,
                        contentDescription = null,
                        tint = deck.colorMain,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Títulos
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ZentTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${deck.cardCount} cartas  •  ${deck.lastStudied}",
                        fontSize = 12.sp,
                        color = ZentGrayText
                    )
                }

                // Menu (3 pontinhos)
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Opções",
                    tint = ZentGrayText,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Barra de Progresso Customizada
            // Calcula progresso (ex: 32 de 45)
            val progress = deck.masteredCount.toFloat() / deck.cardCount.toFloat()

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)), // Bordas redondas na barra
                color = ZentGreenPrimary,
                trackColor = ZentGrayLight,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rodapé (Dominadas vs Para Revisar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${deck.masteredCount} dominadas",
                    fontSize = 12.sp,
                    color = ZentGrayText
                )

                // Destaque se tiver revisão
                val reviewColor = if (deck.toReviewCount > 0) ZentGreenPrimary else ZentGrayText
                val reviewWeight = if (deck.toReviewCount > 0) FontWeight.Bold else FontWeight.Normal

                Text(
                    text = "${deck.toReviewCount} para revisar",
                    fontSize = 12.sp,
                    color = reviewColor,
                    fontWeight = reviewWeight
                )
            }
        }
    }
}
// --- DADOS MOCK (Baseados na imagem) ---
data class DeckMock(
    val title: String,
    val cardCount: Int,
    val lastStudied: String,
    val masteredCount: Int,
    val toReviewCount: Int,
    val colorMain: Color,
    val colorBg: Color
)

fun getLibraryMockData() = listOf(
    DeckMock("História do Brasil", 45, "Hoje", 32, 8, ZentGreenPrimary, ZentGreenLight),
    DeckMock("Inglês - Vocabulário", 67, "Hoje", 48, 5, ZentGreenPrimary, ZentGreenLight),
    DeckMock("Kotlin Básico", 23, "Ontem", 15, 2, ZentPurple, ZentPurpleLight),
    DeckMock("Biologia - Células", 38, "3 dias atrás", 20, 0, ZentGreenPrimary, ZentGreenLight),
    DeckMock("Matemática - Álgebra", 52, "2 dias atrás", 28, 12, ZentGreenPrimary, ZentGreenLight),
)

// --- PREVIEWS ---
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PreviewLibraryScreen() {
    LibraryScreen()
}