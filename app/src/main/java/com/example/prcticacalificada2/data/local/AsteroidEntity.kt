package com.example.prcticacalificada2.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tabla de la opción Asteroides (Alumno 2). */
@Entity(tableName = "asteroides")
data class AsteroidEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "close_approach_date") val closeApproachDate: String,   // AAAA-MM-DD
    @ColumnInfo(name = "close_approach_epoch") val closeApproachEpoch: Long,   // para ordenar
    @ColumnInfo(name = "diameter_min_m") val diameterMinMeters: Double?,
    @ColumnInfo(name = "diameter_max_m") val diameterMaxMeters: Double?,
    @ColumnInfo(name = "velocity_kmh") val velocityKmh: Double?,
    @ColumnInfo(name = "miss_distance_km") val missDistanceKm: Double?,
    @ColumnInfo(name = "is_hazardous") val isPotentiallyHazardous: Boolean,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false
)
