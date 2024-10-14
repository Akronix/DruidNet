package org.druidanet.druidnet.model

import androidx.annotation.StringRes
import org.druidanet.druidnet.R

enum class UsageType (@StringRes val displayText: Int){
    MEDICINAL(displayText = R.string.medicinal_usage_type),
    EDIBLE(displayText = R.string.alimentation_usage_type),
    ANIMAL_FOOD(displayText = R.string.animal_food_usage_type),
    VET(displayText = R.string.vet_usage_type),
    TOXIC(displayText = R.string.toxic_usage_type),
    COMBUSTIBLE(displayText = R.string.combustible_usage_type),
    CONSTRUCTION(displayText = R.string.construction_usage_type),
    INDUSTRY_CRAFT(displayText = R.string.industry_usage_type),
    ENVIRONMENTAL(displayText = R.string.environmental_usage_type),
    ORNAMENTAL(displayText = R.string.ornamental_usage_type),
    SOCIAL(displayText = R.string.social_usage_type),
}

//enum class UsageType.MEDICINAL.SUBTYPES {
//    CIRCULATORIO
//}