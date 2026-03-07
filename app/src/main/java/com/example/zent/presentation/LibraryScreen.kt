package com.example.zent.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.domain.model.Deck
import com.example.zent.viewmodel.ZentViewModel
import org.koin.androidx.compose.koinViewModel

val ZentGreenDarker = Color(0xFF6B8A7A)
val ZentGrayLight = Color(0xFFE5E7EB)

@Composable
fun LibraryScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateToCreateDeck: () -> Unit = {},
    onNavigateToDeckDetails: (String) -> Unit = {}, // Ação de clicar na matéria
    viewModel: ZentViewModel = koinViewModel()
) {
    val decks by viewModel.decks.collectAsState()

    val totalCards = decks.sumOf { it.totalCards }
    val toReview = decks.sumOf { it.toReviewCount }
    val mastered = if (totalCards > 0) totalCards - toReview else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LibraryHeaderSection(
                deckCount = decks.size,
                onCreateClick = onNavigateToCreateDeck
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LibraryStatsRow(totalCards = totalCards, masteredCards = mastered)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (decks.isEmpty()) {
            item {
                EmptyLibraryMessage()
            }
        } else {
            items(decks) { deck ->
                DeckCardItem(
                    deck = deck,
                    onClick = { onNavigateToDeckDetails(deck.id) }
                )
            }
        }
    }
}

@Composable
fun LibraryHeaderSection(deckCount: Int, onCreateClick: () -> Unit) {
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
                text = "$deckCount baralhos",
                fontSize = 14.sp,
                color = ZentGrayText
            )
        }

        Button(
            onClick = onCreateClick,
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
fun LibraryStatsRow(totalCards: Int, masteredCards: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LibraryMiniStat(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Book,
            value = totalCards.toString(),
            label = "Total de Cartas",
            iconBg = ZentGreenLight,
            iconColor = ZentGreenPrimary
        )
        LibraryMiniStat(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.TrendingUp,
            value = masteredCards.toString(),
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
        elevation = CardDefaults.cardElevation(0.dp)
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
fun DeckCardItem(deck: Deck, onClick: () -> Unit) {
    val mainColor = parseHexColor(deck.colorHex)
    val bgColor = mainColor.copy(alpha = 0.2f)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Torna o cartão clicável
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Book,
                        contentDescription = null,
                        tint = mainColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ZentTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${deck.totalCards} assuntos  •  ${deck.description.ifEmpty { "Sem descrição" }}",
                        fontSize = 12.sp,
                        color = ZentGrayText,
                        maxLines = 1
                    )
                }

                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Opções",
                    tint = ZentGrayText,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val progress = if (deck.totalCards > 0) {
                (deck.totalCards - deck.toReviewCount).toFloat() / deck.totalCards.toFloat()
            } else {
                0f
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = mainColor,
                trackColor = ZentGrayLight,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dominadas = if(deck.totalCards > 0) deck.totalCards - deck.toReviewCount else 0
                Text(
                    text = "$dominadas dominadas",
                    fontSize = 12.sp,
                    color = ZentGrayText
                )

                val reviewColor = if (deck.toReviewCount > 0) mainColor else ZentGrayText
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

@Composable
fun EmptyLibraryMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.Book, contentDescription = null, modifier = Modifier.size(64.dp), tint = ZentGrayLight)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sua biblioteca está vazia.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
        Text("Clique em '+ Novo' para começar a estudar!", fontSize = 14.sp, color = ZentGrayText)
    }
}

fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        ZentGreenPrimary
    }
}