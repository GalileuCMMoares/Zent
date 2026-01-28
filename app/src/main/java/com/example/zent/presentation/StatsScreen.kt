package com.zent.app.presentation.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = contentPadding, // Aplica o padding global
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Removido: item { StatsTopBar() }

        item { SummaryCardsRow() }
        item { RetentionChartCard() }
        item { HeatmapCard() }
        item {
            SubjectPerformanceCard()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- COMPONENTES VISUAIS ---

@Composable
fun StatsTopBar() {
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
fun SummaryCardsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            value = "7",
            label = "dias seguidos",
            iconColor = ZentGreenPrimary,
            iconBg = ZentGreenLight
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TrackChanges,
            value = "87%",
            label = "taxa de acerto",
            iconColor = ZentPurple,
            iconBg = ZentPurpleLight
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TrendingUp,
            value = "245",
            label = "cartas estudadas",
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
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ZentTextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = ZentGrayText, lineHeight = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun RetentionChartCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Retenção de Memória", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZentTextDark)
            Text("Últimos 7 dias", fontSize = 12.sp, color = ZentGrayText)

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val dataPoints = listOf(65f, 72f, 78f, 85f, 82f, 89f, 92f)
                    val stepX = width / (dataPoints.size - 1)

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

                Row(
                    modifier = Modifier.fillMaxSize().padding(top = 160.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
                    days.forEach { day -> Text(day, fontSize = 10.sp, color = ZentGrayText) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeatmapCard() {
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
                                val color = when {
                                    colIndex == 3 && rowIndex == 1 -> ZentGreenDarker
                                    colIndex == 2 && rowIndex == 5 -> ZentGreenDarker
                                    colIndex == 4 && rowIndex == 6 -> ZentGreenDarker
                                    (colIndex + rowIndex) % 3 == 0 -> ZentGreenPrimary.copy(alpha = 0.5f)
                                    (colIndex * rowIndex) % 2 == 0 -> ZentGrayLight
                                    else -> ZentGreenLight
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
                    listOf(ZentGrayLight, ZentGreenLight, ZentGreenPrimary.copy(alpha=0.5f), ZentGreenDarker).forEach {
                        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(it))
                    }
                }
                Text("Mais", fontSize = 10.sp, color = ZentGrayText)
            }
        }
    }
}

@Composable
fun SubjectPerformanceCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Desempenho por Matéria", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZentTextDark)

            Spacer(modifier = Modifier.height(20.dp))

            SubjectProgressItem("História", 0.92f, ZentGreenDarker)
            Spacer(modifier = Modifier.height(16.dp))

            SubjectProgressItem("Inglês", 0.88f, ZentGreenPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            SubjectProgressItem("Kotlin", 0.81f, ZentPurple)
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
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = ZentGrayLight
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PreviewStatsScreen() {
    StatsScreen()
}