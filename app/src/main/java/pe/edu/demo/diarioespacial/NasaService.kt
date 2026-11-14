package pe.edu.demo.diarioespacial

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/** APOD API oficial nueva (NASA migró el servicio en septiembre de 2026).
 * Se conserva el uso de NASA_API_KEY desde BuildConfig, fuera del repositorio.
 * Los parámetros date_from/date_to usan YYMMDD, según la guía oficial APOD Basic.
 */
interface NasaService {
    @GET("wp-json/wp/v2/apod-basic")
    suspend fun apods(
        @Query("api_key") key: String,
        @Query("date_from") from: String,
        @Query("date_to") to: String,
        @Query("per_page") perPage: Int = 25
    ): List<ApodDto>
}

data class ApodDto(
    val date: String?,
    val title: String?,
    val explanation: String?,
    val url: String?,
    val hdurl: String?,
    @SerializedName("media_type") val mediaType: String?,
    val copyright: String?
)

/** Datos remotos incompletos no deben causar un cierre inesperado. */
fun ApodDto.toEntity(): ApodEntity? {
    val safeDate = date?.takeIf { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) } ?: return null
    return ApodEntity(
        date = safeDate,
        title = title.orEmpty().ifBlank { "Sin título" },
        explanation = explanation.orEmpty(),
        // En APOD Basic (2026), 'url' es la página web, NO la imagen.
        // La foto está en 'hdurl'. Nunca pasar el permalink a Coil como imagen.
        url = if (mediaType.equals("image", ignoreCase = true)) hdurl.orEmpty() else "",
        hdurl = hdurl,
        mediaType = mediaType.orEmpty().ifBlank { "video" },
        copyright = copyright
    )
}

