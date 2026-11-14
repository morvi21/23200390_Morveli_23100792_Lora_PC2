package com.example.prcticacalificada2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos COMPARTIDA (base común de Alumno 1 y Alumno 2).
 *
 * Para integrar la opción Imágenes (Alumno 1):
 *  1. Agregar ApodEntity::class al arreglo entities.
 *  2. Subir version a 2.
 *  3. Agregar abstract fun apodDao(): ApodDao
 * Se usa fallbackToDestructiveMigration porque los datos se pueden
 * volver a descargar de la API.
 */
@Database(
    entities = [AsteroidEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

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
