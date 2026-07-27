package org.druidanet.druidnet.network

import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit interface
interface iNaturalistApiService {

    @GET("taxa?iconic_taxa=Plantae")
    suspend fun retrieveTaxaRecord(@Query("q") q: String, @Query("rank") rank: String = "species"): Response<JsonObject>

    @GET("observations?photos=true&iconic_taxa=Plantae&quality_grade=research&per_page=5&order=desc&order_by=geo_score&only_id=false")
    suspend fun retrieveImages(@Query("taxon_id") taxonId: Int): Response<JsonObject>

}
