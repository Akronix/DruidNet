package org.druidanet.druidnet.data

import org.druidanet.druidnet.model.Confusion
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Name
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.model.Usage
import org.druidanet.druidnet.model.UsageType


/**
 * [PlantsDataSource] generates a list of [Plant]
 */
object PlantsDataSource {
    fun loadPlants(): List<Plant> {
//        var plantid = 0;

//        val refInventario1 = Reference(
//            refId = "InventarioTomo1",
//            authors = arrayOf("Pardo de Santayana, Manuel", "Morales, Ramón", "Aceituno-Mata, Laura", "Molina, María"),
//            title = "INVENTARIO ESPAÑOL DE LOS CONOCIMIENTOS TRADICIONALES RELATIVOS A LA BIODIVERSIDAD - TOMO 1",
//            year = 2014,
//            editorial = "Ministerio de Agricultura, Alimentación y Medio Ambiente."
//        )

        return listOf<Plant>(

            Plant(
                plantId = 1,
                latinName = "Sambucus Nigra",
                family = "Caprifoliaceae",
                toxic = true,
                toxic_text = "Todas las partes verdes del Saúco, incluidos los frutos verdes, " +
                        "contienen el glucósido cianogénico sambunigrina que es tóxico cuando se come crudo. " +
                        "Estos compuestos pierden su toxicidad cuando se cocinan.",
                displayName = "Saúco",
                commonNames = arrayOf(
                    Name("Saúco", language = LanguageEnum.CASTELLANO),
                    Name("Saüc", language = LanguageEnum.CATALAN),
                    Name("Intsusa", language = LanguageEnum.EUSKERA),
                    Name("Sabugueiro", language = LanguageEnum.GALLEGO)
                ),
                description = "Arbusto o arbolillo caducifolio, de 1-5(10) m, ramificado desde la " +
                        "base, con ramas rectas o arqueadas, copa ancha, redondeada, corteza " +
                        "pardo-grisácea, verrucosa en la madurez, con médula blanca. Hojas " +
                        "4-12 x 2-6 cm, opuestas, compuestas, imparipinnadas, con 5-7(9) folíolos" +
                        " peciolados, pinnatinervios, elípticos, ovados u ovado-lanceolados, " +
                        "aserrados, acuminados y con la base asimétrica y atenuada; pecíolo " +
                        "de 28-52 mm, glabro o con pelos esparcidos. Inflorescencia corimbiforme," +
                        "de 10-27 cm de diámetro, con muchas flores pentámeras, de " +
                        "4-6(9) mm de diámetro, con sépalos triangulares y pétalos, con lóbulos " +
                        "redondeados, color blanco y crema. Fruto en drupas de 5-7 mm, esferoidales, " +
                        "color violeta-negruzco, en grupos colgantes.",
                usages = mapOf(
                        Pair(UsageType.MEDICINAL,
                            listOf(Usage(UsageType.MEDICINAL, "Infusión de sus flores para enfermedades respiratorias"))
                        ),
                        Pair(UsageType.EDIBLE,
                            listOf(Usage(UsageType.EDIBLE, "Buñuelos de flores de Saúco"))
                        )
                ),
                phenology = "Florece de abril a julio y fructifica en julio y agosto.",
                habitat = "Indiferente edáfico, vive en suelos húmedos de los pisos basal y mon" +
                        "tano de la región mediterránea, generalmente en claros de bosques, " +
                        "comunidades herbáceas higronitrófilas, riberas y sotos. 0-1200(1900) m.",
                distribution = "Crece en gran parte de Europa, especialmente en el norte de la " +
                        "cuenca mediterránea, en el W y SW de Asia y es subespontánea en " +
                        "el N de África, en Azores y Madeira. En la Península Ibérica es más " +
                        "abundante en la mitad norte, aunque también se encuentra en algunas" +
                        "zonas del sur. Se cultiva fácilmente de estaquilla y muchas veces, " +
                        "especialmente en Levante, es difícil distinguir si es natural o cultivado." +
                        "Todo esto se refiere a la subsp. nigra, porque en las islas de Gran Canaria" +
                        ", Tenerife, La Gomera y La Palma, vive la subsp. palmensis (Link)" +
                        "Bolli. (=S. palmensis Link), que se diferencia por sus inflorescencias " +
                        "más pequeñas, en general con tres radios principales, y frutos algo " +
                        "más pequeños. Al parecer, también se ha cultivado esta subespecie " +
                        "en la isla de El Hierro. Ambas subespecies tienen usos similares.",
                confusions = arrayOf(
                    Confusion(
                        latinName= "Sambucus ebulus",
                        text="Se puede confundir con el Sauquillo, Yezgo o Saúco menor (Sambucus ebulus), cuyos frutos son tóxicos. Tratándose éste último de una herbáceea, sin parte leñosa;" +
                                " mientras que el Saúco (Sambucus Nigra) tiene porte arbustivo o arbóreo. " +
                                "Además los frutos del Saúco caen en racimos hacia abajo, mientras que los del Sauquillo crecen hacia arriba.",
                        imagePath = "sambucus_ebulus2",
                        captionText = "En la imagen, las plantas que se ven más cercanas son ejemplares de Yezgo (Sambuculus ebulus), mientras que el árbol del fondo es un Saúco (Sambuculus nigra)."
                    )
                ),
//                references = arrayOf(refInventario1),
                imagePath = "sambucus_nigra"
            )


            /*
            Plant(++plantid,
                latinName = "Sambucus Nigra",
                //displayName = "Saúco",
                commonNames = listOf("Saúco", "Saüc", "Intsusa", "Sabugueiro"),
                R.drawable.sambucus_nigra
            ),
            Plant(++plantid,
                latinName = "Silene Vulgaris",
                //displayName = "Colleja",
                commonNames = listOf("Colleja", "Colitx"),
                R.drawable.silene_vulgaris
            ),
            Plant(++plantid,
                latinName = "Urtica dioica",
                //displayName = "Ortiga",
                commonNames = listOf("Ortiga", "Osin"),
                R.drawable.urtica_dioica
            )
             */
        )
    }
}