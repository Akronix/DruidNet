package org.druidanet.druidnet.data.plant

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.druidanet.druidnet.network.iNaturalistApiService
import javax.inject.Inject
import javax.inject.Singleton

data class OnlineImageData(
    val urls: List<String> = emptyList(),
    val attributions: List<String> = emptyList(),
)

@Singleton
class OnlineImagesRepository @Inject constructor(
    private val iNaturalistService: iNaturalistApiService
) {
    private val _onlineImageDataCache = MutableStateFlow<Map<String, OnlineImageData>>(emptyMap())

    fun getOnlineImages(latinName: String): Flow<OnlineImageData> {
        return _onlineImageDataCache.map { it[latinName] ?: OnlineImageData() }
    }

    suspend fun fetchOnlineImages(latinName: String) {
        if (_onlineImageDataCache.value.containsKey(latinName)) return

        var queryName: String
        var rank: String
        if (latinName.contains("spp.")) {
            queryName = latinName.substringBefore(" spp.")
            rank = "genus"
        } else {
            queryName = latinName
            rank = "species"
        }

        try {
            var resp = iNaturalistService.retrieveTaxaRecord(queryName, rank)
            val results = resp.body()?.get("results")?.jsonArray

//            Log.i("OnlineImagesRepository", "retrieveTaxaRecord resp: $resp")
//            Log.i("OnlineImagesRepository", "results: $results")

            var foundTaxa = false
            var taxonId = 0
            var i = 0
            if (results != null) {
                while (!foundTaxa && (i < results.size)) {
                    foundTaxa = results[i].jsonObject["name"]?.jsonPrimitive?.content == queryName
                    if (!foundTaxa)
                        i++
                    else
                        taxonId = results[i].jsonObject["id"]?.jsonPrimitive?.int ?: 0
                }
                if (foundTaxa && taxonId != 0) {
//                    Log.i("OnlineImagesRepository", "taxonId: $taxonId")

                    resp = iNaturalistService.retrieveImages(taxonId)
                    val resultsPhotos = resp.body()?.get("results")?.jsonArray
                        ?.mapNotNull { resultObj ->
                            resultObj.jsonObject["photos"]?.jsonArray?.get(0)?.jsonObject?.get("url")?.jsonPrimitive?.content
                        }

                    val resultsCopyRight = resp.body()?.get("results")?.jsonArray
                        ?.mapNotNull { resultObj ->
                            resultObj.jsonObject["photos"]?.jsonArray?.get(0)?.jsonObject?.get("attribution")?.jsonPrimitive?.content
                        }

                    val photos = resultsPhotos
                        ?.map { it.replace("square", "large") }
                        ?: emptyList()

//                    Log.i("OnlineImagesRepository", "resultsPhotos: $resultsPhotos")
//                    Log.i("OnlineImagesRepository", "photos: $photos")

                    val imageData = OnlineImageData(photos, resultsCopyRight ?: emptyList())
                    _onlineImageDataCache.value += (latinName to imageData)
                }
            }

            if (!foundTaxa) {
                Log.d("OnlineImagesRepository", "No found taxon in iNaturalist")
            }

        } catch (e: Exception) {
            Log.e("OnlineImagesRepository", "Failed to fetch images", e)
        }
    }
}
