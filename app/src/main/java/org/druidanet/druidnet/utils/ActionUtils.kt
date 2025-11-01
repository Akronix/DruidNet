package org.druidanet.druidnet.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun sendEmailAction(context: Context) {

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("druidnetbeta@gmail.com")) // recipients
        putExtra(Intent.EXTRA_SUBJECT, "DruidNetApp: ")
    }
    context.startActivity(intent)
}

fun openURIAction(context: Context, uri: String) {

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = uri.toUri()
    }
    context.startActivity(intent)
}
