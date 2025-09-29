package org.druidanet.druidnet.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.druidanet.druidnet.network.BackendApiService
import org.druidanet.druidnet.network.BackendScalarApiService
import org.druidanet.druidnet.network.PlantNetApiService
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val BASE_URL = "https://backend.druidnet.es/"
    private const val PLANTNET_ENDPOINT = "https://my-api.plantnet.org/v2/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    @Named("RETROFIT_JSON")
    fun provideJsonRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendApiService(@Named("RETROFIT_JSON") retrofit: Retrofit): BackendApiService {
        return retrofit.create(BackendApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("RETROFIT_SCALAR")
    fun provideScalarRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendScalarApiService(@Named("RETROFIT_SCALAR") retrofit: Retrofit): BackendScalarApiService {
        return retrofit.create(BackendScalarApiService::class.java)
    }

     @Provides
     @Singleton
     @Named("RETROFIT_PLANTNET")
     fun providePlantNetRetrofit(okHttpClient: OkHttpClient): Retrofit {
         val contentType = "application/json".toMediaType()
         val json = Json { ignoreUnknownKeys = true }
         return Retrofit.Builder()
             .baseUrl(PLANTNET_ENDPOINT)
             .client(okHttpClient)
             .addConverterFactory(json.asConverterFactory(contentType))
             .build()
     }

    @Provides
    @Singleton
    fun providePlantNetApiService(@Named("RETROFIT_PLANTNET") retrofit: Retrofit): PlantNetApiService {
        return retrofit.create(PlantNetApiService::class.java)
    }

}