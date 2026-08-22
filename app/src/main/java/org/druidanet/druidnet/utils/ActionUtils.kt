package org.druidanet.druidnet.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import androidx.core.net.toUri
import org.druidanet.druidnet.R

fun sendEmailAction(context: Context, subject: String = "DruidNetApp: ", txt: String = "") {

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("druidnetbeta@gmail.com")) // recipients
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, txt)
    }
    context.startActivity(intent)
}

fun shareAction(context: Context, txt: String = "") {

    val iconUri = "android.resource://${context.packageName}/${R.mipmap.ic_launcher}".toUri()
    val sendIntent: Intent = Intent().apply {
        action = ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, txt)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun openURIAction(context: Context, uri: String) {

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = uri.toUri()
    }
    context.startActivity(intent)
}
