package com.example.prcticacalificada2.ui.imagenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prcticacalificada2.ui.asteroides.LoadingContent
import com.example.prcticacalificada2.ui.asteroides.MessageContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodListScreen(
    onApodClick: (String) -> Unit,
    onOpenFavorites: () -> Unit,
    viewModel: ApodListViewModel = viewModel(factory = ApodListViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Imágenes") },
                actions = {
                    TextButton(onClick = onOpenFavorites) { Text("★ Mis favoritas") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading && state.apods.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (state.errorMessage != null && state.apods.isNotEmpty()) {
                Text(
                    text = state.errorMessage!! + " Se muestran los datos guardados.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            when {
                state.apods.isEmpty() && state.isLoading -> LoadingContent()

                state.apods.isEmpty() -> MessageContent(
                    message = state.errorMessage ?: "No hay imágenes para mostrar.",
                    actionLabel = "Reintentar",
                    onAction = viewModel::refresh
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.apods, key = { it.date }) { apod ->
                        ApodItem(
                            apod = apod,
                            onClick = { onApodClick(apod.date) },
                            onToggleFavorite = { viewModel.toggleFavorite(apod) }
                        )
                    }
                }
            }
        }
    }
}
