package com.example.prcticacalificada2.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Configuración COMPARTIDA de Retrofit (base común de Alumno 1 y Alumno 2).
 *
 * Cada opción define su propia interfaz de servicio (NeoApi para Asteroides,
 * ApodApi para Imágenes) y la crea con [create].
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.nasa.gov/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}
