package com.zent.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@Composable
fun ZentTopBar(
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {}
) {
    // Box para definir a altura e o fundo translúcido
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp) // Altura fixa generosa (conta com a status bar)
            .background(Color.White.copy(alpha = 0.80f)) // Efeito Vidro
    ) {
        // Linha com o conteúdo alinhado
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding() // Empurra o conteúdo para baixo da barra de status
                .padding(bottom = 12.dp), // Respiro inferior
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Logo
            Text(
                text = "Zent",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ZentPurple
            )

            // 2. Botão "+ Criar"
            Surface(
                onClick = onActionClick,
                shape = RoundedCornerShape(50),
                color = Color.White,
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
        }

        // Linha divisória sutil na base (opcional)
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 1.dp,
            color = ZentGrayLight.copy(alpha = 0.5f)
        )
    }
}