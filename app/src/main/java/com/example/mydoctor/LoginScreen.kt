package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydoctor.ui.theme.MyDoctorTheme
import kotlinx.coroutines.launch
import com.example.mydoctor.backend.FirebaseManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onNavigateToSignUp: () -> Unit = {}, onNavigateToHome: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val greenColor = Color(0xFF144729)
    val accentGreen = Color(0xFF37B559)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── En-tête vert ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(greenColor)
                .padding(top = 80.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Icône cadenas Canvas
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(32.dp)) {
                        val body = Path().apply {
                            addRoundRect(RoundRect(size.width*0.2f, size.height*0.45f, size.width*0.8f, size.height*0.9f, CornerRadius(size.width*0.15f)))
                        }
                        drawPath(body, color = Color.White, style = Stroke(width = 2.5f.dp.toPx()))
                        val shackle = Path().apply {
                            addArc(Rect(size.width*0.3f, size.height*0.15f, size.width*0.7f, size.height*0.6f), 180f, 180f)
                        }
                        drawPath(shackle, color = Color.White, style = Stroke(width = 2.5f.dp.toPx()))
                        drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(size.width/2f, size.height*0.68f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Bon retour !", color = Color.White, fontSize = 32.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Connectez-vous pour accéder à\nvos données médicales",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        // ── Formulaire ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Email
            Text("Adresse Email", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                placeholder = { Text("exemple@mail.com", color = Color(0xFFA0A0A0)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF9F9F9),
                    focusedBorderColor = accentGreen,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = accentGreen,
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A)
                )
            )

            // Mot de passe
            Text("Mot de passe", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                placeholder = { Text("••••••••", color = Color(0xFFA0A0A0)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (isPasswordVisible) "Cacher" else "Voir",
                        color = accentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { isPasswordVisible = !isPasswordVisible }
                            .padding(end = 12.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF9F9F9),
                    focusedBorderColor = accentGreen,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = accentGreen,
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A)
                )
            )

            // Mot de passe oublié
            Text(
                "Mot de passe oublié ?",
                color = accentGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* TODO: Reset password */ }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Message d'erreur
            if (errorMsg.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFEBEB))
                        .padding(12.dp)
                ) {
                    Text(errorMsg, color = Color(0xFFD32F2F), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }

            // Bouton connexion
            Button(
                onClick = {
                    when {
                        email.isBlank() -> errorMsg = "Veuillez entrer votre adresse email."
                        !email.contains("@") -> errorMsg = "L'adresse email n'est pas valide."
                        password.isBlank() -> errorMsg = "Veuillez entrer votre mot de passe."
                        password.length < 6 -> errorMsg = "Le mot de passe doit contenir au moins 6 caractères."
                        else -> {
                            isLoading = true
                            errorMsg = ""
                            scope.launch {
                                val error = FirebaseManager.login(email.trim(), password)
                                isLoading = false
                                if (error == null) {
                                    onNavigateToHome()
                                } else {
                                    errorMsg = error
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor,
                    disabledContainerColor = greenColor.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Se connecter", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Lien inscription
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Nouveau sur AfraScan ? ", color = Color(0xFF808080), fontSize = 14.sp)
                Text(
                    "Créer un compte",
                    color = accentGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyDoctorTheme { LoginScreen() }
}
