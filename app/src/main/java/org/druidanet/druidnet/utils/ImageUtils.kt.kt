package org.druidanet.druidnet.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

/**
 * Copies the data from a Uri to a temporary file in the app's cache directory.
 * This is the reliable way to get a File from a content Uri.
 */
fun getFileFromUri(context: Context, uri: Uri, prefix: String, suffix: String): File? {
    val contentResolver = context.contentResolver
    // Create a temporary file in the app's cache directory
    val tempFile = File.createTempFile(prefix, suffix, context.cacheDir)
    // Ensure the file is deleted when the VM is shut down, as a fallback.
    tempFile.deleteOnExit()

    // Open an InputStream to the URI's content and a FileOutputStream to the temp file
    contentResolver.openInputStream(uri)?.use { inputStream ->
        FileOutputStream(tempFile).use { outputStream ->
            // Copy the data from the input stream to the output stream
            inputStream.copyTo(outputStream)
        }
    }
    return tempFile
}

fun fileToImageBitmap(file: File?): ImageBitmap? {
    if (file == null || !file.exists()) return null

    // Decode the file into a Bitmap
    val bitmap: Bitmap? = BitmapFactory.decodeFile(file.absolutePath)

    // Convert Bitmap to ImageBitmap
    return bitmap?.asImageBitmap()
}

fun bitmapToFile(bitmap: Bitmap, fileName: String, context: Context): File? {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, fileName)
    try {
        file.createNewFile()
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        fos.flush()
        fos.close()
        return file
    } catch (e: IOException) {
        Log.e("ERROR", "Error converting bitmap to file", e)
        throw e
    }
}

// TODO: Remove and replace by something better (painter to pass to images or Coil)
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