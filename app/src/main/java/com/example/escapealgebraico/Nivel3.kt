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
fun PantallaNivel3(navController: NavHostController) {

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFCCE5FF)
    val textoColor = if (isDark) Color.White else Color.Black
    val botonColor = Color(0xFF00FF00)

    var mapa by remember { mutableStateOf(generarMapaNivel3()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llavesObtenidas by remember { mutableStateOf(0) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var pasoDesbloqueado by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarInstrucciones by remember { mutableStateOf(NivelState.mostrarInstruccionesNivel3) }
    var nivelCompletado by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
    ) { innerPadding ->

        if (mostrarInstrucciones) {

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(fondoColor)
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
                    🔹 En este nivel debes obtener 2 llaves** para abrir la puerta.
                    
                    🔹 Cada vez que tomes una llave, aparecerá una **pregunta matemática.

                    🔹 Son operaciones combinadas**, recuerda la prioridad:
                        • Multiplicación y división primero  
                        • Luego suma y resta  
                        • Siempre de izquierda a derecha  
                    
                    🔹 Si fallas una pregunta:
                        • Se reinicia el nivel  
                        • Pierdes las llaves  
                        • Debes volver a comenzar  
                    
                    🔹 Cuando consigas las 2 llaves:
                        • La puerta se abrirá  
                        • Puedes llegar al final  
                    """.trimIndent(),
                    color = textoColor,
                    textAlign = TextAlign.Left,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(24.dp)
                )

                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            mostrarInstrucciones = false
                            NivelState.mostrarInstruccionesNivel4 = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = botonColor,
                            contentColor = textoColor
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("¡Entendido!", fontFamily = FontFamily.Monospace)
                    }
                }
            }

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

            Spacer(Modifier.height(16.dp))

            Text(
                "🦖 Nivel 3: Operaciones Combinadas",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge
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
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize
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

            Text(mensaje, color = textoColor, fontFamily = FontFamily.Monospace)

            Spacer(Modifier.height(16.dp))

            if (mostrarPregunta) {
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
                            mapa = generarMapaNivel3()
                            jugadorPos = Pair(1, 1)
                            llavesObtenidas = 0
                            pasoDesbloqueado = false
                            mostrarPregunta = false
                            mensaje = "❌ Fallaste. Nivel reiniciado."
                            mostrarInstrucciones = true
                        }
                    }
                )
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
            }

            Spacer(Modifier.height(20.dp))

            if (nivelCompletado) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {

                            mapa = generarMapaNivel3()
                            jugadorPos = Pair(1, 1)
                            llavesObtenidas = 0
                            pasoDesbloqueado = false
                            mostrarPregunta = false
                            mensaje = ""
                            mostrarInstrucciones = true

                            navController.navigate("niveles") {
                                popUpTo("nivel3") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = botonColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = {
                            NivelState.mostrarInstruccionesNivel4 = true
                            navController.navigate("nivel4")
                        }
                    ) {
                        Text("➡️ Siguiente", fontFamily = FontFamily.Monospace)
                    }
                }
            } else {
                Button(
                    onClick = {

                        mapa = generarMapaNivel3()
                        jugadorPos = Pair(1, 1)
                        llavesObtenidas = 0
                        pasoDesbloqueado = false
                        mostrarPregunta = false
                        mensaje = ""
                        mostrarInstrucciones = true

                        navController.navigate("niveles") {
                            popUpTo("nivel3") { inclusive = true }
                        }

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = botonColor,
                        contentColor = textoColor
                    ),
                    modifier = Modifier.padding(bottom = 50.dp)
                ) {
                    Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                }

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
            if (i != divIndex) {
                ops[i] = operadoresSinDivision.random()
            }
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

    while (opciones.size < 3) {
        opciones.add(resultadoCorrecto + desviaciones.random())
    }

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
