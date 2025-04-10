package org.druidanet.druidnet.data.images

import android.util.Log
import org.druidanet.druidnet.network.BackendApi
import java.io.IOException

class ImagesRepository(
    private val localDataSource: ImagesLocalDataSource
) {
    suspend fun fetchImages(imageList : List<String>) {

//        for img in imageList
//        if (imageList not in assets nor local) {
//
//        }
        val urlString = imageList[1]
        val resImg = BackendApi.retrofitService.downloadImage(urlString).body()
        if (resImg != null) {
            val result = localDataSource.saveImage(resImg, urlString)
            Log.i("ImagesRepository", "Image $result saved successfully")
        }
        else
            throw IOException("Failed to download: $urlString")

    }
}