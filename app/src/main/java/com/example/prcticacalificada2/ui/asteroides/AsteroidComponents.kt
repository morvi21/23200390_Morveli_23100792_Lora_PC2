package com.example.prcticacalificada2.ui.asteroides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prcticacalificada2.data.local.AsteroidEntity

private val FavoriteColor = Color(0xFFFFB300)

/** Botón de favorito: estrella llena si está marcado, vacía si no. */
@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = if (isFavorite) "Quitar de favoritos" else "Marcar como favorito"
    IconButton(onClick = onClick, modifier = modifier.semantics { contentDescription = description }) {
        Text(
            text = if (isFavorite) "★" else "☆",
            fontSize = 26.sp,
            color = if (isFavorite) FavoriteColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Etiqueta roja visible para asteroides potencialmente peligrosos. */
@Composable
fun HazardBadge(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = "⚠ Potencialmente peligroso",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun AsteroidItem(
    asteroid: AsteroidEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = if (asteroid.isPotentiallyHazardous) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    } else {
        CardDefaults.cardColors()
    }
    Card(onClick = onClick, colors = colors, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = asteroid.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (asteroid.isPotentiallyHazardous) {
                    HazardBadge(modifier = Modifier.padding(vertical = 2.dp))
                }
                Text("Aproximación: ${AsteroidFormat.date(asteroid.closeApproachDate)}", style = MaterialTheme.typography.bodyMedium)
                Text("Diámetro máx.: ${AsteroidFormat.meters(asteroid.diameterMaxMeters)}", style = MaterialTheme.typography.bodyMedium)
                Text("Velocidad: ${AsteroidFormat.kmh(asteroid.velocityKmh)}", style = MaterialTheme.typography.bodyMedium)
                Text("Distancia: ${AsteroidFormat.km(asteroid.missDistanceKm)}", style = MaterialTheme.typography.bodyMedium)
            }
            FavoriteButton(isFavorite = asteroid.isFavorite, onClick = onToggleFavorite)
        }
    }
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** Mensaje que reemplaza a una lista vacía. */
@Composable
fun MessageContent(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("☄", fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text(message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}
