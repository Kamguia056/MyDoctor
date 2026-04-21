package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydoctor.ui.theme.MyDoctorTheme

@Composable
fun HomeScreen(
    onNavigateToRiskScore: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToAiAnalysis: () -> Unit = {},
    onNavigateToVoiceAi: () -> Unit = {},
    onNavigateToAudioMeasure: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val topGreenColor = Color(0xFF144729)
    val lightGreenColor = Color(0xFF37B559)

    Scaffold(
        bottomBar = {
            BottomNavBar(selectedTab = 0, onTabSelected = onTabSelected, lightGreenColor = lightGreenColor)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // HAUT : Profil et Score
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topGreenColor)
                    .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                Column {
                    // Header: Bonjour Melvin + Avatar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Bonjour \uD83D\uDC4B", color = Color.White.copy(alpha=0.9f), fontSize = 16.sp)
                            Text(
                                text = "Melvin",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Avatar M
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(lightGreenColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "M", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Score Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Progress Indicator
                            Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawArc(
                                        color = Color.White.copy(alpha=0.15f),
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    // 72% de 360 = 259.2
                                    drawArc(
                                        color = lightGreenColor,
                                        startAngle = -90f,
                                        sweepAngle = 259.2f,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Text("72", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Score santé\nglobal", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Aujourd'hui · Bon état", color = Color.White.copy(alpha=0.7f), fontSize = 13.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(lightGreenColor.copy(alpha=0.2f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Bon", color = lightGreenColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // BAS : Modules
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text("MODULES", color = Color(0xFFA0A0A0), fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // Grille de modules
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ModuleCard(
                        modifier = Modifier.weight(1f),
                        iconColor = Color(0xFF37B559), iconBgColor = Color(0xFFE8F6ED),
                        title = "Assistante\nIA Vocale", value = "Parler", valueColor = Color(0xFF144729), subtext = "Posez vos questions",
                        isActive = true,
                        onClick = onNavigateToVoiceAi,
                        drawIcon = { color ->
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val w = size.width; val h = size.height
                                // Microphone icon
                                val capsule = androidx.compose.ui.graphics.Path().apply {
                                    addRoundRect(androidx.compose.ui.geometry.RoundRect(w*0.38f, h*0.05f, w*0.62f, h*0.58f, androidx.compose.ui.geometry.CornerRadius(w*0.12f)))
                                }
                                drawPath(capsule, color, style = Stroke(2.dp.toPx()))
                                val arc = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(w*0.2f, h*0.5f)
                                    quadraticBezierTo(w*0.2f, h*0.82f, w*0.5f, h*0.82f)
                                    quadraticBezierTo(w*0.8f, h*0.82f, w*0.8f, h*0.5f)
                                }
                                drawPath(arc, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                                drawLine(color, androidx.compose.ui.geometry.Offset(w*0.5f, h*0.82f), androidx.compose.ui.geometry.Offset(w*0.5f, h*0.97f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                            }
                        }
                    )
                    ModuleCard(
                        modifier = Modifier.weight(1f),
                        iconColor = Color(0xFF3B82F6), iconBgColor = Color(0xFFF0F4FF),
                        title = "Analyse\nToux", value = "Sain", valueColor = Color(0xFF3B82F6), subtext = "92% conf.",
                        isActive = false,
                        onClick = onNavigateToAudioMeasure,
                        drawIcon = { color -> 
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val w = size.width; val h = size.height
                                val capsule = Path().apply {
                                    addRoundRect(RoundRect(w*0.35f, h*0.1f, w*0.65f, h*0.6f, CornerRadius(w*0.15f, w*0.15f)))
                                }
                                drawPath(capsule, color, style = Stroke(2.dp.toPx()))
                                val curve = Path().apply {
                                    moveTo(w*0.2f, h*0.5f)
                                    quadraticBezierTo(w*0.2f, h*0.8f, w*0.5f, h*0.8f)
                                    quadraticBezierTo(w*0.8f, h*0.8f, w*0.8f, h*0.5f)
                                }
                                drawPath(curve, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                                drawLine(color, Offset(w*0.5f, h*0.8f), Offset(w*0.5f, h*0.95f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ModuleCard(
                        modifier = Modifier.weight(1f),
                        iconColor = Color(0xFF10B981), iconBgColor = Color(0xFFF0FFF4),
                        title = "Localisation\nGPS", value = "2 à Prox.", valueColor = Color(0xFF10B981), subtext = "Centres de santé",
                        isActive = false,
                        onClick = onNavigateToMap,
                        drawIcon = { color -> 
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val w = size.width; val h = size.height
                                val pin = Path().apply {
                                    // simple cercle au dessus et pointe en bas
                                    addOval(androidx.compose.ui.geometry.Rect(w*0.25f, h*0.1f, w*0.75f, h*0.6f))
                                    moveTo(w*0.28f, h*0.48f)
                                    lineTo(w*0.5f, h*0.9f)
                                    lineTo(w*0.72f, h*0.48f)
                                }
                                drawPath(pin, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                                drawCircle(color, radius = 2.dp.toPx(), center = Offset(w/2, h*0.35f))
                            }
                        }
                    )
                    ModuleCard(
                        modifier = Modifier.weight(1f),
                        iconColor = Color(0xFF8B5CF6), iconBgColor = Color(0xFFF5F0FF),
                        title = "IA\nMédicale", value = "Prêt", valueColor = Color(0xFF8B5CF6), subtext = "Poser une question",
                        isActive = false,
                        onClick = onNavigateToAiAnalysis,
                        drawIcon = { color -> 
                             Canvas(modifier = Modifier.size(20.dp)) {
                                val w = size.width; val h = size.height
                                val bubble = Path().apply {
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
                                drawPath(bubble, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleCard(
    modifier: Modifier,
    iconColor: Color, iconBgColor: Color,
    title: String, value: String, valueColor: Color, subtext: String,
    isActive: Boolean,
    onClick: () -> Unit = {},
    drawIcon: @Composable (Color) -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) Color(0xFF37B559) else Color(0xFFF0F0F0),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                drawIcon(iconColor)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(title, color = Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text(value, color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, color = Color(0xFFA0A0A0), fontSize = 13.sp)
        }
    }
}

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit, lightGreenColor: Color) {
    val items = listOf("Accueil", "Examen", "IA", "Carte", "Profil")
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEachIndexed { index, name ->
                val isSelected = index == selectedTab
                val contentColor = if (isSelected) lightGreenColor else Color(0xFFA0A0A0)
                
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val w = size.width; val h = size.height
                        when (index) {
                            0 -> { // Accueil (Home)
                                val path = Path().apply {
                                    moveTo(w*0.5f, h*0.1f)
                                    lineTo(w*0.9f, h*0.45f)
                                    lineTo(w*0.8f, h*0.45f)
                                    lineTo(w*0.8f, h*0.9f)
                                    lineTo(w*0.2f, h*0.9f)
                                    lineTo(w*0.2f, h*0.45f)
                                    lineTo(w*0.1f, h*0.45f)
                                    close()
                                }
                                drawPath(path, contentColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                            1 -> { // Examen (Heartbeat)
                                val path = Path().apply {
                                    moveTo(0f, h/2)
                                    lineTo(w*0.3f, h/2)
                                    lineTo(w*0.45f, h*0.1f)
                                    lineTo(w*0.6f, h*0.9f)
                                    lineTo(w*0.75f, h/2)
                                    lineTo(w, h/2)
                                }
                                drawPath(path, contentColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                            2 -> { // IA (Bubble)
                                val bubble = Path().apply {
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
                                drawPath(bubble, contentColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                            3 -> { // Carte (Pin)
                                val pin = Path().apply {
                                    addOval(androidx.compose.ui.geometry.Rect(w*0.25f, h*0.1f, w*0.75f, h*0.6f))
                                    moveTo(w*0.28f, h*0.48f)
                                    lineTo(w*0.5f, h*0.9f)
                                    lineTo(w*0.72f, h*0.48f)
                                }
                                drawPath(pin, contentColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                            4 -> { // Profil (Person)
                                val path = Path().apply {
                                    addOval(androidx.compose.ui.geometry.Rect(w * 0.25f, h * 0.1f, w * 0.75f, h * 0.5f))
                                    val torso = androidx.compose.ui.geometry.Rect(w * 0.05f, h * 0.65f, w * 0.95f, h * 1.2f)
                                    addRoundRect(androidx.compose.ui.geometry.RoundRect(torso, androidx.compose.ui.geometry.CornerRadius(w*0.2f)))
                                }
                                drawPath(path, contentColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = name, color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyDoctorTheme {
        HomeScreen()
    }
}
