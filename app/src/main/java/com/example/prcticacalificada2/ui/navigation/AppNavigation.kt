package com.example.prcticacalificada2.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.prcticacalificada2.ui.asteroides.AsteroidesRoutes
import com.example.prcticacalificada2.ui.asteroides.asteroidesGraph
import com.example.prcticacalificada2.ui.imagenes.ImagenesRoutes
import com.example.prcticacalificada2.ui.imagenes.imagenesGraph

/** Navegación principal COMPARTIDA: barra inferior con Imágenes y Asteroides. */
private data class BottomItem(val route: String, val label: String, val icon: String)

private val bottomItems = listOf(
    BottomItem(ImagenesRoutes.GRAPH, "Imágenes", "🖼"),
    BottomItem(AsteroidesRoutes.GRAPH, "Asteroides", "☄")
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(item.icon, fontSize = 22.sp) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ImagenesRoutes.GRAPH,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            imagenesGraph(navController)   // Alumno 1
            asteroidesGraph(navController) // Alumno 2
        }
    }
}
