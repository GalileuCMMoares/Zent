package com.example.zent.presentation.login

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zent.presentation.ZentBackground
import com.example.zent.presentation.ZentGrayLight
import com.example.zent.presentation.ZentGrayText
import com.example.zent.presentation.ZentGreenDarker
import com.example.zent.presentation.ZentPurple
import com.example.zent.presentation.ZentTextDark

val ZentPurpleButton = Color(0xFFA69BBE) // Cor do botão Entrar
val ZentInputBackground = Color(0xFFF3F4F6)


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        containerColor = ZentBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()), // Permite rolagem em telas pequenas
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // 1. Cabeçalho (Logo + Títulos)
            HeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Card Principal de Login
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
                        text = "Bem-vindo de volta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZentTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Entre na sua conta para continuar estudando",
                        fontSize = 14.sp,
                        color = ZentGrayText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de E-mail
                    ZentTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "seu@email.com",
                        icon = Icons.Outlined.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de Senha
                    ZentTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha",
                        placeholder = "••••••••",
                        icon = Icons.Outlined.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // "Esqueceu a senha?"
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Text(
                            text = "Esqueceu a senha?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZentGreenDarker,
                            modifier = Modifier.clickable { /* Ação de recuperar senha */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botão Entrar
                    Button(
                        onClick = onLoginSuccess,
                        colors = ButtonDefaults.buttonColors(containerColor = ZentPurpleButton),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divisor "ou"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = ZentGrayLight)
                        Text(
                            text = "ou",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = ZentGrayText,
                            fontSize = 14.sp
                        )
                        Divider(modifier = Modifier.weight(1f), color = ZentGrayLight)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botões Sociais
                    SocialLoginButton(
                        text = "Continuar com Google",
                        icon = Icons.Outlined.AccountCircle, // TODO: Trocar pela logo real do Google
                        onClick = { }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SocialLoginButton(
                        text = "Continuar com Apple",
                        icon = Icons.Outlined.PhoneIphone, // TODO: Trocar pela logo real da Apple
                        onClick = { }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Criar Conta
                    val annotatedText = buildAnnotatedString {
                        append("Não tem uma conta? ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = ZentGreenDarker)) {
                            append("Criar conta")
                        }
                    }
                    Text(
                        text = annotatedText,
                        fontSize = 14.sp,
                        color = ZentGrayText,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
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

// --- COMPONENTES SECUNDÁRIOS ---

@Composable
fun HeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo
        // Se você já tem o ícone configurado no Android Studio (passo anterior), use assim:
        // Image(
        //     painter = painterResource(id = R.drawable.ic_launcher),
        //     contentDescription = "Zent Logo",
        //     modifier = Modifier.size(80.dp).clip(RoundedCornerShape(24.dp))
        // )

        // Simulação do ícone via código para o preview funcionar perfeitamente:
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
            text = "Estude de forma inteligente, memorize com facilidade",
            fontSize = 14.sp,
            color = ZentGrayText
        )
    }
}

@Composable
fun ZentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ZentTextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = ZentGrayText) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = ZentGrayText) },
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar senha", tint = ZentGrayText)
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ZentInputBackground,
                unfocusedContainerColor = ZentInputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = ZentTextDark,
                unfocusedTextColor = ZentTextDark,
                cursorColor = ZentPurple
            )
        )
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ZentGrayLight),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Icon(icon, contentDescription = null, tint = ZentTextDark)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = ZentTextDark)
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}