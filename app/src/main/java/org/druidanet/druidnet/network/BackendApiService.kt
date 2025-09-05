package org.druidanet.druidnet.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import org.druidanet.druidnet.data.bibliography.BibliographyEntity
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url


private const val BASE_URL = "https://backend.druidnet.es/"
//private const val BASE_URL = "https://127.0.0.1:5555/"

private val retroJson = Json { ignoreUnknownKeys = true }

private val retrofit = Retrofit.Builder()
    .addConverterFactory(retroJson.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

// Retrofit interface
interface BackendApiService {
    @GET("dbinfo.json")
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

private val retrofitScalar = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create() )
    .baseUrl(BASE_URL)
    .build()

// Retrofit interface
interface BackendScalarApiService {
    @GET("credits.md")
    suspend fun getCreditsMd() : String

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>

    @Streaming
    @GET("images/{imgSrc}")
    suspend fun downloadImage(@Path("imgSrc") imgSrc: String ): Response<ResponseBody>

}

object BackendScalarApi {
    val retrofitService : BackendScalarApiService by lazy {
        retrofitScalar.create(BackendScalarApiService::class.java)
    }
}
