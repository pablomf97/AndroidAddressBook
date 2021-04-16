package com.figueroa.agenda_practica2.helpers

import android.os.Environment
import android.util.Log
import java.io.*

object SdHelper {
    val isSdWrittable: Boolean
        get() = (Environment.getExternalStorageState()
                == Environment.MEDIA_MOUNTED)

    fun writeToSd(json: String, name: String?): Boolean {
        var res = false
        if (isSdWrittable) {
            val path = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "My contacts")
            path.mkdirs()
            val file = File(path, "$name.json")
            if (file.exists()) {
                Log.e("ERROR", "The file already exists...")
            } else {
                try {
                    val outputStream: OutputStream = FileOutputStream(file)
                    outputStream.write(json.toByteArray())
                    outputStream.close()
                    res = true
                } catch (e: FileNotFoundException) {
                    Log.e("ERROR", "Could not access file...$e")
                } catch (e: IOException) {
                    Log.e("ERROR", "Could not write to file...$e")
                }
            }
        } else {
            Log.e("ERROR", "Could not create file...")
        }
        return res
    }
}