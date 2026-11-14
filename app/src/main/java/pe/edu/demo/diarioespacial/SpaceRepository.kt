package pe.edu.demo.diarioespacial

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SpaceRepository(private val db: SpaceDatabase) {
    // Solo este cliente es para APOD. El módulo NeoWs del Alumno 2 deberá
    // conservar otro cliente con baseUrl("https://api.nasa.gov/").
    private val api: NasaService = Retrofit.Builder()
        .baseUrl("https://science.nasa.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(NasaService::class.java)

    val apods: Flow<List<ApodEntity>> = db.apodDao()
        .observeAll(LocalDate.now().minusDays(6).toString())
    val favoriteApods: Flow<List<ApodEntity>> = db.apodDao().observeFavorites()

    fun apod(date: String): Flow<ApodEntity?> = db.apodDao().observeOne(date)

    suspend fun refreshApods() {
        val to = LocalDate.now()
        val from = to.minusDays(6)
        // APOD Basic nuevo filtra por fecha en formato YYMMDD.
        val nasaDate = DateTimeFormatter.ofPattern("yyMMdd")
        val parsed = api.apods(
            BuildConfig.NASA_API_KEY,
            from.format(nasaDate),
            to.format(nasaDate)
        )
            .mapNotNull { it.toEntity() }
        db.withTransaction {
            val dao = db.apodDao()
            dao.upsert(parsed.map { row ->
                row.copy(favorite = dao.find(row.date)?.favorite ?: false)
            })
        }
    }

    suspend fun toggleApod(date: String) = db.apodDao().toggle(date)
}