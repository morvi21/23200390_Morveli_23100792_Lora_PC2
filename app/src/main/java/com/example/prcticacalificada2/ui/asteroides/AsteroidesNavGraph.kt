package com.example.prcticacalificada2.ui.asteroides

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

/** Rutas de la navegación interna de la opción Asteroides (Alumno 2). */
object AsteroidesRoutes {
    const val GRAPH = "asteroides"
    const val LIST = "asteroides/lista"
    const val FAVORITES = "asteroides/favoritos"
    const val DETAIL = "asteroides/detalle/{${AsteroidDetailViewModel.ARG_ID}}"

    fun detail(id: String) = "asteroides/detalle/$id"
}

fun NavGraphBuilder.asteroidesGraph(navController: NavHostController) {
    navigation(startDestination = AsteroidesRoutes.LIST, route = AsteroidesRoutes.GRAPH) {

        composable(AsteroidesRoutes.LIST) {
            AsteroidListScreen(
                onAsteroidClick = { id -> navController.navigate(AsteroidesRoutes.detail(id)) },
                onOpenFavorites = { navController.navigate(AsteroidesRoutes.FAVORITES) }
            )
        }

        composable(
            route = AsteroidesRoutes.DETAIL,
            arguments = listOf(navArgument(AsteroidDetailViewModel.ARG_ID) { type = NavType.StringType })
        ) {
            AsteroidDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(AsteroidesRoutes.FAVORITES) {
            FavoriteAsteroidsScreen(
                onBack = { navController.popBackStack() },
                onAsteroidClick = { id -> navController.navigate(AsteroidesRoutes.detail(id)) }
            )
        }
    }
}
