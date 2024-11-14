package org.druidanet.druidnet.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlantEntity::class, UsageEntity::class, NameEntity::class, ConfusionEntity::class],
          views = [PlantView::class],
          version = 3,
          exportSchema = true,
          autoMigrations = [
              AutoMigration (from = 1, to = 2),
              AutoMigration (from = 2, to = 3)
          ]
        )
abstract class AppDatabase: RoomDatabase() {

    abstract fun plantDao(): PlantDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

//        val PREPOPULATE_DATA = PlantsDataSource.loadPlants()

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "druid_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    .createFromAsset("databases/druid_database.db")
                    // prepopulate the database after onCreate was called
//                    .addCallback(object : Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                            // moving to a new thread
//                            ioThread {
//                                getDatabase(context).plantDao()
//                                    .populateData(PREPOPULATE_DATA)
//                            }
//                        }
//                    })
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
