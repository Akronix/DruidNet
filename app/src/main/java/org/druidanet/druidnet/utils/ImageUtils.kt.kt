package org.druidanet.druidnet.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale

const val COMPRESS_FACTOR = 4

fun compressImage(uri: Uri, context: Context): File {
    val contentResolver = context.contentResolver

    // 1. Decode the image URI into a Bitmap
    var bitmap = if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
    } else {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }

    // 2. Create a temporary file to store the compressed image
    val tempFile = File.createTempFile("druidnet_comp_", ".jpg", context.cacheDir)
    tempFile.deleteOnExit()
    val stream = FileOutputStream(tempFile)

    val compressHeight = bitmap.height / COMPRESS_FACTOR
    val compressWidth = bitmap.width / COMPRESS_FACTOR

    Log.i("dimensions", "Height is " + compressHeight.toString())
    Log.i("dimensions", "Width downto " + compressWidth.toString())

    // 3. ScaleDown and compress the Bitmap into the file
    bitmap = bitmap.scale( compressWidth, compressHeight, filter = true )
    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
    stream.close()
    bitmap.recycle()

    // 4. Return temp file
    return tempFile
}

fun fileToImageBitmap(file: File?): ImageBitmap? {
    if (file == null || !file.exists()) return null

    // Decode the file into a Bitmap
    val bitmap: Bitmap? = BitmapFactory.decodeFile(file.absolutePath)

    // Convert Bitmap to ImageBitmap
    return bitmap?.asImageBitmap()
}