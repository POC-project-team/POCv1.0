package com.example.anothernfcapp.caching

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder

class CacheForReceivedNotes(val context : Context) {
    private val filename = "cacheForReceivedNotes"
    private val file = File(context.cacheDir, filename)

    fun writeToTheCache(data: String) {
        file.writeText(data)
        Log.d("CACHEGET", "Added your notes to the cache")
    }

    fun clearCache() {
        file.writeText("")
        Log.d("CACHEGET", "Cache was cleared")
    }

    fun getCachedNotes(): String {
        val cachedNotes = StringBuilder()
        return try {
            file.forEachLine {
                cachedNotes.append(it)
            }
            cachedNotes.toString()
        } catch (e: Exception){
            Log.e("CACHEGET", "Cache is empty")
            "Cache is empty"
        }
    }

}