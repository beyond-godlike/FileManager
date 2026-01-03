package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.MediaFile

object MediaStoreImageStrategy : DataSourceStrategy {
    override val uri: Uri
        get() = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    override val projection: Array<String>
        get() = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE
        )
    override val defaultSortOrder: String
        get() = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

    override fun selection(): String =
        "1 = 1"

    override fun selectionArgs(): Array<String>? {
        return null
    }

    override suspend fun fetchFiles(query: Cursor): List<MediaFile> {
        return query.use { parseImages(it) }
    }

    private fun parseImages(cursor: Cursor): List<MediaFile> {
        val images = mutableListOf<MediaFile>()

        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
        val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val path = cursor.getString(pathColumn)
            val date = longToLocalDate(cursor.getLong(dateColumn))
            val name = cursor.getString(nameColumn)
            val mimeType = cursor.getString(mimeTypeColumn)
            val duration = 0
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            val size = formatSize(cursor.getLong(sizeColumn))

            images.add(MediaFile(id, path, date, name, mimeType, duration, contentUri, size))
        }
        return images
    }
}