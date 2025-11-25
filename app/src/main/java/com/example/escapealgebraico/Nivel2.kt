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
fun PantallaNivel2(navController: NavHostController) {

    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFD1F7C4)
    val textoColor = if (isDark) Color.White else Color.Black
    val botonColor = if (isDark) Color(0xFF4CAF50) else Color(0xFF00FF00)

    var mostrarInstrucciones by remember { mutableStateOf(true) }

    if (mostrarInstrucciones) {
        InstruccionesNivel2(
            isDark = isDark,
            textoColor = textoColor,
            fondoColor = fondoColor,
            botonColor = botonColor
        ) {
            mostrarInstrucciones = false
        }
        return
    }

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
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(bottom = 88.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                "🔥Nivel 2: Multiplica o Divide para Avanzar",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp)
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

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("niveles") { popUpTo("nivel2") { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = botonColor,
                        contentColor = textoColor
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("⬅️ Volver", fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (nivelCompletado) {

                mostrarPregunta = false

                ProgressManager.guardarNivel(LocalContext.current, 3)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        onClick = {
                            navController.navigate("niveles") {
                                popUpTo("nivel2") { inclusive = true }
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
                            navController.navigate("nivel3")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = botonColor,
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
fun PreguntaMatematicaNivel2(textoColor: Color, isDark: Boolean, onRespuesta: (Boolean) -> Unit) {

    val operador = if ((0..1).random() == 0) "*" else "/"

    var a = (2..10).random()
    var b = (2..10).random()

    if (operador == "/") {
        b = (2..10).random()
        a = b * (2..10).random()
    }

    val correcta = if (operador == "*") a * b else a / b
    val simbolo = if (operador == "*") "×" else "÷"

    val explicacion = if (operador == "*") {
        "🧮 Multiplicar es sumar un número varias veces.\nEjemplo: 3 × 2 = 6 (3 + 3)."
    } else {
        "🍎 Dividir es repartir en partes iguales.\nEjemplo: 6 ÷ 3 = 2 (6 manzanas entre 3 personas)."
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

@Composable
fun InstruccionesNivel2(
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
                         • Pierdes la llave  
                         • Vuelves al inicio  
                         • La puerta se cierra  

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
