package com.example.prcticacalificada2.ui.imagenes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

/** Rutas de la navegación interna de la opción Imágenes (Alumno 1). */
object ImagenesRoutes {
    const val GRAPH = "imagenes"
    const val LIST = "imagenes/lista"
    const val FAVORITES = "imagenes/favoritas"
    const val DETAIL = "imagenes/detalle/{${ApodDetailViewModel.ARG_DATE}}"

    fun detail(date: String) = "imagenes/detalle/$date"
}

fun NavGraphBuilder.imagenesGraph(navController: NavHostController) {
    navigation(startDestination = ImagenesRoutes.LIST, route = ImagenesRoutes.GRAPH) {

        composable(ImagenesRoutes.LIST) {
            ApodListScreen(
                onApodClick = { date -> navController.navigate(ImagenesRoutes.detail(date)) },
                onOpenFavorites = { navController.navigate(ImagenesRoutes.FAVORITES) }
            )
        }

        composable(
            route = ImagenesRoutes.DETAIL,
            arguments = listOf(navArgument(ApodDetailViewModel.ARG_DATE) { type = NavType.StringType })
        ) {
            ApodDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(ImagenesRoutes.FAVORITES) {
            FavoriteApodsScreen(
                onBack = { navController.popBackStack() },
                onApodClick = { date -> navController.navigate(ImagenesRoutes.detail(date)) }
            )
        }
    }
}
