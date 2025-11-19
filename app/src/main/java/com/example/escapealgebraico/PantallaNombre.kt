package com.example.escapealgebraico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavHostController

@Composable
fun PantallaIngresarNombre(navController: NavHostController) {

    var nombre by rememberSaveable { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "👋 Bienvenido a ESCAPE ALGEBRAICO",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                "Escribe tu nombre:",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(21.dp))

            TextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    mostrarError = false
                },
                placeholder = { Text("Ej: Gaspar") },
                singleLine = true
            )

            if (mostrarError) {
                Text(
                    text = "⚠️ Por favor Ingresa un nombre",
                    color = Color.Red,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 21.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        mostrarError = true
                    } else {

                        navController.navigate("seleccion/$nombre")
                    }
                }
            ) {
                Text("Comenzar", fontFamily = FontFamily.Monospace)
            }
        }
    }
}
