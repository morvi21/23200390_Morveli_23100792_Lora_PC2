package com.example.prcticacalificada2.data.remote.dto

import com.google.gson.annotations.SerializedName

/*
 * DTOs del endpoint NeoWs Feed (neo/rest/v1/feed).
 *
 * Todos los campos son anulables: si la API devuelve datos incompletos
 * la app no debe cerrarse. Gson no respeta la nulabilidad de Kotlin,
 * así que declararlos como "?" nos obliga a validarlos en el mapper.
 *
 * Detalles de la respuesta:
 *  - near_earth_objects es un objeto cuyas claves son fechas ("2026-11-08")
 *    y sus valores son listas de asteroides -> Map<String, List<NeoDto>>.
 *  - Velocidad y distancia llegan como TEXTO -> String?.
 */

data class NeoFeedResponseDto(
    @SerializedName("element_count") val elementCount: Int?,
    @SerializedName("near_earth_objects") val nearEarthObjects: Map<String, List<NeoDto>?>?
)

data class NeoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("is_potentially_hazardous_asteroid") val isPotentiallyHazardous: Boolean?,
    @SerializedName("estimated_diameter") val estimatedDiameter: EstimatedDiameterDto?,
    @SerializedName("close_approach_data") val closeApproachData: List<CloseApproachDto>?
)

data class EstimatedDiameterDto(
    @SerializedName("meters") val meters: DiameterRangeDto?
)

data class DiameterRangeDto(
    @SerializedName("estimated_diameter_min") val min: Double?,
    @SerializedName("estimated_diameter_max") val max: Double?
)

data class CloseApproachDto(
    @SerializedName("close_approach_date") val closeApproachDate: String?,
    @SerializedName("epoch_date_close_approach") val epochDateCloseApproach: Long?,
    @SerializedName("relative_velocity") val relativeVelocity: RelativeVelocityDto?,
    @SerializedName("miss_distance") val missDistance: MissDistanceDto?
)

data class RelativeVelocityDto(
    @SerializedName("kilometers_per_hour") val kilometersPerHour: String?
)

data class MissDistanceDto(
    @SerializedName("kilometers") val kilometers: String?
)
