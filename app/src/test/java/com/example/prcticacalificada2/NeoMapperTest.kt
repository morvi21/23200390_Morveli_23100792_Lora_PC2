package com.example.prcticacalificada2

import com.example.prcticacalificada2.data.remote.dto.CloseApproachDto
import com.example.prcticacalificada2.data.remote.dto.DiameterRangeDto
import com.example.prcticacalificada2.data.remote.dto.EstimatedDiameterDto
import com.example.prcticacalificada2.data.remote.dto.MissDistanceDto
import com.example.prcticacalificada2.data.remote.dto.NeoDto
import com.example.prcticacalificada2.data.remote.dto.NeoFeedResponseDto
import com.example.prcticacalificada2.data.remote.dto.RelativeVelocityDto
import com.example.prcticacalificada2.data.remote.dto.toEntities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NeoMapperTest {

    private fun neo(id: String?, velocity: String? = "12345.678", hazardous: Boolean? = true) = NeoDto(
        id = id,
        name = "(2026 AB)",
        isPotentiallyHazardous = hazardous,
        estimatedDiameter = EstimatedDiameterDto(DiameterRangeDto(10.0, 22.5)),
        closeApproachData = listOf(
            CloseApproachDto(
                closeApproachDate = "2026-11-10",
                epochDateCloseApproach = 1_000L,
                relativeVelocity = RelativeVelocityDto(velocity),
                missDistance = MissDistanceDto("987654.321")
            )
        )
    )

    @Test
    fun aplanaElFeedAgrupadoPorFechaYConvierteTextoANumero() {
        val feed = NeoFeedResponseDto(
            elementCount = 2,
            nearEarthObjects = mapOf(
                "2026-11-10" to listOf(neo("1")),
                "2026-11-11" to listOf(neo("2"))
            )
        )
        val entities = feed.toEntities()
        assertEquals(2, entities.size)
        assertEquals(12345.678, entities[0].velocityKmh!!, 0.0001)
        assertEquals(987654.321, entities[0].missDistanceKm!!, 0.0001)
        assertTrue(entities[0].isPotentiallyHazardous)
    }

    @Test
    fun datosIncompletosNoRompenElMapeo() {
        val feed = NeoFeedResponseDto(
            elementCount = null,
            nearEarthObjects = mapOf(
                "2026-11-10" to listOf(
                    neo(id = null),                                   // sin id -> se descarta
                    neo(id = "3", velocity = "no-numero", hazardous = null),
                    NeoDto("4", null, null, null, null)               // casi todo nulo
                ),
                "2026-11-11" to null
            )
        )
        val entities = feed.toEntities()
        assertEquals(2, entities.size)
        assertNull(entities.first { it.id == "3" }.velocityKmh)
        assertFalse(entities.first { it.id == "3" }.isPotentiallyHazardous)
        val sinDatos = entities.first { it.id == "4" }
        assertEquals("2026-11-10", sinDatos.closeApproachDate) // toma la fecha del grupo
        assertNull(sinDatos.diameterMaxMeters)
    }

    @Test
    fun feedNuloDevuelveListaVacia() {
        assertTrue(NeoFeedResponseDto(null, null).toEntities().isEmpty())
    }
}
