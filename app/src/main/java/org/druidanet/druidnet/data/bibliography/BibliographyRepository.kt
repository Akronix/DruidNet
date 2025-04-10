package org.druidanet.druidnet.data.bibliography

import org.druidanet.druidnet.network.BackendApi

class BibliographyRepository {

    suspend fun getBiblioData() : List<BibliographyEntity> =
        BackendApi.retrofitService.downloadBiblio()

}