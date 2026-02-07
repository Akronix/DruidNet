package org.druidanet.druidnet.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP INDEX IF EXISTS index_Confusion_latin_name")
        db.execSQL("DROP VIEW PlantView")
        db.execSQL("CREATE VIEW `PlantView` AS SELECT p.plantId, p.latin_name, n.common_name, n.language, p.image_path FROM Plant p JOIN Name n ON p.plantId = n.plantId WHERE isDisplayName = 1")
    }
}
