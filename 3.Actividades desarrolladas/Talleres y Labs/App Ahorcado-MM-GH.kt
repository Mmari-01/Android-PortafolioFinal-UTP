package com.example.ahorcado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ABECEDARIO = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ".toList()

/* DEVUELVE EL ID DE LA IMAGEN SEGÚN LOS ERRORES
recibe el número de errores y devuelve
el recurso de imagen correspondiente */
fun imagenMuneco(errores: Int): Int {
    return when (errores) {
        0 -> R.drawable.img_1_1
        1 -> R.drawable.img_2_1
        2 -> R.drawable.img_3_1
        3 -> R.drawable.img_4_1
        4 -> R.drawable.img_5_1
        5 -> R.drawable.img_6_1
        else -> R.drawable.img_7_1
    }
}

//MAIN
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaAhorcado()
            }
        }
    }
}

//PANTALLA PRINCIPAL DEL JUEGO
@Composable
fun PantallaAhorcado() {

    /*VARIABLES CON remember
    remember hace que compose recuerde el valor
    aunque la pantalla se redibuje
    mutableStateOf(...) avisa a compose cuando
    el valor cambia para que actualice la pantalla*/

    //controla si el juego ya inició o aún se está ingresando la palabra
    var juegoIniciado by remember { mutableStateOf(false) }

    //palabra secreta ingresada por el usuario
    var palabraActual by remember { mutableStateOf("") }

    //texto que escribe el usuario en el campo de palabra secreta
    var palabraEscrita by remember { mutableStateOf("") }

    //mensaje de error de validación de la palabra
    var errorPalabra by remember { mutableStateOf("") }

    //lista de letras ya intentadas
    var letrasUsadas by remember { mutableStateOf(setOf<Char>()) }

    //contador de errores
    var errores by remember { mutableStateOf(0) }

    //VARIABLES CALCULADAS sin remember
    //los huecos: si la letra fue adivinada se muestra, si no "_"
    val huecos = palabraActual.map { letra ->
        if (letra in letrasUsadas) letra else '_'
    }

    //gano o perdio
    val gano = juegoIniciado && '_' !in huecos
    val perdio = errores >= 6
    val juegoTerminado = gano || perdio

    /* VALIDA E INICIA EL JUEGO
    verifica que la palabra no esté vacía y solo tenga letras */
    fun iniciarJuego() {
        when {
            palabraEscrita.isBlank() -> {
                errorPalabra = "La palabra no puede estar vacía"
            }
            !palabraEscrita.all { it.isLetter() } -> {
                errorPalabra = "Solo se permiten letras"
            }
            else -> {
                palabraActual = palabraEscrita.uppercase()
                palabraEscrita = ""
                errorPalabra = ""
                letrasUsadas = setOf()
                errores = 0
                juegoIniciado = true
            }
        }
    }

    //procesar letra presionada desde los botones
    fun procesarLetra(letra: Char) {
        val letraMayus = letra.uppercaseChar()
        if (letraMayus in letrasUsadas || juegoTerminado) return

        letrasUsadas = letrasUsadas + letraMayus

        if (letraMayus !in palabraActual) {
            errores += 1 //suma cuando la letra no esta en la palabra
        }
    }

    //reiniciar partida y vuelve a la pantalla de ingreso de palabra
    fun reiniciar() {
        juegoIniciado = false
        palabraActual = ""
        palabraEscrita = ""
        errorPalabra = ""
        letrasUsadas = setOf()
        errores = 0
    }

    //INTERFAZ
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //TITULO
        Text(
            text = "AHORCADO",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        /* PANTALLA DE INGRESO DE PALABRA SECRETA
        se muestra antes de iniciar el juego */
        if (!juegoIniciado) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ingresa la palabra secreta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    //CAMPO TIPO CONTRASEÑA para ocultar la palabra
                    OutlinedTextField(
                        value = palabraEscrita,
                        onValueChange = { nueva ->
                            //solo letras, sin límite de longitud
                            if (nueva.all { it.isLetter() || it == ' ' }) {
                                palabraEscrita = nueva
                                errorPalabra = ""
                            }
                        },
                        label = { Text("Palabra secreta") },
                        //visualTransformation oculta el texto como contraseña
                        visualTransformation = PasswordVisualTransformation(),
                        isError = errorPalabra.isNotEmpty(),
                        supportingText = {
                            if (errorPalabra.isNotEmpty()) {
                                Text(text = errorPalabra, color = Color(0xFFB71C1C))
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    Button(
                        onClick = { iniciarJuego() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar juego")
                    }
                }
            }

        } else {

            //IMAGEN DEL MUÑECO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Image(
                    painter = painterResource(id = imagenMuneco(errores)),
                    contentDescription = "Ahorcado con $errores errores",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )
            }

            //ERRORES
            Text(
                text = "Errores: $errores / 6",
                fontSize = 16.sp,
                color = if (errores >= 4) Color.Red else Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            //PALABRA CON HUECOS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                //lazyrow muestra cada letra/hueco como un item
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(huecos) { caracter ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Text(
                                text = caracter.toString(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (caracter == '_') Color.Gray else Color(0xFF1565C0)
                            )
                            //linea debajo de cada letra
                            Spacer(modifier = Modifier
                                .width(22.dp)
                                .height(2.dp)
                                .background(Color.DarkGray))
                        }
                    }
                }
            }

            /*BOTONES DEL ABECEDARIO
            reemplaza el campo de texto, cada letra es un botón
            +si está en letrasUsadas Y en la palabra, verde
            +si está en letrasUsadas Y no está, rojo
            +si no fue usada, azul normal */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    //dividimos el abecedario en filas de 7 letras
                    ABECEDARIO.chunked(7).forEach { fila ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            fila.forEach { letra ->
                                val usada = letra in letrasUsadas
                                val acierto = usada && letra in palabraActual

                                //when decide el color del botón segun su estado
                                val colorBoton = when {
                                    acierto -> Color(0xFF2E7D32)   // verde si acerto
                                    usada   -> Color(0xFFB71C1C)   // rojo si fallo
                                    else    -> Color(0xFF1565C0)   // azul si no usada
                                }

                                Button(
                                    onClick = { procesarLetra(letra) },
                                    enabled = !usada && !juegoTerminado,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorBoton,
                                        disabledContainerColor = colorBoton.copy(alpha = 0.6f)
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .size(38.dp)
                                ) {
                                    Text(
                                        text = letra.toString(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textDecoration = if (usada) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                }
                            }
                        }
                    }
                }
            }

            //MENSAJE DE FIN DE JUEGO
            if (juegoTerminado) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (gano) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (gano) "¡Ganaste!" else "Perdiste",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gano) Color(0xFF2E7D32) else Color(0xFFB71C1C)
                        )
                        if (perdio) {
                            Text(
                                text = "La palabra era: $palabraActual",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Button(
                            onClick = { reiniciar() },
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Jugar de nuevo")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}