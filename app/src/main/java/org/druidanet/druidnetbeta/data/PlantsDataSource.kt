package org.druidanet.druidnetbeta.data

import org.druidanet.druidnetbeta.R
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.model.PlantType


/**
 * [PlantsDataSource] generates a list of [Affirmation]
 */
class PlantsDataSource {
    fun loadPlants(): List<Plant> {
        var plantid = 0;
        return listOf<Plant>(
            Plant(++plantid,
                latinName = "Sambucus Nigra",
                commonNames = listOf("saúco", "saüc", "intsusa", "sabugueiro"),
                plantType = PlantType.BUSH,
                R.drawable.sambucus_nigra
            ),
            Plant(++plantid,
                latinName = "Silene Vulgaris",
                commonNames = listOf("colleja", "colitx"),
                plantType = PlantType.PLANT,
                R.drawable.silene_vulgaris
            ),
            Plant(++plantid,
                latinName = "Urtica dioica",
                commonNames = listOf("ortiga", "osin"),
                plantType = PlantType.PLANT,
                R.drawable.urtica_dioica
            )
        )
    }
}