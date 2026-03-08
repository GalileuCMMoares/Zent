package com.example.zent.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.domain.model.Question
import com.example.zent.domain.model.Topic
import com.example.zent.viewmodel.ZentViewModel
import com.zent.app.presentation.StatItem
import com.zent.app.presentation.ZentPurpleButton
import com.zent.app.presentation.getNextReviewText
import com.zent.app.presentation.isReviewTodayOrPast
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailsScreen(
    topicId: String,
    onBackClick: () -> Unit,
    onNavigateToQuiz: (String) -> Unit, // Função de navegação para o Quiz
    viewModel: ZentViewModel = koinViewModel()
){
    LaunchedEffect(topicId) { viewModel.loadTopicDetails(topicId) }

    val topic by viewModel.selectedTopic.collectAsState()
    val questions by viewModel.selectedTopicQuestions.collectAsState()

    if (topic == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ZentGreenPrimary) }
        return
    }

    // Como os dados da matéria não estão nesta tela, usamos as cores padrão
    val mainColor = ZentGreenPrimary
    val bgColor = mainColor.copy(alpha = 0.15f)

    val progress = if (topic!!.intervalDays > 5) 1f else (topic!!.intervalDays / 5f).coerceIn(0f, 1f)
    val nextReviewText = getNextReviewText(topic!!.nextReviewDate)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zent", fontWeight = FontWeight.Bold, color = ZentPurpleButton, fontSize = 24.sp) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZentTextDark) } },
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

            // 1. Cabeçalho do Assunto
            item { TopicHeaderCard(topic = topic!!, progress = progress) }

            // 2. Estatísticas
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatItem(modifier = Modifier.weight(1f), icon = Icons.Outlined.Book, value = questions.size.toString(), label = "Cartas")
                    StatItem(modifier = Modifier.weight(1f), icon = Icons.Outlined.TrendingUp, value = topic!!.repetitions.toString(), label = "Dominadas")
                    StatItem(modifier = Modifier.weight(1f), icon = Icons.Outlined.PlayArrow, value = if (isReviewTodayOrPast(topic!!.nextReviewDate)) "Sim" else "Não", label = "Hoje")
                }
            }

            // 3. Botões de Ação
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onNavigateToQuiz(topicId) }, // <-- AGORA CONECTADO AO QUIZ!
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZentPurpleButton), shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Outlined.PlayArrow, null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Revisar Assunto", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    OutlinedButton(
                        onClick = { /* Ver texto */ }, modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, ZentGrayLight), colors = ButtonDefaults.outlinedButtonColors(contentColor = ZentTextDark)
                    ) {
                        Icon(Icons.Outlined.Visibility, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Material", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // 4. Lista de Cartas
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Cartas (${questions.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                    TextButton(onClick = { /* Adicionar manual */ }) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = ZentTextDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adicionar", color = ZentTextDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            items(questions) { question ->
                QuestionCardItem(question = question)
            }

            // 5. Footer Dica
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Event, null, tint = ZentGreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Próxima Revisão: $nextReviewText", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Este assunto tem ${questions.size} cartas. Quanto melhor o seu desempenho, maior será o intervalo até a próxima revisão!", fontSize = 13.sp, color = ZentGrayText, lineHeight = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TopicHeaderCard(topic: Topic, progress: Float) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Matéria › ${topic.title}", fontSize = 12.sp, color = ZentGrayText)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(ZentGrayLight.copy(alpha=0.5f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Book, null, tint = ZentGrayText, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(topic.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ZentTextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(topic.sourceMaterial.take(60) + "...", fontSize = 14.sp, color = ZentGrayText, lineHeight = 20.sp, maxLines = 2)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Domínio do Assunto", fontSize = 12.sp, color = ZentGrayText)
                Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)), color = ZentGreenPrimary, trackColor = ZentGrayLight)
        }
    }
}

@Composable
fun QuestionCardItem(question: Question) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Frente", fontSize = 11.sp, color = ZentGrayText, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Default.MoreVert, null, tint = ZentGrayText, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(question.questionText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = ZentTextDark)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Verso", fontSize = 11.sp, color = ZentGrayText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(question.correctAnswer, fontSize = 14.sp, color = ZentGrayText, lineHeight = 20.sp)
        }
    }
}