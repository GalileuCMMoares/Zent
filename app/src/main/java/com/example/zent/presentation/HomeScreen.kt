package com.example.zent.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Question
import com.example.zent.domain.model.Topic
import com.example.zent.viewmodel.ZentViewModel
import com.zent.app.presentation.isReviewTodayOrPast
import com.zent.app.presentation.parseHexColor
import org.koin.androidx.compose.koinViewModel

// --- CORES DA UI ---
val ZentBackground = Color(0xFFF9FAFB)
val ZentGreenPrimary = Color(0xFF7E9F8F)
val ZentTextDark = Color(0xFF1F2937)
val ZentPurple = Color(0xFFA89BC6)
val ZentPurpleLight = Color(0xFFF3E8FF)
val ZentGreenLight = Color(0xFFE8F5E9)
val ZentGrayText = Color(0xFF6B7280)

@Composable
fun HomeScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateToDeckDetails: (String) -> Unit = {},
    viewModel: ZentViewModel = koinViewModel()
) {
    val decks by viewModel.decks.collectAsState()
    val allTopics by viewModel.allTopics.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()

    // Estatísticas reais
    val pendingReviews = allTopics.count { isReviewTodayOrPast(it.nextReviewDate) }
    val totalCards = allQuestions.size
    val masteredTopics = allTopics.count { it.intervalDays > 5 }
    val totalTopics = allTopics.size
    val accuracyPercent = if (totalTopics > 0) {
        ((masteredTopics.toFloat() / totalTopics) * 100).toInt()
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))

        HeroSection(pendingReviews = pendingReviews, masteredTopics = masteredTopics)
        Spacer(modifier = Modifier.height(32.dp))

        RecentsSection(
            decks = decks,
            allTopics = allTopics,
            allQuestions = allQuestions,
            onDeckClick = onNavigateToDeckDetails
        )
        Spacer(modifier = Modifier.height(24.dp))

        StatsSection(totalCards = totalCards, accuracyPercent = accuracyPercent)

        Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
    }
}

// --- HERO CARD ---
@Composable
fun HeroSection(pendingReviews: Int, masteredTopics: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ZentGreenPrimary),
        modifier = Modifier.fillMaxWidth().height(280.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = Color.White.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Revisão de Hoje", color = Color.White, fontWeight = FontWeight.Medium)
                }
                Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$masteredTopics dominados", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Column {
                Text(
                    text = pendingReviews.toString(),
                    fontSize = 64.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 64.sp
                )
                Text(
                    text = if (pendingReviews == 1) "assunto pendente" else "assuntos pendentes",
                    fontSize = 18.sp, color = Color.White.copy(alpha = 0.9f)
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = pendingReviews > 0
            ) {
                Text(
                    text = if (pendingReviews > 0) "Comece a Estudar" else "Tudo em dia!",
                    color = ZentGreenPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- MATÉRIAS RECENTES ---
@Composable
fun RecentsSection(
    decks: List<Deck>,
    allTopics: List<Topic>,
    allQuestions: List<Question>,
    onDeckClick: (String) -> Unit
) {
    Column {
        Text("Matérias Recentes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ZentGrayText)
        Spacer(modifier = Modifier.height(16.dp))

        if (decks.isEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Nenhuma matéria ainda", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Crie sua primeira matéria para começar!", fontSize = 13.sp, color = ZentGrayText, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(decks) { deck ->
                    val deckTopics = allTopics.filter { it.deckId == deck.id }
                    val deckQuestionCount = allQuestions.count { q -> deckTopics.any { it.id == q.topicId } }
                    val mastered = deckTopics.count { it.intervalDays > 5 }
                    val progress = if (deckTopics.isNotEmpty()) mastered.toFloat() / deckTopics.size else 0f

                    DeckCard(
                        deck = deck,
                        topicCount = deckTopics.size,
                        questionCount = deckQuestionCount,
                        progress = progress,
                        onClick = { onDeckClick(deck.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeckCard(deck: Deck, topicCount: Int, questionCount: Int, progress: Float, onClick: () -> Unit) {
    val color = parseHexColor(deck.colorHex)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(160.dp).height(180.dp).clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Book, null, tint = color, modifier = Modifier.size(20.dp))
            }

            Column {
                Text(deck.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZentTextDark, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$topicCount assuntos · $questionCount cartas", fontSize = 12.sp, color = ZentGrayText, maxLines = 1)
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                color = color,
                trackColor = Color(0xFFE5E7EB)
            )
        }
    }
}

// --- ESTATÍSTICAS ---
@Composable
fun StatsSection(totalCards: Int, accuracyPercent: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Total de Cartas",
            value = totalCards.toString(),
            subLabel = "em todas as matérias"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Domínio Geral",
            value = "$accuracyPercent%",
            subLabel = "assuntos dominados"
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
