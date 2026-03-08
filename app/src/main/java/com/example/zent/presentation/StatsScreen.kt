package com.zent.app.presentation.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.domain.model.Deck
import com.example.zent.domain.model.Topic
import com.example.zent.viewmodel.ZentViewModel
import com.zent.app.presentation.parseHexColor
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

// --- CORES ---
val ZentBackground = Color(0xFFF9FAFB)
val ZentGreenPrimary = Color(0xFF7E9F8F)
val ZentGreenDarker = Color(0xFF6B8A7A)
val ZentTextDark = Color(0xFF1F2937)
val ZentPurple = Color(0xFFA89BC6)
val ZentPurpleLight = Color(0xFFF3E8FF)
val ZentGreenLight = Color(0xFFE8F5E9)
val ZentGrayText = Color(0xFF9CA3AF)
val ZentGrayLight = Color(0xFFE5E7EB)

@Composable
fun StatsScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: ZentViewModel = koinViewModel()
) {
    val decks by viewModel.decks.collectAsState()
    val allTopics by viewModel.allTopics.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()

    // Estatísticas reais
    val totalCards = allQuestions.size
    val totalTopics = allTopics.size
    val masteredTopics = allTopics.count { it.intervalDays > 5 }
    val studiedTopics = allTopics.count { it.repetitions > 0 }
    val masteryPercent = if (totalTopics > 0) ((masteredTopics.toFloat() / totalTopics) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SummaryCardsRow(
                studiedTopics = studiedTopics,
                masteryPercent = masteryPercent,
                totalCards = totalCards
            )
        }
        item { RetentionChartCard(decks = decks, allTopics = allTopics) }
        item { HeatmapCard(allTopics = allTopics) }
        item {
            SubjectPerformanceCard(decks = decks, allTopics = allTopics)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- COMPONENTES ---

@Composable
fun SummaryCardsRow(studiedTopics: Int, masteryPercent: Int, totalCards: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            value = studiedTopics.toString(),
            label = "assuntos estudados",
            iconColor = ZentGreenPrimary,
            iconBg = ZentGreenLight
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TrackChanges,
            value = "$masteryPercent%",
            label = "domínio geral",
            iconColor = ZentPurple,
            iconBg = ZentPurpleLight
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TrendingUp,
            value = totalCards.toString(),
            label = "cartas criadas",
            iconColor = ZentGreenPrimary,
            iconBg = ZentGreenLight
        )
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    iconColor: Color,
    iconBg: Color
) {
    Card(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ZentTextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = ZentGrayText, lineHeight = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun RetentionChartCard(decks: List<Deck>, allTopics: List<Topic>) {
    // Calcula o progresso de domínio por matéria (0-100%)
    val deckProgress = decks.map { deck ->
        val deckTopics = allTopics.filter { it.deckId == deck.id }
        val mastered = deckTopics.count { it.intervalDays > 5 }
        val progress = if (deckTopics.isNotEmpty()) (mastered.toFloat() / deckTopics.size * 100f) else 0f
        Pair(deck.title, progress)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Domínio por Matéria", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZentTextDark)
            Text("Percentual de assuntos dominados", fontSize = 12.sp, color = ZentGrayText)

            Spacer(modifier = Modifier.height(24.dp))

            if (deckProgress.isEmpty()) {
                Text("Nenhuma matéria criada ainda.", fontSize = 14.sp, color = ZentGrayText, modifier = Modifier.padding(vertical = 24.dp))
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        val dataPoints = if (deckProgress.isNotEmpty()) {
                            deckProgress.map { it.second }
                        } else {
                            listOf(0f)
                        }

                        if (dataPoints.size == 1) {
                            // Ponto único: desenha um círculo
                            val y = height - (dataPoints[0] / 100f * height)
                            drawCircle(color = ZentGreenPrimary, center = Offset(width / 2f, y), radius = 8f)
                        } else {
                            val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

                            val gridLines = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
                            gridLines.forEach { percent ->
                                drawLine(
                                    color = ZentGrayLight,
                                    start = Offset(0f, height * percent),
                                    end = Offset(width, height * percent),
                                    strokeWidth = 2f
                                )
                            }

                            val path = Path()
                            dataPoints.forEachIndexed { index, value ->
                                val x = index * stepX
                                val y = height - (value / 100f * height)
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                drawCircle(color = ZentGreenPrimary, center = Offset(x, y), radius = 8f)
                            }

                            drawPath(
                                path = path,
                                color = ZentGreenPrimary,
                                style = Stroke(width = 6f, cap = StrokeCap.Round)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    deckProgress.forEach { (name, _) ->
                        Text(name.take(8), fontSize = 10.sp, color = ZentGrayText, maxLines = 1)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeatmapCard(allTopics: List<Topic>) {
    // Gera heatmap baseado na atividade de estudo real
    // Usa nextReviewDate e repetitions para inferir atividade
    val today = Calendar.getInstance()
    val totalDays = 35 // 5 semanas

    // Calcula atividade por dia: quantos assuntos tinham revisão agendada
    val activityMap = mutableMapOf<Int, Int>()
    for (dayOffset in 0 until totalDays) {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -dayOffset)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
        }
        val dayStart = Calendar.getInstance().apply {
            timeInMillis = cal.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        val dayEnd = Calendar.getInstance().apply {
            timeInMillis = cal.timeInMillis
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59)
        }.timeInMillis

        // Conta assuntos que tinham revisão marcada nesse dia (com repetitions > 0)
        val count = allTopics.count { topic ->
            topic.repetitions > 0 && topic.nextReviewDate in dayStart..dayEnd
        }
        activityMap[dayOffset] = count
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = ZentTextDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Calendário de Estudos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
                    Text("Últimas 5 semanas", fontSize = 12.sp, color = ZentGrayText)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height(160.dp).padding(end = 8.dp)
                ) {
                    Text("Seg", fontSize = 10.sp, color = ZentGrayText)
                    Text("Qua", fontSize = 10.sp, color = ZentGrayText)
                    Text("Sex", fontSize = 10.sp, color = ZentGrayText)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(5) { colIndex ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            repeat(7) { rowIndex ->
                                val dayOffset = (4 - colIndex) * 7 + (6 - rowIndex)
                                val activity = activityMap[dayOffset] ?: 0
                                val color = when {
                                    activity >= 3 -> ZentGreenDarker
                                    activity == 2 -> ZentGreenPrimary
                                    activity == 1 -> ZentGreenPrimary.copy(alpha = 0.5f)
                                    else -> ZentGrayLight
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(color)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Menos", fontSize = 10.sp, color = ZentGrayText)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(ZentGrayLight, ZentGreenPrimary.copy(alpha = 0.5f), ZentGreenPrimary, ZentGreenDarker).forEach {
                        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(it))
                    }
                }
                Text("Mais", fontSize = 10.sp, color = ZentGrayText)
            }
        }
    }
}

@Composable
fun SubjectPerformanceCard(decks: List<Deck>, allTopics: List<Topic>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Desempenho por Matéria", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZentTextDark)

            Spacer(modifier = Modifier.height(20.dp))

            if (decks.isEmpty()) {
                Text("Nenhuma matéria criada ainda.", fontSize = 14.sp, color = ZentGrayText)
            } else {
                decks.forEachIndexed { index, deck ->
                    val deckTopics = allTopics.filter { it.deckId == deck.id }
                    val mastered = deckTopics.count { it.intervalDays > 5 }
                    val progress = if (deckTopics.isNotEmpty()) mastered.toFloat() / deckTopics.size else 0f
                    val color = parseHexColor(deck.colorHex)

                    SubjectProgressItem(name = deck.title, progress = progress, color = color)
                    if (index < decks.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectProgressItem(name: String, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ZentTextDark)
            Text("${(progress * 100).toInt()}%", fontSize = 14.sp, color = ZentGrayText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
            color = color,
            trackColor = ZentGrayLight
        )
    }
}
