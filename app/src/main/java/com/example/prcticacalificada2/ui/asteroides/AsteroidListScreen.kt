package com.example.prcticacalificada2.ui.asteroides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteroidListScreen(
    onAsteroidClick: (String) -> Unit,
    onOpenFavorites: () -> Unit,
    viewModel: AsteroidListViewModel = viewModel(factory = AsteroidListViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asteroides") },
                actions = {
                    TextButton(onClick = onOpenFavorites) { Text("★ Mis asteroides") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Filtro
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Solo potencialmente peligrosos",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = state.onlyHazardous, onCheckedChange = viewModel::setOnlyHazardous)
            }

            // Indicador de carga mientras se consulta la API (aunque haya datos guardados)
            if (state.isLoading && state.asteroids.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Error con datos guardados: se avisa sin ocultar la lista
            if (state.errorMessage != null && state.asteroids.isNotEmpty()) {
                Text(
                    text = state.errorMessage!! + " Se muestran los datos guardados.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            when {
                state.asteroids.isEmpty() && state.isLoading -> LoadingContent()

                state.asteroids.isEmpty() -> MessageContent(
                    message = state.errorMessage
                        ?: if (state.onlyHazardous) "No hay asteroides potencialmente peligrosos en los últimos 7 días."
                        else "No hay asteroides para mostrar.",
                    actionLabel = "Reintentar",
                    onAction = viewModel::refresh
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.asteroids, key = { it.id }) { asteroid ->
                        AsteroidItem(
                            asteroid = asteroid,
                            onClick = { onAsteroidClick(asteroid.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(asteroid) }
                        )
                    }
                }
            }
        }
    }
}
