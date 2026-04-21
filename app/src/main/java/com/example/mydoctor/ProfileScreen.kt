package com.example.mydoctor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

import kotlinx.coroutines.launch
import com.example.mydoctor.backend.FirebaseManager
import com.example.mydoctor.models.User
import com.example.mydoctor.models.Measurement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(onTabSelected: (Int) -> Unit = {}, onLogOut: () -> Unit = {}) {
    val topGreenColor = Color(0xFF144729)
    val lightGreenColor = Color(0xFF37B559)
    
    var user by remember { mutableStateOf<User?>(null) }
    var history by remember { mutableStateOf<List<Measurement>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        user = FirebaseManager.getUserProfile()
        history = FirebaseManager.getUserMeasurements()
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(selectedTab = 4, onTabSelected = onTabSelected, lightGreenColor = lightGreenColor)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // HAUT : Avatar et infos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topGreenColor)
                    .padding(top = 40.dp, bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar M
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(width = 2.dp, color = lightGreenColor, shape = CircleShape)
                            .background(lightGreenColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user?.name?.firstOrNull()?.uppercase() ?: "M", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(user?.name ?: "Chargement...", color = Color.White, fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    val dateStr = user?.createdAt?.let { SimpleDateFormat("MMMM yyyy", Locale.FRANCE).format(Date(it)) } ?: "..."
                    Text("Membre depuis $dateStr", color = lightGreenColor, fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onLogOut,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightGreenColor.copy(alpha=0.2f))
                    ) {
                        Text("Déconnexion", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Cartes de statistiques chevauchant le haut (avec offset)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), value = "${history.size}", label = "Examens", valueColor = topGreenColor)
                StatCard(modifier = Modifier.weight(1f), value = "${user?.globalRiskScore ?: 0}", label = "Score moy.", valueColor = topGreenColor)
                StatCard(modifier = Modifier.weight(1f), value = "0", label = "Alertes", valueColor = topGreenColor)
            }

            // Historique
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text("HISTORIQUE", color = Color(0xFFA0A0A0), fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // Dynamique depuis Firestore
                if (history.isEmpty()) {
                    Text("Aucun examen récent.", color = Color(0xFFA0A0A0), fontSize = 14.sp, modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    history.forEach { measure ->
                        val isHeartRate = measure.type == "HEART_RATE"
                        val dateLabel = SimpleDateFormat("dd/MM HH:mm", Locale.FRANCE).format(Date(measure.timestamp))
                        
                        HistoryCard(
                            title = if (isHeartRate) "Fréquence Cardiaque" else "Analyse Toux",
                            subtitle = if (isHeartRate) "Caméra - 30 secondes" else "Micro - 5 toux",
                            value = measure.resultValue,
                            date = dateLabel,
                            valueColor = if (isHeartRate) Color.Black else topGreenColor,
                            iconBgAction = { modifier ->
                                if (isHeartRate) {
                                    Box(modifier = modifier.background(Color(0xFFFFF0F0)), contentAlignment = Alignment.Center) {
                                        Canvas(modifier = Modifier.size(20.dp)) {
                                            val path = Path().apply { moveTo(0f, size.height/2); lineTo(size.width*0.3f, size.height/2); lineTo(size.width*0.45f, size.height*0.1f); lineTo(size.width*0.6f, size.height*0.9f); lineTo(size.width*0.75f, size.height/2); lineTo(size.width, size.height/2) }
                                            drawPath(path, Color(0xFFF03E3E), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                                        }
                                    }
                                } else {
                                    Box(modifier = modifier.background(Color(0xFFF0F4FF)), contentAlignment = Alignment.Center) {
                                        Canvas(modifier = Modifier.size(20.dp)) {
                                            val w = size.width; val h = size.height
                                            val capsule = Path().apply { addRoundRect(RoundRect(w*0.35f, h*0.1f, w*0.65f, h*0.6f, CornerRadius(w*0.15f, w*0.15f))) }
                                            drawPath(capsule, Color(0xFF3B82F6), style = Stroke(2.dp.toPx()))
                                            val curve = Path().apply { moveTo(w*0.2f, h*0.5f); quadraticBezierTo(w*0.2f, h*0.8f, w*0.5f, h*0.8f); quadraticBezierTo(w*0.8f, h*0.8f, w*0.8f, h*0.5f) }
                                            drawPath(curve, Color(0xFF3B82F6), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                                            drawLine(Color(0xFF3B82F6), Offset(w*0.5f, h*0.8f), Offset(w*0.5f, h*0.95f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                                        }
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, value: String, label: String, valueColor: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = valueColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = Color(0xFFA0A0A0), fontSize = 12.sp)
        }
    }
}

@Composable
fun HistoryCard(
    title: String, subtitle: String, value: String, date: String, valueColor: Color,
    iconBgAction: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconBgAction(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)))
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = Color(0xFFA0A0A0), fontSize = 13.sp)
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(date, color = Color(0xFFA0A0A0), fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MyDoctorTheme {
        ProfileScreen()
    }
}
