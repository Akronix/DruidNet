package org.druidanet.druidnet.data.images

import org.druidanet.druidnet.network.BackendApi

class ImagesRemoteDataSource {
    suspend fun downloadImage(imgSrc: String) = BackendApi.retrofitService.downloadImage(imgSrc).body()
}