package com.example.filemanager.data.storage.strategies

import android.database.Cursor
import android.net.Uri
import com.example.filemanager.data.repository.MediaFile

interface DataSourceStrategy {
    val uri: Uri
    val projection: Array<String>
    val defaultSortOrder: String

    fun selection(): String
    fun selectionArgs(): Array<String>?
    suspend fun fetchFiles(cursor: Cursor): List<MediaFile>
}