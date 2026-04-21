package com.example.mydoctor

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mydoctor.backend.FirebaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("MissingPermission")
@Composable
fun AudioMeasurementScreen(onBack: () -> Unit = {}, onMeasureComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val topGreenColor = Color(0xFF144729)
    val micBlueColor = Color(0xFF3B82F6)
    val coughOrange = Color(0xFFFF6B35)

    var isRecording by remember { mutableStateOf(false) }
    var coughCount by remember { mutableStateOf(0) }
    var amplitude by remember { mutableStateOf(0f) }
    var isCoughFlash by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Prêt à analyser votre toux") }
    val targetCoughs = 5

    // Microphone permission
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasAudioPermission = granted }

    // ── Real cough detection with AudioRecord ─────────────────────
    LaunchedEffect(isRecording, hasAudioPermission) {
        if (!isRecording || !hasAudioPermission) return@LaunchedEffect

        withContext(Dispatchers.IO) {
            val sampleRate = 44100
            val minBuf = AudioRecord.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBuf * 4

            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            recorder.startRecording()
            val buffer = ShortArray(minBuf)

            var lastCoughTime = 0L
            val cooldownMs = 1800L       // Min time between 2 coughs
            val amplitudeHistory = ArrayDeque<Float>(200)

            withContext(Dispatchers.Main) { statusText = "Toussez $targetCoughs fois devant le micro..." }

            while (isRecording) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    // Calculate RMS amplitude
                    var sumSq = 0.0
                    for (i in 0 until read) sumSq += buffer[i].toLong() * buffer[i].toLong()
                    val rms = sqrt(sumSq / read).toFloat()

                    // Track ambient noise for dynamic threshold
                    amplitudeHistory.addLast(rms)
                    if (amplitudeHistory.size > 150) amplitudeHistory.removeFirst()
                    val ambientLevel = amplitudeHistory.average().toFloat()

                    // Cough threshold: 4x above ambient noise, min 2000
                    val threshold = (ambientLevel * 4f).coerceAtLeast(2000f)
                    val now = System.currentTimeMillis()
                    val isCough = rms > threshold && (now - lastCoughTime) > cooldownMs

                    withContext(Dispatchers.Main) {
                        amplitude = rms
                        isCoughFlash = isCough

                        if (isCough) {
                            lastCoughTime = now
                            coughCount++
                            statusText = if (coughCount < targetCoughs)
                                "✓ Toux détectée ! Encore ${targetCoughs - coughCount}..."
                            else "Analyse terminée !"

                            if (coughCount >= targetCoughs) {
                                isRecording = false
                                scope.launch {
                                    FirebaseManager.saveMeasurement(
                                        "COUGH_AUDIO",
                                        "$targetCoughs toux analysées",
                                        "Analyse complète"
                                    )
                                    onMeasureComplete()
                                }
                            }
                        }
                    }
                }
            }

            recorder.stop()
            recorder.release()
        }
    }

    // ── Wave animation ────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "waves")
    val waveAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "waveAnim"
    )

    // ── UI ────────────────────────────────────────────────────────
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().background(topGreenColor)
                .padding(top = 40.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f)).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Analyse Acoustique", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!hasAudioPermission) {
                // ── Permission request ──
                Text(
                    "Le microphone est requis\npour détecter et analyser vos toux",
                    color = Color(0xFF1A1A1A), fontSize = 18.sp, fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center, lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = micBlueColor)
                ) {
                    Text("Autoriser le microphone", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(statusText, color = Color(0xFF1A1A1A), fontSize = 18.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 26.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Placez le téléphone à 20cm de votre bouche.",
                    color = Color(0xFFA0A0A0), fontSize = 14.sp, textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // ── Live amplitude visualizer ──
                Box(modifier = Modifier.fillMaxWidth().height(70.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerY = size.height / 2
                        val barW = 5.dp.toPx()
                        val gap = 9.dp.toPx()
                        val maxBars = (size.width / (barW + gap)).toInt()
                        val ampNorm = (amplitude / 6000f).coerceIn(0f, 1f)

                        for (i in 0..maxBars) {
                            val x = i * (barW + gap)
                            val wave = abs(sin((i.toFloat() + waveAnim * 15) * 0.5).toFloat())
                            val barH = if (isRecording)
                                wave * (size.height / 2) * ampNorm * 1.5f + 6f
                            else 4f
                            val color = when {
                                isCoughFlash -> coughOrange.copy(alpha = 0.9f)
                                isRecording -> micBlueColor.copy(alpha = 0.5f + ampNorm * 0.5f)
                                else -> Color(0xFFE0E0E0)
                            }
                            drawLine(color, Offset(x, centerY - barH / 2), Offset(x, centerY + barH / 2), strokeWidth = barW, cap = StrokeCap.Round)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Cough counter circles ──
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in 1..targetCoughs) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape)
                                .background(if (i <= coughCount) micBlueColor else Color(0xFFF0F4FF))
                                .border(1.dp, if (i <= coughCount) micBlueColor else Color(0xFFD0D0D0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (i <= coughCount) "✓" else "$i",
                                color = if (i <= coughCount) Color.White else Color(0xFFA0A0A0),
                                fontSize = 14.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Mic icon ──
                Box(
                    modifier = Modifier.size(88.dp).clip(CircleShape)
                        .background(if (isRecording) micBlueColor.copy(alpha = 0.1f) else Color(0xFFF0F4FF))
                        .border(2.dp, if (isRecording) micBlueColor else Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(40.dp)) {
                        val w = size.width; val h = size.height
                        val capsule = Path().apply { addRoundRect(RoundRect(w*0.35f, h*0.1f, w*0.65f, h*0.6f, CornerRadius(w*0.15f))) }
                        drawPath(capsule, micBlueColor, style = Stroke(3.dp.toPx()))
                        val curve = Path().apply { moveTo(w*0.2f, h*0.5f); quadraticBezierTo(w*0.2f, h*0.8f, w*0.5f, h*0.8f); quadraticBezierTo(w*0.8f, h*0.8f, w*0.8f, h*0.5f) }
                        drawPath(curve, micBlueColor, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
                        drawLine(micBlueColor, Offset(w*0.5f, h*0.8f), Offset(w*0.5f, h*0.95f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!isRecording) {
                    Button(
                        onClick = { coughCount = 0; isRecording = true },
                        modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = micBlueColor)
                    ) {
                        Text("Démarrer l'analyse", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Text(
                        "$coughCount / $targetCoughs toux détectées",
                        color = micBlueColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
