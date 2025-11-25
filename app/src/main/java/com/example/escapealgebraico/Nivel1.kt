package com.example.escapealgebraico

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
import androidx.navigation.NavHostController
import com.example.escapealgebraico.utils.ProgressManager

@Composable
fun PantallaNivel1(navController: NavHostController) {

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFFFF3CD)
    val textoColor = if (isDark) Color.White else Color.Black
    val botonColor = Color(0xFF00FF00)

    var mapa by remember { mutableStateOf(generarMapaNivel1()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llaveTomada by remember { mutableStateOf(false) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var puertaAbierta by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarInstrucciones by remember { mutableStateOf(true) }
    var nivelCompletado by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
    ) { innerPadding ->

        if (mostrarInstrucciones) {
            InstruccionesNivel1(
                isDark = isDark,
                textoColor = textoColor,
                fondoColor = fondoColor,
                botonColor = botonColor,
                onCerrar = { mostrarInstrucciones = false }
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(fondoColor)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(12.dp))

            Text(
                "🦖 Nivel 1: Sumas y Restas",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(10.dp))

            // MAPA
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (y in mapa.indices) {
                    Row {
                        for (x in mapa[y].indices) {
                            val emoji = when {
                                jugadorPos.first == x && jugadorPos.second == y -> "🦖"
                                mapa[y][x] == "W" -> "🧱"
                                mapa[y][x] == "G" -> if (puertaAbierta) "🍖" else "🚪"
                                mapa[y][x] == "K" -> "🔑"
                                else -> "🟩"
                            }
                            Text(
                                text = emoji,
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(mensaje, color = textoColor, fontFamily = FontFamily.Monospace)

            Spacer(Modifier.height(16.dp))

            // PREGUNTA o CONTROLES
            if (mostrarPregunta) {
                PreguntaMatematicaNivel1(
                    textoColor = textoColor,
                    isDark = isDark,
                    onRespuesta = { correcta ->
                        if (correcta) {
                            SoundManager.playCorrectSound(context)
                            llaveTomada = true
                            mostrarPregunta = false
                            mensaje = "🔑 ¡Bien hecho! Obtuviste la llave."
                            puertaAbierta = true
                        } else {
                            SoundManager.playWrongSound(context)
                            mapa = generarMapaNivel1()
                            jugadorPos = Pair(1, 1)
                            llaveTomada = false
                            puertaAbierta = false
                            mostrarPregunta = false
                            mensaje = "❌ Fallaste. Nivel reiniciado."
                            mostrarInstrucciones = true
                        }
                    }
                )

            } else if (!nivelCompletado) {
                ControlesMovimiento(
                    onMove = { dx, dy ->
                        val nuevaPos = Pair(jugadorPos.first + dx, jugadorPos.second + dy)

                        if (puedeMoverse(mapa, nuevaPos)) {
                            jugadorPos = nuevaPos
                            val (x, y) = nuevaPos

                            when (mapa[y][x]) {
                                "K" -> {
                                    val nuevoMapa = mapa.map { it.toMutableList() }.toMutableList()
                                    nuevoMapa[y][x] = " "
                                    mapa = nuevoMapa
                                    mostrarPregunta = true
                                }
                                "G" -> {
                                    if (puertaAbierta) {
                                        nivelCompletado = true
                                        mensaje = "🎉 ¡Ganaste el Nivel 1!"
                                        ProgressManager.guardarNivel(context, 2)
                                    } else mensaje = "🚪 Necesitas la llave."
                                }
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            // BOTÓN VOLVER – SIEMPRE VISIBLE ABAJO, TAMAÑO NORMAL
            if (!nivelCompletado) {
                Button(
                    onClick = {
                        mapa = generarMapaNivel1()
                        jugadorPos = Pair(1, 1)
                        llaveTomada = false
                        mostrarPregunta = false
                        puertaAbierta = false
                        mensaje = ""
                        navController.navigate("niveles") {
                            popUpTo("nivel1") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = botonColor,
                        contentColor = textoColor
                    ),
                    modifier = Modifier.padding(8.dp)
                ) { Text("⬅️ Volver", fontFamily = FontFamily.Monospace) }
            }

            if (nivelCompletado) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        onClick = {
                            mapa = generarMapaNivel1()
                            jugadorPos = Pair(1, 1)
                            llaveTomada = false
                            mostrarPregunta = false
                            puertaAbierta = false
                            mensaje = ""
                            navController.navigate("niveles") {
                                popUpTo("nivel1") { inclusive = true }
                            }
                        }
                    ) { Text("⬅️ Volver") }

                    Button(
                        onClick = { navController.navigate("nivel2") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = botonColor,
                            contentColor = textoColor
                        )
                    ) { Text("Siguiente ➡️", fontFamily = FontFamily.Monospace) }
                }
            }
        }
    }
}

@Composable
fun InstruccionesNivel1(
    isDark: Boolean,
    textoColor: Color,
    fondoColor: Color,
    botonColor: Color,
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
                "📘 Instrucciones del Nivel 1",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                        text = """
                🔹 En este nivel aprenderás sumas y restas básicas.
        
                🔹 Cuando tomes la llave, aparecerá una pregunta.
        
                ⭐ SUMAS
                5 + 3 = 8
                2 + 7 = 9
        
                ⭐ RESTAS
                7 - 2 = 5      (restamos un número más pequeño del más grande)
                2 - 7 = -5     (si restamos un número más grande del más pequeño, obtenemos un número negativo)
                3 - 10 = -7    (los números negativos representan cantidades "por debajo de cero")
        
                🔹 Recuerda:
                - Una resta negativa indica que hemos quitado más de lo que teníamos.
                - Los números negativos se leen como "menos cinco", "menos siete", etc.
        
                🔹 Si fallas:
                El nivel se reinicia.
        
                🔹 Si aciertas:
                Obtienes la llave y la puerta se abrirá.
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
                    containerColor = botonColor,
                    contentColor = textoColor
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Text("¡Jugar!", fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun PreguntaMatematicaNivel1(textoColor: Color, isDark: Boolean, onRespuesta: (Boolean) -> Unit) {

    val operadores = listOf("+", "-")

    fun generarPregunta(): Triple<Int, Int, String> {
        val a = (1..20).random()
        val b = (1..20).random()
        val op = operadores.random()
        return Triple(a, b, op)
    }

    val (a, b, op) = generarPregunta()

    val resultadoCorrecto = when (op) {
        "+" -> a + b
        "-" -> a - b
        else -> 0
    }

    val opciones = mutableSetOf(resultadoCorrecto)
    while (opciones.size < 3) opciones.add(resultadoCorrecto + listOf(-5, -3, -1, 1, 3, 5).random())

    val listaOpciones = opciones.shuffled()

    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            "Resuelve: $a $op $b",
            color = textoColor,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(8.dp))

        listaOpciones.forEach { opcion ->
            Button(
                onClick = {
                    // reproducir sonido y notificar al padre
                    if (opcion == resultadoCorrecto) {
                        SoundManager.playCorrectSound(context)
                        onRespuesta(true)
                    } else {
                        SoundManager.playWrongSound(context)
                        onRespuesta(false)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF4CAF50) else Color(0xFF00FF00),
                    contentColor = textoColor
                ),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(opcion.toString(), color = textoColor)
            }
        }
    }
}

fun generarMapaNivel1(): List<List<String>> {
    return listOf(
        listOf("W","W","W","W","W","W","W"),
        listOf("W"," ","W","K"," "," ","W"),
        listOf("W"," ","W","W","W"," ","W"),
        listOf("W"," "," "," "," "," ","W"),
        listOf("W"," ","W","W","W","W","W"),
        listOf("W"," "," "," "," ","G","W"),
        listOf("W","W","W","W","W","W","W")
    )
}
