package com.example.prcticacalificada2.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tabla de la opción Imágenes (Alumno 1). La fecha AAAA-MM-DD es única por APOD. */
@Entity(tableName = "apods")
data class ApodEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val url: String?,
    val hdurl: String?,
    @ColumnInfo(name = "media_type") val mediaType: String,
    val copyright: String?,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false
) {
    val isVideo: Boolean get() = !mediaType.equals("image", ignoreCase = true)
}
