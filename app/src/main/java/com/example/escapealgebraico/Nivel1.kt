package com.example.escapealgebraico

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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

@Composable
fun PantallaNivel1(navController: NavHostController) {

    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFD1F7C4)
    val textoColor = if (isDark) Color.White else Color.Black

    var mapa by remember { mutableStateOf(generarMapaNivel1()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var tieneLlave by remember { mutableStateOf(false) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var pasoDesbloqueado by remember { mutableStateOf(false) }
    var nivelCompletado by remember { mutableStateOf(false) }

    var mensaje by remember {
        mutableStateOf("Encuentra la llave 🔑 para abrir la puerta 🚪")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp), // dejar espacio para los botones inferiores
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                "🦖 Nivel 1: Consigue la llave y llega a la carne 🍖",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mapa (emojis)
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
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                mensaje,
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (mostrarPregunta) {
                PreguntaMatematica(
                    textoColor = textoColor,
                    isDark = isDark,
                    onRespuesta = { correcta, operador ->
                        if (correcta) {
                            SoundManager.playCorrectSound(context)
                            tieneLlave = true
                            pasoDesbloqueado = true
                            mostrarPregunta = false
                            mensaje = "✅ ¡Correcto! Has obtenido la llave"
                        } else {
                            SoundManager.playWrongSound(context)
                            mensaje = if (operador == "+") {
                                "❌ Casi lo logras. Revisa el ejemplo y vuelve a intentarlo."
                            } else {
                                "❌ No fue correcto. Repasa el ejemplo y prueba otra vez."
                            }
                            mostrarPregunta = false
                            tieneLlave = false
                            pasoDesbloqueado = false
                            jugadorPos = Pair(1, 1)
                            mapa = generarMapaNivel1()
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
                                "K" -> if (!tieneLlave) {
                                    mostrarPregunta = true
                                    mensaje = "🔢 Resuelve la operación para conseguir la llave"
                                }

                                "G" -> {
                                    if (pasoDesbloqueado) {
                                        mensaje = "🎉 ¡Ganaste el Nivel 1!"
                                        nivelCompletado = true

                                        ProgressManager.guardarNivel(context, 2)
                                    } else {
                                        mensaje =
                                            "🚪 La puerta está bloqueada. Necesitas la llave 🔑"
                                    }
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!nivelCompletado) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        navController.navigate("niveles") {
                            popUpTo("nivel1") { inclusive = true }
                        }
                    },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                }
            }

        } else {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            navController.navigate("niveles") {
                                popUpTo("nivel1") { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF00),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = { navController.navigate("nivel2") },
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
}

@Composable
fun PreguntaMatematica(
    onRespuesta: (Boolean, String) -> Unit,
    textoColor: Color,
    isDark: Boolean
) {
    val operador = if ((0..1).random() == 0) "+" else "-"
    val a = (1..10).random()
    val b = (1..10).random()
    val correcta = if (operador == "+") a + b else a - b
    val pregunta = "¿Cuánto es $a $operador $b?"

    val ejemploA = (3..9).random()
    val ejemploB = (1..5).random()
    val ejemploTexto =
        if (operador == "+")
            "✨ Imagina que tienes $ejemploA dulces y te dan $ejemploB más."
        else
            "🍎 Imagina que tienes $ejemploA manzanas y te quitan $ejemploB."
    val ejemploResultado =
        if (operador == "+") ejemploA + ejemploB else ejemploA - ejemploB

    // Opciones: mantengo el tamaño original (no fillMaxWidth), con padding
    val opciones = listOf(correcta, correcta + 1, correcta - 1).shuffled()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(pregunta, color = textoColor, fontFamily = FontFamily.Monospace)

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            "$ejemploTexto\n➡️ Resultado del ejemplo: $ejemploResultado",
            color = textoColor,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        opciones.forEach { opcion ->
            Button(
                onClick = { onRespuesta(opcion == correcta, operador) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF4CAF50) else Color(0xFF00FF00),
                    contentColor = textoColor
                ),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(opcion.toString(), color = textoColor, fontFamily = FontFamily.Monospace)
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
