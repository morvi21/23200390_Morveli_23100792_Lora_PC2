package com.example.prcticacalificada2.ui.imagenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

/**
 * PUNTO DE INTEGRACIÓN DEL ALUMNO 1 (opción Imágenes / APOD).
 *
 * Reemplazar el composable de marcador por la lista de APOD y agregar
 * aquí las rutas de detalle y de "Mis imágenes favoritas".
 * No cambiar ImagenesRoutes.GRAPH porque la barra inferior lo usa.
 */
object ImagenesRoutes {
    const val GRAPH = "imagenes"
    const val LIST = "imagenes/lista"
}

@Suppress("UNUSED_PARAMETER")
fun NavGraphBuilder.imagenesGraph(navController: NavHostController) {
    navigation(startDestination = ImagenesRoutes.LIST, route = ImagenesRoutes.GRAPH) {
        composable(ImagenesRoutes.LIST) { ImagenesPlaceholder() }
    }
}

@Composable
private fun ImagenesPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Imágenes (APOD)\nEn desarrollo por el Alumno 1",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}
