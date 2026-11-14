package com.example.prcticacalificada2

import android.content.Context
import com.example.prcticacalificada2.data.local.AppDatabase
import com.example.prcticacalificada2.data.remote.NeoApi
import com.example.prcticacalificada2.data.remote.RetrofitClient
import com.example.prcticacalificada2.data.repository.AsteroidRepository

/**
 * Contenedor manual de dependencias (base común).
 * El Alumno 1 agrega aquí su ApodRepository de la misma forma.
 */
class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val asteroidRepository: AsteroidRepository by lazy {
        AsteroidRepository(
            api = RetrofitClient.create<NeoApi>(),
            dao = database.asteroidDao()
        )
    }
}
