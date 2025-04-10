package org.druidanet.druidnet.data.images

import android.util.Log
import java.io.IOException

class ImagesRepository(
    private val remoteDataSource: ImagesRemoteDataSource,
    private val localDataSource: ImagesLocalDataSource
) {
    suspend fun fetchImages(imageList : List<String>) {
        val localImages : List<String> = localDataSource.listLocalImages()
        Log.i("ImagesRepository", "Local images: $localImages")
        val newImages : List<String> = imageList.filter { !localImages.contains(it) }
        Log.i("ImagesRepository", "new images: $newImages")
        for (imgSrc in imageList) {
            val resImg = remoteDataSource.downloadImage(imgSrc)
            if (resImg != null) {
                val result = localDataSource.saveImage(resImg, imgSrc)
                Log.i("ImagesRepository", "Image $result saved successfully")
            }
            else
                throw IOException("Failed to download: $imgSrc")
        }

    }
}