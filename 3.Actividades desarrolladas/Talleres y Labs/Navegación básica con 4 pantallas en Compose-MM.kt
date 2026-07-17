package com.example.taller6mm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*

// rutas de navegacion de cada pantalla
sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Profile  : Screen("profile")
    object Settings : Screen("settings")
    // details recibe un parametro con el nombre de la pantalla de origen
    object Details  : Screen("details/{nombre}") {
        fun createRoute(nombre: String) = "details/$nombre"
    }
}

// cada item del menu inferior tiene etiqueta, icono y ruta
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// pantallas que aparecen en la barra inferior
val bottomNavItems = listOf(
    BottomNavItem("Inicio",  Icons.Default.Home,     Screen.Home.route),
    BottomNavItem("Perfil",  Icons.Default.Person,   Screen.Profile.route),
    BottomNavItem("Ajustes", Icons.Default.Settings, Screen.Settings.route)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                AppNavigation()
            }
        }
    }
}

// toda la navegacion de la app
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // se oculta la barra inferior en la pantalla de detalles
            if (rutaActual != null && !rutaActual.startsWith("details")) {
                BarraInferior(navController = navController, rutaActual = rutaActual)
            }
        }
    ) { paddingInterno ->

        // contenedor que muestra la pantalla activa
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(paddingInterno)
        ) {
            composable(
                route           = Screen.Home.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)) },
                exitTransition  = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut(tween(300)) }
            ) {
                PantallaHome(navController)
            }

            composable(
                route           = Screen.Profile.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)) },
                exitTransition  = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut(tween(300)) }
            ) {
                PantallaPerfil(navController)
            }

            composable(
                route           = Screen.Settings.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)) },
                exitTransition  = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut(tween(300)) }
            ) {
                PantallaConfiguracion(navController)
            }

            // detalles recibe el nombre de la pantalla desde donde se navego
            composable(
                route           = Screen.Details.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) + fadeIn(tween(300)) },
                exitTransition  = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300)) }
            ) { backStackEntry ->
                val nombre = backStackEntry.arguments?.getString("nombre") ?: "Inicio"
                PantallaDetalles(navController = navController, pantalla = nombre)
            }
        }
    }
}

// barra de navegacion inferior
@Composable
fun BarraInferior(navController: NavController, rutaActual: String) {
    NavigationBar(
        containerColor = Color(0xFF0D0D0D),
        contentColor   = Color.White
    ) {
        bottomNavItems.forEach { item ->
            val seleccionado = rutaActual == item.route
            NavigationBarItem(
                selected  = seleccionado,
                onClick   = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon      = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label     = { Text(item.label) },
                colors    = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFFE91E8C),
                    selectedTextColor   = Color(0xFFE91E8C),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor      = Color(0xFF1A1A1A)
                )
            )
        }
    }
}

// pantalla de inicio
@Composable
fun PantallaHome(navController: NavController) {

    // estado del TextField con el nombre de la desarrolladora
    var nombre by remember { mutableStateOf("Maria Madrid") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0030), Color(0xFF0D0D0D))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier            = Modifier.padding(32.dp)
        ) {

            Text(
                text  = "Inicio",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )

            // TextField con el nombre de la desarrolladora
            OutlinedTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = { Text("Desarrolladora", color = Color.White.copy(alpha = 0.6f)) },
                textStyle     = androidx.compose.ui.text.TextStyle(
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFFE91E8C),
                    textAlign  = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFFE91E8C),
                    unfocusedBorderColor = Color(0xFFE91E8C).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // icono de esta pantalla
            Icon(
                imageVector        = Icons.Default.Home,
                contentDescription = "icono inicio",
                modifier           = Modifier.size(130.dp),
                tint               = Color(0xFFE91E8C)
            )

            // boton que navega a detalles indicando que viene de Inicio
            Button(
                onClick  = { navController.navigate(Screen.Details.createRoute("Inicio")) },
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E8C)),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    text       = "Ver Detalles",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }
        }
    }
}

// pantalla de perfil
@Composable
fun PantallaPerfil(navController: NavController) {

    // estado del TextField con el nombre de la desarrolladora
    var nombre by remember { mutableStateOf("Maria Madrid") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF001A30), Color(0xFF0D0D0D))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier            = Modifier.padding(32.dp)
        ) {

            Text(
                text     = "Perfil",
                fontSize = 16.sp,
                color    = Color.White.copy(alpha = 0.6f)
            )

            // TextField con el nombre de la desarrolladora
            OutlinedTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = { Text("Desarrolladora", color = Color.White.copy(alpha = 0.6f)) },
                textStyle     = androidx.compose.ui.text.TextStyle(
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFF29B6F6),
                    textAlign  = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF29B6F6),
                    unfocusedBorderColor = Color(0xFF29B6F6).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // icono de esta pantalla
            Icon(
                imageVector        = Icons.Default.AccountCircle,
                contentDescription = "icono perfil",
                modifier           = Modifier.size(130.dp),
                tint               = Color(0xFF29B6F6)
            )

            // tarjeta con datos de perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFF0A1929)),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Nombre: Maria Madrid", color = Color.White, fontSize = 15.sp)
                    Text("Carrera: Ingenieria",  color = Color.White, fontSize = 15.sp)
                    Text("Semestre: 7",          color = Color.White, fontSize = 15.sp)
                }
            }

            // boton que navega a detalles indicando que viene de Perfil
            Button(
                onClick  = { navController.navigate(Screen.Details.createRoute("Perfil")) },
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B6F6)),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    text       = "Ver Detalles",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF0D0D0D)
                )
            }
        }
    }
}

// pantalla de configuracion
@Composable
fun PantallaConfiguracion(navController: NavController) {

    // estado del TextField con el nombre de la desarrolladora
    var nombre by remember { mutableStateOf("Maria Madrid") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D1F00), Color(0xFF0D0D0D))
                )
            )
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text     = "Configuracion",
                fontSize = 16.sp,
                color    = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TextField con el nombre de la desarrolladora
            OutlinedTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = { Text("Desarrolladora", color = Color.White.copy(alpha = 0.6f)) },
                textStyle     = androidx.compose.ui.text.TextStyle(
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFF69F0AE),
                    textAlign  = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF69F0AE),
                    unfocusedBorderColor = Color(0xFF69F0AE).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // icono de esta pantalla
            Icon(
                imageVector        = Icons.Default.Build,
                contentDescription = "icono configuracion",
                modifier           = Modifier.size(110.dp),
                tint               = Color(0xFF69F0AE)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // tarjeta con informacion de la app
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFF0A1F00)),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Version: 1.0.0",           color = Color.White, fontSize = 15.sp)
                    Text("Plataforma: Android",       color = Color.White, fontSize = 15.sp)
                    Text("Framework: Jetpack Compose", color = Color.White, fontSize = 15.sp)
                    Text("Taller: 6MM",               color = Color.White, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // boton que manda a detalles indicando que viene de Configuracion
            Button(
                onClick  = { navController.navigate(Screen.Details.createRoute("Configuracion")) },
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF69F0AE)),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    text       = "Ver Detalles",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF0D0D0D)
                )
            }
        }
    }
}

// pantalla de detalles. recibe el nombre de la pantalla desde donde se navego
@Composable
fun PantallaDetalles(navController: NavController, pantalla: String) {

    // estado del TextField con el nombre de la desarrolladora
    var nombre by remember { mutableStateOf("Maria Madrid") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1F1000), Color(0xFF0D0D0D))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier            = Modifier.padding(32.dp)
        ) {

            Text(
                text     = "Detalles",
                fontSize = 16.sp,
                color    = Color.White.copy(alpha = 0.6f)
            )

            // TextField con el nombre de la desarrolladora
            OutlinedTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = { Text("Desarrolladora", color = Color.White.copy(alpha = 0.6f)) },
                textStyle     = androidx.compose.ui.text.TextStyle(
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFFFFAB40),
                    textAlign  = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFFFFAB40),
                    unfocusedBorderColor = Color(0xFFFFAB40).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // icono de esta pantalla
            Icon(
                imageVector        = Icons.Default.Star,
                contentDescription = "icono detalles",
                modifier           = Modifier.size(110.dp),
                tint               = Color(0xFFFFAB40)
            )

            // tarjeta que muestra desde que pantalla se navego hasta aqui
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFF1F1000)),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text     = "Llegaste desde:",
                        color    = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // muestra el nombre de la pantalla de origen
                    Text(
                        text       = pantalla,
                        color      = Color(0xFFFFAB40),
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // boton para regresar a la pantalla anterior
            Button(
                onClick  = { navController.popBackStack() },
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    text       = "Volver",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF0D0D0D)
                )
            }
        }
    }
}