package com.example.hw1_mobile_computing

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream


fun saveGalleryItems(context: Context, items: List<GalleryItem>) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = Json.encodeToString(items)
    sharedPref.edit { putString(KEY_GALLERY_ITEMS, json) }
}

fun loadGalleryItems(context: Context): List<GalleryItem> {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = sharedPref.getString(KEY_GALLERY_ITEMS, null)
    return if (json != null) Json.decodeFromString(json) else emptyList()
}

fun makeImagePermanent(context: Context, uri: Uri): Uri {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "saved_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        file.toUri()
    } catch (e: Exception) { uri }
}