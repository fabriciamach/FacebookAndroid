package com.example.facebookandroid

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log

class ImageProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        // Implementar se necessário
        return null
    }

    override fun getType(uri: Uri): String? {
        return "image/*" // ou o tipo MIME correto
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Implementar se necessário
        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        // Implementar se necessário
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // Implementar se necessário
        return 0
    }
}
