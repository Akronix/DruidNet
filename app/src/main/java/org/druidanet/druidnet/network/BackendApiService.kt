package org.druidanet.druidnet.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.druidanet.druidnet.data.bibliography.BibliographyEntity
import org.druidanet.druidnet.data.plant.PlantData
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.scalars.ScalarsConverterFactory


private const val BASE_URL = "https://backend.druidnet.es/"
//private const val BASE_URL = "https://127.0.0.1:5555/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface BackendApiService {
    @GET("database/lastupdate")
    suspend fun getLastUpdate() : DataBaseUpdateInfo

    @GET("database/allbiblio")
    suspend fun downloadBiblio() : List<BibliographyEntity>

    @GET("database/allplants")
    suspend fun downloadPlantData() : PlantDataDTO

}

object BackendApi {
    val retrofitService : BackendApiService by lazy {
        retrofit.create(BackendApiService::class.java)
    }
}
