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
import com.example.escapealgebraico.utils.ProgressManager
import kotlin.math.round

@Composable
fun PantallaNivel4(navController: NavHostController) {

    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val fondoColor = if (isDark) Color(0xFF121212) else Color(0xFFD1F7C4)
    val textoColor = if (isDark) Color.White else Color.Black

    var mapa by remember { mutableStateOf(generarMapaNivel4()) }
    var jugadorPos by remember { mutableStateOf(Pair(1, 1)) }
    var llavesObtenidas by remember { mutableStateOf(0) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var pasoDesbloqueado by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    var mostrarInstrucciones by remember { mutableStateOf(NivelState.mostrarInstruccionesNivel4) }
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
                    .verticalScroll(rememberScrollState()), // 🔥 SCROLL
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(24.dp))

                Text(
                    "🌟 Nivel 4: Fracciones y Decimales 🌟",
                    color = textoColor,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    """
                        🧠 **Instrucciones del nivel:**
                        
                        En este nivel deberás resolver una operación matemática siguiendo **el orden correcto de las operaciones**.  
                        Esto significa que no puedes resolverla de izquierda a derecha sin más, sino respetando estas reglas:
                        
                        🔹 **1. Multiplicaciones (×) y divisiones (÷) van primero.**  
                        Se resuelven en el orden en que aparezcan, de izquierda a derecha.
                        
                        🔹 **2. Sumas (+) y restas (–) van después.**  
                        Una vez que las multiplicaciones y divisiones estén calculadas, continúas con estas operaciones.
                        
                        🔹 **3. Redondea el resultado final a un solo decimal.**  
                        Si la respuesta tiene muchos decimales, quédate solo con uno.  
                        Ejemplo: 6.3333 → 6.3
                        
                        ✨ **Ejemplo resuelto paso a paso:**  
                        Operación: 1.5 + 2 ÷ 4 × 3
                        
                        1️⃣ Primero 2 ÷ 4 = 0.5  
                        2️⃣ Luego 0.5 × 3 = 1.5  
                        3️⃣ Ahora sumas: 1.5 + 1.5 = **3.0**  
                        
                        ✔ Ese es el resultado final.
                    """.trimIndent(),
                    color = textoColor,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )


                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        mostrarInstrucciones = false
                        NivelState.mostrarInstruccionesNivel4 = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = textoColor
                    ),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Text("¡Entendido!", fontFamily = FontFamily.Monospace)
                }
            }

            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(fondoColor)
                .verticalScroll(rememberScrollState()), // 🔥 SCROLL DEL NIVEL
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Text(
                "🧮 Nivel 4: Decimales 🔢",
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
                            Text(text = emoji, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        }
                    }
                }
            }

            Text(
                "Llaves obtenidas: $llavesObtenidas / 3",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(mensaje, color = textoColor, fontFamily = FontFamily.Monospace)

            Spacer(Modifier.height(16.dp))

            if (mostrarPregunta) {
                PreguntaMatematicaNivel4(
                    textoColor = textoColor,
                    isDark = isDark,
                    onRespuesta = { correcta ->

                        if (correcta) {
                            SoundManager.playCorrectSound(context)
                            llavesObtenidas++
                            mensaje = "✅ ¡Bien! Obtuviste una llave ($llavesObtenidas/3)."
                            mostrarPregunta = false

                            if (llavesObtenidas >= 3) {
                                pasoDesbloqueado = true
                                mensaje = "🔓 ¡Puerta desbloqueada!"
                            }

                        } else {
                            SoundManager.playWrongSound(context)

                            mapa = generarMapaNivel4()
                            jugadorPos = Pair(1, 1)
                            llavesObtenidas = 0
                            pasoDesbloqueado = false

                            mostrarPregunta = false
                            mensaje = "❌ Fallaste. Inténtalo otra vez."

                            mostrarInstrucciones = true
                            NivelState.mostrarInstruccionesNivel4 = true
                        }
                    }
                )
            } else {

                if (!nivelCompletado) {
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
                                            mensaje = "🎉 ¡Nivel 4 completado! 🍖"
                                            nivelCompletado = true
                                            ProgressManager.guardarNivel(context, 5)
                                        } else {
                                            mensaje = "🚪 La puerta sigue cerrada."
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(
                    onClick = {
                        navController.navigate("niveles") {
                            popUpTo("nivel4") { inclusive = true }
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

                    Button(
                        onClick = {
                            NivelState.mostrarInstruccionesNivel5 = true
                            navController.navigate("nivel5")
                        },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF00),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Siguiente ➡️", fontFamily = FontFamily.Monospace)
                    }
                }
            }
            Spacer(Modifier.height(50.dp))
        }
    }
}

@Composable
fun PreguntaMatematicaNivel4(textoColor: Color, isDark: Boolean, onRespuesta: (Boolean) -> Unit) {
    val operadores = listOf("+", "-", "×", "÷")

    fun numeroSimple(): Double {
        return if ((1..2).random() == 1) {
            (1..20).random().toDouble()
        } else {
            ((1..20).random() + 0.5)
        }
    }

    fun divisionSimple(): Pair<Double, Double> {
        val b = (1..10).random()
        val a = b * (1..10).random()
        return Pair(a.toDouble(), b.toDouble())
    }

    val nums = MutableList(4) { numeroSimple() }
    val ops = List(3) { operadores.random() }

    for (i in ops.indices) {
        if (ops[i] == "÷") {
            val (a, b) = divisionSimple()
            nums[i] = a
            nums[i + 1] = b
        }
    }

    val expresion = "${nums[0]} ${ops[0]} ${nums[1]} ${ops[1]} ${nums[2]} ${ops[2]} ${nums[3]}"

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

        return (round(resultado * 10) / 10.0)
    }

    val resultadoCorrecto = calcularResultado()
    val opciones = mutableSetOf(resultadoCorrecto)

    while (opciones.size < 3) {
        val offset = listOf(-1.0, -0.5, 0.5, 1.0).random()
        opciones.add(round((resultadoCorrecto + offset) * 10) / 10.0)
    }

    val listaOpciones = opciones.shuffled()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text("Resuelve: $expresion", color = textoColor, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text("(Redondea al primer decimal)", color = textoColor.copy(alpha = 0.7f))
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

fun generarMapaNivel4(): List<List<String>> {
    return listOf(
        listOf("W","W","W","W","W","W","W","W","W","W","W"),
        listOf("W"," "," "," ","W"," "," "," ","W","K","W"),
        listOf("W","W","W"," ","W","W","W"," ","W"," ","W"),
        listOf("W","K","W"," "," "," ","W"," ","W"," ","W"),
        listOf("W"," ","W","W","W"," ","W"," ","W"," ","W"),
        listOf("W"," "," "," "," "," "," "," "," "," ","W"),
        listOf("W","W","W","W","W","W","W","W","W"," ","W"),
        listOf("W"," "," "," "," "," "," "," "," "," ","W"),
        listOf("W","W"," ","W"," ","W","W","W","W"," ","W"),
        listOf("W","K"," ","W"," "," ","W","G"," "," ","W"),
        listOf("W","W","W","W","W","W","W","W","W","W","W")
    )
}
