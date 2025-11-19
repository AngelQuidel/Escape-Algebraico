package com.example.escapealgebraico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.escapealgebraico.utils.ProgressManager
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun PantallaNivel2(navController: NavHostController) {

    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFD1F7C4)
    val textoColor = if (isDark) Color.White else Color.Black

    var mapa by remember { mutableStateOf(generarMapaNivel2()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var tieneLlave by remember { mutableStateOf(false) }
    var pasoDesbloqueado by remember { mutableStateOf(false) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var nivelCompletado by remember { mutableStateOf(false) }

    var mensaje by remember {
        mutableStateOf("Encuentra la llave 🔑 resolviendo multiplicación o división")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "🔥 Nivel 2: Multiplica o Divide para Avanzar",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (y in mapa.indices) {
                    Row {
                        for (x in mapa[y].indices) {

                            val emoji = when {
                                jugadorPos.first == x && jugadorPos.second == y -> "🦖"
                                mapa[y][x] == "W" -> "🧱"
                                mapa[y][x] == "G" -> if (pasoDesbloqueado) "🍖" else "🚪"
                                mapa[y][x] == "K" && !tieneLlave -> "🔑"
                                else -> "🟩"
                            }

                            Text(
                                text = emoji,
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                color = textoColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                mensaje,
                color = textoColor,
                fontFamily = FontFamily.Monospace
            )

            // ---------------------------
            // PREGUNTA
            // ---------------------------

            if (mostrarPregunta) {

                PreguntaMatematicaNivel2(
                    textoColor = textoColor,
                    isDark = isDark,
                    onRespuesta = { correcta ->
                        if (correcta) {
                            SoundManager.playCorrectSound(context)
                            tieneLlave = true
                            pasoDesbloqueado = true
                            mostrarPregunta = false
                            mensaje = "✅ ¡Correcto! Obtuviste la llave del Nivel 2"
                        } else {
                            SoundManager.playWrongSound(context)
                            mostrarPregunta = false
                            mensaje = "❌ Incorrecto. Regresaste al inicio."
                            jugadorPos = Pair(1, 1)
                            tieneLlave = false
                            pasoDesbloqueado = false
                        }
                    }
                )

            } else if (!nivelCompletado) {

                // ---------------------------
                // CONTROLES DE MOVIMIENTO
                // ---------------------------
                ControlesMovimiento(
                    onMove = { dx, dy ->
                        val nuevaPos = Pair(jugadorPos.first + dx, jugadorPos.second + dy)

                        if (puedeMoverse(mapa, nuevaPos)) {

                            jugadorPos = nuevaPos
                            val (x, y) = nuevaPos

                            when (mapa[y][x]) {

                                "K" -> if (!tieneLlave) {
                                    mostrarPregunta = true
                                    mensaje = "🔢 Resuelve para obtener la llave"
                                }

                                "G" -> {
                                    if (pasoDesbloqueado) {
                                        nivelCompletado = true
                                        mensaje = "🎉 ¡Has completado el Nivel 2!"
                                    } else {
                                        mensaje = "🚪 La puerta está cerrada. Falta la llave."
                                    }
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------------------
            // BOTONES CUANDO COMPLETA
            // ---------------------------

            if (nivelCompletado) {

                ProgressManager.guardarNivel(LocalContext.current, 3)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Button(
                            onClick = {
                                navController.navigate("niveles") {
                                    popUpTo("nivel2") { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF00),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                navController.navigate("nivel3")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF00),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Siguiente ➡️", fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // ---------------------------
        // BOTÓN VOLVER ABAJO (si no ha ganado)
        // ---------------------------
        if (!nivelCompletado) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    onClick = {
                        navController.navigate("niveles") {
                            popUpTo("nivel2") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    )
                ) {
                    Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun PreguntaMatematicaNivel2(textoColor: Color, isDark: Boolean, onRespuesta: (Boolean) -> Unit) {

    val operador = if ((0..1).random() == 0) "*" else "/"

    var a = (2..10).random()
    var b = (2..10).random()

    if (operador == "/") {
        b = (2..10).random()
        a = b * (2..10).random() // resultado entero SIEMPRE
    }

    val correcta = if (operador == "*") a * b else a / b
    val simbolo = if (operador == "*") "×" else "÷"

    val explicacion = if (operador == "*") {
        "🧮 Multiplicar es sumar un número varias veces.\nEjemplo: 3 × 2 = 6 (3 + 3)."
    } else {
        "🍎 Dividir es repartir por igual.\nEjemplo: 6 ÷ 3 = 2 (6 manzanas entre 3 personas)."
    }

    val opciones = mutableSetOf(correcta)
    while (opciones.size < 3) opciones.add(correcta + (-3..3).random())
    val listaOpciones = opciones.shuffled()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {

        Text(explicacion, fontFamily = FontFamily.Monospace, color = textoColor)
        Spacer(Modifier.height(6.dp))

        Text("¿Cuánto es $a $simbolo $b?", fontFamily = FontFamily.Monospace, color = textoColor)

        Spacer(Modifier.height(8.dp))

        listaOpciones.forEach { opcion ->
            Button(
                onClick = { onRespuesta(opcion == correcta) },
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
