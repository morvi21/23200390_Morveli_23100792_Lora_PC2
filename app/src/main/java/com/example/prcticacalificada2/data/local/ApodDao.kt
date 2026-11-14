package com.example.prcticacalificada2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {

    /** Imágenes desde una fecha, de la más reciente a la más antigua. */
    @Query("SELECT * FROM apods WHERE date >= :fromDate ORDER BY date DESC")
    fun observeFrom(fromDate: String): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apods WHERE is_favorite = 1 ORDER BY date DESC")
    fun observeFavorites(): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apods WHERE date = :date")
    fun observeByDate(date: String): Flow<ApodEntity?>

    @Query("SELECT date FROM apods WHERE is_favorite = 1")
    suspend fun getFavoriteDates(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ApodEntity>)

    @Query("UPDATE apods SET is_favorite = :favorite WHERE date = :date")
    suspend fun setFavorite(date: String, favorite: Boolean)

    /** Guarda lo recibido de la API sin perder las marcas de favorito. */
    @Transaction
    suspend fun upsertKeepingFavorites(items: List<ApodEntity>) {
        val favorites = getFavoriteDates().toSet()
        insertAll(items.map { it.copy(isFavorite = it.date in favorites) })
    }
}
