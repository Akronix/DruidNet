package org.druidanet.druidnet.network

import kotlinx.serialization.Serializable
import org.druidanet.druidnet.data.bibliography.BibliographyEntity
import org.druidanet.druidnet.data.bibliography.PlantReferenceEntity
import org.druidanet.druidnet.data.plant.ConfusionEntity
import org.druidanet.druidnet.data.plant.NameEntity
import org.druidanet.druidnet.data.plant.PlantEntity
import org.druidanet.druidnet.data.plant.UsageEntity

@Serializable
data class BiblioDTO(

    val biblio: List<BibliographyEntity>,
    val refs: List<PlantReferenceEntity>
    )
