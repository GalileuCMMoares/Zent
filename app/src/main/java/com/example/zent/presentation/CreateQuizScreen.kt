package com.zent.app.presentation.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity

// --- CORES (Reutilizando e Adicionando novas) ---
val ZentPurple = Color(0xFFA89BC6)
val ZentTextDark = Color(0xFF1F2937)
val ZentGrayText = Color(0xFF6B7280)
val ZentGreenLight = Color(0xFFE8F5E9) // Fundo selecionado
val ZentGreenBorder = Color(0xFF7E9F8F) // Borda selecionada
val ZentBackground = Color(0xFFF9FAFB)

@Composable
fun CreateQuizScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp) // Recebe padding global
) {
    // Estado
    var selectedOption by remember { mutableStateOf(CreateOption.PDF) }

    // NÃO TEM MAIS SCAFFOLD AQUI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Espaço para compensar a TopBar Global
        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))

        Spacer(modifier = Modifier.height(16.dp))

        // Cabeçalho
        Text(
            text = "Criar Conteúdo com IA",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ZentTextDark
        )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Transforme seus materiais em quizzes inteligentes",
                fontSize = 14.sp,
                color = ZentGrayText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Seletor de Opções (3 Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OptionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.FileUpload,
                    label = "Upload PDF",
                    isSelected = selectedOption == CreateOption.PDF,
                    onClick = { selectedOption = CreateOption.PDF }
                )
                OptionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Description,
                    label = "Colar Texto",
                    isSelected = selectedOption == CreateOption.TEXT,
                    onClick = { selectedOption = CreateOption.TEXT }
                )
                OptionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CameraAlt,
                    label = "Escanear Foto",
                    isSelected = selectedOption == CreateOption.PHOTO,
                    onClick = { selectedOption = CreateOption.PHOTO }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Área de Conteúdo Dinâmico
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Altura fixa para manter layout estável
            ) {
                when (selectedOption) {
                    CreateOption.PDF -> PdfUploadContent()
                    CreateOption.TEXT -> TextPasteContent()
                    CreateOption.PHOTO -> PhotoScanContent()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Gerar (Roxo)
            Button(
                onClick = { /* Lógica de Gerar */ },
                colors = ButtonDefaults.buttonColors(containerColor = ZentPurple),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gerar Quiz com IA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dica
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.Lightbulb, null, tint = Color(0xFFEAB308), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Dica", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZentTextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Quanto mais detalhado o conteúdo, melhores serão as perguntas geradas pela IA. Recomendamos textos com pelo menos 200 palavras.",
                        fontSize = 13.sp,
                        color = ZentGrayText,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }


// --- Enum para as Opções ---
enum class CreateOption { PDF, TEXT, PHOTO }

// --- Componentes Menores ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("Zent", fontWeight = FontWeight.Bold, color = ZentPurple, fontSize = 24.sp)
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZentTextDark)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ZentBackground)
    )
}

@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) ZentGreenBorder else Color(0xFFE5E7EB)
    val containerColor = if (isSelected) Color.White else Color.White // Mantém branco, só borda muda no print
    // No print a selecionada parece não ter fundo verde forte, apenas borda verde.
    // Mas vamos colocar um leve tint se quiser, ou seguir o print estrito (apenas borda).

    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Círculo com ícone
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ZentGreenLight, shape = RoundedCornerShape(50)), // Fundo verde claro do ícone
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ZentGreenBorder,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = ZentTextDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- Conteúdos Dinâmicos ---

@Composable
fun PdfUploadContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .dashedBorder(2.dp, Color(0xFFD1D5DB), 16.dp), // Borda pontilhada
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(ZentGreenLight, shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.FileUpload, null, tint = ZentGreenBorder, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Arraste seu PDF aqui", fontWeight = FontWeight.Bold, color = ZentTextDark)
            Text("ou clique para selecionar", fontSize = 12.sp, color = ZentGrayText)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(50)
            ) {
                Text("Selecionar Arquivo", color = ZentTextDark, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun TextPasteContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Cole seu conteúdo aqui", fontSize = 14.sp, color = ZentGrayText)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)), // Cinza input
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        "Cole aqui o texto que deseja transformar em quiz... Pode ser um resumo, notas de aula, trechos de livros, etc.",
                        fontSize = 14.sp,
                        color = ZentGrayText
                    )
                },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun PhotoScanContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .dashedBorder(2.dp, Color(0xFFD1D5DB), 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(ZentGreenLight, shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.CameraAlt, null, tint = ZentGreenBorder, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tire uma foto do conteúdo", fontWeight = FontWeight.Bold, color = ZentTextDark)
            Text("Use a câmera do dispositivo", fontSize = 12.sp, color = ZentGrayText)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(50)
            ) {
                Text("Abrir Câmera", color = ZentTextDark, fontSize = 12.sp)
            }
        }
    }
}

// --- Utilitário de Borda Pontilhada ---
fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawBehind {
                val stroke = Stroke(
                    width = strokeWidthPx,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                )

                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx)
                )
            }
        )
    }
)
