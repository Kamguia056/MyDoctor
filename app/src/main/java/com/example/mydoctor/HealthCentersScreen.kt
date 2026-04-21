package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydoctor.ui.theme.MyDoctorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCentersScreen(onBack: () -> Unit = {}, onTabSelected: (Int) -> Unit = {}) {
    val topGreenColor = Color(0xFF144729)
    val lightGreenColor = Color(0xFF37B559)

    Scaffold(
        bottomBar = {
            BottomNavBar(selectedTab = 3, onTabSelected = onTabSelected, lightGreenColor = lightGreenColor)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFE8F6ED)) // Light green/map ground
        ) {
        // App Bar Vert
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(topGreenColor)
                .padding(top = 40.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Centres de Santé", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Barre de recherche
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Rechercher...", color = Color.White.copy(alpha=0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription=null, tint=Color.White.copy(alpha=0.6f)) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color.White.copy(alpha=0.15f),
                    focusedContainerColor = Color.White.copy(alpha=0.15f),
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Bouton SOS Urgence
            Button(
                onClick = { /* TODO: Appeler les urgences */ },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                      Canvas(modifier = Modifier.size(16.dp)) {
                          drawCircle(Color.White, style = Stroke(2.dp.toPx()))
                          drawLine(Color.White, Offset(size.width/2, size.height*0.2f), Offset(size.width/2, size.height*0.8f), strokeWidth=2.dp.toPx(), cap=StrokeCap.Round)
                          drawLine(Color.White, Offset(size.width*0.2f, size.height/2), Offset(size.width*0.8f, size.height/2), strokeWidth=2.dp.toPx(), cap=StrokeCap.Round)
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("APPELER LES URGENCES (SOS)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                 }
            }
        }

        // Zone Carte (Simulée)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Grille pour simuler une carte
                val step = 40.dp.toPx()
                for (x in 0..size.width.toInt() step step.toInt()) {
                    drawLine(Color.White.copy(alpha=0.5f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1.dp.toPx())
                }
                for (y in 0..size.height.toInt() step step.toInt()) {
                    drawLine(Color.White.copy(alpha=0.5f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1.dp.toPx())
                }
                
                // Point de position actuelle (bleu)
                drawCircle(Color(0xFF3B82F6).copy(alpha=0.2f), radius = 24.dp.toPx(), center = Offset(size.width/2, size.height/2))
                drawCircle(Color.White, radius = 8.dp.toPx(), center = Offset(size.width/2, size.height/2))
                drawCircle(Color(0xFF3B82F6), radius = 6.dp.toPx(), center = Offset(size.width/2, size.height/2))
                
                // Fonction pour dessiner les repères
                fun drawPin(color: Color, cx: Float, cy: Float) {
                    val w = 24.dp.toPx(); val h = 32.dp.toPx()
                    val pin = Path().apply {
                        addOval(androidx.compose.ui.geometry.Rect(cx-w/2, cy-h, cx+w/2, cy-h+w))
                        moveTo(cx-w*0.4f, cy-h+w*0.8f)
                        lineTo(cx, cy)
                        lineTo(cx+w*0.4f, cy-h+w*0.8f)
                    }
                    drawPath(pin, color)
                    drawCircle(Color.White, radius=4.dp.toPx(), center=Offset(cx, cy-h+w/2))
                }
                
                drawPin(Color(0xFFF03E3E), size.width*0.35f, size.height*0.3f) // CHU
                drawPin(Color(0xFF37B559), size.width*0.75f, size.height*0.55f) // Clinique
                drawPin(Color(0xFFF57F17), size.width*0.25f, size.height*0.7f) // Pharmacie
            }
            
            // Labels simulés
            Box(Modifier.offset(x = 100.dp, y = 140.dp).background(Color.White, RoundedCornerShape(8.dp)).padding(4.dp)) {
                Text("CHU Central", fontSize=12.sp, fontWeight=FontWeight.Bold)
            }
            Box(Modifier.offset(x = 230.dp, y = 260.dp).background(Color.White, RoundedCornerShape(8.dp)).padding(4.dp)) {
                Text("Clinique Santé+", fontSize=12.sp, fontWeight=FontWeight.Bold)
            }
            Box(Modifier.offset(x = 60.dp, y = 330.dp).background(Color.White, RoundedCornerShape(8.dp)).padding(4.dp)) {
                Text("Pharmacie", fontSize=12.sp, fontWeight=FontWeight.Bold)
            }

            // Bottom Card
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column {
                    Text("Centre le plus proche", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8F6ED)), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val path = Path().apply { moveTo(0f, size.height/2); lineTo(size.width*0.3f, size.height/2); lineTo(size.width*0.45f, size.height*0.1f); lineTo(size.width*0.6f, size.height*0.9f); lineTo(size.width*0.75f, size.height/2); lineTo(size.width, size.height/2) }
                                drawPath(path, lightGreenColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clinique Santé+", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Ouverte · 7h-20h · Cardio", fontSize = 12.sp, color = Color(0xFFA0A0A0))
                        }
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(lightGreenColor.copy(alpha=0.15f)).padding(horizontal=10.dp, vertical=4.dp)) {
                            Text("0.8 km", color = lightGreenColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightGreenColor)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Obtenir l'itinéraire", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun HealthCentersScreenPreview() {
    MyDoctorTheme {
        HealthCentersScreen()
    }
}
