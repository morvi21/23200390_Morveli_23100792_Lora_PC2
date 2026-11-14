package com.example.prcticacalificada2.data.remote.dto

import com.example.prcticacalificada2.data.local.AsteroidEntity
import java.time.LocalDate
import java.time.ZoneOffset

/*
 * Conversión explícita DTO (API) -> Entity (Room) de la opción Asteroides.
 *
 * Si un asteroide llega sin id o sin fecha de aproximación se descarta
 * (devuelve null) en lugar de lanzar una excepción. Los valores numéricos
 * que llegan como texto se convierten con toDoubleOrNull().
 */

/** Aplana el feed (agrupado por fecha) en una sola lista de entidades. */
fun NeoFeedResponseDto.toEntities(): List<AsteroidEntity> =
    nearEarthObjects.orEmpty()
        .flatMap { (feedDate, neos) -> neos.orEmpty().mapNotNull { it.toEntity(feedDate) } }
        .distinctBy { it.id }

fun NeoDto.toEntity(feedDate: String? = null): AsteroidEntity? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val approach = closeApproachData?.firstOrNull()
    val date = approach?.closeApproachDate ?: feedDate ?: return null

    return AsteroidEntity(
        id = safeId,
        name = name?.takeIf { it.isNotBlank() } ?: "Asteroide $safeId",
        closeApproachDate = date,
        closeApproachEpoch = approach?.epochDateCloseApproach ?: date.toEpochMillisOrZero(),
        diameterMinMeters = estimatedDiameter?.meters?.min,
        diameterMaxMeters = estimatedDiameter?.meters?.max,
        velocityKmh = approach?.relativeVelocity?.kilometersPerHour?.toDoubleOrNull(),
        missDistanceKm = approach?.missDistance?.kilometers?.toDoubleOrNull(),
        isPotentiallyHazardous = isPotentiallyHazardous ?: false
    )
}

private fun String.toEpochMillisOrZero(): Long =
    runCatching {
        LocalDate.parse(this).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    }.getOrDefault(0L)
