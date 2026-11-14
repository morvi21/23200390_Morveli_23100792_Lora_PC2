package com.example.prcticacalificada2.ui.imagenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.prcticacalificada2.data.local.ApodEntity
import com.example.prcticacalificada2.ui.asteroides.AsteroidFormat
import com.example.prcticacalificada2.ui.asteroides.FavoriteButton

/**
 * Imagen de un APOD. Si es video no se intenta cargar nada:
 * se muestra el texto "Contenido en video".
 */
@Composable
fun ApodImage(
    apod: ApodEntity,
    imageUrl: String?,
    height: Dp,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    if (!apod.isVideo && imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = apod.title,
            contentScale = contentScale,
            modifier = modifier.fillMaxWidth().height(height)
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(if (apod.isVideo) 56.dp else height)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (apod.isVideo) "Contenido en video" else "Imagen no disponible",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ApodItem(
    apod: ApodEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column {
            ApodImage(
                apod = apod,
                imageUrl = apod.url ?: apod.hdurl,
                height = 190.dp,
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = apod.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = AsteroidFormat.date(apod.date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                FavoriteButton(isFavorite = apod.isFavorite, onClick = onToggleFavorite)
            }
        }
    }
}
