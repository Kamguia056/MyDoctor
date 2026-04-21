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
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydoctor.ui.theme.MyDoctorTheme

@Composable
fun AiAnalysisScreen(onBack: () -> Unit = {}, onNavigateToChat: () -> Unit = {}, onTabSelected: (Int) -> Unit = {}) {
    val topGreenColor = Color(0xFF144729)
    val lightGreenColor = Color(0xFF37B559)

    Scaffold(
        bottomBar = {
            BottomNavBar(selectedTab = 2, onTabSelected = onTabSelected, lightGreenColor = lightGreenColor)
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(topGreenColor)) {
        // App Bar Vert
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.1f)).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Analyse IA de vos mesures", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        }

        // Zone Blanche
        Column(
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(Color.White).padding(24.dp).verticalScroll(rememberScrollState())
        ) {
            // Analyse textuelle complète
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(20.dp)).padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(20.dp)) { // Clock icon
                            drawCircle(lightGreenColor, style = Stroke(2.dp.toPx()))
                            drawLine(lightGreenColor, Offset(size.width/2, size.height/2), Offset(size.width/2, size.height*0.2f), strokeWidth=2.dp.toPx(), cap=StrokeCap.Round)
                            drawLine(lightGreenColor, Offset(size.width/2, size.height/2), Offset(size.width*0.75f, size.height*0.5f), strokeWidth=2.dp.toPx(), cap=StrokeCap.Round)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Analyse complète du jour", color = Color(0xFF1A1A1A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("L'IA a analysé vos ")
                            withStyle(SpanStyle(color = topGreenColor, fontWeight = FontWeight.Bold)) { append("3 mesures") }
                            append(" du jour. Votre état de santé global est ")
                            withStyle(SpanStyle(color = topGreenColor, fontWeight = FontWeight.Bold)) { append("satisfaisant") }
                            append(". La FC de 72 BPM est dans la norme pour votre âge. L'analyse toux montre ")
                            withStyle(SpanStyle(color = topGreenColor, fontWeight = FontWeight.Bold)) { append("85% de probabilité de santé normale") }
                            append(", aucune alerte respiratoire.")
                        },
                        fontSize = 15.sp, color = Color(0xFF4A4A4A), lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("RECOMMANDATIONS IA", color = Color(0xFFA0A0A0), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            RecommendationCard(
                iconColor = lightGreenColor, bgColor = Color(0xFFE8F6ED), text = "Refaites la mesure FC dans 7 jours",
                drawIcon = { c -> Canvas(modifier=Modifier.size(16.dp)){ val p = Path().apply { moveTo(0f, size.height/2); lineTo(size.width*0.3f, size.height/2); lineTo(size.width*0.45f, 0f); lineTo(size.width*0.6f, size.height); lineTo(size.width*0.75f, size.height/2); lineTo(size.width, size.height/2) }; drawPath(p, c, style=Stroke(2.dp.toPx(), cap=StrokeCap.Round, join=androidx.compose.ui.graphics.StrokeJoin.Round)) } }
            )
            Spacer(modifier = Modifier.height(12.dp))
            RecommendationCard(
                iconColor = Color(0xFFF57F17), bgColor = Color(0xFFFFF9E6), text = "Hydratez-vous (min. 2L d'eau/jour)",
                drawIcon = { c -> Canvas(modifier=Modifier.size(16.dp)){ val p = Path().apply { moveTo(size.width/2, 0f); quadraticBezierTo(size.width, size.height*0.5f, size.width, size.height*0.75f); drawArc(c, 0f, 180f, false, topLeft = Offset(0f, size.height/2), size = androidx.compose.ui.geometry.Size(size.width, size.height/2)); quadraticBezierTo(0f, size.height*0.5f, size.width/2, 0f) }; drawPath(p, c, style=Stroke(2.dp.toPx(), cap=StrokeCap.Round)) } }
            )
            Spacer(modifier = Modifier.height(12.dp))
            RecommendationCard(
                iconColor = Color(0xFF3B82F6), bgColor = Color(0xFFF0F4FF), text = "Poser une question à l'IA", isAction = true, onClick = onNavigateToChat,
                drawIcon = { c -> Canvas(modifier=Modifier.size(16.dp)){ val w=size.width;val h=size.height; val p = Path().apply { moveTo(w*0.1f, h*0.2f); lineTo(w*0.9f, h*0.2f); lineTo(w*0.9f, h*0.7f); lineTo(w*0.4f, h*0.7f); lineTo(w*0.1f, h*0.9f); lineTo(w*0.1f, h*0.2f) }; drawPath(p, c, style=Stroke(2.dp.toPx(), cap=StrokeCap.Round, join=androidx.compose.ui.graphics.StrokeJoin.Round)) } }
            )

            Spacer(modifier = Modifier.height(24.dp))
            
             // Warning box
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFFFF9E6)).border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp)).padding(16.dp), verticalAlignment = Alignment.Top) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val path = Path().apply { moveTo(size.width/2, 0f); lineTo(size.width, size.height); lineTo(0f, size.height); close() }
                    drawPath(path, Color(0xFFF57F17), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                    drawLine(Color(0xFFF57F17), Offset(size.width/2, size.height*0.4f), Offset(size.width/2, size.height*0.7f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    drawCircle(Color(0xFFF57F17), radius = 1.dp.toPx(), center = Offset(size.width/2, size.height*0.85f))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Ces recommandations ne remplacent pas un avis médical professionnel.", color = Color(0xFF795548), fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}
}

@Composable
fun RecommendationCard(iconColor: Color, bgColor: Color, text: String, isAction: Boolean = false, onClick: () -> Unit = {}, drawIcon: @Composable (Color) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(16.dp)).clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(bgColor), contentAlignment = Alignment.Center) { drawIcon(iconColor) }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, modifier = Modifier.weight(1f), color = if (isAction) iconColor else Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription=null, tint=if (isAction) iconColor else Color(0xFFD0D0D0))
    }
}

@Preview(showBackground = true)
@Composable
fun AiAnalysisScreenPreview() {
    MyDoctorTheme {
        AiAnalysisScreen()
    }
}
