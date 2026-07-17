package org.druidanet.druidnet.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.druidanet.druidnet.R

enum class UsageType (@StringRes val displayText: Int, @DrawableRes val iconRes: Int, @DrawableRes val iconSelectedRes: Int){
    MEDICINAL(displayText = R.string.medicinal_usage_type, iconRes = R.drawable.medicinal_unfill, iconSelectedRes = R.drawable.medicinal_filled),
    EDIBLE(displayText = R.string.alimentation_usage_type, iconRes = R.drawable.edible_unfill, iconSelectedRes = R.drawable.edible_filled),
    ANIMAL_FOOD(displayText = R.string.animal_food_usage_type, iconRes = R.drawable.animal_food_unfill, iconSelectedRes = R.drawable.animal_food_filled),
    VET(displayText = R.string.vet_usage_type, iconRes = R.drawable.vet_unfill, iconSelectedRes = R.drawable.vet_filled),
    TOXIC(displayText = R.string.toxic_usage_type, iconRes = R.drawable.toxic_unfill, iconSelectedRes = R.drawable.toxic_filled),
    COMBUSTIBLE(displayText = R.string.combustible_usage_type, iconRes = R.drawable.combustible_unfill, iconSelectedRes = R.drawable.combustible_filled),
    CONSTRUCTION(displayText = R.string.construction_usage_type, iconRes = R.drawable.construction, iconSelectedRes = R.drawable.construction),
    INDUSTRY_CRAFT(displayText = R.string.industry_usage_type, iconRes = R.drawable.craft_unfill, iconSelectedRes = R.drawable.craft_filled),
    ENVIRONMENTAL(displayText = R.string.environmental_usage_type, iconRes = R.drawable.environmental_unfill, iconSelectedRes = R.drawable.environmental_filled),
    ORNAMENTAL(displayText = R.string.ornamental_usage_type, iconRes = R.drawable.ornamental_unfill, iconSelectedRes = R.drawable.ornamental_filled),
    SOCIAL(displayText = R.string.social_usage_type, iconRes = R.drawable.social_filled, iconSelectedRes = R.drawable.social_filled),
}

data class Usage (
    val usageId: Int,
    val type: UsageType,
    val subType: String,
    val text: String
)

data class PlantUseResult (
    val plantId: Int,
    val usageId: Int,
    val text: String,
    val matchOffsets: String
)

data class PlantUseCard (
    val plant: PlantCard,
    val usageId: Int,
    val text: String,
    val matchOffsets: String
)

//enum class UsageType.MEDICINAL.SUBTYPES {
//    CIRCULATORIO
//}