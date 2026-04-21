package com.example.mydoctor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDoctorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen = remember { mutableStateOf(if(com.example.mydoctor.backend.FirebaseManager.isLoggedIn()) "HOME" else "SPLASH") }

                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2500)
                        if (currentScreen.value == "SPLASH") {
                            currentScreen.value = "ONBOARDING"
                        }
                    }

                    val handleTabSelection = { tab: Int ->
                        when(tab) {
                            0 -> currentScreen.value = "HOME"
                            1 -> currentScreen.value = "RISK_SCORE"
                            2 -> currentScreen.value = "VOICE_AI" // Redirection vers le nouveau chat
                            3 -> currentScreen.value = "MAP"
                            4 -> currentScreen.value = "PROFILE"
                        }
                    }

                    when (currentScreen.value) {
                        "SPLASH" -> AfraScanSplashScreen()
                        "ONBOARDING" -> OnboardingScreen(onNavigateToLogin = { currentScreen.value = "LOGIN" })
                        "LOGIN" -> LoginScreen(
                            onNavigateToSignUp = { currentScreen.value = "SIGNUP" },
                            onNavigateToHome = { currentScreen.value = "HOME" }
                        )
                        "SIGNUP" -> SignUpScreen(
                            onNavigateToLogin = { currentScreen.value = "LOGIN" },
                            onNavigateToHome = { currentScreen.value = "HOME" }
                        )
                        "HOME" -> HomeScreen(
                            onTabSelected = handleTabSelection,
                            onNavigateToRiskScore = { currentScreen.value = "RISK_SCORE" },
                            onNavigateToMap = { currentScreen.value = "MAP" },
                            onNavigateToAiAnalysis = { currentScreen.value = "VOICE_AI" }, // Redirection
                            onNavigateToVoiceAi = { currentScreen.value = "VOICE_AI" },
                            onNavigateToAudioMeasure = { currentScreen.value = "AUDIO_MEASURE" }
                        )
                        "VOICE_AI" -> VoiceAiScreen(
                            onBack = { currentScreen.value = "HOME" }
                        )
                        "AUDIO_MEASURE" -> AudioMeasurementScreen(
                            onBack = { currentScreen.value = "HOME" },
                            onMeasureComplete = { currentScreen.value = "RISK_SCORE" }
                        )
                        "PROFILE" -> ProfileScreen(
                            onTabSelected = handleTabSelection,
                            onLogOut = {
                                com.example.mydoctor.backend.FirebaseManager.logout()
                                currentScreen.value = "LOGIN"
                            }
                        )
                        "RISK_SCORE" -> RiskScoreScreen(
                            onBack = { currentScreen.value = "HOME" },
                            onTabSelected = handleTabSelection
                        )
                        "MAP" -> HealthCentersScreen(
                            onBack = { currentScreen.value = "HOME" },
                            onTabSelected = handleTabSelection
                        )
                        "AI_ANALYSIS" -> AiAnalysisScreen(
                            onBack = { currentScreen.value = "HOME" },
                            onNavigateToChat = { currentScreen.value = "AI_CHAT" },
                            onTabSelected = handleTabSelection
                        )
                        "AI_CHAT" -> AiChatScreen(onBack = { currentScreen.value = "AI_ANALYSIS" })
                    }
                }
            }
        }
    }
}

@Composable
fun AfraScanSplashScreen() {
    val darkGreenColor = Color(0xFF0B2416)
    val lightGreenColor = Color(0xFF144729)
    val logoGreenColor = Color(0xFF37B559)
    val textLightGreen = Color(0xFF8ACB9E)

    // Glowing animation for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Loading bar animation
    val progressAnim = rememberInfiniteTransition(label = "progress")
    val progressOffset by progressAnim.animateFloat(
        initialValue = -1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progressOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(lightGreenColor, darkGreenColor)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-40).dp)
        ) {
            // Animated Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Outer Glow (Animated)
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(46.dp))
                        .background(logoGreenColor.copy(alpha = 0.08f))
                )
                // Inner Glow
                Box(
                    modifier = Modifier
                        .size(125.dp)
                        .clip(RoundedCornerShape(38.dp))
                        .background(logoGreenColor.copy(alpha = 0.15f))
                )
                // Main Rounded Box
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF44CC68), logoGreenColor),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Medical SVG icon (Heartbeat rate)
                    Canvas(modifier = Modifier.size(45.dp)) {
                        val path = Path().apply {
                            val w = size.width
                            val h = size.height
                            moveTo(0f, h * 0.5f)
                            lineTo(w * 0.3f, h * 0.5f)
                            lineTo(w * 0.45f, h * 0.2f)
                            lineTo(w * 0.6f, h * 0.8f)
                            lineTo(w * 0.75f, h * 0.5f)
                            lineTo(w, h * 0.5f)
                        }
                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(
                                width = 10f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            // Typography
            Text(
                text = "AfraScan",
                color = Color.White,
                fontSize = 42.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Dépistage médical intelligent par\ncapteurs smartphone",
                color = textLightGreen,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            // Indeterminate Loading Bar
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.35f)
                        .offset(x = (110f * progressOffset).dp)
                        .clip(RoundedCornerShape(50))
                        .background(logoGreenColor)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MyDoctorTheme {
        AfraScanSplashScreen()
    }
}