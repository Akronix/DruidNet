package org.druidanet.druidnet.utils

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

fun Context.getResourceId(filename:String): Int {
    val resID = this.resources.getIdentifier(filename, "drawable", this.packageName)
    return resID
}

fun Context.assetsToBitmap(filename:String): ImageBitmap {
    val assetManager = this.assets
    val imgFn = "$filename.webp"
    val localStorageDir = "/data/user/0/org.druidanet.druidnet/app_images"

    val inputStream = if (assetManager.list("images/plants/")?.toSet()?.contains(imgFn) == true)
        assetManager.open("images/plants/$imgFn")
    else
        File("$localStorageDir/$imgFn").inputStream()

    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream.close()
    return bitmap.asImageBitmap()
}
