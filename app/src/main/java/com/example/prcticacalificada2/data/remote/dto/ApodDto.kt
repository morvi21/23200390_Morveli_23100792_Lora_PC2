package com.example.prcticacalificada2.data.remote.dto

import com.example.prcticacalificada2.data.local.ApodEntity
import com.google.gson.annotations.SerializedName

/**
 * DTO de APOD (planetary/apod). Todos los campos son anulables:
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

/** Conversión explícita DTO -> Entity. Sin fecha válida el elemento se descarta. */
fun ApodDto.toEntity(): ApodEntity? {
    val safeDate = date?.trim()?.takeIf { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) } ?: return null
    return ApodEntity(
        date = safeDate,
        title = title?.takeIf { it.isNotBlank() } ?: "Sin título",
        explanation = explanation.orEmpty(),
        url = url?.takeIf { it.isNotBlank() },
        hdurl = hdurl?.takeIf { it.isNotBlank() },
        mediaType = mediaType?.takeIf { it.isNotBlank() } ?: "image",
        copyright = copyright?.trim()?.takeIf { it.isNotBlank() }
    )
}
