package com.example.mydoctor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

@SuppressLint("MissingPermission")
@Composable
fun CameraMeasurementScreen(onBack: () -> Unit = {}, onMeasureComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val topGreenColor = Color(0xFF144729)
    val measureRedColor = Color(0xFFE53935)

    var isMeasuring by remember { mutableStateOf(false) }
    var bpm by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }
    var fingerDetected by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Appuyez pour démarrer") }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    // ── PPG signal: list of (timestamp ms, avg luminance) ────────
    val ppgSignal = remember { mutableListOf<Pair<Long, Float>>() }

    // ── Reliable BPM calculation using timestamp-aware peak detection ──
    fun calculateBPM(signal: List<Pair<Long, Float>>): Int {
        if (signal.size < 60) return 0
        val values = signal.map { it.second }
        val timestamps = signal.map { it.first }

        // Moving average smoothing (window = 7)
        val smoothed = values.mapIndexed { i, _ ->
            val s = maxOf(0, i - 3); val e = minOf(values.size, i + 4)
            values.subList(s, e).average().toFloat()
        }
        val mean = smoothed.average()
        val peaks = mutableListOf<Int>()

        for (i in 1 until smoothed.size - 1) {
            if (smoothed[i] > smoothed[i - 1] &&
                smoothed[i] > smoothed[i + 1] &&
                smoothed[i] > mean) {
                // Min 300ms between peaks (max 200 BPM)
                val lastTs = if (peaks.isEmpty()) 0L else timestamps[peaks.last()]
                if (timestamps[i] - lastTs > 300L) peaks.add(i)
            }
        }

        if (peaks.size < 3) return 0
        val intervals = peaks.zipWithNext { a, b -> timestamps[b] - timestamps[a] }
        val avgInterval = intervals.average()
        return (60_000.0 / avgInterval).toInt().coerceIn(40, 200)
    }

    // ── Camera2 ImageReader for real PPG ──────────────────────────
    DisposableEffect(isMeasuring, hasCameraPermission) {
        if (!isMeasuring || !hasCameraPermission) return@DisposableEffect onDispose {}

        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // Find back camera
        val backCamId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        } ?: return@DisposableEffect onDispose {}

        // Background thread for camera operations
        val camThread = HandlerThread("PPGCamera").also { it.start() }
        val camHandler = Handler(camThread.looper)

        // ImageReader: small resolution for fast processing
        val imageReader = ImageReader.newInstance(160, 120, ImageFormat.YUV_420_888, 3)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                val plane = image.planes[0]
                val buffer = plane.buffer
                val rowStride = plane.rowStride
                val pixelStride = plane.pixelStride
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)

                // Average luminance (Y channel) — correlated with blood volume with torch
                var sum = 0L
                var count = 0
                var row = 0
                while (row < 120) {
                    var col = 0
                    while (col < 160) {
                        val idx = row * rowStride + col * pixelStride
                        if (idx < bytes.size) {
                            sum += bytes[idx].toInt() and 0xFF
                            count++
                        }
                        col += 4 // sample every 4 pixels
                    }
                    row += 4
                }
                val avg = if (count > 0) sum.toFloat() / count else 0f
                val ts = System.currentTimeMillis()

                ContextCompat.getMainExecutor(context).execute {
                    ppgSignal.add(Pair(ts, avg))
                    // With torch on and finger pressed: high and oscillating brightness
                    fingerDetected = avg > 60f
                }
            } finally {
                image.close()
            }
        }, camHandler)

        var cameraDevice: CameraDevice? = null
        var captureSession: CameraCaptureSession? = null

        cameraManager.openCamera(backCamId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                val surface = imageReader.surface
                @Suppress("DEPRECATION")
                camera.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        try {
                            val req = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                                addTarget(surface)
                                // Enable torch for PPG illumination
                                set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
                                // Lock exposure to avoid brightness auto-correction masking PPG signal
                                set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF)
                                set(CaptureRequest.SENSOR_EXPOSURE_TIME, 8_000_000L) // 8ms
                                set(CaptureRequest.SENSOR_SENSITIVITY, 300)
                            }.build()
                            session.setRepeatingRequest(req, null, camHandler)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, camHandler)
            }
            override fun onDisconnected(camera: CameraDevice) { camera.close() }
            override fun onError(camera: CameraDevice, error: Int) { camera.close() }
        }, camHandler)

        onDispose {
            captureSession?.close()
            cameraDevice?.close()
            imageReader.close()
            camThread.quitSafely()
        }
    }

    // ── Progress loop + live BPM computation ─────────────────────
    LaunchedEffect(isMeasuring) {
        if (!isMeasuring) return@LaunchedEffect
        ppgSignal.clear()
        val totalMs = 30_000L
        val startTime = System.currentTimeMillis()
        statusText = "Posez votre index sur la caméra et le flash..."

        while (System.currentTimeMillis() - startTime < totalMs) {
            delay(1000)
            val elapsed = System.currentTimeMillis() - startTime
            progress = elapsed.toFloat() / totalMs

            // Start computing BPM after 8 seconds of signal collection
            if (elapsed > 8_000 && ppgSignal.size > 80) {
                val calc = calculateBPM(ppgSignal.toList())
                if (calc > 0) {
                    bpm = calc
                    statusText = "Signal cardiaque détecté ✓"
                }
            }
        }

        val finalBpm = if (bpm in 40..200) bpm else 0
        val resultStatus = when {
            finalBpm == 0 -> "Inconclusive (doigt mal positionné ?)"
            finalBpm < 60 -> "Bradycardie (lent: $finalBpm BPM)"
            finalBpm > 100 -> "Tachycardie (rapide: $finalBpm BPM)"
            else -> "Normal ($finalBpm BPM)"
        }

        scope.launch {
            if (finalBpm > 0) {
                FirebaseManager.saveMeasurement("HEART_RATE", "$finalBpm BPM", resultStatus)
            }
            onMeasureComplete()
        }
    }

    // ── Animation ─────────────────────────────────────────────────
    val pulseMs = if (bpm > 0) (60_000 / bpm).coerceIn(300, 1500) else 900
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(pulseMs / 2, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val waveAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )

    // ── UI ────────────────────────────────────────────────────────
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D))) {
        Row(
            modifier = Modifier.fillMaxWidth()
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
            Column {
                Text("Rythme Cardiaque", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                Text("PPG via caméra + flash", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!hasCameraPermission) {
                Text(
                    "La caméra est requise\npour la mesure PPG\n(photopléthysmographie)",
                    color = Color.White, fontSize = 17.sp, textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif, lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = topGreenColor)
                ) {
                    Text("Autoriser la caméra", fontWeight = FontWeight.Bold)
                }
            } else {
                // BPM display
                if (bpm > 0) {
                    Text("$bpm", color = measureRedColor, fontSize = 82.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 82.sp)
                    Text("BPM", color = Color.White.copy(alpha = 0.7f), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(statusText, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center, lineHeight = 22.sp)

                if (!isMeasuring) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Technique PPG : la lumière du flash traverse votre peau.\nLe capteur détecte les variations dues au flux sanguin.",
                        color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp,
                        textAlign = TextAlign.Center, lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ECG waveform
                if (isMeasuring) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(55.dp)) {
                        val pts = 80
                        val step = size.width / pts
                        for (i in 0 until pts - 1) {
                            val t = (i.toFloat() / pts + waveAnim) * 2 * Math.PI
                            val y = size.height / 2 - sin(t).toFloat() * size.height * 0.4f
                            val t2 = ((i + 1).toFloat() / pts + waveAnim) * 2 * Math.PI
                            val y2 = size.height / 2 - sin(t2).toFloat() * size.height * 0.4f
                            drawLine(
                                measureRedColor.copy(alpha = 0.8f),
                                Offset(i * step, y), Offset((i + 1) * step, y2),
                                strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Heart pulse circle
                Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                    if (isMeasuring) {
                        Box(modifier = Modifier.size(150.dp).scale(pulseScale).clip(CircleShape).background(measureRedColor.copy(alpha = 0.1f)))
                        Box(modifier = Modifier.size(110.dp).scale(pulseScale).clip(CircleShape).background(measureRedColor.copy(alpha = 0.25f)))
                    }
                    Box(
                        modifier = Modifier.size(88.dp).clip(CircleShape)
                            .background(if (isMeasuring) measureRedColor else Color(0xFF2A2A2A))
                            .clickable(enabled = !isMeasuring) { isMeasuring = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(44.dp)) {
                            val path = Path().apply {
                                moveTo(0f, size.height / 2)
                                lineTo(size.width * 0.25f, size.height / 2)
                                lineTo(size.width * 0.38f, 0f)
                                lineTo(size.width * 0.5f, size.height)
                                lineTo(size.width * 0.62f, size.height / 2)
                                lineTo(size.width, size.height / 2)
                            }
                            drawPath(path, Color.White, style = Stroke(3.5f.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (isMeasuring) {
                    Text("${(progress * 100).toInt()}%", color = measureRedColor, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(0.8f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = measureRedColor, trackColor = Color(0xFF2A2A2A)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (fingerDetected) "✓ Signal détecté — Flash actif" else "⚠ Appuyez votre doigt sur la caméra + flash",
                        color = if (fingerDetected) Color(0xFF37B559) else Color(0xFFFFB300),
                        fontSize = 13.sp, textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Trames analysées : ${ppgSignal.size}",
                        color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp
                    )
                } else {
                    Button(
                        onClick = { isMeasuring = true },
                        modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = topGreenColor)
                    ) {
                        Text("Démarrer la mesure", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
