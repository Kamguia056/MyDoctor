package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydoctor.ui.theme.MyDoctorTheme

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // En-tête vert avec l'icône de bouclier
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF22A34A))
                .padding(top = 80.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icône Bouclier
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(32.dp)) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width / 2f, 0f)
                            lineTo(size.width, size.height * 0.2f)
                            lineTo(size.width, size.height * 0.65f)
                            quadraticBezierTo(size.width * 0.95f, size.height, size.width / 2f, size.height)
                            quadraticBezierTo(size.width * 0.05f, size.height, 0f, size.height * 0.65f)
                            lineTo(0f, size.height * 0.2f)
                            close()
                        }
                        drawPath(
                            path = path, 
                            color = Color.White, 
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 2.5.dp.toPx(), 
                                cap = androidx.compose.ui.graphics.StrokeCap.Round, 
                                join = androidx.compose.ui.graphics.StrokeJoin.Round
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Titre
                Text(
                    text = "Autorisations",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sous-titre
                Text(
                    text = "AfraScan utilise vos capteurs pour des\nmesures médicales",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        // Zone Blanche avec la liste des permissions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Carte Caméra
            PermissionCard(
                iconBackgroundColor = Color(0xFFFFF0F0),
                iconColor = Color(0xFFF03E3E),
                title = "Caméra + Flash",
                description = "Fréquence cardiaque PPG",
                iconType = "camera"
            )

            // Carte Microphone
            PermissionCard(
                iconBackgroundColor = Color(0xFFF0F4FF),
                iconColor = Color(0xFF3B82F6),
                title = "Microphone",
                description = "Analyse acoustique de la toux",
                iconType = "mic"
            )

            // Carte Localisation
            PermissionCard(
                iconBackgroundColor = Color(0xFFF0FFF4),
                iconColor = Color(0xFF10B981),
                title = "Localisation GPS",
                description = "Centres de santé proches",
                iconType = "location"
            )

            // Carte IA
            PermissionCard(
                iconBackgroundColor = Color(0xFFF5F0FF),
                iconColor = Color(0xFF8B5CF6),
                title = "IA Médicale",
                description = "Analyse intelligente des mesures",
                iconType = "ai"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton Suivant pour aller vers le login
            androidx.compose.material3.Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF37B559))
            ) {
                Text(
                    text = "Suivant",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    iconBackgroundColor: Color,
    iconColor: Color,
    title: String,
    description: String,
    iconType: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFF0F0F0), // Bordure gris très clair
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Zone de l'icône colorée
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            when (iconType) {
                "camera" -> {
                    // Icône personnalisée de flash/caméra dessinée
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val center = Offset(size.width / 2, size.height / 2)
                        drawCircle(color = iconColor, radius = 3.dp.toPx(), center = center)
                        
                        // Petits points autour (Flash de la caméra)
                        val offsetDistance = 8.dp.toPx()
                        drawCircle(color = iconColor.copy(alpha = 0.5f), radius = 1.5f.dp.toPx(), center = center.copy(y = center.y - offsetDistance))
                        drawCircle(color = iconColor.copy(alpha = 0.5f), radius = 1.5f.dp.toPx(), center = center.copy(y = center.y + offsetDistance))
                        drawCircle(color = iconColor.copy(alpha = 0.5f), radius = 1.5f.dp.toPx(), center = center.copy(x = center.x - offsetDistance))
                        drawCircle(color = iconColor.copy(alpha = 0.5f), radius = 1.5f.dp.toPx(), center = center.copy(x = center.x + offsetDistance))
                    }
                }
                "mic" -> {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val w = size.width
                        val h = size.height
                        val capsule = androidx.compose.ui.graphics.Path().apply {
                            addRoundRect(androidx.compose.ui.geometry.RoundRect(w*0.35f, h*0.1f, w*0.65f, h*0.6f, androidx.compose.ui.geometry.CornerRadius(w*0.15f, w*0.15f)))
                        }
                        drawPath(capsule, color = iconColor, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))
                        val curve = androidx.compose.ui.graphics.Path().apply {
                            moveTo(w*0.2f, h*0.5f)
                            quadraticBezierTo(w*0.2f, h*0.8f, w*0.5f, h*0.8f)
                            quadraticBezierTo(w*0.8f, h*0.8f, w*0.8f, h*0.5f)
                        }
                        drawPath(curve, color = iconColor, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                        drawLine(iconColor, Offset(w*0.5f, h*0.8f), Offset(w*0.5f, h*0.95f), strokeWidth = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    }
                }
                "location" -> Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                "ai" -> {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val w = size.width
                        val h = size.height
                        val bubble = androidx.compose.ui.graphics.Path().apply {
                            moveTo(w * 0.15f, h * 0.2f)
                            lineTo(w * 0.85f, h * 0.2f)
                            quadraticBezierTo(w * 0.95f, h * 0.2f, w * 0.95f, h * 0.3f)
                            lineTo(w * 0.95f, h * 0.7f)
                            quadraticBezierTo(w * 0.95f, h * 0.8f, w * 0.85f, h * 0.8f)
                            lineTo(w * 0.4f, h * 0.8f)
                            lineTo(w * 0.2f, h * 0.95f)
                            lineTo(w * 0.2f, h * 0.8f)
                            lineTo(w * 0.15f, h * 0.8f)
                            quadraticBezierTo(w * 0.05f, h * 0.8f, w * 0.05f, h * 0.7f)
                            lineTo(w * 0.05f, h * 0.3f)
                            quadraticBezierTo(w * 0.05f, h * 0.2f, w * 0.15f, h * 0.2f)
                            close()
                        }
                        drawPath(bubble, color = iconColor, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Textes
        Column {
            Text(
                text = title,
                color = Color(0xFF1A1A1A),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = Color(0xFFA0A0A0),
                fontSize = 13.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MyDoctorTheme {
        OnboardingScreen()
    }
}
