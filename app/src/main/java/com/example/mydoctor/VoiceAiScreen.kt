package com.example.mydoctor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

// --- Modèle ---
data class VoiceChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

private val SYSTEM_PROMPT = """
Vous êtes le Dr. AfraScan, un médecin expert en diagnostic d'orientation. Votre objectif est de mener une consultation médicale structurée et rigoureuse.

LIGNE DE CONDUITE :
1. **Investigation** : Posez des questions précises sur les symptômes (localisation, durée, intensité, facteurs aggravants). Ne donnez pas de conclusion avant d'avoir assez d'infos.
2. **Analyse** : Une fois les symptômes recueillis, synthétisez ce que cela pourrait être (diagnostic d'orientation).
3. **Action & Médication** : 
   - Proposez des traitements de premier secours (repos, hydratation, alimentation).
   - Proposez des médicaments courants sans ordonnance (ex: Paracétamol pour la fièvre, pansement gastrique pour aigreurs) avec les précautions d'usage.
4. **Triage Hospitalier (CRITIQUE)** : Si l'utilisateur présente des signes de gravité (douleur de poitrine irradiante, étouffement, perte de conscience, hémorragie, paralysie subite), ordonnez d'appeler les urgences ou d'aller à l'hôpital IMMÉDIATEMENT.
5. **Ton** : Professionnel, rassurant mais sérieux. Soyez concis mais complet.
""".trimIndent()

private const val GEMINI_API_KEY = "VOTRE CLE API"
private const val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$GEMINI_API_KEY"

// --- Logique Médicale ---
fun getOfflineConsultationResponse(history: List<VoiceChatMessage>): String {
    val count = history.filter { it.isUser }.size
    return when (count) {
        0 -> "Bonjour, je suis le Dr. AfraScan. Comment vous sentez-vous ?"
        1 -> "D'accord. Avez-vous de la fièvre ou des frissons ?"
        2 -> "Depuis combien de temps cela dure-t-il ?"
        else -> "Je comprends. En mode secours, je vous conseille de rester hydraté. Voulez-vous continuer ?"
    }
}

suspend fun callGeminiNativeAi(userMsg: String, history: List<VoiceChatMessage>): String {
    return withContext(Dispatchers.IO) {
        try {
            if (userMsg == "BONJOUR_INIT") {
                return@withContext "Bonjour, je suis le Dr. AfraScan, votre assistant médical intelligent. Je peux vous aider à analyser vos symptômes et vous orienter. \n\n*Note : Je suis une IA, mes conseils ne remplacent pas une consultation physique en cas d'urgence grave.*\n\nQu'est-ce qui vous amène aujourd'hui ?"
            }

            if (GEMINI_API_KEY == "YOUR_GEMINI_API_KEY") return@withContext getOfflineConsultationResponse(history + VoiceChatMessage(userMsg, true))
            val contents = JSONArray()
            contents.put(JSONObject().put("role", "user").put("parts", JSONArray().put(JSONObject().put("text", SYSTEM_PROMPT))))
            history.takeLast(10).forEach { msg ->
                contents.put(JSONObject().put("role", if (msg.isUser) "user" else "model")
                    .put("parts", JSONArray().put(JSONObject().put("text", msg.text))))
            }
            contents.put(JSONObject().put("role", "user").put("parts", JSONArray().put(JSONObject().put("text", userMsg))))
            val body = JSONObject().put("contents", contents)
            val conn = URL(GEMINI_URL).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 8000
            conn.outputStream.use { it.write(body.toString().toByteArray()) }

            if (conn.responseCode == 200) {
                val text = conn.inputStream.bufferedReader().readText()
                JSONObject(text).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
            } else getOfflineConsultationResponse(history + VoiceChatMessage(userMsg, true))
        } catch (e: Exception) { getOfflineConsultationResponse(history + VoiceChatMessage(userMsg, true)) }
    }
}

@Composable
fun VoiceAiScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember { mutableStateOf(listOf<VoiceChatMessage>()) }
    var isListening by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var liveTranscript by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("") }
    
    // Timer de sécurité pour le micro
    var listeningJob by remember { mutableStateOf<Job?>(null) }

    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    fun stopListeningAggressive() {
        listeningJob?.cancel()
        speechRecognizer.cancel()
        isListening = false
        liveTranscript = ""
    }

    fun startListeningWithSafety() {
        tts.value?.stop()
        stopListeningAggressive()
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Paramètres de silence auto-stop
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
        }
        
        isListening = true
        speechRecognizer.startListening(intent)

        // Timeout de sécurité : si rien après 12s, on ferme
        listeningJob = scope.launch {
            delay(12000)
            if (isListening) {
                stopListeningAggressive()
                liveTranscript = "Délai dépassé - Appuyez pour reprendre"
            }
        }
    }

    fun handleAI(txt: String) {
        listeningJob?.cancel()
        if (txt.isNotBlank() && txt != "BONJOUR_INIT") messages = messages + VoiceChatMessage(txt, true)
        isLoading = true
        scope.launch {
            val res = callGeminiNativeAi(txt, messages.dropLast(if (txt == "BONJOUR_INIT") 0 else 1))
            messages = messages + VoiceChatMessage(res, false)
            isLoading = false
            tts.value?.speak(res, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    DisposableEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.FRENCH
                tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) { isSpeaking = true }
                    override fun onDone(id: String?) { 
                        isSpeaking = false
                        scope.launch { delay(600); startListeningWithSafety() }
                    }
                    override fun onError(id: String?) { isSpeaking = false }
                })
                handleAI("BONJOUR_INIT")
            }
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p: Bundle?) { liveTranscript = "Écoute..." }
            override fun onBeginningOfSpeech() { listeningJob?.cancel() } // Détecté la parole, on annule le timeout court
            override fun onResults(r: Bundle?) {
                isListening = false
                listeningJob?.cancel()
                r?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { if (it.isNotEmpty()) handleAI(it) }
            }
            override fun onError(e: Int) { 
                isListening = false
                listeningJob?.cancel()
                liveTranscript = if (e == SpeechRecognizer.ERROR_NO_MATCH) "Rien entendu" else ""
            }
            override fun onEndOfSpeech() { isListening = false }
            override fun onPartialResults(p: Bundle?) { p?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { liveTranscript = it } }
            override fun onRmsChanged(r: Float) {}
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEvent(ev: Int, p: Bundle?) {}
        })

        onDispose { tts.value?.shutdown(); stopListeningAggressive(); speechRecognizer.destroy() }
    }

    // UI
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF144729)).padding(top = 44.dp, bottom = 15.dp, start = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "", tint = Color.White) }
                Text("Dr. AfraScan", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(messages) { msg ->
                if (msg.text.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart) {
                        Surface(color = if (msg.isUser) Color(0xFF144729) else Color.White, shape = RoundedCornerShape(12.dp), tonalElevation = 2.dp, modifier = Modifier.widthIn(max = 280.dp)) {
                            Text(msg.text, modifier = Modifier.padding(12.dp), color = if (msg.isUser) Color.White else Color.Black)
                        }
                    }
                }
            }
            if (isLoading) item { Text("Analyse...", color = Color.Gray, fontSize = 12.sp) }
        }
        // Barre de saisie Hybride
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Transcript vocal flottant
                if (liveTranscript.isNotEmpty()) {
                    Text(
                        text = "\"$liveTranscript\"",
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF37B559),
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
                        fontSize = 14.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Bouton Micro
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isListening) Color(0xFF37B559) else Color(0xFF144729))
                            .clickable { if (isListening) stopListeningAggressive() else startListeningWithSafety() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Micro",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Champ de texte
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Écrivez votre message...", fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF144729),
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color(0xFFF9F9F9),
                            unfocusedContainerColor = Color(0xFFF9F9F9)
                        ),
                        maxLines = 3,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Bouton Envoyer
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                handleAI(textInput)
                                textInput = ""
                            }
                        },
                        enabled = textInput.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (textInput.isNotBlank() && !isLoading) Color(0xFF144729) else Color.LightGray,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Envoyer",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
