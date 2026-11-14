package com.example.prcticacalificada2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {

    /** Todos los asteroides, los de aproximación más reciente primero. */
    @Query("SELECT * FROM asteroides ORDER BY close_approach_epoch DESC, name ASC")
    fun observeAll(): Flow<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroides WHERE is_hazardous = 1 ORDER BY close_approach_epoch DESC, name ASC")
    fun observeHazardous(): Flow<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroides WHERE is_favorite = 1 ORDER BY close_approach_epoch DESC, name ASC")
    fun observeFavorites(): Flow<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroides WHERE id = :id")
    fun observeById(id: String): Flow<AsteroidEntity?>

    @Query("SELECT id FROM asteroides WHERE is_favorite = 1")
    suspend fun getFavoriteIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(asteroids: List<AsteroidEntity>)

    @Query("DELETE FROM asteroides WHERE is_favorite = 0")
    suspend fun deleteNonFavorites()

    @Query("UPDATE asteroides SET is_favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    /**
     * Reemplaza los datos del feed conservando los favoritos:
     * borra lo que no es favorito e inserta lo nuevo manteniendo
     * la marca de favorito de los asteroides que ya existían.
     */
    @Transaction
    suspend fun replaceFeed(asteroids: List<AsteroidEntity>) {
        val favorites = getFavoriteIds().toSet()
        deleteNonFavorites()
        insertAll(asteroids.map { it.copy(isFavorite = it.id in favorites) })
    }
}
