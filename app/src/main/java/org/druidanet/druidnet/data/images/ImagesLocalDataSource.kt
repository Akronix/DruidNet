package org.druidanet.druidnet.data.images

import okhttp3.ResponseBody
import java.io.InputStream
import java.io.FileOutputStream
import java.io.IOException


class ImagesLocalDataSource (
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

}