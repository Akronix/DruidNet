package org.druidanet.druidnetbeta.data

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import org.druidanet.druidnetbeta.model.LanguageEnum
import org.druidanet.druidnetbeta.model.UsageType

@Entity(tableName = "Plant",
    indices = [Index(value = ["latin_name"], unique = true)]
)
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val plantId: Int = 1,
    @ColumnInfo(name = "latin_name")
    val latinName: String,

    val family: String,
    val toxic: Boolean = false,
    val description: String,
    val habitat: String,
    val phenology: String,
    val distribution: String,

    val observations: String? = null,

    @ColumnInfo(name = "image")
    @DrawableRes val imageResourceId: Int

    // otherImages
)

@Entity(
    tableName = "Name",
    foreignKeys = arrayOf(ForeignKey(entity = PlantEntity::class,
                                    parentColumns = arrayOf("plantId"),
                                    childColumns = arrayOf("nameId"),
                                    onDelete = ForeignKey.CASCADE)))
data class NameEntity (
    @PrimaryKey(autoGenerate = true) val nameId: Int = 0,
    @ColumnInfo(index = true) val plantId: Int,
    @ColumnInfo(name = "common_name") val commonName: String,
    @ColumnInfo(defaultValue = "0") val isDisplayName: Boolean,
    val language: LanguageEnum
)

//@Entity
//data class Reference(
//    @PrimaryKey val refId: String,
//    val title: String,
//    val year: Int,
//    val authors: Array<String>,
//    val editorial: String
//)

@Entity(
    tableName = "Confusion",
    foreignKeys = arrayOf(ForeignKey(entity = PlantEntity::class,
                                    parentColumns = arrayOf("plantId"),
                                    childColumns = arrayOf("confusionId"),
                                    onDelete = ForeignKey.CASCADE)),
            indices = [Index(value = ["latin_name"], unique = true)])
data class ConfusionEntity(
    @PrimaryKey(autoGenerate = true) val confusionId: Int = 0,
    @ColumnInfo(index = true) val plantId: Int,
    @ColumnInfo(name = "latin_name") val latinName: String,
    val text: String
)

@Entity(
    tableName = "Usage",
    foreignKeys = arrayOf(ForeignKey(entity = PlantEntity::class,
                                    parentColumns = arrayOf("plantId"),
                                    childColumns = arrayOf("usageId"),
                                    onDelete = ForeignKey.CASCADE)))
data class UsageEntity (
    @PrimaryKey(autoGenerate = true) val usageId: Int = 1,
    @ColumnInfo(index = true) val plantId: Int,
    val type: UsageType,
//    val subType: String,
    val text: String
)

@DatabaseView("SELECT p.plantId, p.latin_name, n.common_name, n.language, p.image" +
        " FROM Plant p JOIN Name n ON p.plantId = n.plantId WHERE isDisplayName = TRUE"
)
data class PlantView(
    val plantId: Int,
    val latin_name: String,
//        @Relation(
//        parentColumn = "plantId",
//        entityColumn = "plantId"
//    )
    val common_name: String,
    val language: LanguageEnum,
//    val common_name: String,
    val image: Int,
)
