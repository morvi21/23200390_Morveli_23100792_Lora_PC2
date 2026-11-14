package com.example.prcticacalificada2.ui.imagenes

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
import com.example.prcticacalificada2.ui.asteroides.BackButton
import com.example.prcticacalificada2.ui.asteroides.LoadingContent
import com.example.prcticacalificada2.ui.asteroides.MessageContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteApodsScreen(
    onBack: () -> Unit,
    onApodClick: (String) -> Unit,
    viewModel: FavoriteApodsViewModel = viewModel(factory = FavoriteApodsViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis imágenes favoritas") },
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            state.apods.isEmpty() -> MessageContent(
                message = "Aún no marcaste imágenes como favoritas.\nToca ☆ en la lista para agregarlas.",
                modifier = Modifier.padding(padding)
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.apods, key = { it.date }) { apod ->
                    ApodItem(
                        apod = apod,
                        onClick = { onApodClick(apod.date) },
                        onToggleFavorite = { viewModel.removeFavorite(apod) }
                    )
                }
            }
        }
    }
}
