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
        MediaStore.Images.Media.MIME_TYPE,
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

    override suspend fun fetchFiles(query: Cursor): List<MediaFile> {
        val files = mutableListOf<MediaFile>()
        query.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(FileColumns._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(FileColumns.RELATIVE_PATH)
            val nameColumn = cursor.getColumnIndexOrThrow(FileColumns.DISPLAY_NAME)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(FileColumns.MIME_TYPE)
            val dateColumn = cursor.getColumnIndexOrThrow(FileColumns.DATE_MODIFIED)
            val sizeColumn = cursor.getColumnIndexOrThrow(FileColumns.SIZE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn) ?: "unknown"
                val duration = 0
                val date = Ext.longToLocalDate(cursor.getLong(dateColumn))
                val size = Ext.formatSize(cursor.getLong(sizeColumn))
                val contentUri = ContentUris.withAppendedId(uri, id)
                files.add(MediaFile(id, path, date, name, mimeType, duration, contentUri, size))
            }
        }
        return files
    }
}