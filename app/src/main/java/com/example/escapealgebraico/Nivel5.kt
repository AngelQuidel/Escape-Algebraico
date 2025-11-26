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
import androidx.compose.ui.*
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
import kotlin.math.round
import com.example.escapealgebraico.utils.ProgressManager

@Composable
fun PantallaNivel5(navController: NavHostController) {
    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFFFF3CD)
    val textoColor = if (isDark) Color.White else Color.Black

    var mapa by remember { mutableStateOf(generarMapaNivel5()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llavesObtenidas by remember { mutableStateOf(0) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var puertaAbierta by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarInstrucciones by remember { mutableStateOf(NivelState.mostrarInstruccionesNivel5) }
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
            InstruccionesNivel5(
                textoColor = textoColor,
                fondoColor = fondoColor,
                onCerrar = {
                    mostrarInstrucciones = false
                    NivelState.mostrarInstruccionesNivel5 = false
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
                    ProgressManager.guardarNivel(context, 5)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fondoColor)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 80.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(12.dp))

                Text(
                    "🌈 Nivel 5: ¡Desafío Final! 🔢",
                    color = textoColor,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

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
                                Text(text = emoji, fontSize = 24.sp) // Mapa compacto
                            }
                        }
                    }
                }

                Text(
                    "Llaves obtenidas: $llavesObtenidas / 4",
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

                if (!nivelCompletado) {
                    if (mostrarPregunta) {
                        key(intentoPregunta) {
                            PreguntaMatematicaNivel5(
                                textoColor = textoColor,
                                isDark = isDark,
                                onRespuesta = { correcta ->
                                    if (correcta) {
                                        SoundManager.playCorrectSound(context)
                                        llavesObtenidas++
                                        mensaje = "✅ ¡Muy bien! Has conseguido una llave ($llavesObtenidas/4)."
                                        mostrarPregunta = false
                                        if (llavesObtenidas >= 4) {
                                            puertaAbierta = true
                                            mensaje = "🔓 ¡Tienes todas las llaves!"
                                        }
                                    } else {
                                        SoundManager.playWrongSound(context)
                                        vidas--
                                        if (vidas <= 0) {
                                            // Reiniciar nivel
                                            mapa = generarMapaNivel5()
                                            jugadorPos = Pair(1, 1)
                                            llavesObtenidas = 0
                                            puertaAbierta = false
                                            mostrarPregunta = false
                                            mensaje = "💔 ¡Sin vidas! Nivel reiniciado."
                                            mostrarInstrucciones = true
                                            vidas = 3
                                            intentoPregunta = 0
                                            NivelState.mostrarInstruccionesNivel5 = true
                                        } else {
                                            mensaje = "❌ Incorrecto. Pierdes 1 vida."
                                            intentoPregunta++ // Cambiar pregunta
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
                                            if (puertaAbierta) {
                                                mensaje = "🎉 ¡Completaste el nivel final! 🌟"
                                                nivelCompletado = true
                                            } else {
                                                mensaje = "🚪 Necesitas 4 llaves."
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                if (!nivelCompletado && !mostrarPregunta) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            mapa = generarMapaNivel5()
                            jugadorPos = Pair(1, 1)
                            llavesObtenidas = 0
                            puertaAbierta = false
                            mostrarPregunta = false
                            mensaje = ""
                            vidas = 3
                            intentoPregunta = 0
                            navController.navigate("niveles") {
                                popUpTo("nivel5") { inclusive = true }
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
                }

                if (nivelCompletado) {
                    ProgressManager.guardarNivel(context, 5)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {

                        Button(
                            onClick = {
                                navController.navigate("niveles") {
                                    popUpTo("nivel5") { inclusive = true }
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
                                navController.navigate("pantallaFinal")
                            },
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006400),
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Yellow)
                        ) {
                            Text("Sorpresa ➡️", fontFamily = FontFamily.Monospace, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstruccionesNivel5(
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

            Spacer(Modifier.height(24.dp))

            Text(
                "🌟 Nivel 5: Potencias y Raíces 🌟",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                "En este nivel usarás todo lo que aprendiste:\n\n" +
                        "👉 Primero se hacen las multiplicaciones y divisiones.\n" +
                        "👉 Después las sumas y restas, de izquierda a derecha.\n\n" +
                        "📏 Si te salen números con muchos decimales (como 3.44444), " +
                        "solo redondéalos al número más cercano, por ejemplo 3.444 → 3 y 3.6 → 4.\n\n" +
                        "🎯 Tienes que conseguir las 4 llaves para abrir la puerta final.\n\n" +
                        "Ten paciencia, tú puedes. Recuerda que puedes usar una calculadora si lo necesitas.\n\n" +
                        "¡Piensa bien y demuestra todo lo que sabes! 🧩",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 24.dp)
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
                Text("¡Desafío Final!", fontFamily = FontFamily.Monospace, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun PreguntaMatematicaNivel5(textoColor: Color, isDark: Boolean, onRespuesta: (Boolean) -> Unit
) {
    val operadores = listOf("+", "-", "×", "÷")

    val nums = List(4) {
        val valor: Double = when ((1..3).random()) {
            1 -> (1..9).random().toDouble()
            2 -> {
                val entero = (1..9).random()
                val decimal = (0..9).random() / 10.0
                entero + decimal
            }
            else -> {
                val numerador = (1..9).random().toDouble()
                val denominador = listOf(2.0, 3.0, 4.0).random()
                numerador / denominador
            }
        }
        round(valor * 10) / 10.0
    }

    val ops = List(3) { operadores.random() }

    val expresion = "${String.format("%.1f", nums[0])} ${ops[0]} " +
            "${String.format("%.1f", nums[1])} ${ops[1]} " +
            "${String.format("%.1f", nums[2])} ${ops[2]} " +
            "${String.format("%.1f", nums[3])}"

    fun calcularResultado(): Double {
        val lista = mutableListOf<Any>()
        for (i in nums.indices) {
            lista.add(nums[i])
            if (i < ops.size) lista.add(ops[i])
        }

        var i = 0
        while (i < lista.size) {
            if (lista[i] == "×" || lista[i] == "÷") {
                val op = lista[i] as String
                val a = lista[i - 1] as Double
                val b = lista[i + 1] as Double
                val resultado = if (op == "×") a * b else a / b
                lista[i - 1] = resultado
                lista.removeAt(i)
                lista.removeAt(i)
                i--
            }
            i++
        }

        var resultado = lista[0] as Double
        i = 1
        while (i < lista.size) {
            val op = lista[i] as String
            val b = lista[i + 1] as Double
            when (op) {
                "+" -> resultado += b
                "-" -> resultado -= b
            }
            i += 2
        }

        return round(resultado * 10) / 10.0
    }

    val resultadoCorrecto = calcularResultado()
    val opciones = mutableSetOf(resultadoCorrecto)
    while (opciones.size < 3) {
        val offset = listOf(-1.5, -0.7, -0.2, 0.3, 0.8, 1.4, 2.0).random()
        val opcionIncorrecta = round((resultadoCorrecto + offset) * 10) / 10.0
        if (opcionIncorrecta != resultadoCorrecto) {
            opciones.add(opcionIncorrecta)
        }
    }
    val listaOpciones = opciones.shuffled()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Resuelve: $expresion", color = textoColor, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text("(Redondea el resultado final al primer decimal)", color = textoColor.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(8.dp))

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

fun generarMapaNivel5(): List<List<String>> {
    return listOf(
        listOf("W","W","W","W","W","W","W","W","W","W","W","W","W"),
        listOf("W"," "," "," "," "," "," "," "," ","W","K"," ","W"),
        listOf("W","W","W"," ","W","W","W"," ","W","W","W"," ","W"),
        listOf("W","K","W"," "," ","K","W"," "," "," ","W"," ","W"),
        listOf("W"," ","W"," ","W","W","W","W","W"," ","W"," ","W"),
        listOf("W"," ","W"," "," "," "," "," "," "," "," "," ","W"),
        listOf("W"," ","W","W","W","W","W","W","W"," ","W"," ","W"),
        listOf("W"," "," "," "," "," "," "," "," "," ","W"," ","W"),
        listOf("W","W"," ","W","W","W","W","W","W"," ","W","W","W"),
        listOf("W"," "," "," "," "," "," ","K","W"," "," ","G","W"),
        listOf("W","W","W","W","W","W","W","W","W","W","W","W","W")
    )
}
