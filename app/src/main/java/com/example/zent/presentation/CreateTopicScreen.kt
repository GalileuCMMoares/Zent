package com.zent.app.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zent.viewmodel.AuthEvent
import com.example.zent.viewmodel.AuthState
import com.example.zent.viewmodel.ZentViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTopicScreen(
    deckId: String,
    onBackClick: () -> Unit,
    viewModel: ZentViewModel = koinViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateBack -> onBackClick()
                is AuthEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    val deck by viewModel.selectedDeck.collectAsState()
    LaunchedEffect(deckId) { if (deck?.id != deckId) viewModel.loadDeckDetails(deckId) }

    var topicName by remember { mutableStateOf("") }
    var topicDescription by remember { mutableStateOf("") }
    var materialText by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("Ensino Médio") }
    val levelOptions = listOf("Fundamental", "Ensino Médio", "Faculdade/Concurso")

    var currentStep by remember { mutableStateOf(1) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val authState by viewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading
    val displayStep = if (isLoading) 3 else currentStep

    // --- ARQUIVOS ANEXADOS ---
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            selectedPdfUri = null // Limpa PDF se escolher Foto
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedPdfUri = uri
            selectedImageUri = null // Limpa Foto se escolher PDF
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zent", fontWeight = FontWeight.Bold, color = ZentPurpleButton, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = { if (currentStep == 2 && !isLoading) currentStep = 1 else onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZentTextDark)
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Book, null, tint = ZentGrayText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(deck?.title ?: "Matéria", fontSize = 12.sp, color = ZentGrayText)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Criar Novo Assunto", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)

                val subtitle = when(displayStep) {
                    1 -> "Dê um nome e descrição ao assunto"
                    2 -> "Adicione o material de estudo"
                    else -> "Gerando cartas com IA..."
                }
                Text(subtitle, fontSize = 14.sp, color = ZentGrayText)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(50)).background(if (displayStep >= 1) ZentGreenPrimary else ZentGrayLight))
                Box(modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(50)).background(if (displayStep >= 2) ZentGreenPrimary else ZentGrayLight))
                Box(modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(50)).background(if (displayStep >= 3) ZentGreenPrimary else ZentGrayLight))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (displayStep == 3) {
                Card(
                    shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = ZentGreenPrimary, modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Analisando material e gerando cartas...", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZentTextDark, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("A IA está processando o conteúdo e criando perguntas inteligentes", fontSize = 13.sp, color = ZentGrayText, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary.copy(alpha = 0.7f)))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ZentGreenPrimary.copy(alpha = 0.4f)))
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ZentGrayLight), modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        if (currentStep == 1) {
                            Text("Nome do Assunto *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = topicName, onValueChange = { topicName = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = ZentGrayLight, focusedBorderColor = ZentGreenPrimary), singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Descrição (opcional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = topicDescription, onValueChange = { topicDescription = it }, placeholder = { Text("Descreva brevemente...", color = ZentGrayText, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = ZentGrayLight, focusedBorderColor = ZentGreenPrimary)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text("Nível Escolar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                levelOptions.forEach { level ->
                                    FilterChip(
                                        selected = (selectedLevel == level), onClick = { selectedLevel = level }, label = { Text(level, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ZentGreenPrimary.copy(alpha = 0.15f), selectedLabelColor = ZentGreenPrimary),
                                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = (selectedLevel == level), borderColor = if (selectedLevel == level) ZentGreenPrimary else ZentGrayLight)
                                    )
                                }
                            }
                        } else {
                            Row(modifier = Modifier.fillMaxWidth().background(ZentBackground, RoundedCornerShape(16.dp)).padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                val tabs = listOf(Pair("Texto", Icons.Outlined.TextFields), Pair("PDF", Icons.Outlined.Description), Pair("Foto", Icons.Outlined.Image))
                                tabs.forEachIndexed { index, tab ->
                                    val isSelected = selectedTabIndex == index
                                    Row(
                                        modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color.White else Color.Transparent)
                                            .clickable { selectedTabIndex = index },
                                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(tab.second, null, tint = if (isSelected) ZentTextDark else ZentGrayText, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tab.first, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) ZentTextDark else ZentGrayText)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            when (selectedTabIndex) {
                                0 -> {
                                    Text("Cole ou digite o conteúdo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = materialText, onValueChange = { materialText = it }, placeholder = { Text("Cole aqui o texto...", color = ZentGrayText, fontSize = 14.sp) },
                                        modifier = Modifier.fillMaxWidth().height(200.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = ZentGrayLight, focusedBorderColor = ZentGreenPrimary)
                                    )
                                }
                                1 -> {
                                    if (selectedPdfUri != null) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(200.dp).background(ZentGreenPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)).border(1.dp, ZentGreenPrimary, RoundedCornerShape(12.dp)).clickable { pdfPickerLauncher.launch("application/pdf") },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Outlined.CheckCircle, null, tint = ZentGreenPrimary, modifier = Modifier.size(40.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("PDF Anexado e Pronto!", fontWeight = FontWeight.Bold, color = ZentGreenPrimary)
                                                Text("Clique novamente se quiser trocar", fontSize = 12.sp, color = ZentGrayText)
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(200.dp).background(ZentBackground, RoundedCornerShape(12.dp)).border(1.dp, ZentGrayLight, RoundedCornerShape(12.dp)).clickable { pdfPickerLauncher.launch("application/pdf") },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Outlined.Description, null, tint = ZentGreenPrimary, modifier = Modifier.size(32.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Clique para anexar PDF", fontWeight = FontWeight.Bold, color = ZentTextDark)
                                                Text("A IA lerá os slides/páginas inteiras", fontSize = 12.sp, color = ZentGrayText)
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    if (selectedImageUri != null) {
                                        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                                            AsyncImage(model = selectedImageUri, contentDescription = "Foto Selecionada", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Outlined.CheckCircle, null, tint = Color.White)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Foto Anexada (Clique p/ trocar)", color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(200.dp).background(ZentBackground, RoundedCornerShape(12.dp)).border(1.dp, ZentGrayLight, RoundedCornerShape(12.dp)).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Outlined.Image, null, tint = ZentGreenPrimary, modifier = Modifier.size(32.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Tirar foto do material", fontWeight = FontWeight.Bold, color = ZentTextDark)
                                                Text("Tire uma foto de livros ou cadernos", fontSize = 12.sp, color = ZentGrayText)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { if (currentStep == 2) currentStep = 1 else onBackClick() }, modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, ZentGrayLight), colors = ButtonDefaults.outlinedButtonColors(contentColor = ZentTextDark)
                    ) {
                        Text(if (currentStep == 2) "Voltar" else "Cancelar", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (currentStep == 1) {
                                if (topicName.isNotBlank()) currentStep = 2
                            } else {
                                if (materialText.isNotBlank() || selectedImageUri != null || selectedPdfUri != null) {
                                    viewModel.generateTopicWithIA(deckId, topicName, topicDescription, materialText, selectedLevel, selectedImageUri, selectedPdfUri, context)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentStep == 1) ZentGreenPrimary else ZentPurpleButton),
                        enabled = if (currentStep == 1) topicName.isNotBlank() else (materialText.isNotBlank() || selectedImageUri != null || selectedPdfUri != null)
                    ) {
                        Text(if (currentStep == 1) "Próximo" else "✨ Gerar com IA", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}