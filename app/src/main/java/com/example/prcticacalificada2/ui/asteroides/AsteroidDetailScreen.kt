package com.example.prcticacalificada2.ui.asteroides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prcticacalificada2.data.local.AsteroidEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteroidDetailScreen(
    onBack: () -> Unit,
    viewModel: AsteroidDetailViewModel = viewModel(factory = AsteroidDetailViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del asteroide") },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    state.asteroid?.let {
                        FavoriteButton(isFavorite = it.isFavorite, onClick = viewModel::toggleFavorite)
                    }
                }
            )
        }
    ) { padding ->
        val asteroid = state.asteroid
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            asteroid == null -> MessageContent(
                message = "No se encontró el asteroide.",
                modifier = Modifier.padding(padding)
            )
            else -> AsteroidDetailContent(asteroid, Modifier.padding(padding))
        }
    }
}

@Composable
private fun AsteroidDetailContent(asteroid: AsteroidEntity, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(asteroid.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (asteroid.isPotentiallyHazardous) HazardBadge()

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("ID", asteroid.id)
                DetailRow("Fecha de aproximación", AsteroidFormat.date(asteroid.closeApproachDate))
                DetailRow("Diámetro mínimo estimado", AsteroidFormat.meters(asteroid.diameterMinMeters))
                DetailRow("Diámetro máximo estimado", AsteroidFormat.meters(asteroid.diameterMaxMeters))
                DetailRow("Velocidad relativa", AsteroidFormat.kmh(asteroid.velocityKmh))
                DetailRow("Distancia de paso", AsteroidFormat.km(asteroid.missDistanceKm))
                DetailRow(
                    label = "Potencialmente peligroso",
                    value = AsteroidFormat.yesNo(asteroid.isPotentiallyHazardous),
                    valueColor = if (asteroid.isPotentiallyHazardous) MaterialTheme.colorScheme.error else null,
                    showDivider = false
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color? = null,
    showDivider: Boolean = true
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
    if (showDivider) HorizontalDivider()
}

/** Botón "atrás" sin depender de material-icons. */
@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = "Volver" }) {
        Text("←", fontSize = 24.sp)
    }
}
