package org.druidanet.druidnet.data.images

import org.druidanet.druidnet.network.BackendScalarApiService
import javax.inject.Inject

class ImagesRemoteDataSource @Inject constructor(private val backendScalarApiService: BackendScalarApiService) {
    suspend fun downloadImage(imgSrc: String) = backendScalarApiService.downloadImage(imgSrc).body()
}