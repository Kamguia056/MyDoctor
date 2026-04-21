package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
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

@Composable
fun RiskScoreScreen(onBack: () -> Unit = {}, onTabSelected: (Int) -> Unit = {}) {
    val topGreenColor = Color(0xFF144729)
    val lightGreenColor = Color(0xFF37B559)
    val bgGrayColor = Color(0xFF1A1A1A)

    Scaffold(
        bottomBar = {
            BottomNavBar(selectedTab = 1, onTabSelected = onTabSelected, lightGreenColor = lightGreenColor)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(topGreenColor)
        ) {
        // App Bar Vert
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Text(
                "Score de Risque Global",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        }

        // Zone Blanche
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFF1A1A1A)) // Dark grey app background for this part
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Carte Score Principal (Blanche)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Cercle brisé pour la jauge
                    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = Color(0xFFF0F0F0),
                                startAngle = 140f, sweepAngle = 260f, useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = lightGreenColor,
                                startAngle = 140f, sweepAngle = 260f * 0.28f, useCenter = false, // 28%
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text("28", color = topGreenColor, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Serif)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = lightGreenColor, modifier = Modifier.size(16.dp)) // simple check stand-in
                        Text(" Faible Risque", color = lightGreenColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Aucune alerte critique", color = Color(0xFFA0A0A0), fontSize = 13.sp)
                }
            }

            // Fréquence Cardiaque Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFF0F0)), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val path = Path().apply {
                            moveTo(0f, size.height/2); lineTo(size.width*0.3f, size.height/2); lineTo(size.width*0.45f, size.height*0.1f)
                            lineTo(size.width*0.6f, size.height*0.9f); lineTo(size.width*0.75f, size.height/2); lineTo(size.width, size.height/2)
                        }
                        drawPath(path, Color(0xFFF03E3E), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fréquence Cardiaque", color = Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("72 BPM · Normal", color = Color(0xFFA0A0A0), fontSize = 13.sp)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(lightGreenColor.copy(alpha=0.15f)).padding(horizontal=12.dp, vertical=6.dp)) {
                    Text("Normal", color = lightGreenColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Analyse Toux Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F4FF)), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val w = size.width; val h = size.height
                        val capsule = Path().apply { addRoundRect(RoundRect(w*0.35f, h*0.1f, w*0.65f, h*0.6f, CornerRadius(w*0.15f, w*0.15f))) }
                        drawPath(capsule, Color(0xFF3B82F6), style = Stroke(2.dp.toPx()))
                        val curve = Path().apply { moveTo(w*0.2f, h*0.5f); quadraticBezierTo(w*0.2f, h*0.8f, w*0.5f, h*0.8f); quadraticBezierTo(w*0.8f, h*0.8f, w*0.8f, h*0.5f) }
                        drawPath(curve, Color(0xFF3B82F6), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                        drawLine(Color(0xFF3B82F6), Offset(w*0.5f, h*0.8f), Offset(w*0.5f, h*0.95f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Analyse Toux", color = Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("85% sain", color = Color(0xFFA0A0A0), fontSize = 13.sp)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF3B82F6).copy(alpha=0.15f)).padding(horizontal=12.dp, vertical=6.dp)) {
                    Text("Sain", color = Color(0xFF3B82F6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Warning box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF9E6))
                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Warning icon drawing
                Canvas(modifier = Modifier.size(20.dp)) {
                    val path = Path().apply {
                        moveTo(size.width/2, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, Color(0xFFF57F17), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                    drawLine(Color(0xFFF57F17), Offset(size.width/2, size.height*0.4f), Offset(size.width/2, size.height*0.7f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    drawCircle(Color(0xFFF57F17), radius = 1.dp.toPx(), center = Offset(size.width/2, size.height*0.85f))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Score indicatif uniquement. Ne remplace pas un examen médical.", color = Color(0xFF795548), fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun RiskScoreScreenPreview() {
    MyDoctorTheme {
        RiskScoreScreen()
    }
}
