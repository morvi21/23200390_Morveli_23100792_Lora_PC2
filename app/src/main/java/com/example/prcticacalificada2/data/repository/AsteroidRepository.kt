package com.example.prcticacalificada2.data.repository

import com.example.prcticacalificada2.BuildConfig
import com.example.prcticacalificada2.data.local.AsteroidDao
import com.example.prcticacalificada2.data.local.AsteroidEntity
import com.example.prcticacalificada2.data.remote.NeoApi
import com.example.prcticacalificada2.data.remote.dto.toEntities
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository de Asteroides: única puerta de acceso a la API y a Room.
 * La UI SIEMPRE lee de Room; refreshFeed() descarga de la API y guarda.
 */
class AsteroidRepository(
    private val api: NeoApi,
    private val dao: AsteroidDao,
    private val apiKey: String = BuildConfig.NASA_API_KEY
) {

    fun observeAsteroids(onlyHazardous: Boolean): Flow<List<AsteroidEntity>> =
        if (onlyHazardous) dao.observeHazardous() else dao.observeAll()

    fun observeFavorites(): Flow<List<AsteroidEntity>> = dao.observeFavorites()

    fun observeAsteroid(id: String): Flow<AsteroidEntity?> = dao.observeById(id)

    suspend fun setFavorite(id: String, favorite: Boolean) = dao.setFavorite(id, favorite)

    /** Consulta los últimos 7 días (desde hace 6 días hasta hoy) y los guarda en Room. */
    suspend fun refreshFeed(today: LocalDate = LocalDate.now()) {
        check(apiKey.isNotBlank()) {
            "Falta NASA_API_KEY en local.properties"
        }
        val start = today.minusDays(6)
        val response = api.getFeed(
            startDate = start.toString(),
            endDate = today.toString(),
            apiKey = apiKey
        )
        dao.replaceFeed(response.toEntities())
    }
}
