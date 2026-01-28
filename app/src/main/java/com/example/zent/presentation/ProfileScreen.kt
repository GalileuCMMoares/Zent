package com.zent.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- CORES ---
val ZentBackground = Color(0xFFF9FAFB)
val ZentGreenPrimary = Color(0xFF7E9F8F)
val ZentTextDark = Color(0xFF1F2937)
val ZentPurple = Color(0xFFA89BC6)
val ZentPurpleLight = Color(0xFFF3E8FF)
val ZentPurpleButton = Color(0xFFB0A2CE) // Cor do botão Upgrade
val ZentGreenLight = Color(0xFFE8F5E9)
val ZentGrayText = Color(0xFF6B7280)
val ZentGrayLight = Color(0xFFE5E7EB)
val ZentRedError = Color(0xFFDC2626)

@Composable
fun ProfileScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = contentPadding, // Aplica padding global
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Removido: item { ProfileTopBar() }

        item {
            UserProfileCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            PremiumCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            SectionTitle("Preferências")
            SettingsGroupCard {
                SettingsRow(Icons.Outlined.Notifications, "Notificações", "Lembretes de estudo", { SimpleSwitch() })
                Divider(color = ZentBackground, thickness = 1.dp)
                SettingsRow(Icons.Outlined.Nightlight, "Modo Escuro", "Tema visual", { SimpleSwitch() })
                Divider(color = ZentBackground, thickness = 1.dp)
                SettingsRow(Icons.Outlined.Language, "Idioma", "Português (BR)", { })
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

            // 5. Configurações de Estudo
            item {
                SectionTitle("Configurações de Estudo")
                SettingsGroupCard {
                    SettingsValueRow(title = "Cards por sessão", value = "20 cartas")
                    Divider(color = ZentBackground, thickness = 1.dp)
                    SettingsValueRow(title = "Meta diária", value = "50 cartas")
                    Divider(color = ZentBackground, thickness = 1.dp)
                    SettingsValueRow(title = "Lembrete diário", value = "19:00")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 6. Suporte
            item {
                SectionTitle("Suporte")
                SettingsGroupCard {
                    SettingsRow(
                        icon = Icons.Outlined.HelpOutline,
                        title = "Central de Ajuda",
                        subtitle = null,
                        trailing = {}
                    )
                    Divider(color = ZentBackground, thickness = 1.dp)
                    // Item Sair Customizado
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Logout, null, tint = ZentRedError, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Sair",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZentRedError
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 7. Rodapé
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Zent v1.0.0", fontSize = 12.sp, color = ZentGrayText)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Feito com ", fontSize = 12.sp, color = ZentGrayText)
                    Text("💚", fontSize = 10.sp)
                    Text(" para estudantes", fontSize = 12.sp, color = ZentGrayText)
                }
            }
        }
    }
}

// --- COMPONENTES DA TELA ---

@Composable
fun ProfileTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Zent", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ZentPurple)
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, ZentGrayLight),
            onClick = { /* Ação */ }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp), tint = ZentTextDark)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Criar", fontWeight = FontWeight.SemiBold, color = ZentTextDark)
            }
        }
    }
}

@Composable
fun UserProfileCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ZentGreenPrimary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "João Silva",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        "joao.silva@email.com",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Botão Transparente
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PremiumCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)), // Cinza bem claro
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE9E5F0)), // Lilás bem claro
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.WorkspacePremium, // Coroa ou similar
                        null,
                        tint = ZentPurpleButton,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Zent Premium", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZentTextDark)
                    Text("Acesso ilimitado", fontSize = 12.sp, color = ZentGrayText)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = ZentPurpleButton),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(45.dp)
            ) {
                Text("Upgrade agora", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = ZentGrayText,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsGroupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = ZentTextDark, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = ZentTextDark)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = ZentGrayText)
            }
        }
        trailing()
    }
}

@Composable
fun SettingsValueRow(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = ZentTextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 12.sp, color = ZentGrayText)
    }
}

@Composable
fun SimpleSwitch() {
    val checked = remember { mutableStateOf(false) }
    Switch(
        checked = checked.value,
        onCheckedChange = { checked.value = it },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = ZentGreenPrimary,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = ZentGrayLight,
            uncheckedBorderColor = Color.Transparent
        ),
        modifier = Modifier.scale(0.8f) // Deixar um pouco menor
    )
}

// Pequeno utilitário para escalar o switch
fun Modifier.scale(scale: Float) = this.then(
    androidx.compose.ui.Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)



@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}