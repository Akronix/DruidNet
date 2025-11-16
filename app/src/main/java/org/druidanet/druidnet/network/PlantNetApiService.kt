package org.druidanet.druidnet.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Retrofit interface
interface PlantNetApiService {

    @Multipart
    @POST("identify/k-southwestern-europe?include-related-images=true&no-reject=false&nb-results=10&lang=es&type=kt")
    suspend fun plantIdentify(
        @Part images: MultipartBody.Part, // Represents the images file part
        @Part("organs") organs: RequestBody    // Represents the 'organs' text part
    ): PlantNetResponse           // Ensure PlantIdentifyResponse is defined to match API output

}
