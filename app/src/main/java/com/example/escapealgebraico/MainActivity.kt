package com.example.escapealgebraico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.escapealgebraico.ui.theme.EscapeAlgebraicoTheme
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EscapeAlgebraicoTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "inicio") {

                    // Inicio
                    composable("inicio") { PantallaInicio(navController) }

                    // Información (con o sin nombre)
                    composable("informacion") { PantallaInformacion(navController = navController, nombre = "") }
                    composable("informacion/{nombre}") { backStackEntry ->
                        val nombreArg = backStackEntry.arguments?.getString("nombre") ?: ""
                        PantallaInformacion(navController = navController, nombre = nombreArg)
                    }

                    composable("niveles") { PantallaSeleccionNivel(navController) }

                    // Niveles del juego
                    composable("nivel1") { PantallaNivel1(navController) }
                    composable("nivel2") { PantallaNivel2(navController) }
                    composable("nivel3") { PantallaNivel3(navController) }
                    composable("nivel4") { PantallaNivel4(navController) }
                    composable("nivel5") { PantallaNivel5(navController) }
                    
                    composable("pantallaFinal") {
                        val context = LocalContext.current
                        PantallaFinalFelicidades(navController, context)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SoundManager.playBackgroundMusic(this)
    }

    override fun onPause() {
        super.onPause()
        SoundManager.stopBackgroundMusic()
    }
}

@Composable
fun PantallaInicio(navController: NavHostController) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var saludoVisible by rememberSaveable { mutableStateOf(false) }
    val scale = remember { Animatable(0.5f) }

    val isDark = isSystemInDarkTheme()

    val textColor = if (isDark) Color.White else Color.Black
    val placeholderColor = if (isDark) Color.Gray else Color.DarkGray
    val borderColor = if (isDark) Color.Gray else Color.Black

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            )
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .scale(scale.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    "ESCAPE ALGEBRAICO",
                    color = Color(0xFF00FF00),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = "Imagen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Ingresar nombre", fontSize = 22.sp, color = textColor) },
                    placeholder = { Text("", fontSize = 22.sp, color = placeholderColor) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 24.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF00),
                        unfocusedBorderColor = borderColor,
                        focusedLabelColor = Color(0xFF00FF00),
                        unfocusedLabelColor = textColor,
                        cursorColor = textColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (nombre.isNotBlank()) saludoVisible = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aceptar nombre", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (saludoVisible) {
                    Text(
                        text = "¡Bienvenido, ${nombre.trim()}!",
                        color = Color(0xFF00FF00),
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier.padding(6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (saludoVisible && nombre.isNotBlank()) {
                            val rutaNombre = nombre.trim().replace("/", "_")
                            navController.navigate("informacion/$rutaNombre")
                        } else {
                            navController.navigate("informacion")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Empezar Aprendizaje",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Text(
                text = "Creador: \"Angel Quidel\"",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun PantallaInformacion(navController: NavHostController, nombre: String) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),

        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF00),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = { navController.navigate("niveles") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF00),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Ir a Jugar ➡️", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            if (nombre.isNotBlank()) {
                Text(
                    "¡Bienvenido, ${nombre.trim()}!",
                    color = Color(0xFF00FF00),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                "INSTRUCCIONES DEL JUEGO",
                color = Color(0xFFFFCC00),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "🟩 Nivel 1: Sumas y Restas (6 a 8 años)\n" +
                        "Comienza la aventura resolviendo sumas y restas simples. Cada respuesta correcta abre un camino nuevo. ¡Perfecto para entrar en ritmo!\n\n" +

                        "🟨 Nivel 2: Multiplicaciones y Divisiones (8 a 9 años)\n" +
                        "Aquí necesitarás multiplicar y dividir para avanzar entre los pasillos del laberinto. Los números suben… pero tu poder también. ¡Sigue adelante!\n\n" +

                        "🟦 Nivel 3: Operaciones Combinadas (9 a 10 años)\n" +
                        "Ahora todo se mezcla: suma, resta, multiplica y divide. Recuerda siempre que las multiplicaciones y divisiones van primero. ¡Con calma lo lograrás!\n\n" +

                        "🟥 Nivel 4: Decimales (10 a 11 años)\n" +
                        "Los decimales aparecen en tu camino. Si un número tiene muchos decimales, redondéalo al más cercano. No te asustes, los decimales también son amigos.\n\n" +

                        "🟦 Nivel 5: Desafío Final — Gran Laberinto Algebraico (11 a 12+ años)\n" +
                        "Este es el último reto. Combina todo lo que aprendiste y demuestra que eres un maestro del Escape Algebraico. ¡Es una prueba para verdaderos aventureros!\n\n" +

                        "💡 Consejo final:\n" +
                        "Respira, piensa con calma y revisa tus cuentas. Si algo te cuesta un poco… ¡puedes usar una calculadora sin problema! Lo importante es aprender y disfrutar.\n\n" +
                        "🔥 Créelo: tú puedes con esto. Ya has llegado lejos, sigue así. ¡Vamos que se puede! 💪",
                color = Color.Gray,
                style = TextStyle(fontSize = 21.sp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PantallaSeleccionNivel(navController: NavHostController) {

    LaunchedEffect(Unit) {
        NivelState.mostrarInstruccionesNivel4 = true
        NivelState.mostrarInstruccionesNivel5 = true
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "SELECCIONA UN NIVEL",
                color = Color(0xFFFFCC00),
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Monospace)
            )

            Spacer(modifier = Modifier.height(24.dp))

            for (nivel in 1..5) {
                Button(
                    onClick = {
                        navController.navigate("nivel$nivel") {
                            // Mantiene esta pantalla debajo del nivel
                            popUpTo("seleccionarNivel") { inclusive = false }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        "Nivel $nivel",
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("informacion") {
                        popUpTo("informacion") { inclusive = false }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52EA00),
                    contentColor = Color.Black
                )
            ) {
                Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PantallaSeleccionNivelPreview() {
    EscapeAlgebraicoTheme {
        PantallaSeleccionNivel(navController = rememberNavController())
    }
}
