package com.example.prcticacalificada2.data.remote.dto

import android.text.Html
import com.example.prcticacalificada2.data.local.ApodEntity
import com.google.gson.annotations.SerializedName

/**
 * DTO de APOD Basic (science.nasa.gov). Todos los campos son anulables:
 * hdurl y copyright pueden no venir, y la app no debe cerrarse.
 */
data class ApodDto(
    @SerializedName("date") val date: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("hdurl") val hdurl: String?,
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("copyright") val copyright: String?
)

/**
 * Conversión explícita DTO -> Entity. Sin fecha válida el elemento se descarta.
 * En APOD Basic 'url' es la página web del artículo, no la imagen: para imágenes
 * se usa 'hdurl'. La explicación y el autor llegan con HTML y se limpian.
 */
fun ApodDto.toEntity(): ApodEntity? {
    val safeDate = date?.trim()?.takeIf { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) } ?: return null
    val type = mediaType?.takeIf { it.isNotBlank() } ?: "image"
    val isImage = type.equals("image", ignoreCase = true)
    val image = hdurl?.takeIf { it.isNotBlank() }
    return ApodEntity(
        date = safeDate,
        title = title?.htmlToText()?.takeIf { it.isNotBlank() } ?: "Sin título",
        explanation = explanation?.htmlToText()?.removePrefix("Explanation:")?.trim().orEmpty(),
        url = if (isImage) image else null,
        hdurl = if (isImage) image else null,
        mediaType = type,
        copyright = copyright?.htmlToText()?.takeIf { it.isNotBlank() }
    )
}

private fun String.htmlToText(): String =
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
        .replace(' ', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()
