package com.example.escapealgebraico

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PantallaFinalFelicidades(navController: NavHostController, context: Context) {

    val isDark = isSystemInDarkTheme()

    val fondoColor = if (isDark) Color.Black else Color.White
    val textoColor = if (isDark) Color.White else Color.Black

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🌟 ¡Estamos muy orgullosos de ti! 🌟",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Has demostrado un gran esfuerzo y dedicación.\n\n" +
                        "Cada vez que aprendiste, resolviste un problema o seguiste intentando, " +
                        "te hiciste más fuerte y más sabio 💪.\n\n" +
                        "Sigue aprendiendo, sigue creyendo en ti, " +
                        "y nunca olvides que puedes lograr todo lo que te propongas 💖.",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "🍗 ¡Eres un verdadero campeón del conocimiento! 🍗",
                color = textoColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    navController.popBackStack(navController.graph.startDestinationId, false)
                    navController.navigate("inicio")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400),
                    contentColor = Color.Black
                ),
                border = BorderStroke(2.dp, Color.Yellow)
            ) {
                Text(
                    "🏠 Volver al inicio",
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    fontSize = 20.sp
                )
            }
        }
    }
}
