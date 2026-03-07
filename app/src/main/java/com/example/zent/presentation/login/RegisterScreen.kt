package com.example.zent.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
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

// --- CORES ---
val ZentBackground = Color(0xFFF9FAFB)
val ZentPurple = Color(0xFFA89BC6)
val ZentTextDark = Color(0xFF1F2937)
val ZentGrayText = Color(0xFF6B7280)
val ZentGrayLight = Color(0xFFE5E7EB)
val ZentGreenDarker = Color(0xFF6B8A7A)

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: ZentViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
        }
    }

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

            RegisterHeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Criar conta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZentTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Preencha os dados para começar",
                        fontSize = 14.sp,
                        color = ZentGrayText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ZentTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            viewModel.resetState()
                        },
                        label = "Nome completo",
                        placeholder = "Seu nome",
                        icon = Icons.Outlined.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ZentTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel.resetState()
                        },
                        label = "Email",
                        placeholder = "seu@email.com",
                        icon = Icons.Outlined.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ZentTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            viewModel.resetState()
                        },
                        label = "Senha",
                        placeholder = "Mínimo 6 caracteres",
                        icon = Icons.Outlined.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ZentTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            viewModel.resetState()
                        },
                        label = "Confirmar senha",
                        placeholder = "Digite a senha novamente",
                        icon = Icons.Outlined.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = { viewModel.register(name, email, password, confirmPassword) },
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
                            Text("Criar conta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divisor "ou"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = ZentGrayLight)
                        Text(
                            text = "ou",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = ZentGrayText,
                            fontSize = 14.sp
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = ZentGrayLight)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botões Sociais
                    SocialLoginButton(
                        text = "Continuar com Google",
                        icon = Icons.Outlined.AccountCircle,
                        onClick = { }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SocialLoginButton(
                        text = "Continuar com Apple",
                        icon = Icons.Outlined.PhoneIphone,
                        onClick = { }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Já tem conta? Fazer login
                    val annotatedText = buildAnnotatedString {
                        append("Já tem uma conta? ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = ZentGreenDarker)) {
                            append("Fazer login")
                        }
                    }
                    Text(
                        text = annotatedText,
                        fontSize = 14.sp,
                        color = ZentGrayText,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Rodapé Legal
            Text(
                text = "Ao criar uma conta, você concorda com nossos Termos de Uso e Política\nde Privacidade",
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

// --- COMPONENTES ---

@Composable
fun RegisterHeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Simulação do ícone com gradiente
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF7E9F8F), Color(0xFFA89BC6)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Zent",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ZentPurple
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Comece sua jornada de aprendizado",
            fontSize = 14.sp,
            color = ZentGrayText
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen()
}