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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlin.math.round
import com.example.escapealgebraico.utils.ProgressManager

@Composable
fun PantallaNivel5(navController: NavHostController) {
    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val fondoClaro = Color(0xFFFFF4CC)
    val fondoOscuro = Color(0xFF121212)
    val fondoColor = if (isDark) fondoOscuro else fondoClaro
    val textoColor = if (isDark) Color.White else Color.Black
    val botonPrincipalColor = if (isDark) Color(0xFFFFD600) else Color(0xFFFFC300)
    val botonOpcionColor = Color(0xFF00FF00)
    val botonFinalColor = Color(0xFF00FF00)

    var mostrarExplicacion by remember { mutableStateOf(true) }
    var mapa by remember { mutableStateOf(generarMapaNivel5()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llavesObtenidas by remember { mutableStateOf(0) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var pasoDesbloqueado by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var nivelCompletado by remember { mutableStateOf(false) }

    if (mostrarExplicacion) {
        InstruccionesNivel5(
            textoColor = textoColor,
            fondoColor = fondoColor,
            botonColor = botonPrincipalColor,
            onCerrar = { mostrarExplicacion = false }
        )
    }
    else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(fondoColor)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(fondoColor)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        "🌈 Nivel 5: ¡Desafío Final! 🔢",
                        color = textoColor,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )

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
                                    Text(text = emoji, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                                }
                            }
                        }
                    }

                    Text(
                        "Llaves obtenidas: $llavesObtenidas / 4",
                        color = textoColor,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(mensaje, color = textoColor, fontFamily = FontFamily.Monospace)

                    if (mostrarPregunta) {
                        val context = LocalContext.current
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
                                        pasoDesbloqueado = true
                                        mensaje = "🔓 ¡Tienes todas las llaves!"
                                    }
                                } else {
                                    SoundManager.playWrongSound(context)
                                    mapa = generarMapaNivel5()
                                    jugadorPos = Pair(1, 1)
                                    llavesObtenidas = 0
                                    pasoDesbloqueado = false
                                    mostrarPregunta = false
                                    mensaje = "❌ Fallaste. Volviste al inicio."
                                    mostrarExplicacion = true
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Button(
                            onClick = {
                                navController.navigate("niveles") {
                                    popUpTo("nivel5") { inclusive = true }
                                }
                            },
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF00),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                        }

                        if (nivelCompletado) {
                            ProgressManager.guardarNivel(context, 5)
                            Button(
                                onClick = {
                                    navController.navigate("pantallaFinal")
                                },
                                modifier = Modifier.padding(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = botonFinalColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Finalizar", fontFamily = FontFamily.Monospace)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun InstruccionesNivel5(
    textoColor: Color,
    fondoColor: Color,
    botonColor: Color,
    onCerrar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(24.dp))

        Text(
            "🌟 Nivel 5: Potencias y Raíces 🌟",
            color = textoColor,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
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
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onCerrar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = botonColor,
                    contentColor = textoColor
                )
            ) {
                Text("¡Comenzar!", fontFamily = FontFamily.Monospace)
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
