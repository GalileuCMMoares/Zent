package com.zent.app.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.domain.model.Question
import com.example.zent.viewmodel.ZentViewModel
import org.json.JSONArray
import org.koin.androidx.compose.koinViewModel

// --- CORES DE FEEDBACK (CARTÃO VERSO) ---
val ColorCorrectBg = Color(0xFFF0FDF4)
val ColorCorrectText = Color(0xFF16A34A)
val ColorWrongBg = Color(0xFFFEF2F2)
val ColorWrongText = Color(0xFFDC2626)

@Composable
fun QuizScreen(
    topicId: String,
    onBackClick: () -> Unit,
    viewModel: ZentViewModel = koinViewModel()
) {
    LaunchedEffect(topicId) { viewModel.loadTopicDetails(topicId) }

    val topic by viewModel.selectedTopic.collectAsState()
    val questions by viewModel.selectedTopicQuestions.collectAsState()

    // Captura as questões da sessão uma única vez para evitar recomposição
    // quando o ViewModel deleta/regenera questões em background
    var sessionLoaded by remember { mutableStateOf(false) }
    var sessionQuestions by remember { mutableStateOf(listOf<Question>()) }

    LaunchedEffect(questions) {
        if (!sessionLoaded && questions.isNotEmpty()) {
            sessionQuestions = questions.takeLast(10).shuffled()
            sessionLoaded = true
        }
    }

    if (topic == null || sessionQuestions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(ZentBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ZentGreenPrimary)
        }
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isFlipped by remember { mutableStateOf(false) }
    var questionResults by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var isFinished by remember { mutableStateOf(false) }

    // Deriva contadores da lista de resultados para a UI
    val correctCount = questionResults.count { it.second }
    val wrongCount = questionResults.count { !it.second }

    val currentQuestion = sessionQuestions[currentIndex]

    val optionsWithLetters = remember(currentQuestion) {
        val list = mutableListOf<String>()
        try {
            val wrongOptionsArray = JSONArray(currentQuestion.options)
            for (i in 0 until wrongOptionsArray.length()) { list.add(wrongOptionsArray.getString(i)) }
        } catch (e: Exception) {
            list.add("Alternativa incorreta genérica")
        }
        list.add(currentQuestion.correctAnswer)

        list.shuffled().mapIndexed { index, text ->
            val letter = ('A' + index).toString()
            Pair(letter, text)
        }
    }

    if (isFinished) {
        QuizResultScreen(
            correctCount = correctCount,
            totalCount = sessionQuestions.size,
            onBackToHome = onBackClick
        )
    } else {
        Scaffold(
            containerColor = ZentBackground,
            topBar = {
                Column {
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / sessionQuestions.size },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = ZentGreenPrimary,
                        trackColor = ZentGrayLight
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Sair", tint = ZentTextDark) }
                        Text("${currentIndex + 1} de ${sessionQuestions.size} • ${topic?.title}", fontSize = 14.sp, color = ZentTextDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                FlipCard(
                    isFlipped = isFlipped,
                    front = { QuizCardFront(questionText = currentQuestion.questionText) },
                    back = {
                        val isCorrect = selectedOption == currentQuestion.correctAnswer
                        val correctPair = optionsWithLetters.find { it.second == currentQuestion.correctAnswer }
                        QuizCardBack(
                            isCorrect = isCorrect,
                            correctLetter = correctPair?.first ?: "A",
                            correctAnswerText = currentQuestion.correctAnswer
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isFlipped) {
                    Text("Leia a pergunta e selecione a alternativa", fontSize = 12.sp, color = ZentGrayText, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))

                    optionsWithLetters.forEach { (letter, optionText) ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, ZentGrayLight),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable {
                                selectedOption = optionText
                                isFlipped = true
                                questionResults = questionResults + Pair(
                                    currentQuestion.difficulty,
                                    optionText == currentQuestion.correctAnswer
                                )
                            }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(letter, fontWeight = FontWeight.Bold, color = ZentGrayText, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(optionText, fontSize = 14.sp, color = ZentTextDark)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (currentIndex < sessionQuestions.size - 1) {
                                currentIndex++
                                selectedOption = null
                                isFlipped = false
                            } else {
                                topic?.let { viewModel.finishQuizAndUpdateTopic(it, questionResults) }
                                isFinished = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZentGreenPrimary)
                    ) {
                        Text(if (currentIndex < sessionQuestions.size - 1) "Próxima Questão →" else "Ver Resultados →", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun FlipCard(isFlipped: Boolean, front: @Composable () -> Unit, back: @Composable () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            Box(Modifier.fillMaxSize()) { front() }
        } else {
            Box(Modifier.fillMaxSize().graphicsLayer { rotationY = 180f }) { back() }
        }
    }
}

@Composable
fun QuizCardFront(questionText: String) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = ZentGreenPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("PERGUNTA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZentGrayText, letterSpacing = 1.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Text(questionText, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ZentTextDark, textAlign = TextAlign.Center, lineHeight = 28.sp)
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.background(ZentBackground, RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Selecione uma alternativa abaixo", fontSize = 12.sp, color = ZentGrayText)
            }
        }
    }
}

@Composable
fun QuizCardBack(isCorrect: Boolean, correctLetter: String, correctAnswerText: String) {
    val bgColor = if (isCorrect) ColorCorrectBg else ColorWrongBg
    val accentColor = if (isCorrect) ColorCorrectText else ColorWrongText
    val icon = if (isCorrect) Icons.Default.Check else Icons.Default.Close
    val title = if (isCorrect) "RESPOSTA CORRETA! \uD83C\uDF89" else "RESPOSTA INCORRETA"

    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = bgColor), border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)), modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = accentColor, letterSpacing = 1.sp)

            Spacer(modifier = Modifier.height(24.dp))

            if (!isCorrect) {
                Text("A resposta correta era:", fontSize = 13.sp, color = ZentGrayText)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.background(ZentGrayLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("$correctLetter. $correctAnswerText", fontWeight = FontWeight.Bold, color = ZentTextDark, textAlign = TextAlign.Center)
                }
            } else {
                Text("Você acertou! A alternativa correta é:", fontSize = 13.sp, color = ZentGrayText)
                Spacer(modifier = Modifier.height(12.dp))
                Text("$correctLetter. $correctAnswerText", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZentTextDark, textAlign = TextAlign.Center, lineHeight = 24.sp)
            }
        }
    }
}

@Composable
fun QuizResultScreen(correctCount: Int, totalCount: Int, onBackToHome: () -> Unit) {
    val accuracy = if (totalCount > 0) ((correctCount.toFloat() / totalCount) * 100).toInt() else 0
    val wrongCount = totalCount - correctCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZentBackground)
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(ZentGrayLight.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = ZentGreenPrimary, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Continue praticando! \uD83D\uDCAA", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
        Text("Você completou a sessão de estudo", fontSize = 14.sp, color = ZentGrayText)

        Spacer(modifier = Modifier.height(32.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(ZentBackground), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.TrendingUp, null, tint = ZentGreenPrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Taxa de Acerto", fontSize = 12.sp, color = ZentGrayText)
                            Text("$accuracy%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(progress = { accuracy / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)), color = ZentGreenPrimary, trackColor = ZentGrayLight)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Distribuição de Respostas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZentTextDark)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary)); Spacer(modifier = Modifier.width(8.dp)); Text("Fácil/Bom", fontSize = 13.sp, color = ZentTextDark) }
                    Text("$correctCount cartas", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ColorWrongText)); Spacer(modifier = Modifier.width(8.dp)); Text("Errei", fontSize = 13.sp, color = ZentTextDark) }
                    Text("$wrongCount cartas", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ZentBackground), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.CheckCircle, null, tint = ZentGreenPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("A consistência é a chave!", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZentTextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("As $wrongCount cartas que você errou voltarão em breve.", fontSize = 13.sp, color = ZentGrayText, lineHeight = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onBackToHome, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = ZentPurpleButton)) {
            Text("Voltar ao Início", fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
