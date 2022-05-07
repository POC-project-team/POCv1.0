package com.example.anothernfcapp.caching

import android.content.Context
import android.widget.Toast
import java.io.File
import java.lang.StringBuilder

class CacheForSendingNotes(val context: Context) {
    private val fileName = "cacheFileForSentNotes"
    private val cacheFileSentNotes = File(context.cacheDir, fileName)

    fun writeToTheCache(data: String){
        cacheFileSentNotes.appendText(data + "\n")
        Toast.makeText(context, "Added to cache", Toast.LENGTH_SHORT).show()
    }

    fun clearCache(){
        cacheFileSentNotes.delete()
    }

    fun getCachedNotes() : String{
        return try {
            val cachedNotes = StringBuilder()
            cacheFileSentNotes.forEachLine {
                cachedNotes.append(it)
                cachedNotes.append("\n")
            }
            cachedNotes.toString()
        } catch (e: Exception){
            Toast.makeText(context, "Cache is empty", Toast.LENGTH_SHORT).show()
            "cache is empty"
        }
    }

    fun isEmptyCache() : Boolean{
        if(cacheFileSentNotes.length() == 0L){
            return true
        }
        return false
    }
}