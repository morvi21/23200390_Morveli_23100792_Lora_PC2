package com.example.prcticacalificada2.data.remote

import com.example.prcticacalificada2.data.remote.dto.NeoFeedResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** Servicio Retrofit de la opción Asteroides (Alumno 2). */
interface NeoApi {

    /** Feed de asteroides cercanos. El rango máximo permitido es de 7 días. */
    @GET("neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("start_date") startDate: String, // AAAA-MM-DD
        @Query("end_date") endDate: String,     // AAAA-MM-DD
        @Query("api_key") apiKey: String
    ): NeoFeedResponseDto
}
