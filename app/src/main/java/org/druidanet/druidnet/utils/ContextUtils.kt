package org.druidanet.druidnet.utils

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

// TO REMOVE and change by something better (painter to pass to images or Coil)
fun Context.assetsToBitmap(filename:String): ImageBitmap {
    val assetManager = this.assets
    val imgFn = "$filename.webp"
    val localStorageDir = this.getDir("images", Context.MODE_PRIVATE).absolutePath

    val inputStream = if (assetManager.list("images/plants/")?.toSet()?.contains(imgFn) == true)
        assetManager.open("images/plants/$imgFn")
    else
        if (File("$localStorageDir/$imgFn").exists())
            File("$localStorageDir/$imgFn").inputStream()
        else
            assetManager.open("images/broken_image.jpg")

    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream.close()
    return bitmap.asImageBitmap()
}
