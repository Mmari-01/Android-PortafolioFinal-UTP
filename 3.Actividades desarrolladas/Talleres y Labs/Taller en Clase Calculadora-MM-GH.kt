package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaSuma() {
    var texto1 by remember { mutableStateOf("") }
    var texto2 by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("0.0") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CALCULADORA",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("A", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
            OutlinedTextField(
                value = texto1,
                onValueChange = {
                    if (it.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                        texto1 = it
                    }
                },
                label = { Text("Número 1") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("B", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
            OutlinedTextField(
                value = texto2,
                onValueChange = {
                    if (it.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                        texto2 = it
                    }
                },
                label = { Text("Número 2") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(60.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Total: $resultado", fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (texto1.isBlank() || texto2.isBlank()) {
                    mensaje = "Ingrese valores en ambos campos"
                    return@Button
                }

                val n1 = texto1.toDoubleOrNull()
                val n2 = texto2.toDoubleOrNull()

                if (n1 == null || n2 == null) {
                    mensaje = "Solo se permiten valores numéricos"
                    return@Button
                }

                mensaje = ""

                resultado = String.format("%.2f", n1 + n2)
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(55.dp)
        ) {
            Text("Suma", fontSize = 18.sp)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaSuma()
            }
        }
    }
}
//Pertenece a Maria Madrid y Gonzalo Hooker