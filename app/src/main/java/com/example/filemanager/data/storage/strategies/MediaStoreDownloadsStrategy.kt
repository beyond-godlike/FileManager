package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Files
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.MediaFile

object MediaStoreDownloadsStrategy : DataSourceStrategy {
    override val uri: Uri = Files.getContentUri("external")
    override val projection: Array<String> = arrayOf(
        Files.FileColumns._ID,
        Files.FileColumns.DATA,
        Files.FileColumns.DISPLAY_NAME,
        Files.FileColumns.MIME_TYPE,
        Files.FileColumns.DATE_ADDED,
        Files.FileColumns.SIZE,
        Files.FileColumns.RELATIVE_PATH
    )
    override val defaultSortOrder: String = "${Files.FileColumns.DATE_ADDED} DESC"

    override fun selection(): String = "${Files.FileColumns.RELATIVE_PATH} LIKE ?"

    override fun selectionArgs(): Array<String>? {
        return arrayOf(
            "Download%"
        )
    }

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        val downloads = mutableListOf<MediaFile>()
        val idCol = cursor.getColumnIndexOrThrow(Files.FileColumns._ID)
        val dataCol = cursor.getColumnIndexOrThrow(Files.FileColumns.DATA)
        val nameCol = cursor.getColumnIndexOrThrow(Files.FileColumns.DISPLAY_NAME)
        val mimeCol = cursor.getColumnIndexOrThrow(Files.FileColumns.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(Files.FileColumns.DATE_ADDED)
        val sizeCol = cursor.getColumnIndexOrThrow(Files.FileColumns.SIZE)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val path = cursor.getString(dataCol) ?: ""
            val name = cursor.getString(nameCol) ?: "Unknown"
            val mimeType = cursor.getString(mimeCol) ?: "application/octet-stream"
            val date = longToLocalDate(cursor.getLong(dateCol))
            val size = formatSize(cursor.getLong(sizeCol))

            val contentUri = ContentUris.withAppendedId(uri, id)

            downloads.add(MediaFile(id, path, date, name, mimeType, duration = 0, contentUri, size))
        }
        return downloads
    }
}