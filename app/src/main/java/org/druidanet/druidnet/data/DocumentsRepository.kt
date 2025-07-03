package org.druidanet.druidnet.data

import android.content.res.AssetManager
import android.content.res.loader.AssetsProvider
import android.util.Log
import okhttp3.ResponseBody
import org.druidanet.druidnet.network.BackendScalarApi
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DocumentsRepository(
    private val localStorageDir: String,
    private val assetsMgr: AssetManager
    ) {

    suspend fun downloadCreditsMd() = downloadFile("credits.md")
    suspend fun downloadRecommendationsMd() = downloadFile("collecting_recommendations.md")
    suspend fun downloadGlossaryMd() = downloadFile("glossary.md")

    private suspend fun downloadFile(fileName: String) : Boolean {
        val responseBody = BackendScalarApi.retrofitService.downloadFile(fileName).body()
        return saveFile(responseBody, fileName) != ""
    }

    fun getCreditsMd() : String = getFile("credits.md")
    fun getRecommendationsMd(): String = getFile("collecting_recommendations.md")
    fun getGlossaryMd(): String = getFile("glossary.md")

    private fun getFile(fileName: String) : String {
        return if (File("$localStorageDir/$fileName").exists())
            readFile(fileName)
        else
            assetsMgr.open("texts/$fileName").bufferedReader().use { it.readText() }
    }

    private fun readFile(fileName: String) : String {
        val file = File("$localStorageDir/$fileName")
        return file.readText()

    }

    private fun saveFile (body: ResponseBody?, fileName: String): String{
        if (body==null)
            return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream("$localStorageDir/$fileName")
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return "$localStorageDir/$fileName"
        }catch (e:Exception){
            Log.e("saveFile",e.toString())
        }
        finally {
            input?.close()
        }
        return ""
    }

}