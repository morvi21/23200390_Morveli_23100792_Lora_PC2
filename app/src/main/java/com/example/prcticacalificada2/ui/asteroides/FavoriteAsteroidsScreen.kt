package com.example.prcticacalificada2.ui.asteroides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteAsteroidsScreen(
    onBack: () -> Unit,
    onAsteroidClick: (String) -> Unit,
    viewModel: FavoriteAsteroidsViewModel = viewModel(factory = FavoriteAsteroidsViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis asteroides") },
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            state.asteroids.isEmpty() -> MessageContent(
                message = "Aún no marcaste asteroides como favoritos.\nToca ☆ en la lista para agregarlos.",
                modifier = Modifier.padding(padding)
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.asteroids, key = { it.id }) { asteroid ->
                    AsteroidItem(
                        asteroid = asteroid,
                        onClick = { onAsteroidClick(asteroid.id) },
                        onToggleFavorite = { viewModel.removeFavorite(asteroid) }
                    )
                }
            }
        }
    }
}
