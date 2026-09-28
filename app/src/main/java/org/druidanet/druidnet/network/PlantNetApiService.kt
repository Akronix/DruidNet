package org.druidanet.druidnet.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit interface
interface PlantNetApiService {

    @Multipart
    @POST("identify/{project}?include-related-images=true&no-reject=false&nb-results=10&type=kt")
    suspend fun plantIdentify(
        @Path("project") project: String = "k-southwestern-europe",
        @Part images: MultipartBody.Part, // Represents the images file part
        @Query("lang") lang: String,
        @Part("organs") organs: RequestBody    // Represents the 'organs' text part
    ): PlantNetResponse           // Ensure PlantIdentifyResponse is defined to match API output

}
