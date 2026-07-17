package com.example.taller5_mm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

//PAKETA DE COLORES
object AppColors {
    val Background     = Color(0xFF0A0F1E)
    val Surface        = Color(0xFF111827)
    val SurfaceCard    = Color(0xFF1A2540)
    val Border         = Color(0xFF1E3A5F)
    val AccentBlue     = Color(0xFF1D6FE8)
    val AccentBlueDark = Color(0xFF1558C0)
    val AccentCyan     = Color(0xFF00B4D8)
    val TextPrimary    = Color(0xFFE8F0FE)
    val TextSecondary  = Color(0xFF7A9CC0)
    val TextHint       = Color(0xFF3A5A7A)
    val Success        = Color(0xFF1B6CA8)
    val SuccessLight   = Color(0xFFBFD7F7)
    val Error          = Color(0xFF8B1A2E)
    val ErrorLight     = Color(0xFFFFB3BE)
    val Warning        = Color(0xFF1E4A7A)
    val WarningLight   = Color(0xFFADD8E6)
}

//MODELO DEL RANKING
data class JugadorRanking(
    val nombre: String,
    val intentosUsados: Int,
    val tiempoSegundos: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColors.Background)
                ) {
                    PantallaJuego()
                }
            }
        }
    }
}

@Composable
fun PantallaJuego() {
    //ESTADOS
    var numeroSecreto    by remember { mutableStateOf((1..10).random()) }
    var inputUsuario     by remember { mutableStateOf("") }
    var intentos         by remember { mutableStateOf(0) }
    var mensaje          by remember { mutableStateOf("Adivina el numero del 1 al 10") }
    var juegoTerminado   by remember { mutableStateOf(false) }
    var gano             by remember { mutableStateOf(false) }
    var segundos         by remember { mutableStateOf(0) }
    var cronometroActivo by remember { mutableStateOf(true) }
    var nombreJugador    by remember { mutableStateOf("") }
    var mostrarDialogo   by remember { mutableStateOf(false) }
    var ranking          by remember { mutableStateOf(listOf<JugadorRanking>()) }

    val maxIntentos = 3

    //CRONOMETRO
    LaunchedEffect(cronometroActivo) {
        while (cronometroActivo) {
            delay(1000L)
            segundos++
        }
    }

    //VALIRDAR INTENTO
    fun validarIntento() {
        val numero = inputUsuario.toIntOrNull()

        if (numero == null) {
            mensaje = "Ingresa solo numeros"
            return
        }
        if (numero < 1 || numero > 10) {
            mensaje = "El numero debe estar entre 1 y 10"
            return
        }

        intentos++

        when {
            numero == numeroSecreto -> {
                gano = true
                juegoTerminado = true
                cronometroActivo = false
                mensaje = "Correcto! Adivinaste en $intentos intento(s) y ${segundos}s"
                mostrarDialogo = true
            }
            intentos >= maxIntentos -> {
                juegoTerminado = true
                cronometroActivo = false
                mensaje = "Agotaste los $maxIntentos intentos. El numero era $numeroSecreto"
            }
            numero < numeroSecreto -> {
                val restantes = maxIntentos - intentos
                mensaje = when (restantes) {
                    1 -> "El numero es mayor que $numero — ultimo intento!"
                    else -> "El numero es mayor que $numero — te quedan $restantes intentos"
                }
            }
            else -> {
                val restantes = maxIntentos - intentos
                mensaje = when (restantes) {
                    1 -> "El numero es menor que $numero — ultimo intento!"
                    else -> "El numero es menor que $numero — te quedan $restantes intentos"
                }
            }
        }
        inputUsuario = ""
    }

    //REINICIAR
    fun reiniciar() {
        numeroSecreto    = (1..10).random()
        inputUsuario     = ""
        intentos         = 0
        mensaje          = "Adivina el numero del 1 al 10"
        juegoTerminado   = false
        gano             = false
        segundos         = 0
        cronometroActivo = true
    }

    //DIALOGO PARA EL NOMBRE───
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            containerColor = AppColors.SurfaceCard,
            titleContentColor = AppColors.TextPrimary,
            textContentColor = AppColors.TextSecondary,
            title = {
                Text(
                    "Registro de puntuacion",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.AccentCyan
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Tiempo: ${segundos}s  |  Intentos: $intentos",
                        color = AppColors.TextSecondary,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = nombreJugador,
                        onValueChange = { nombreJugador = it },
                        label = { Text("Tu nombre", color = AppColors.TextHint) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AppColors.AccentBlue,
                            unfocusedBorderColor = AppColors.Border,
                            cursorColor          = AppColors.AccentCyan,
                            focusedTextColor     = AppColors.TextPrimary,
                            unfocusedTextColor   = AppColors.TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreJugador.isNotBlank()) {
                            val nuevo = JugadorRanking(nombreJugador.trim(), intentos, segundos)
                            ranking = (ranking + nuevo)
                                .sortedWith(compareBy({ it.intentosUsados }, { it.tiempoSegundos }))
                            nombreJugador  = ""
                            mostrarDialogo = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentBlue)
                ) { Text("Guardar", color = AppColors.TextPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Saltar", color = AppColors.TextSecondary)
                }
            }
        )
    }

    //COLOR DEL MENSAJE SEGUN EL ESTADO
    val (bgMensaje, textMensaje) = when {
        gano           -> AppColors.Success  to AppColors.SuccessLight
        juegoTerminado -> AppColors.Error    to AppColors.ErrorLight
        intentos > 0   -> AppColors.Warning  to AppColors.WarningLight
        else           -> AppColors.SurfaceCard to AppColors.TextSecondary
    }

    //INTERFASZ
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        //TITULO
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "ADIVINA EL NUMERO",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = AppColors.AccentCyan,
                textAlign = TextAlign.Center
            )
            Text(
                text = "del 1 al 10",
                fontSize = 13.sp,
                color = AppColors.TextHint,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        //CRONOMETRO E INTNETOS
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.SurfaceCard)
                    .border(1.dp, AppColors.Border, RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox(label = "TIEMPO", value = "${segundos}s")
                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = AppColors.Border
                )
                StatBox(label = "INTENTOS", value = "$intentos / $maxIntentos")
            }
        }

        //BARRA DE INTENTOS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(maxIntentos) { i ->
                    val usado = i < intentos
                    val color = when {
                        usado && gano          -> AppColors.AccentCyan
                        usado && juegoTerminado -> AppColors.Error
                        usado                  -> AppColors.AccentBlue
                        else                   -> AppColors.Border
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
            }
        }

        //MENSAJE DE STADO
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgMensaje)
                    .border(1.dp, AppColors.Border, RoundedCornerShape(10.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mensaje,
                    color = textMensaje,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        //NUMERO POR EL USUARIO
        item {
            OutlinedTextField(
                value = inputUsuario,
                onValueChange = { inputUsuario = it },
                label = { Text("Ingresa un numero (1-10)", color = AppColors.TextHint) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !juegoTerminado,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AppColors.AccentBlue,
                    unfocusedBorderColor = AppColors.Border,
                    disabledBorderColor  = AppColors.TextHint,
                    cursorColor          = AppColors.AccentCyan,
                    focusedTextColor     = AppColors.TextPrimary,
                    unfocusedTextColor   = AppColors.TextPrimary,
                    disabledTextColor    = AppColors.TextHint,
                    focusedContainerColor   = AppColors.SurfaceCard,
                    unfocusedContainerColor = AppColors.SurfaceCard,
                    disabledContainerColor  = AppColors.Surface
                )
            )
        }

        //BOTON PARA VALIDAR
        item {
            Button(
                onClick = { validarIntento() },
                enabled = !juegoTerminado && inputUsuario.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = AppColors.AccentBlue,
                    disabledContainerColor = AppColors.Border
                )
            ) {
                Text(
                    "VALIDAR",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = AppColors.TextPrimary
                )
            }
        }

        //BOTON PAR NUEVO JUEGO
        item {
            Button(
                onClick = { reiniciar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Success
                )
            ) {
                Text(
                    "NUEVO JUEGO",
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.SuccessLight
                )
            }
        }

        //RANKING
        if (ranking.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = AppColors.Border
                    )
                    Text(
                        "  RANKING  ",
                        color = AppColors.TextHint,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = AppColors.Border
                    )
                }
            }

            itemsIndexed(ranking) { index, jugador ->
                FilaRanking(posicion = index + 1, jugador = jugador)
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

//COMPONENTE-CAJA DE ESTADISTICA
@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = AppColors.TextHint
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.AccentCyan
        )
    }
}

//COMPONENRE-FILA DE RANKING
@Composable
fun FilaRanking(posicion: Int, jugador: JugadorRanking) {
    val accentColor = when (posicion) {
        1    -> AppColors.AccentCyan
        2    -> AppColors.AccentBlue
        else -> AppColors.TextHint
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.SurfaceCard)
            .border(1.dp, AppColors.Border, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "#$posicion",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.width(36.dp)
        )
        Text(
            text = jugador.nombre,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${jugador.intentosUsados} intento(s)",
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
            Text(
                "${jugador.tiempoSegundos}s",
                fontSize = 12.sp,
                color = AppColors.TextHint
            )
        }
    }
}