package org.druidanet.druidnet.data.images

import android.content.res.AssetManager
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.FileOutputStream
import java.io.IOException

private const val ASSETS_IMAGE_PATH = "images/plants"

class ImagesLocalDataSource(
    private val assetsManager: AssetManager,
    private val localStorageDir: String
){

    fun saveImage(body: ResponseBody, imgName: String) : String {
        var input: InputStream? = null
        try {
            input = body.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val fos = FileOutputStream("$localStorageDir/$imgName")
            fos.use { targetOutputStream ->
                input.copyTo(targetOutputStream)
            }
            return ("$localStorageDir/$imgName")
        }catch (e: IOException){
            throw e
        }
        finally {
            input?.close()
        }
    }

    fun listLocalImages(): List<String> {
        return (File(localStorageDir).list()?.toList() ?: emptyList()) +
        (assetsManager.list(ASSETS_IMAGE_PATH)?.toList() ?: emptyList())
    }

    fun getAbsolutePathToImg(): String = localStorageDir

}