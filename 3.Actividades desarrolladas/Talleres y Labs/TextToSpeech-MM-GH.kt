package com.example.texttospeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextToSpeechApp()
        }
    }
}

// Inicializa el motor TTS y lo destruye cuando el composable sale de la pantalla
@Composable
fun rememberTextToSpeech(onReady: (TextToSpeech) -> Unit): TextToSpeech? {
    val context = LocalContext.current
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.setLanguage(Locale.US)
                // Cuando el motor esté listo, ejecutamos el callback para obtener las voces
                ttsInstance?.let { onReady(it) }
            }
        }
        ttsInstance
    }

    // Libera los recursos del TTS cuando el composable sale de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    return tts
}

// Convierte el nombre técnico de la voz en algo legible para el usuario
fun formatVoiceName(voice: Voice, index: Int): String {
    val lang = voice.locale.language
    val numero = index + 1
    return when (lang) {
        "es" -> "Voz $numero en Español"
        "en" -> "Voz $numero en Inglés"
        else -> "Voz $numero"
    }
}

@Composable
fun TextToSpeechApp() {
    var textToSpeak by remember { mutableStateOf("") }
    var speed by remember { mutableStateOf(1.0f) }

    // Lista de voces disponibles en el dispositivo
    var availableVoices by remember { mutableStateOf<List<Voice>>(emptyList()) }
    var selectedVoice by remember { mutableStateOf<Voice?>(null) }
    var voiceDropdownExpanded by remember { mutableStateOf(false) }

    var selectedIdiomaIndex by remember { mutableStateOf(0) }
    var idiomaDropdownExpanded by remember { mutableStateOf(false) }

    // Inicializa el TTS y carga las voces del dispositivo
    val tts = rememberTextToSpeech { engine ->
        availableVoices = engine.voices?.toList() ?: emptyList()
        selectedVoice = availableVoices.firstOrNull()
    }

// Genera la lista de idiomas únicos a partir de las voces reales del dispositivo
    val idiomasDisponibles = remember(availableVoices) {
        availableVoices
            .map { it.locale }
            .distinctBy { it.language }
            .map { locale ->
                val nombre = when (locale.language) {
                    "es" -> "Español"
                    "en" -> "Inglés"
                    "fr" -> "Francés"
                    "de" -> "Alemán"
                    "it" -> "Italiano"
                    "ja" -> "Japonés"
                    else -> locale.displayLanguage
                }
                nombre to locale
            }
            // Ordena: español primero, inglés segundo, el resto alfabético al final
            .sortedWith(compareBy {
                when (it.second.language) {
                    "es" -> 0
                    "en" -> 1
                    else -> 2
                }
            })
    }
    // Filtra las voces según el idioma seleccionado, sin repetir variantes
    val voicesFiltradas = remember(selectedIdiomaIndex, availableVoices, idiomasDisponibles) {
        if (idiomasDisponibles.isEmpty()) return@remember emptyList()
        val locale = idiomasDisponibles[selectedIdiomaIndex].second
        availableVoices.filter { it.locale.language == locale.language }
    }

    // Cuando cambia el idioma, resetea la voz seleccionada a la primera disponible
    LaunchedEffect(voicesFiltradas) {
        selectedVoice = voicesFiltradas.firstOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Text To Speech", fontSize = 26.sp)
        Spacer(modifier = Modifier.height(4.dp))
        // Nombres de los integrantes del grupo
        Text(text = "Integrantes: Maria Madrid  |  Gonzalo Hooker", fontSize = 13.sp)

        Spacer(modifier = Modifier.height(28.dp))

        // --- SELECTOR DE IDIOMA ---
        Text(text = "Idioma", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { idiomaDropdownExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (idiomasDisponibles.isNotEmpty())
                        idiomasDisponibles[selectedIdiomaIndex].first
                    else
                        "Cargando idiomas..."
                )
            }
            DropdownMenu(
                expanded = idiomaDropdownExpanded,
                onDismissRequest = { idiomaDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Muestra solo los idiomas únicos detectados en el dispositivo
                idiomasDisponibles.forEachIndexed { index, (nombre, locale) ->
                    DropdownMenuItem(
                        text = { Text(nombre) },
                        onClick = {
                            selectedIdiomaIndex = index
                            // Cambia el idioma en el motor TTS
                            tts?.setLanguage(locale)
                            idiomaDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SELECTOR DE VOZ ---
        Text(text = "Voz", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { if (voicesFiltradas.isNotEmpty()) voiceDropdownExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val indexSeleccionado = voicesFiltradas.indexOf(selectedVoice)
                Text(
                    text = if (selectedVoice != null && indexSeleccionado >= 0)
                        formatVoiceName(selectedVoice!!, indexSeleccionado)
                    else
                        "Sin voces disponibles"
                )
            }
            DropdownMenu(
                expanded = voiceDropdownExpanded,
                onDismissRequest = { voiceDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Muestra cada voz con nombre legible en lugar del nombre técnico del sistema
                voicesFiltradas.forEachIndexed { index, voice ->
                    DropdownMenuItem(
                        text = { Text(formatVoiceName(voice, index)) },
                        onClick = {
                            selectedVoice = voice
                            voiceDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CONTROL DE VELOCIDAD ---
        Text(
            text = "Velocidad: ${"%.1f".format(speed)}x",
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Slider(
            value = speed,
            onValueChange = { speed = it },
            valueRange = 0.5f..2.0f,
            steps = 5,
            modifier = Modifier.fillMaxWidth()
        )
        // Etiquetas de referencia del slider
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0.5x", fontSize = 11.sp)
            Text("1.0x", fontSize = 11.sp)
            Text("2.0x", fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO DE TEXTO MULTILINEA ---
        OutlinedTextField(
            value = textToSpeak,
            onValueChange = { textToSpeak = it },
            label = { Text("Escribe el texto aquí") },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- BOTÓN REPRODUCIR ---
        Button(
            onClick = {
                tts?.apply {
                    // Aplica la velocidad seleccionada antes de hablar
                    setSpeechRate(speed)
                    // Aplica la voz seleccionada si hay una disponible
                    selectedVoice?.let { voice -> setVoice(voice) }
                    // QUEUE_FLUSH cancela el audio previo y reproduce el nuevo inmediatamente
                    speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            },
            // Solo se habilita si el TTS está listo y hay texto escrito
            enabled = tts != null && textToSpeak.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reproducir audio", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- BOTÓN DETENER ---
        OutlinedButton(
            onClick = { tts?.stop() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Detener", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}