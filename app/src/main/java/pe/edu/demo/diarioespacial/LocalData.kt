package pe.edu.demo.diarioespacial

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/** CONTRATO APOD: el Alumno 2 debe conservar entidad y DAO al integrar la BD común. */
@Entity(tableName = "apods")
data class ApodEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdurl: String?,
    val mediaType: String,
    val copyright: String?,
    val favorite: Boolean = false
)

@Dao
interface ApodDao {
    @Query("SELECT * FROM apods WHERE date >= :fromDate ORDER BY date DESC")
    fun observeAll(fromDate: String): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apods WHERE date = :date LIMIT 1")
    fun observeOne(date: String): Flow<ApodEntity?>

    @Query("SELECT * FROM apods WHERE date = :date LIMIT 1")
    suspend fun find(date: String): ApodEntity?

    @Query("SELECT * FROM apods WHERE favorite = 1 ORDER BY date DESC")
    fun observeFavorites(): Flow<List<ApodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(items: List<ApodEntity>)

    @Query("UPDATE apods SET favorite = CASE WHEN favorite = 1 THEN 0 ELSE 1 END WHERE date = :date")
    suspend fun toggle(date: String)
}

/** BASE TEMPORAL PARA PRUEBAS INDIVIDUALES.
 * El Alumno 2 integrará AsteroidEntity, AsteroidDao y agregará asteroidDao() en el PR
 * de la base común. No mezclar dos clases SpaceDatabase distintas.
 */
@Database(entities = [ApodEntity::class], version = 1, exportSchema = false)
abstract class SpaceDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
    companion object {
        @Volatile private var INSTANCE: SpaceDatabase? = null
        fun get(context: Context): SpaceDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext, SpaceDatabase::class.java, "diario_espacial_alumno1.db"
            ).build().also { INSTANCE = it }
        }
    }
}
