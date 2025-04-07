package org.druidanet.druidnet.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.scalars.ScalarsConverterFactory


private const val BASE_URL = "https://backend.druidnet.es/"
//private const val BASE_URL = "https://localhost/"

private val retrofit = Retrofit.Builder()
//    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BackendApiService {
    @GET("database/lastupdate")
    suspend fun getLastUpdate() : String

}

object BackendApi {
    val retrofitService : BackendApiService by lazy {
        retrofit.create(BackendApiService::class.java)
    }
}
