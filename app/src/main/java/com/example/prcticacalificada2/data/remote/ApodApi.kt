package com.example.prcticacalificada2.data.remote

import com.example.prcticacalificada2.data.remote.dto.ApodDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio Retrofit de la opción Imágenes (Alumno 1).
 *
 * NASA migró APOD a science.nasa.gov. En noviembre de 2026 el endpoint antiguo
 * api.nasa.gov/planetary/apod devuelve para todas las fechas el mismo marcador
 * ("NASA Science" con el logo), así que se usa el servicio nuevo "APOD Basic".
 * La URL es absoluta y reemplaza a la URL base del RetrofitClient común.
 * Las fechas date_from y date_to van en formato AAMMDD.
 */
interface ApodApi {

    @GET("https://science.nasa.gov/wp-json/wp/v2/apod-basic")
    suspend fun getApods(
        @Query("date_from") dateFrom: String, // AAMMDD
        @Query("date_to") dateTo: String,     // AAMMDD
        @Query("api_key") apiKey: String,
        @Query("per_page") perPage: Int = 25
    ): List<ApodDto>
}
