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
fun PantallaNivel3(navController: NavHostController) {

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFCCE5FF)
    val textoColor = if (isDark) Color.White else Color.Black

    var mapa by remember { mutableStateOf(generarMapaNivel3()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llavesObtenidas by remember { mutableStateOf(0) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var pasoDesbloqueado by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarInstrucciones by remember { mutableStateOf(NivelState.mostrarInstruccionesNivel3) }
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
            InstruccionesNivel3(
                isDark = isDark,
                textoColor = textoColor,
                fondoColor = fondoColor,
                onCerrar = { 
                    mostrarInstrucciones = false
                    NivelState.mostrarInstruccionesNivel4 = false
                }
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
                    ProgressManager.guardarNivel(context, 3)
                    Toast.makeText(context, "Progreso guardado", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000080), // Azul oscuro diferente
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
                    .padding(top = 80.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "🦖 Nivel 3: Operaciones Combinadas",
                    color = textoColor,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(10.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    for (y in mapa.indices) {
                        Row {
                            for (x in mapa[y].indices) {
                                val emoji = when {
                                    jugadorPos.first == x && jugadorPos.second == y -> "🦖"
                                    mapa[y][x] == "W" -> "🧱"
                                    mapa[y][x] == "G" -> if (pasoDesbloqueado) "🍖" else "🚪"
                                    mapa[y][x] == "K" -> "🔑"
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

                Text(
                    "Llaves obtenidas: $llavesObtenidas / 2",
                    color = textoColor,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    mensaje, 
                    color = textoColor, 
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Controles solo si NO se completó el nivel
                if (!nivelCompletado) {
                    if (mostrarPregunta) {
                        key(intentoPregunta) {
                            PreguntaMatematicaNivel3(
                                textoColor = textoColor,
                                isDark = isDark,
                                onRespuesta = { correcta ->
                                    if (correcta) {
                                        SoundManager.playCorrectSound(context)
                                        llavesObtenidas++
                                        mostrarPregunta = false

                                        mensaje = "🔑 ¡Correcto! Obtuviste una llave ($llavesObtenidas / 2)"

                                        if (llavesObtenidas == 2) {
                                            pasoDesbloqueado = true
                                            mensaje = "🔓 ¡La puerta está abierta!"
                                        }

                                    } else {
                                        SoundManager.playWrongSound(context)
                                        vidas--
                                        if (vidas <= 0) {
                                            mapa = generarMapaNivel3()
                                            jugadorPos = Pair(1, 1)
                                            llavesObtenidas = 0
                                            pasoDesbloqueado = false
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
                    } else {
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
                                            if (pasoDesbloqueado) {
                                                nivelCompletado = true
                                                mensaje = "🎉 ¡Ganaste el Nivel 3! 🍖"
                                                ProgressManager.guardarNivel(context, 4)
                                            } else {
                                                mensaje = "🚪 Necesitas 2 llaves."
                                            }
                                        }
                                    }
                                }
                            }
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                mapa = generarMapaNivel3()
                                jugadorPos = Pair(1, 1)
                                llavesObtenidas = 0
                                pasoDesbloqueado = false
                                mostrarPregunta = false
                                mensaje = ""
                                vidas = 3
                                intentoPregunta = 0
                                mostrarInstrucciones = true

                                navController.navigate("niveles") {
                                    popUpTo("nivel3") { inclusive = true }
                                }

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006400),
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Yellow),
                            modifier = Modifier.padding(bottom = 50.dp)
                        ) {
                            Text("⬅️ Volver", fontFamily = FontFamily.Monospace, fontSize = 20.sp)
                        }
                    }
                }

                if (nivelCompletado) {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                            onClick = {

                                mapa = generarMapaNivel3()
                                jugadorPos = Pair(1, 1)
                                llavesObtenidas = 0
                                pasoDesbloqueado = false
                                mostrarPregunta = false
                                mensaje = ""
                                vidas = 3
                                intentoPregunta = 0
                                mostrarInstrucciones = true

                                navController.navigate("niveles") {
                                    popUpTo("nivel3") { inclusive = true }
                                }
                            },
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
                                NivelState.mostrarInstruccionesNivel4 = true
                                navController.navigate("nivel4")
                            },
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

@Composable
fun InstruccionesNivel3(
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
                .padding(bottom = 90.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Text(
                "📘 Instrucciones del Nivel 3",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = """
                    🔹 En este nivel debes obtener 2 llaves para abrir la puerta.
                    
                    🔹 Cada vez que tomes una llave, aparecerá una pregunta matemática.

                    🔹 Son operaciones combinadas, recuerda la prioridad:
                        • Multiplicación y división primero  
                        • Luego suma y resta  
                        • Siempre de izquierda a derecha  
                    
                    🔹 Si fallas una pregunta:
                        • Pierdes una vida (tienes 3)
                        • La pregunta cambiará
                        • Si pierdes todas las vidas, reinicias el nivel desde 0

                    🔹 Cuando consigas las 2 llaves:
                        • La puerta se abrirá  
                        • Puedes llegar al final  
                """.trimIndent(),
                color = textoColor,
                textAlign = TextAlign.Left,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            Spacer(Modifier.height(24.dp))
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
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

fun evaluarConPrioridad(numsOriginal: List<Int>, opsOriginal: List<String>): Int {
    val nums = numsOriginal.map { it.toDouble() }.toMutableList()
    val ops = opsOriginal.toMutableList()

    var i = 0
    while (i < ops.size) {
        when (ops[i]) {
            "×" -> {
                val res = nums[i] * nums[i + 1]
                nums[i] = res
                nums.removeAt(i + 1)
                ops.removeAt(i)
            }
            "÷" -> {
                val res = nums[i] / nums[i + 1]
                nums[i] = res
                nums.removeAt(i + 1)
                ops.removeAt(i)
            }
            else -> i++
        }
    }

    var resultado = nums[0]
    for (j in ops.indices) {
        val b = nums[j + 1]
        resultado = when (ops[j]) {
            "+" -> resultado + b
            "-" -> resultado - b
            else -> resultado
        }
    }

    return resultado.toInt()
}

@Composable
fun PreguntaMatematicaNivel3(textoColor: Color, isDark: Boolean, onRespuesta: (Boolean) -> Unit) {
    val operadoresSinDivision = listOf("+", "-", "×")

    fun generarPregunta(): Triple<List<Int>, List<String>, Int> {
        val divIndex = (0..2).random()
        val ops = MutableList(3) { "" }
        ops[divIndex] = "÷"
        for (i in 0..2) {
            if (i != divIndex) ops[i] = operadoresSinDivision.random()
        }
        val nums = MutableList(4) { (2..20).random() }
        val divisor = (2..10).random()
        val resultadoEntero = (2..12).random()
        nums[divIndex] = divisor * resultadoEntero
        nums[divIndex + 1] = divisor

        val resultadoCorrecto = evaluarConPrioridad(nums, ops)
        return Triple(nums, ops, resultadoCorrecto)
    }

    val (nums, ops, resultadoCorrecto) = generarPregunta()
    val expresion = "${nums[0]} ${ops[0]} ${nums[1]} ${ops[1]} ${nums[2]} ${ops[2]} ${nums[3]}"

    val opciones = mutableSetOf(resultadoCorrecto)
    val desviaciones = listOf(-5, -3, -1, 1, 3, 5)
    while (opciones.size < 3) opciones.add(resultadoCorrecto + desviaciones.random())
    val listaOpciones = opciones.shuffled()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            "Resuelve: $expresion",
            color = textoColor,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(8.dp))

        listaOpciones.forEach { opcion ->
            Button(
                onClick = { onRespuesta(opcion == resultadoCorrecto) },
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

fun generarMapaNivel3(): List<List<String>> {
    return listOf(
        listOf("W","W","W","W","W","W","W","W"),
        listOf("W"," "," "," ","W","K"," ","W"),
        listOf("W","W","W"," ","W","W"," ","W"),
        listOf("W","K","W"," "," "," "," ","W"),
        listOf("W"," ","W","W"," ","W","W","W"),
        listOf("W"," "," "," "," "," ","G","W"),
        listOf("W","W","W","W","W","W","W","W")
    )
}
