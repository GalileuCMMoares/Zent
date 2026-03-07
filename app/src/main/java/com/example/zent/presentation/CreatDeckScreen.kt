package com.example.zent.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.viewmodel.AuthEvent
import com.example.zent.viewmodel.AuthState
import com.example.zent.viewmodel.ZentViewModel
import org.koin.androidx.compose.koinViewModel

val ZentInputBackground = Color(0xFFF3F4F6)
val ZentCardBorder = Color(0xFFE5E7EB)

// Cores Disponíveis para o Baralho
data class DeckColor(val name: String, val hex: String, val color: Color)

val availableColors = listOf(
    DeckColor("Verde Menta", "#7E9F8F", Color(0xFF7E9F8F)),
    DeckColor("Verde Sálvia", "#A6C4B4", Color(0xFFA6C4B4)),
    DeckColor("Roxo Suave", "#A89BC6", Color(0xFFA89BC6)),
    DeckColor("Rosa Suave", "#D3A9A9", Color(0xFFD3A9A9)),
    DeckColor("Pêssego", "#F2C7B0", Color(0xFFF2C7B0)),
    DeckColor("Azul Claro", "#B0E0E6", Color(0xFFB0E0E6))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckScreen(
    onBackClick: () -> Unit,
    viewModel: ZentViewModel = koinViewModel() // <-- Usando o seu ViewModel único!
) {
    val context = LocalContext.current

    // Estados do Formulário
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(availableColors[0]) }

    // Estado de Carregamento (para o botão)
    val authState by viewModel.authState.collectAsState()

    // Escutar eventos de navegação e Toast
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateBack -> onBackClick() // Salvo com sucesso ou Cancelado
                is AuthEvent.NavigateToHome -> onBackClick() // Segurança extra
                is AuthEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zent", fontWeight = FontWeight.Bold, color = Color(0xFFA89BC6), fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // Cancelar/Voltar pelo botão do topo
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Nova Matéria", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
            Text("Crie um novo baralho de estudos", fontSize = 14.sp, color = ZentGrayText)

            Spacer(modifier = Modifier.height(24.dp))

            // 1. INFORMAÇÕES BÁSICAS
            CardSection(icon = Icons.Outlined.MenuBook, title = "Informações Básicas") {
                Text("Nome da Matéria *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZentTextDark)
                Spacer(modifier = Modifier.height(8.dp))
                ZentSimpleTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Ex: História do Brasil, Inglês Avançado..."
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Descrição (Opcional)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZentTextDark)
                Spacer(modifier = Modifier.height(8.dp))
                ZentSimpleTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Adicione uma breve descrição sobre o conteúdo desta matéria...",
                    minLines = 3
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. ESCOLHA DE COR
            CardSection(icon = Icons.Outlined.ColorLens, title = "Escolha uma Cor") {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(200.dp), // Altura fixa para o Grid funcionar dentro do Scroll
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(availableColors) { colorItem ->
                        ColorSelectorItem(
                            item = colorItem,
                            isSelected = selectedColor == colorItem,
                            onClick = { selectedColor = colorItem }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. VISUALIZAÇÃO
            CardSection(icon = Icons.Outlined.Book, title = "Visualização") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ZentBackground),
                    border = BorderStroke(1.dp, ZentCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedColor.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Book, null, tint = selectedColor.color, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = name.ifBlank { "Nome da Matéria" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = ZentTextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("0 cartas  •  Criado agora", fontSize = 12.sp, color = ZentGrayText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÕES DE AÇÃO
            Button(
                onClick = { viewModel.createDeck(name, description, selectedColor.hex) }, // <-- Chama o ViewModel!
                enabled = authState !is AuthState.Loading, // Desativa se estiver carregando
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA89BC6)), // Roxo Zent
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Criar Matéria", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick, // <-- Botão cancelar apenas volta
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ZentCardBorder),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Cancelar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZentTextDark)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // FOOTER (Dica)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZentInputBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Lightbulb, null, tint = Color(0xFFEAB308), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Próximos Passos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Após criar sua matéria, você poderá adicionar conteúdo fazendo upload de PDFs, colando textos ou escaneando documentos. Nossa IA irá gerar questões automaticamente!",
                        fontSize = 13.sp,
                        color = ZentGrayText,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- COMPONENTES SECUNDÁRIOS ---

@Composable
fun CardSection(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ZentCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(ZentInputBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Color(0xFF7E9F8F), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun ZentSimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = ZentGrayText, fontSize = 14.sp) },
        minLines = minLines,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = ZentInputBackground,
            unfocusedContainerColor = ZentInputBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = ZentTextDark,
            unfocusedTextColor = ZentTextDark
        )
    )
}

@Composable
fun ColorSelectorItem(item: DeckColor, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) item.color else ZentCardBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(contentAlignment = Alignment.TopEnd) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(borderWidth, borderColor)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(item.color)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.name, fontSize = 11.sp, color = ZentTextDark, fontWeight = FontWeight.Medium)
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(item.color)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}