package org.druidanet.druidnet.data

import org.druidanet.druidnet.data.bibliography.BibliographyEntity
import org.druidanet.druidnet.network.BackendApi

class BibliographyRepository {

    suspend fun getBiblioData() : List<BibliographyEntity> =
        BackendApi.retrofitService.downloadBiblio()

}