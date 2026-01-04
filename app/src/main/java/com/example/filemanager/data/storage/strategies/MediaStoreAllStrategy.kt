package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaFile

object MediaStoreAllStrategy : DataSourceStrategy {
    override val uri: Uri = MediaStore.Files.getContentUri(
        MediaStore.VOLUME_EXTERNAL
    )
    override val projection: Array<String> = arrayOf(
        FileColumns._ID,
        FileColumns.DISPLAY_NAME,
        FileColumns.MIME_TYPE,
        FileColumns.MEDIA_TYPE,
        FileColumns.SIZE,
        FileColumns.DATE_MODIFIED,
        FileColumns.RELATIVE_PATH,
        FileColumns.DATA
    )
    override val defaultSortOrder: String = "${FileColumns.DATE_MODIFIED} DESC"
    override fun selection(): String =
        "1 = 1"
    override fun selectionArgs(): Array<String>? {
        return null
    }

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        val files = mutableListOf<MediaFile>()
        val idCol = cursor.getColumnIndexOrThrow(FileColumns._ID)
        val pathCol = cursor.getColumnIndexOrThrow(FileColumns.RELATIVE_PATH)
        val nameCol = cursor.getColumnIndexOrThrow(FileColumns.DISPLAY_NAME)
        val mimeTypeCol = cursor.getColumnIndexOrThrow(FileColumns.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(FileColumns.DATE_MODIFIED)
        val sizeCol = cursor.getColumnIndexOrThrow(FileColumns.SIZE)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val path = cursor.getString(pathCol)
            val name = cursor.getString(nameCol)
            val mimeType = cursor.getString(mimeTypeCol) ?: "unknown"
            val date = Ext.longToLocalDate(cursor.getLong(dateCol))
            val size = Ext.formatSize(cursor.getLong(sizeCol))
            val contentUri = ContentUris.withAppendedId(uri, id)

            files.add(MediaFile(id, path, date, name, mimeType, duration = 0, contentUri, size))
        }
        return files
    }
}