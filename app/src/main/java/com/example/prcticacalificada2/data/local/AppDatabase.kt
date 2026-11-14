package com.example.prcticacalificada2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos COMPARTIDA (base común de Alumno 1 y Alumno 2).
 *
 * Tablas: "apods" (Imágenes, Alumno 1) y "asteroides" (Asteroides, Alumno 2).
 * Se usa fallbackToDestructiveMigration porque los datos se pueden
 * volver a descargar de la API.
 */
@Database(
    entities = [ApodEntity::class, AsteroidEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun apodDao(): ApodDao

    abstract fun asteroidDao(): AsteroidDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diario_espacial.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
