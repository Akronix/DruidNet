package org.druidanet.druidnetbeta.utils

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun Context.getResourceId(filename:String): Int {
    val resID = this.resources.getIdentifier(filename, "drawable", this.packageName)
    return resID
}

fun Context.assetsToBitmap(filename:String): ImageBitmap? {
    return try {
        val assetManager = this.assets
        val inputStream = assetManager.open("images/plants/$filename.webp")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
