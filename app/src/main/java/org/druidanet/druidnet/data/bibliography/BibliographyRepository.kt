package org.druidanet.druidnet.data.bibliography

import org.druidanet.druidnet.network.BackendApiService
import org.druidanet.druidnet.network.BiblioDTO
import javax.inject.Inject

class BibliographyRepository @Inject constructor(private val backendApiService: BackendApiService) {

    suspend fun getBiblioData() : BiblioDTO =
        backendApiService.downloadBiblio()

}