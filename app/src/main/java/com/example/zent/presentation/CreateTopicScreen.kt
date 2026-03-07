package com.zent.app.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.viewmodel.AuthEvent
import com.example.zent.viewmodel.ZentViewModel
import org.koin.androidx.compose.koinViewModel

val ZentInputBackground = Color(0xFFF3F4F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTopicScreen(
    deckId: String,
    onBackClick: () -> Unit,
    viewModel: ZentViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val deck by viewModel.selectedDeck.collectAsState()

    // O ViewModel garante que temos os dados do Deck para o cabeçalho
    LaunchedEffect(deckId) {
        viewModel.loadDeckDetails(deckId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateBack -> onBackClick()
                is AuthEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    // --- ESTADOS DO FLUXO ---
    var currentStep by remember { mutableIntStateOf(1) } // 1: Info, 2: Material, 3: Gerando
    var topicName by remember { mutableStateOf("") }
    var topicDesc by remember { mutableStateOf("") }
    var materialText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Texto, 1: PDF, 2: Foto

    // Dispara a IA e avança para o passo 3
    val onGenerateClick = {
        if (materialText.isNotBlank()) {
            currentStep = 3
            viewModel.generateTopicWithIA(deckId, topicName, topicDesc, materialText)
        } else {
            Toast.makeText(context, "Cole algum material para a IA analisar.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zent", fontWeight = FontWeight.Bold, color = ZentPurpleButton, fontSize = 24.sp) },
                navigationIcon = {
                    if (currentStep < 3) {
                        IconButton(onClick = {
                            if (currentStep == 2) currentStep = 1 else onBackClick()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZentTextDark)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZentBackground)
            )
        },
        containerColor = ZentBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Cabeçalho da Matéria
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Book, null, tint = ZentGrayText, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(deck?.title ?: "Carregando...", fontSize = 14.sp, color = ZentGrayText)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text("Criar Novo Assunto", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
            Text(
                text = when (currentStep) {
                    1 -> "Dê um nome e descrição ao assunto"
                    2 -> "Adicione o material de estudo"
                    else -> "Gerando cartas com IA..."
                },
                fontSize = 14.sp, color = ZentGrayText
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Progresso (3 linhas)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i <= currentStep) ZentGreenPrimary else ZentGrayLight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CONTEÚDO DINÂMICO BASEADO NO PASSO ---
            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    1 -> Step1Info(
                        name = topicName,
                        onNameChange = { topicName = it },
                        desc = topicDesc,
                        onDescChange = { topicDesc = it }
                    )
                    2 -> Step2Material(
                        selectedTab = selectedTab,
                        onTabChange = { selectedTab = it },
                        text = materialText,
                        onTextChange = { materialText = it }
                    )
                    3 -> Step3Generating()
                }
            }

            // --- BOTÕES DO RODAPÉ ---
            if (currentStep == 1) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, ZentGrayLight)
                    ) {
                        Text("Cancelar", color = ZentTextDark, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { if (topicName.isNotBlank()) currentStep = 2 else Toast.makeText(context, "Preencha o nome", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZentPurpleButton),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Próximo", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (currentStep == 2) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { currentStep = 1 },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, ZentGrayLight)
                    ) {
                        Text("Voltar", color = ZentTextDark, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onGenerateClick,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZentGreenPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Outlined.AutoAwesome, null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerar com IA", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun Step1Info(name: String, onNameChange: (String) -> Unit, desc: String, onDescChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ZentGrayLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Nome do Assunto *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZentTextDark)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("Ex: Período Colonial, Independência...", color = ZentGrayText, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ZentInputBackground, unfocusedContainerColor = ZentInputBackground,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Descrição (opcional)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZentTextDark)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = desc,
                onValueChange = onDescChange,
                placeholder = { Text("Descreva brevemente o que será estudado...", color = ZentGrayText, fontSize = 14.sp) },
                minLines = 4,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ZentInputBackground, unfocusedContainerColor = ZentInputBackground,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun Step2Material(selectedTab: Int, onTabChange: (Int) -> Unit, text: String, onTextChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ZentGrayLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Tabs Customizadas
            Row(
                modifier = Modifier.fillMaxWidth().background(ZentInputBackground, RoundedCornerShape(50)).padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TabButton("Texto", Icons.Outlined.TextFields, selectedTab == 0) { onTabChange(0) }
                TabButton("PDF", Icons.Outlined.FileUpload, selectedTab == 1) { onTabChange(1) }
                TabButton("Foto", Icons.Outlined.CameraAlt, selectedTab == 2) { onTabChange(2) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conteúdo da Tab
            when (selectedTab) {
                0 -> {
                    Text("Cole ou digite o conteúdo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZentTextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = text,
                        onValueChange = onTextChange,
                        placeholder = { Text("Cole aqui o texto do seu material de estudo...", color = ZentGrayText, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(250.dp).border(1.dp, ZentGreenPrimary.copy(alpha=0.5f), RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ZentInputBackground, unfocusedContainerColor = ZentInputBackground,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Text("${text.length} caracteres", fontSize = 11.sp, color = ZentGrayText, modifier = Modifier.padding(top = 4.dp))
                }
                1 -> PlaceholderMaterial(Icons.Outlined.FileUpload, "Clique para fazer upload", "PDF até 10MB")
                2 -> PlaceholderMaterial(Icons.Outlined.CameraAlt, "Tirar foto do material", "Tire uma foto de livros ou cadernos")
            }
        }
    }
}

@Composable
fun RowScope.TabButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color.White else Color.Transparent
    val contentColor = if (isSelected) ZentTextDark else ZentGrayText

    Row(
        modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(50)).background(bgColor).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = contentColor, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
fun PlaceholderMaterial(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxWidth().height(250.dp).background(ZentBackground, RoundedCornerShape(16.dp)).border(1.dp, ZentGrayLight, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(ZentGreenPrimary.copy(alpha=0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = ZentGreenPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Bold, color = ZentTextDark)
            Text(subtitle, fontSize = 12.sp, color = ZentGrayText)
        }
    }
}

@Composable
fun Step3Generating() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(32.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(ZentGrayLight.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ZentGreenPrimary, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Analisando material e gerando cartas...", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
                Spacer(modifier = Modifier.height(16.dp))
                Text("A IA está processando o conteúdo e criando perguntas inteligentes", fontSize = 14.sp, color = ZentGrayText, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary.copy(alpha=0.6f)))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary.copy(alpha=0.3f)))
                }
            }
        }
    }
}