package com.example.prcticacalificada2.data.repository

import com.example.prcticacalificada2.BuildConfig
import com.example.prcticacalificada2.data.local.ApodDao
import com.example.prcticacalificada2.data.local.ApodEntity
import com.example.prcticacalificada2.data.remote.ApodApi
import com.example.prcticacalificada2.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Repository de Imágenes: accede a la API y a Room.
 * La UI lee siempre de Room; refreshApods() descarga y guarda.
 */
class ApodRepository(
    private val api: ApodApi,
    private val dao: ApodDao,
    private val apiKey: String = BuildConfig.NASA_API_KEY
) {

    /** Últimos 7 días: desde hace 6 días hasta hoy. */
    fun observeLastWeek(today: LocalDate = LocalDate.now()): Flow<List<ApodEntity>> =
        dao.observeFrom(today.minusDays(6).toString())

    fun observeFavorites(): Flow<List<ApodEntity>> = dao.observeFavorites()

    fun observeApod(date: String): Flow<ApodEntity?> = dao.observeByDate(date)

    suspend fun setFavorite(date: String, favorite: Boolean) = dao.setFavorite(date, favorite)

    suspend fun refreshApods(today: LocalDate = LocalDate.now()) {
        check(apiKey.isNotBlank()) { "Falta NASA_API_KEY en local.properties" }
        val nasaDate = DateTimeFormatter.ofPattern("yyMMdd")
        val response = api.getApods(
            dateFrom = today.minusDays(6).format(nasaDate),
            dateTo = today.format(nasaDate),
            apiKey = apiKey
        )
        dao.upsertKeepingFavorites(response.mapNotNull { it.toEntity() })
    }
}
