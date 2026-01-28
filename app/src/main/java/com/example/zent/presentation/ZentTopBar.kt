package com.zent.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Cores
val ZentPurple = Color(0xFFA89BC6)
val ZentTextDark = Color(0xFF1F2937)
val ZentGrayLight = Color(0xFFE5E7EB)
val ZentBackground = Color(0xFFF9FAFB) // Cor de fundo padrão das telas

@Composable
fun ZentTopBar(
    modifier: Modifier = Modifier,
    title: String = "Zent",
    isMainScreen: Boolean = true, // Define se é tela principal ou detalhe
    onActionClick: () -> Unit = {}, // Ação do botão direito (Criar)
    onBackClick: () -> Unit = {}    // Ação do botão esquerdo (Voltar)
) {
    val backgroundColor = if (isMainScreen) {
        Color.White.copy(alpha = 0.80f) // Vidro na Home
    } else {
        ZentBackground // Sólido (igual ao fundo da tela) na Create
    }

    val showDivider = isMainScreen // Só mostra a linha divisória na Home

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- LADO ESQUERDO (Logo ou Voltar) ---
            if (isMainScreen) {
                // Modo Home: Apenas Texto Logo
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZentPurple
                )
            } else {
                // Modo Detalhe: Botão Voltar + Texto Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(28.dp) // Ajuste fino de tamanho
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = ZentTextDark
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZentPurple
                    )
                }
            }

            // --- LADO DIREITO (Botão Criar ou Vazio) ---
            if (isMainScreen) {
                Surface(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, ZentGrayLight),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = ZentTextDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Criar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZentTextDark
                        )
                    }
                }
            } else {
                // Se não for main screen, lado direito fica vazio (ou pode por outra ação)
                Spacer(modifier = Modifier.width(36.dp))
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                thickness = 1.dp,
                color = ZentGrayLight.copy(alpha = 0.6f)
            )
        }
    }
}
