package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun SignUpScreen(onNavigateToLogin: () -> Unit = {}, onNavigateToHome: () -> Unit = {}) {
    var prenom by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sexe by remember { mutableStateOf("Homme") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var antecedents by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val topGreenColor = Color(0xFF144729)
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
                .background(topGreenColor)
                .padding(top = 70.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(32.dp)) {
                        val path = Path().apply {
                            addOval(Rect(size.width*0.25f, size.height*0.1f, size.width*0.75f, size.height*0.55f))
                            val torso = Rect(size.width*0.05f, size.height*0.7f, size.width*0.95f, size.height*1.5f)
                            addRoundRect(RoundRect(torso, CornerRadius(size.width*0.2f)))
                        }
                        drawPath(path, color = Color.White, style = Stroke(width = 2.5f.dp.toPx()))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Votre profil santé", color = Color.White, fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Ces informations améliorent la\nprécision de vos analyses",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        // ── Formulaire ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Prénom
            Text("Prénom *", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = prenom,
                onValueChange = { prenom = it; errorMsg = "" },
                placeholder = { Text("Votre prénom", color = Color(0xFFA0A0A0)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = signUpFieldColors(accentGreen)
            )

            // Âge
            Text("Âge", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                placeholder = { Text("Ex: 28", color = Color(0xFFA0A0A0)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = signUpFieldColors(accentGreen)
            )

            // Sexe
            Text("Sexe", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SelectableButton(text = "Homme", isSelected = sexe == "Homme", onClick = { sexe = "Homme" }, modifier = Modifier.weight(1f))
                SelectableButton(text = "Femme", isSelected = sexe == "Femme", onClick = { sexe = "Femme" }, modifier = Modifier.weight(1f))
            }

            // Email
            Text("Adresse Email *", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                placeholder = { Text("votre@email.com", color = Color(0xFFA0A0A0)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = signUpFieldColors(accentGreen)
            )

            // Mot de passe
            Text("Mot de passe *", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                placeholder = { Text("Min. 6 caractères", color = Color(0xFFA0A0A0)) },
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
                colors = signUpFieldColors(accentGreen)
            )

            // Antécédents
            Text("Antécédents médicaux", color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = antecedents,
                onValueChange = { antecedents = it },
                placeholder = { Text("Diabète, hypertension... (optionnel)", color = Color(0xFFA0A0A0)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = signUpFieldColors(accentGreen)
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

            // Bouton inscription
            Button(
                onClick = {
                    when {
                        prenom.isBlank() -> errorMsg = "Veuillez entrer votre prénom."
                        email.isBlank() -> errorMsg = "Veuillez entrer votre adresse email."
                        !email.contains("@") -> errorMsg = "L'adresse email n'est pas valide."
                        password.isBlank() -> errorMsg = "Veuillez choisir un mot de passe."
                        password.length < 6 -> errorMsg = "Le mot de passe doit contenir au moins 6 caractères."
                        else -> {
                            isLoading = true
                            errorMsg = ""
                            scope.launch {
                                val error = FirebaseManager.signUp(
                                    email.trim(), password,
                                    prenom.trim(), age, sexe, antecedents
                                )
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
                    containerColor = accentGreen,
                    disabledContainerColor = accentGreen.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Créer mon compte", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Lien connexion
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Vous avez déjà un compte ? ", color = Color(0xFF808080), fontSize = 14.sp)
                Text(
                    "Se connecter",
                    color = accentGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
private fun signUpFieldColors(accentGreen: Color) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFF9F9F9),
    focusedBorderColor = accentGreen,
    unfocusedBorderColor = Color(0xFFE0E0E0),
    cursorColor = accentGreen,
    focusedTextColor = Color(0xFF1A1A1A),
    unfocusedTextColor = Color(0xFF1A1A1A)
)

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isFocused: Boolean = false,
    minLines: Int = 1,
    isPassword: Boolean = false
) {
    val accentGreen = Color(0xFF37B559)
    Column {
        Text(text = label, color = Color(0xFF1A1A1A), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color(0xFFA0A0A0)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isFocused) Color.White else Color(0xFFF9F9F9),
                unfocusedContainerColor = if (isFocused) Color.White else Color(0xFFF9F9F9),
                focusedBorderColor = accentGreen,
                unfocusedBorderColor = if (isFocused) accentGreen else Color(0xFFE0E0E0),
                cursorColor = accentGreen,
                focusedTextColor = Color(0xFF1A1A1A),
                unfocusedTextColor = Color(0xFF1A1A1A)
            )
        )
    }
}

@Composable
fun SelectableButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFE8F6ED) else Color.White)
            .border(1.dp, if (isSelected) Color(0xFF37B559) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF144729) else Color(0xFF808080),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MyDoctorTheme { SignUpScreen() }
}
