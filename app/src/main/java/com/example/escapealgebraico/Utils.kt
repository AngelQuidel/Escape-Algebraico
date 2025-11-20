package com.example.escapealgebraico

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.Pair

fun puedeMoverse(mapa: List<List<String>>, nuevaPos: Pair<Int, Int>): Boolean {
    val (x, y) = nuevaPos
    return y in mapa.indices && x in mapa[y].indices && mapa[y][x] != "W"
}

@Composable
fun ControlesMovimiento(onMove: (dx: Int, dy: Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp), // más espacio entre filas
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 24.dp) // baja los controles
    ) {
        // Botón arriba
        Button(
            onClick = { onMove(0, -1) },
            modifier = Modifier.size(80.dp)
        ) {
            Text("⬆️")
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(60.dp), // 🔥 más separados aún
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Button(
                onClick = { onMove(-1, 0) },
                modifier = Modifier.size(80.dp)
            ) {
                Text("⬅️")
            }

            Button(
                onClick = { onMove(1, 0) },
                modifier = Modifier.size(80.dp)
            ) {
                Text("➡️")
            }
        }

        Button(
            onClick = { onMove(0, 1) },
            modifier = Modifier.size(80.dp)
        ) {
            Text("⬇️")
        }
    }
}
