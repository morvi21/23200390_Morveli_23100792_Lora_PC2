package com.example.prcticacalificada2

import android.content.Context
import com.example.prcticacalificada2.data.local.AppDatabase
import com.example.prcticacalificada2.data.remote.ApodApi
import com.example.prcticacalificada2.data.remote.NeoApi
import com.example.prcticacalificada2.data.remote.RetrofitClient
import com.example.prcticacalificada2.data.repository.ApodRepository
import com.example.prcticacalificada2.data.repository.AsteroidRepository

/**
 * Contenedor manual de dependencias (base común).
 */
class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val apodRepository: ApodRepository by lazy {
        ApodRepository(
            api = RetrofitClient.create<ApodApi>(),
            dao = database.apodDao()
        )
    }

    val asteroidRepository: AsteroidRepository by lazy {
        AsteroidRepository(
            api = RetrofitClient.create<NeoApi>(),
            dao = database.asteroidDao()
        )
    }
}
