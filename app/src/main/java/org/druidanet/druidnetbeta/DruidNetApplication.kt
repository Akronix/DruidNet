package org.druidanet.druidnetbeta

import android.app.Application
import android.util.Log
import org.druidanet.druidnetbeta.data.AppDatabase

class DruidNetApplication: Application() {
    val database: AppDatabase by lazy { Log.d("DRUIDNET", "HEllo!"); AppDatabase.getDatabase(this) }
}