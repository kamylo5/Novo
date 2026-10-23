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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var heardText by mutableStateOf("Sistema pronto. Toque em TESTAR VOZ para começar.")
    private var voiceStatus by mutableStateOf("AGUARDANDO COMANDO")
    private var voiceReady = false

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val phrase = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!phrase.isNullOrBlank()) {
            heardText = "Você disse: $phrase"
            voiceStatus = "COMANDO RECEBIDO"
            val reply = answerFor(phrase)
            heardText = "Você: $phrase\n\nJARVIS: $reply"
            if (voiceReady) tts?.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply")
        } else {
            heardText = "Não consegui reconhecer a fala. Toque em TESTAR VOZ e tente novamente."
            voiceStatus = "SEM ÁUDIO RECONHECIDO"
        }
    }

    private val microphonePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startListening() else {
            heardText = "A permissão do microfone foi negada. Habilite-a nas configurações do Android para testar a voz."
            voiceStatus = "MICROFONE SEM PERMISSÃO"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF06111C)) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("J.A.R.V.I.S.", color = Color(0xFF65E7FF), fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("SISTEMA DE ASSISTÊNCIA PESSOAL", color = Color(0xFF91AFC0), fontSize = 12.sp)
                        Spacer(Modifier.height(28.dp))
                        Text("● $voiceStatus", color = Color(0xFF65E7FF), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(18.dp))
                        Text(
                            heardText,
                            modifier = Modifier.widthIn(max = 420.dp).background(Color(0xFF102332), RoundedCornerShape(16.dp)).padding(18.dp),
                            color = Color(0xFFE5F5FF),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { requestAndListen() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF087E9B))
                        ) { Text("TESTAR VOZ", fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.height(12.dp))
                        Text("Protótipo • reconhecimento de fala + resposta falada", color = Color(0xFF91AFC0), fontSize = 11.sp)
                    }
                }
            }
        }
    }

    private fun answerFor(input: String): String {
        val command = input.lowercase(Locale.ROOT)
        return when {
            "seu nome" in command || "quem é você" in command -> "Eu sou o JARVIS, seu assistente Android em fase de protótipo."
            "horas" in command || "que horas" in command -> "Agora são " + java.text.SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(java.util.Date()) + "."
            "data" in command || "dia é hoje" in command -> "Hoje é " + java.text.SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR")).format(java.util.Date()) + "."
            "olá" in command || "oi" in command || "bom dia" in command || "boa noite" in command -> "Olá. Estou ouvindo. O teste de voz está funcionando."
            else -> "Recebi seu comando. Ainda estou na versão inicial e preciso da integração com inteligência artificial para responder perguntas gerais."
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
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale com o JARVIS em português")
        }
        try {
            voiceStatus = "OUVINDO"
            speechLauncher.launch(intent)
        } catch (_: Exception) {
            heardText = "Não há um serviço de reconhecimento de voz disponível neste dispositivo."
            voiceStatus = "SERVIÇO INDISPONÍVEL"
        }
    }

    override fun onInit(status: Int) {
        voiceReady = status == TextToSpeech.SUCCESS
        if (voiceReady) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                heardText = "Instale uma voz em português nas configurações de Texto para voz do Android."
                voiceStatus = "VOZ PT-BR NÃO DISPONÍVEL"
            }
        } else {
            voiceStatus = "SÍNTESE DE VOZ INDISPONÍVEL"
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
