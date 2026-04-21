package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
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
fun AiChatScreen(onBack: () -> Unit = {}) {
    val topGreenColor = Color(0xFF144729)
    val lightGreenColor = Color(0xFF37B559)

    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(topGreenColor)) {
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
            Column {
                Text("IA Médicale AfraScan", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, lightGreenColor, CircleShape).background(Color.White.copy(alpha=0.1f)), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(24.dp)) {
                    val w=size.width;val h=size.height; val p = Path().apply { moveTo(w*0.1f, h*0.2f); lineTo(w*0.9f, h*0.2f); lineTo(w*0.9f, h*0.7f); lineTo(w*0.4f, h*0.7f); lineTo(w*0.1f, h*0.9f); lineTo(w*0.1f, h*0.2f) }
                    drawPath(p, lightGreenColor, style=Stroke(2.dp.toPx(), cap=StrokeCap.Round, join=androidx.compose.ui.graphics.StrokeJoin.Round))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Dr. AfraScan IA", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(modifier=Modifier.size(6.dp).clip(CircleShape).background(lightGreenColor))
                    Spacer(modifier=Modifier.width(6.dp))
                    Text("En ligne · Répond en temps réel", color = Color.White.copy(alpha=0.7f), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Zone Blanche du Chat
        Column(
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(Color.White)
        ) {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp), contentPadding=PaddingValues(vertical=24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    ChatBubble(isUser = false, text = "Bonjour Melvin 👋 Je suis votre assistant santé IA. J'ai analysé vos mesures du jour. Votre fréquence cardiaque de 72 BPM est normale. Comment puis-je vous aider ?", lightGreenColor = lightGreenColor)
                }
                item {
                    ChatBubble(isUser = true, text = "Mon score de toux est bon mais j'ai des douleurs thoraciques légères, c'est grave ?", lightGreenColor = topGreenColor) // The image shows dark green for user
                }
                item {
                    ChatBubble(isUser = false, text = "Des douleurs thoraciques...", lightGreenColor = lightGreenColor)
                }
            }
            
            // Suggestions & Input
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=8.dp)) {
                // Suggestions
                Row(modifier = Modifier.fillMaxWidth().padding(bottom=16.dp), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    SuggestionChip("💊 Quels médicaments pour la toux ?")
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom=16.dp), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    SuggestionChip("🏥 Quand consulter en urgence ?")
                }

                // Input
                Row(modifier=Modifier.fillMaxWidth().padding(bottom=16.dp), verticalAlignment=Alignment.CenterVertically) {
                    Box(modifier=Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF9F9F9)), contentAlignment=Alignment.Center) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val w = size.width; val h = size.height
                            val capsule = Path().apply { addRoundRect(RoundRect(w*0.35f, h*0.1f, w*0.65f, h*0.6f, CornerRadius(w*0.15f, w*0.15f))) }
                            drawPath(capsule, Color(0xFFA0A0A0), style = Stroke(2.dp.toPx()))
                            val curve = Path().apply { moveTo(w*0.2f, h*0.5f); quadraticBezierTo(w*0.2f, h*0.8f, w*0.5f, h*0.8f); quadraticBezierTo(w*0.8f, h*0.8f, w*0.8f, h*0.5f) }
                            drawPath(curve, Color(0xFFA0A0A0), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                            drawLine(Color(0xFFA0A0A0), Offset(w*0.5f, h*0.8f), Offset(w*0.5f, h*0.95f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                        }
                    }
                    Spacer(modifier=Modifier.width(12.dp))
                    OutlinedTextField(
                        value = input, onValueChange = { input = it }, placeholder = { Text("Posez votre question santé...", color=Color(0xFFA0A0A0)) },
                        modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(26.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor=Color.Transparent, unfocusedBorderColor=Color.Transparent, focusedContainerColor=Color(0xFFF9F9F9), unfocusedContainerColor=Color(0xFFF9F9F9))
                    )
                    Spacer(modifier=Modifier.width(12.dp))
                    Box(modifier=Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF37B559)), contentAlignment=Alignment.Center) {
                         Icon(Icons.Default.Send, contentDescription="Envoyer", tint=Color.White, modifier=Modifier.size(20.dp).offset(x=2.dp,y=(-2).dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String) {
    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFE8F6ED)).padding(horizontal=12.dp, vertical=10.dp)) {
        Text(text, color = Color(0xFF144729), fontSize=13.sp, fontWeight=FontWeight.SemiBold)
    }
}

@Composable
fun ChatBubble(isUser: Boolean, text: String, lightGreenColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        if (!isUser) {
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).border(1.dp, lightGreenColor, CircleShape), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    val w=size.width;val h=size.height; val p = Path().apply { moveTo(w*0.1f, h*0.2f); lineTo(w*0.9f, h*0.2f); lineTo(w*0.9f, h*0.7f); lineTo(w*0.4f, h*0.7f); lineTo(w*0.1f, h*0.9f); lineTo(w*0.1f, h*0.2f) }
                    drawPath(p, lightGreenColor, style=Stroke(1.dp.toPx(), cap=StrokeCap.Round, join=androidx.compose.ui.graphics.StrokeJoin.Round))
                }
            }
            Spacer(modifier=Modifier.width(8.dp))
        }
        Column(modifier = Modifier.fillMaxWidth(if(isUser) 0.8f else 0.85f), horizontalAlignment = if(isUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                )).background(if (isUser) lightGreenColor else Color(0xFFF9F9F9)).padding(16.dp)
            ) {
                Text(text, color = if (isUser) Color.White else Color(0xFF1A1A1A), fontSize = 15.sp, lineHeight = 22.sp)
            }
            if (!isUser) {
                Row(
                    modifier = Modifier.padding(top = 6.dp, start = 8.dp).clickable { /* TODO: Lancer la synthèse vocale TTs */ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        val p = Path().apply { moveTo(size.width*0.2f, size.height*0.3f); lineTo(size.width*0.5f, 0f); lineTo(size.width*0.5f, size.height); lineTo(size.width*0.2f, size.height*0.7f); close() }
                        drawPath(p, Color(0xFFA0A0A0))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Écouter (Audio)", color = Color(0xFFA0A0A0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    MyDoctorTheme {
        AiChatScreen()
    }
}
