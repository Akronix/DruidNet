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
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Create the FTS5 table
        db.execSQL(
            """
            CREATE VIRTUAL TABLE IF NOT EXISTS `PlantUseFTS` USING fts4(
                `usageId`,
                `plantId`,
                 `text`,
                content=`Usage`
            )
        """.trimIndent()
        )

        // 2. IMPORTANT: Copy existing data into the FTS table
        // Without this, the search table remains empty!
        db.execSQL(
            """
           INSERT INTO PlantUseFTS(PlantUseFTS) VALUES('rebuild');
        """.trimIndent()
        )

        db.execSQL(
        """
            CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_PlantUseFTS_BEFORE_UPDATE BEFORE UPDATE ON `Usage` BEGIN 
                 DELETE FROM `PlantUseFTS` WHERE `docid`=OLD.`rowid`; 
             END;
        """.trimIndent()
        )

        db.execSQL(
        """
            CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_PlantUseFTS_BEFORE_DELETE BEFORE DELETE ON `Usage` BEGIN 
                 DELETE FROM `PlantUseFTS` WHERE `docid`=OLD.`rowid`; 
             END
         """.trimIndent()
         )

        /*
        db.execSQL(
            """
                CREATE TRIGGER create_uses_search_autoupdate AFTER INSERT ON Usage BEGIN
                  INSERT INTO PlantUseFTS(rowid, usageId, plantId, text)
                  VALUES (new.usageId, new.plantId, new.text);
                END;
            """.trimIndent()
        )
        */

        /*
        db.execSQL(
            """
                CREATE TRIGGER update_uses_search_autoupdate AFTER INSERT ON Usage BEGIN
                  UPDATE PlantUseFTS SET(rowid, usageId, plantId, text)
                  VALUES (new.id, new.title, new.body);
                END;
            """.trimIndent()
        )

        db.execSQL(
            """
                CREATE TRIGGER uses_search_autoupdate AFTER INSERT ON Usage BEGIN
                  INSERT INTO PlantUseFTS(rowid, usageId, plantId, text)
                  VALUES (new.id, new.title, new.body);
                END;
            """.trimIndent()
        )
        */



    }
}


