package com.example.prcticacalificada2.ui.imagenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prcticacalificada2.data.local.ApodEntity
import com.example.prcticacalificada2.ui.asteroides.AsteroidFormat
import com.example.prcticacalificada2.ui.asteroides.BackButton
import com.example.prcticacalificada2.ui.asteroides.FavoriteButton
import com.example.prcticacalificada2.ui.asteroides.LoadingContent
import com.example.prcticacalificada2.ui.asteroides.MessageContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodDetailScreen(
    onBack: () -> Unit,
    viewModel: ApodDetailViewModel = viewModel(factory = ApodDetailViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la imagen") },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    state.apod?.let {
                        FavoriteButton(isFavorite = it.isFavorite, onClick = viewModel::toggleFavorite)
                    }
                }
            )
        }
    ) { padding ->
        val apod = state.apod
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            apod == null -> MessageContent(
                message = "No se encontró la imagen.",
                modifier = Modifier.padding(padding)
            )
            else -> ApodDetailContent(apod, Modifier.padding(padding))
        }
    }
}

@Composable
private fun ApodDetailContent(apod: ApodEntity, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Imagen en grande: hdurl si existe, si no url.
        ApodImage(
            apod = apod,
            imageUrl = apod.hdurl ?: apod.url,
            height = 320.dp,
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(apod.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(AsteroidFormat.date(apod.date), style = MaterialTheme.typography.bodyMedium)
            apod.copyright?.let {
                Text("Autor: $it", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = apod.explanation.ifBlank { "Sin explicación disponible." },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
