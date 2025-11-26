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
fun PantallaNivel1(navController: NavHostController) {

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFFFF3CD)
    val textoColor = if (isDark) Color.White else Color.Black

    var mapa by remember { mutableStateOf(generarMapaNivel1()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llaveTomada by remember { mutableStateOf(false) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var puertaAbierta by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
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
            InstruccionesNivel1(
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
            // Botón Guardar Progreso (Arriba a la izquierda, fijo)
            Button(
                onClick = {
                    ProgressManager.guardarNivel(context, 1)
                    Toast.makeText(context, "Progreso guardado", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000080), // Azul oscuro
                    contentColor = Color.White
                ),
                border = BorderStroke(2.dp, Color.Yellow)
            ) {
                Text("💾 Guardar", fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            }

            // Vidas (Arriba a la derecha, fijo)
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
                    .padding(top = 80.dp, bottom = 24.dp), // Espacio superior para botones y vidas
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "🦖 Nivel 1: Sumas y Restas",
                    color = textoColor,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(10.dp))

                // MAPA Compacto
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
                                    fontSize = 24.sp 
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

                // PREGUNTA o CONTROLES
                if (mostrarPregunta) {
                    key(intentoPregunta) {
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
                                    vidas--
                                    if (vidas <= 0) {
                                        mapa = generarMapaNivel1()
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

                if (!nivelCompletado) {
                    Button(
                        onClick = {
                            mapa = generarMapaNivel1()
                            jugadorPos = Pair(1, 1)
                            llaveTomada = false
                            mostrarPregunta = false
                            puertaAbierta = false
                            mensaje = ""
                            vidas = 3
                            intentoPregunta = 0
                            navController.navigate("niveles") {
                                popUpTo("nivel1") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400),
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(2.dp, Color.Yellow),
                        modifier = Modifier.padding(8.dp)
                    ) { Text("⬅️ Volver", fontFamily = FontFamily.Monospace, fontSize = 20.sp) }
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
                                    popUpTo("nivel1") { inclusive = true }
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

                        Button(
                            onClick = {
                                NivelState.mostrarInstruccionesNivel5 = true
                                navController.navigate("nivel2")
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
                    Spacer(Modifier.height(50.dp))
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
                    🔹 En este nivel aprenderás a trabajar con sumas y restas, incluyendo números positivos y negativos.
                
                    🔸 ¿Qué es sumar?
                    Sumar es juntar cantidades.
                    Ejemplos:
                    5 + 3 = 8
                    2 + 7 = 9
                
                    🔸 ¿Qué es restar?
                    Restar es quitar una cantidad de otra.
                    Ejemplos:
                    7 - 2 = 5      (restamos un número más pequeño de uno más grande)
                
                    🔸 ¿Qué pasa si restamos un número más grande?
                    Cuando quitamos más de lo que tenemos, el resultado es un número negativo.
                    Ejemplos:
                    2 - 7 = -5
                    3 - 10 = -7
                
                    🔸 ¿Qué son los números negativos?
                    Son valores "por debajo de cero".
                    Se leen como “menos cinco”, “menos siete”, etc.
                
                    🔸 Sumar números negativos
                    - Si ambos números son negativos, el resultado también es negativo:
                      -4 + (-3) = -7
                
                    - Si un número es positivo y el otro negativo, se restan sus valores:
                      8 + (-3) = 5
                      4 + (-9) = -5
                
                    🔸 Restar números negativos
                    Restar un número negativo es lo mismo que sumar su positivo:
                      6 - (-2) = 8
                      -3 - (-4) = 1
                
                    🔸 Resumen importante:
                    - Quitar más de lo que tenemos => resultado negativo.
                    - Restar un número negativo es sumar.
                    - Los signos importan: + suma, – resta.
                
                    🔹 Cuando tomes la llave, aparecerá una pregunta.
                    🔹 Si fallas: el nivel se reinicia.
                    🔹 Si aciertas: obtienes la llave y la puerta se abrirá.
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

                    if (opcion == resultadoCorrecto) {
                        SoundManager.playCorrectSound(context)
                        onRespuesta(true)
                    } else {
                        SoundManager.playWrongSound(context)
                        onRespuesta(false)
                    }
                },
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
