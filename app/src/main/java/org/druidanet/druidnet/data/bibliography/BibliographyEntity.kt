package org.druidanet.druidnet.data.bibliography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
tableName = "Bibliography",
)
@Serializable
data class BibliographyEntity(
    @PrimaryKey(autoGenerate = true) val refId: Int = 1,
    val type: String,
    val title: String,
    val authors: String? = null,
    val publisher: String? = null,
    val date: String? = null,
    val isbn: String? = null,
    val edition: String? = null,
    val url: String? = null,
    val subtitle: String? = null,
    val notes: String? = null
) {

    fun toMarkdownString() : String  {
        if (type != "online") {
            return "_${title}_" +
                    if (subtitle != null) ":  _${subtitle}_" else {""} +
                    ". " +
                    authors + ". " +
                    publisher + ". " +
                    date + ". " +
                    if (edition != null) "$edition. " else {""} +
                    if (url != null) "[Enlace a la web]($url). " else {""} +
                    if (notes != null) "$notes." else {""}
        } else {
            return "Portal web _${title}_: $url" +
                    if (date != null) ". _Consultado por última vez: ${date}_" else {""} +
                    if (notes != null) ". $notes." else {""}
        }
    }

}