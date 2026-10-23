package com.kamylo5.jarvisai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var heardText by mutableStateOf("Toque no botão e fale um comando.")
    private var voiceReady = false

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val phrase = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!phrase.isNullOrBlank()) {
            heardText = "Você disse: $phrase"
            tts?.speak("Entendi. Você disse: $phrase", TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply")
        } else {
            heardText = "Não consegui reconhecer a fala. Tente novamente."
        }
    }

    private val microphonePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startListening() else heardText = "Permissão de microfone negada. Você pode habilitá-la nas configurações do Android."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("J.A.R.V.I.S.", style = MaterialTheme.typography.headlineLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("ASSISTENTE PESSOAL • PROTÓTIPO", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(28.dp))
                        Text(heardText, modifier = Modifier.widthIn(max = 420.dp), style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { requestAndListen() }) { Text("Falar com o JARVIS") }
                        Spacer(Modifier.height(12.dp))
                        Text("Versão inicial: reconhecimento de voz e resposta falada.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    private fun requestAndListen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Pode falar com o JARVIS")
        }
        try {
            speechLauncher.launch(intent)
        } catch (_: Exception) {
            heardText = "O serviço de reconhecimento de voz não está disponível neste dispositivo."
        }
    }

    override fun onInit(status: Int) {
        voiceReady = status == TextToSpeech.SUCCESS
        if (voiceReady) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                heardText = "A voz em português não está instalada. Configure uma voz em português nas configurações do Android."
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
