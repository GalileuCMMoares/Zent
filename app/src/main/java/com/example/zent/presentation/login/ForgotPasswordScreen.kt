package com.example.zent.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.viewmodel.AuthState
import com.example.zent.viewmodel.ZentViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onBackToLoginClick: () -> Unit = {},
    viewModel: ZentViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // Controlo local para mostrar mensagem de sucesso, já que o ViewModel volta para Idle
    var showSuccessMessage by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ZentBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 1. Cabeçalho (Logo + Títulos) - Assumindo que HeaderSection() está acessível
            HeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Card Principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Botão Voltar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onBackToLoginClick() }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = ZentGrayText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Voltar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZentGrayText
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Esqueceu a senha?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZentTextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sem problemas! Digite seu email e enviaremos um link para você redefinir sua senha.",
                        fontSize = 14.sp,
                        color = ZentGrayText,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de E-mail
                    ZentTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel.resetState()
                            showSuccessMessage = false
                        },
                        label = "Email",
                        placeholder = "seu@email.com",
                        icon = Icons.Outlined.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else if (showSuccessMessage) {
                        Text(
                            text = "Link de recuperação enviado! Verifique a sua caixa de entrada.",
                            color = ZentGreenDarker,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Botão Enviar Link
                    Button(
                        onClick = {
                            viewModel.sendPasswordReset(email)
                            showSuccessMessage = true
                        },
                        enabled = authState !is AuthState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = ZentPurpleButton),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Enviar Link de Recuperação", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão Voltar para Login (Fundo Cinza Claro)
                    Button(
                        onClick = onBackToLoginClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ZentInputBackground),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        val annotatedText = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = ZentGrayText, fontWeight = FontWeight.Normal)) {
                                append("Lembrou sua senha? ")
                            }
                            withStyle(style = SpanStyle(color = ZentGreenDarker, fontWeight = FontWeight.Bold)) {
                                append("Fazer login")
                            }
                        }
                        Text(text = annotatedText, fontSize = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Rodapé Legal
            Text(
                text = "Ao continuar, você concorda com nossos Termos de Uso e Política\nde Privacidade",
                fontSize = 12.sp,
                color = ZentGrayText,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PreviewForgotPasswordScreen() {
    ForgotPasswordScreen(
    )
}