package com.example.taller2mm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2mm.ui.theme.Taller2MMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Taller2MMTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContenidoPrincipal(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ContenidoPrincipal(modifier: Modifier = Modifier) {
    val favoritos = remember { mutableStateListOf<String>() }
    var verFavoritos by remember { mutableStateOf(false) }

    if (verFavoritos) {
        PantallaFavoritos(favoritos = favoritos, alVolver = { verFavoritos = false }, modifier = modifier)
    } else {
        PantallaListaPaises(favoritos = favoritos, alVerFavoritos = { verFavoritos = true }, modifier = modifier)
    }
}

@Composable
fun PantallaListaPaises(favoritos: MutableList<String>, alVerFavoritos: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val manejadorFoco = LocalFocusManager.current
    val paises = remember {
        mutableStateListOf(
            "Argentina", "Bolivia", "Brasil", "Chile", "Colombia",
            "Costa Rica", "Cuba", "Ecuador", "El Salvador", "España",
            "Guatemala", "Honduras", "México", "Nicaragua", "Panamá",
            "Paraguay", "Perú", "Uruguay", "Venezuela", "Italia",
            "Francia", "Alemania", "Japón", "China", "Canadá",
            "Australia", "Portugal", "India", "Corea del Sur", "Filipinas"
        )
    }
    var nuevoPais by remember { mutableStateOf("") }

    fun esTextoValido(texto: String): Boolean {
        return texto.all { it.isLetter() || it == ' ' }
    }

    fun agregarPais() {
        val p = nuevoPais.trim()
        when {
            p.isEmpty() -> Toast.makeText(context, "Escribe un nombre de país", Toast.LENGTH_SHORT).show()
            !esTextoValido(p) -> Toast.makeText(context, "Solo se permiten letras y espacios", Toast.LENGTH_SHORT).show()
            paises.contains(p) -> Toast.makeText(context, "$p ya está en la lista", Toast.LENGTH_SHORT).show()
            else -> {
                paises.add(p)
                nuevoPais = ""
                manejadorFoco.clearFocus()
                Toast.makeText(context, "$p agregado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        Text("Países del Mundo", fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp))

        Button(onClick = alVerFavoritos, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Ver Favoritos (${favoritos.size})")
        }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = nuevoPais,
                onValueChange = { if (esTextoValido(it) || it.isEmpty()) nuevoPais = it },
                label = { Text("Agregar país") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { agregarPais() })
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { agregarPais() }) { Text("Agregar") }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(paises) { pais ->
                TarjetaPais(pais, favoritos.contains(pais)) {
                    if (favoritos.contains(pais)) favoritos.remove(pais) else favoritos.add(pais)
                }
            }
        }
    }
}

@Composable
fun TarjetaPais(pais: String, esFavorito: Boolean, alTocarFavorito: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esFavorito) Color(0xFF1565C0) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = pais,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = if (esFavorito) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Button(onClick = {
                Toast.makeText(context, "País: $pais", Toast.LENGTH_SHORT).show()
            }) { Text("Detalles") }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(onClick = alTocarFavorito) {
                Text(if (esFavorito) "❤️" else "🤍", fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun PantallaFavoritos(favoritos: List<String>, alVolver: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        Text("Mis Favoritos", fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp))
        Button(onClick = alVolver, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver")
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (favoritos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes favoritos", textAlign = TextAlign.Center, color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(favoritos) { pais ->
                    Card(modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))) {
                        Text(pais, modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaApp() {
    Taller2MMTheme { ContenidoPrincipal() }
}