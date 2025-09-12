package org.druidanet.druidnet.data.bibliography

import org.druidanet.druidnet.network.BackendApiService
import javax.inject.Inject

class BibliographyRepository @Inject constructor(private val backendApiService: BackendApiService) {

    suspend fun getBiblioData() : List<BibliographyEntity> =
        backendApiService.downloadBiblio()

}