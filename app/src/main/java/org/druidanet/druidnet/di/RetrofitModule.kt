package org.druidanet.druidnet.di

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.druidanet.druidnet.BuildConfig
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
    @Named("GENERIC_OKHTTP")
    fun provideOkHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(provideHttpLoggingInterceptor())
        }
        return okHttpClient.build()
    }

    @Provides
    @Singleton
    @Named("PLANTNET_OKHTTP")
    fun providePlantNetOkHttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api-key", BuildConfig.PLANTNET_API_KEY)
                .build()

            val requestBuilder = original.newBuilder().url(url)
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(provideHttpLoggingInterceptor())
        }
        return okHttpClient.build()
    }

    @Provides
    @Singleton
    @Named("RETROFIT_JSON")
    fun provideJsonRetrofit(@Named("GENERIC_OKHTTP") okHttpClient: OkHttpClient): Retrofit {
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
    fun provideScalarRetrofit(@Named("GENERIC_OKHTTP") okHttpClient: OkHttpClient): Retrofit {
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

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("OkHttp", message) // Log messages with "OkHttp" tag at DEBUG level
            }
        })
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        return loggingInterceptor
    }

     @Provides
     @Singleton
     @Named("RETROFIT_PLANTNET")
     fun providePlantNetRetrofit(@Named("PLANTNET_OKHTTP") okHttpClient: OkHttpClient): Retrofit {
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