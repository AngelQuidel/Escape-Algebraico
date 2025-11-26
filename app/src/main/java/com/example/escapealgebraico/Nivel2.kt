package com.example.escapealgebraico

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.escapealgebraico.utils.ProgressManager

@Composable
fun PantallaNivel2(navController: NavHostController) {

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFD1F7C4)
    val textoColor = if (isDark) Color.White else Color.Black

    var mapa by remember { mutableStateOf(generarMapaNivel2()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llaveTomada by remember { mutableStateOf(false) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var puertaAbierta by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("Encuentra la llave 🔑 resolviendo multiplicación o división") }
    var mostrarInstrucciones by remember { mutableStateOf(true) }
    var nivelCompletado by remember { mutableStateOf(false) }
    
    // Sistema de Vidas
    var vidas by remember { mutableStateOf(3) }
    var intentoPregunta by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
    ) { innerPadding ->

        if (mostrarInstrucciones) {
            InstruccionesNivel2(
                isDark = isDark,
                textoColor = textoColor,
                fondoColor = fondoColor,
                onCerrar = { mostrarInstrucciones = false }
            )
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Botón Guardar Progreso (Arriba a la izquierda)
            Button(
                onClick = {
                    ProgressManager.guardarNivel(context, 2)
                    Toast.makeText(context, "Progreso guardado", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(1f), // Asegura que esté por encima
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000080), // Azul oscuro
                    contentColor = Color.White
                ),
                border = BorderStroke(2.dp, Color.Yellow)
            ) {
                Text("💾 Guardar", fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            }

            // Vidas (Arriba a la derecha)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .zIndex(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Text(
                        text = if (index < vidas) "❤️" else "💔",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }

            // Contenido con Scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fondoColor)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 80.dp, bottom = 24.dp), // Espacio para el botón superior
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "🔥 Nivel 2: Multiplicación y División",
                    color = textoColor,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(10.dp))

                // --- MAPA Compacto
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    for (y in mapa.indices) {
                        Row {
                            for (x in mapa[y].indices) {

                                val emoji = when {
                                    jugadorPos.first == x && jugadorPos.second == y -> "🦖"
                                    mapa[y][x] == "W" -> "🧱"
                                    mapa[y][x] == "G" -> if (puertaAbierta) "🍖" else "🚪"
                                    mapa[y][x] == "K" && !llaveTomada -> "🔑"
                                    else -> "🟩"
                                }

                                Text(
                                    text = emoji,
                                    fontSize = 24.sp // Compactamos el mapa
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    mensaje, 
                    color = textoColor, 
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                // --- Pregunta o controles ---
                if (mostrarPregunta) {
                    key(intentoPregunta) {
                        PreguntaMatematicaNivel2(
                            textoColor = textoColor,
                            isDark = isDark,
                            onRespuesta = { correcta ->
                                if (correcta) {
                                    SoundManager.playCorrectSound(context)
                                    llaveTomada = true
                                    puertaAbierta = true
                                    mostrarPregunta = false
                                    mensaje = "🔑 ¡Correcto! Obtuviste la llave."
                                } else {
                                    SoundManager.playWrongSound(context)
                                    vidas--
                                    if (vidas <= 0) {
                                        mapa = generarMapaNivel2()
                                        jugadorPos = Pair(1, 1)
                                        llaveTomada = false
                                        puertaAbierta = false
                                        mostrarPregunta = false
                                        mensaje = "💔 ¡Sin vidas! Nivel reiniciado."
                                        mostrarInstrucciones = true
                                        vidas = 3
                                        intentoPregunta = 0
                                    } else {
                                        mensaje = "❌ Incorrecto. Pierdes 1 vida."
                                        intentoPregunta++
                                    }
                                }
                            }
                        )
                    }

                } else if (!nivelCompletado) {
                    ControlesMovimiento(
                        onMove = { dx, dy ->
                            val nuevaPos = Pair(jugadorPos.first + dx, jugadorPos.second + dy)

                            if (puedeMoverse(mapa, nuevaPos)) {
                                jugadorPos = nuevaPos
                                val (x, y) = nuevaPos

                                when (mapa[y][x]) {
                                    "K" -> if (!llaveTomada) {
                                        mostrarPregunta = true
                                        mensaje = "🔢 Resuelve para obtener la llave"
                                    }
                                    "G" -> {
                                        if (puertaAbierta) {
                                            nivelCompletado = true
                                            mensaje = "🎉 ¡Has completado el Nivel 2!"
                                            ProgressManager.guardarNivel(context, 3)
                                        } else {
                                            mensaje = "🚪 La puerta está cerrada."
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (!nivelCompletado) {
                    Button(
                        onClick = {
                            mapa = generarMapaNivel2()
                            jugadorPos = Pair(1, 1)
                            llaveTomada = false
                            puertaAbierta = false
                            mostrarPregunta = false
                            mensaje = "Encuentra la llave 🔑 resolviendo multiplicación o división"
                            vidas = 3
                            intentoPregunta = 0
                            navController.navigate("niveles") {
                                popUpTo("nivel2") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400),
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(2.dp, Color.Yellow),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("⬅️ Volver", fontFamily = FontFamily.Monospace, fontSize = 20.sp)
                    }
                }

                if (nivelCompletado) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 180.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("niveles") {
                                    popUpTo("nivel4") { inclusive = true }
                                }
                            },
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006400),
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Yellow)
                        ) {
                            Text("⬅️ Volver", fontFamily = FontFamily.Monospace, fontSize = 20.sp)
                        }

                        if (nivelCompletado) {
                            Button(
                                onClick = {
                                    NivelState.mostrarInstruccionesNivel5 = true
                                    navController.navigate("nivel3")
                                },
                                modifier = Modifier.padding(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF006400),
                                    contentColor = Color.Black
                                ),
                                border = BorderStroke(2.dp, Color.Yellow)
                            ) {
                                Text("Siguiente ➡️", fontFamily = FontFamily.Monospace, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstruccionesNivel2(
    isDark: Boolean,
    textoColor: Color,
    fondoColor: Color,
    onCerrar: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Text(
                "📘 Instrucciones del Nivel 2",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = """
                    🔥 En este nivel trabajarás **multiplicación y división**.

                    ⭐ MULTIPLICACIÓN  
                    Es sumar un número varias veces.  
                    Ejemplos:  
                    • 3 × 2 = 6  
                    • 5 × 4 = 20  

                    ⭐ DIVISIÓN  
                    Es repartir en partes iguales.  
                    Ejemplos:  
                    • 6 ÷ 3 = 2  
                    • 12 ÷ 4 = 3  

                    🔹 Cuando encuentres la llave tendrás que resolver
                       una operación para obtenerla.

                    🔹 Si respondes mal:
                         • Pierdes una vida (tienes 3)
                         • La pregunta cambiará
                         • Si pierdes todas las vidas, reinicias el nivel

                    🔹 Si respondes bien:
                         • Obtienes la llave  
                         • La puerta se desbloquea  
                         • Puedes avanzar a la meta  

                    Recuerda:  
                    ✔ Las divisiones SIEMPRE tendrán resultado entero.  
                """.trimIndent(),
                color = textoColor,
                textAlign = TextAlign.Left,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onCerrar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400),
                    contentColor = Color.Black
                ),
                border = BorderStroke(2.dp, Color.Yellow),
                modifier = Modifier.height(48.dp)
            ) {
                Text("¡Jugar!", fontFamily = FontFamily.Monospace, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun PreguntaMatematicaNivel2(textoColor: Color, isDark: Boolean, onRespuesta: (Boolean) -> Unit) {

    val operadores = if ((0..1).random() == 0) "*" else "/"

    var a = (2..10).random()
    var b = (2..10).random()

    if (operadores == "/") {
        b = (2..10).random()
        a = b * (2..10).random()
    }

    val correcta = if (operadores == "*") a * b else a / b
    val simbolo = if (operadores == "*") "×" else "÷"

    val explicacion = if (operadores == "*") {
        "🧮 Multiplicar es sumar un número varias veces.\nEjemplo: 3 × 2 = 6 (3 + 3)."
    } else {
        "🍎 Dividir es repartir en partes iguales.\nEjemplo: 6 ÷ 3 = 2 (6 manzanas entre 3 personas)."
    }

    val opciones = mutableSetOf(correcta)
    while (opciones.size < 3) opciones.add(correcta + (-3..3).random())
    val listaOpciones = opciones.shuffled()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Text(explicacion, fontFamily = FontFamily.Monospace, color = textoColor)
        Spacer(Modifier.height(6.dp))

        Text("¿Cuánto es $a $simbolo $b?", fontFamily = FontFamily.Monospace, color = textoColor)

        Spacer(Modifier.height(8.dp))

        listaOpciones.forEach { opcion ->
            Button(
                onClick = { onRespuesta(opcion == correcta) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400),
                    contentColor = Color.Black
                ),
                border = BorderStroke(2.dp, Color.Yellow),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(opcion.toString(), color = Color.Black, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

fun generarMapaNivel2(): List<List<String>> {
    return listOf(
        listOf("W","W","W","W","W","W","W"),
        listOf("W"," ","W","K"," "," ","W"),
        listOf("W"," ","W","W","W"," ","W"),
        listOf("W"," "," "," "," "," ","W"),
        listOf("W","W","W"," ","W"," ","W"),
        listOf("W"," "," "," ","W","G","W"),
        listOf("W","W","W","W","W","W","W")
    )
}
