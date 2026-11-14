package com.example.prcticacalificada2.data.remote

import com.example.prcticacalificada2.data.remote.dto.ApodDto
import retrofit2.http.GET
import retrofit2.http.Query

/** Servicio Retrofit de la opción Imágenes (Alumno 1). */
interface ApodApi {

    /** Con start_date y end_date la API devuelve una lista de APOD. */
    @GET("planetary/apod")
    suspend fun getApods(
        @Query("start_date") startDate: String, // AAAA-MM-DD
        @Query("end_date") endDate: String,     // AAAA-MM-DD
        @Query("api_key") apiKey: String
    ): List<ApodDto>
}
