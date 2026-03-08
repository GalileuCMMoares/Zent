package com.zent.app.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.zent.domain.model.Topic
import com.example.zent.viewmodel.ZentViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

// --- CORES DA TELA ---
val ZentBackground = Color(0xFFF9FAFB)
val ZentTextDark = Color(0xFF1F2937)
val ZentGrayLight = Color(0xFFE5E7EB)
val ZentPurpleButton = Color(0xFFA89BC6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailsScreen(
    deckId: String,
    onBackClick: () -> Unit,
    onNavigateToCreateTopic: (String) -> Unit = {},
    onNavigateToTopicDetails: (String) -> Unit = {}, // NOVO: Para clicar no assunto!
    viewModel: ZentViewModel = koinViewModel()
) {
    LaunchedEffect(deckId) { viewModel.loadDeckDetails(deckId) }

    val deck by viewModel.selectedDeck.collectAsState()
    val topics by viewModel.selectedDeckTopics.collectAsState()
    val allQuestions by viewModel.selectedDeckQuestions.collectAsState() // Mágica dos dados reais!

    if (deck == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ZentGreenPrimary)
        }
        return
    }

    val mainColor = parseHexColor(deck!!.colorHex)
    val bgColor = mainColor.copy(alpha = 0.15f)

    // Estatísticas Reais
    val totalTopics = topics.size
    val totalCards = allQuestions.size
    val masteredTopics = topics.count { it.intervalDays > 5 }
    val todayReviews = topics.count { isReviewTodayOrPast(it.nextReviewDate) }

    val overallProgress = if (totalTopics > 0) masteredTopics.toFloat() / totalTopics.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zent", fontWeight = FontWeight.Bold, color = ZentPurpleButton, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZentTextDark) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZentBackground)
            )
        },
        containerColor = ZentBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { DeckHeaderCard(deck = deck!!, mainColor = mainColor, bgColor = bgColor, progress = overallProgress) }
            item { StatsGrid(topicsCount = totalTopics, cardsCount = totalCards, masteredCount = masteredTopics, todayCount = todayReviews) }
            item { ActionButtons(mainColor = mainColor, onCreateTopicClick = { onNavigateToCreateTopic(deckId) }, hasReviewsToday = todayReviews > 0) }

            item {
                Text("Assuntos ($totalTopics)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (topics.isEmpty()) {
                item { EmptyTopicsMessage() }
            } else {
                items(topics) { topic ->
                    // Conta as cartas específicas deste assunto
                    val cardsInThisTopic = allQuestions.count { it.topicId == topic.id }

                    TopicCardItem(
                        topic = topic,
                        mainColor = mainColor,
                        totalCards = cardsInThisTopic,
                        onClick = { onNavigateToTopicDetails(topic.id) }
                    )
                }
            }
            item { ReviewHintCard(todayReviews); Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun DeckHeaderCard(deck: Deck, mainColor: Color, bgColor: Color, progress: Float) {
    Card(
        shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(bgColor), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Book, null, tint = mainColor, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(deck.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ZentTextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(deck.description.ifEmpty { "Sem descrição disponível." }, fontSize = 14.sp, color = ZentGrayText, lineHeight = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progresso Geral", fontSize = 12.sp, color = ZentGrayText)
                Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)), color = mainColor, trackColor = ZentGrayLight)
        }
    }
}

@Composable
fun StatsGrid(topicsCount: Int, cardsCount: Int, masteredCount: Int, todayCount: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatItem(modifier = Modifier.weight(1f), icon = Icons.Outlined.Book, value = topicsCount.toString(), label = "Assuntos")
        StatItem(modifier = Modifier.weight(1f), icon = Icons.Outlined.Event, value = cardsCount.toString(), label = "Cartas")
        StatItem(modifier = Modifier.weight(1f), icon = Icons.Outlined.TrendingUp, value = masteredCount.toString(), label = "Dominadas")
        StatItem(modifier = Modifier.weight(1f), icon = Icons.Outlined.PlayArrow, value = todayCount.toString(), label = "Hoje")
    }
}

@Composable
fun StatItem(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Card(
        modifier = modifier.aspectRatio(0.85f), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(ZentGrayLight.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = ZentGrayText, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ZentTextDark)
            Text(label, fontSize = 11.sp, color = ZentGrayText)
        }
    }
}

@Composable
fun ActionButtons(mainColor: Color, onCreateTopicClick: () -> Unit, hasReviewsToday: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { /* Iniciar Quiz */ }, modifier = Modifier.weight(1f).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZentPurpleButton), shape = RoundedCornerShape(16.dp), enabled = hasReviewsToday
        ) {
            Icon(Icons.Outlined.PlayArrow, null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Estudar Agora", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        OutlinedButton(
            onClick = onCreateTopicClick, modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, ZentGrayLight), colors = ButtonDefaults.outlinedButtonColors(contentColor = ZentTextDark)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Novo Assunto", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun TopicCardItem(topic: Topic, mainColor: Color, totalCards: Int, onClick: () -> Unit) {
    val progress = if (topic.intervalDays > 5) 1f else (topic.intervalDays / 5f).coerceIn(0f, 1f)
    val nextReviewText = getNextReviewText(topic.nextReviewDate)

    Card(
        shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ZentGrayLight),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { onClick() } // CLICÁVEL AQUI
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Book, null, tint = ZentGrayText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(topic.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(topic.sourceMaterial.take(50) + "...", fontSize = 12.sp, color = ZentGrayText, maxLines = 1)
                    }
                }
                Icon(Icons.Default.MoreVert, null, tint = ZentGrayText, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$totalCards cartas", fontSize = 11.sp, color = ZentGrayText)
                Text("${(progress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)), color = mainColor, trackColor = ZentGrayLight)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Event, null, tint = ZentGrayText, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Próxima: $nextReviewText", fontSize = 12.sp, color = ZentGrayText)
            }
        }
    }
}

@Composable
fun EmptyTopicsMessage() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Nenhum assunto ainda.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
        Text("Clique em '+ Novo Assunto' para adicionar material e gerar cartas com IA.", fontSize = 14.sp, color = ZentGrayText, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp))
    }
}

@Composable
fun ReviewHintCard(todayReviews: Int) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Lightbulb, null, tint = Color(0xFFEAB308), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dica de Revisão", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
            }
            Spacer(modifier = Modifier.height(12.dp))
            val text = if (todayReviews > 0) "Você tem $todayReviews assuntos para revisar hoje. Revisar regularmente melhora a retenção!" else "Tudo em dia! Você não tem assuntos pendentes para hoje. Que tal criar um novo?"
            Text(text, fontSize = 13.sp, color = ZentGrayText, lineHeight = 20.sp)
        }
    }
}

fun parseHexColor(hex: String): Color { return try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { ZentGreenPrimary } }
fun isReviewTodayOrPast(timestamp: Long): Boolean { if (timestamp == 0L) return true; val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59) }.timeInMillis; return timestamp <= today }
fun getNextReviewText(timestamp: Long): String {
    if (timestamp == 0L) return "Hoje"
    val diffDays = (timestamp - Calendar.getInstance().timeInMillis) / (1000 * 60 * 60 * 24)
    return when { diffDays <= 0 -> "Hoje"; diffDays == 1L -> "Amanhã"; else -> "$diffDays dias" }
}