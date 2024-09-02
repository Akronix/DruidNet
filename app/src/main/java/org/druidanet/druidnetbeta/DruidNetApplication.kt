package org.druidanet.druidnetbeta

import android.app.Application
import android.content.Context
import org.druidanet.druidnetbeta.data.AppDatabase
import org.druidanet.druidnetbeta.model.LanguageEnum

public val LANGUAGE_APP = LanguageEnum.CASTELLANO

class DruidNetApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }
}