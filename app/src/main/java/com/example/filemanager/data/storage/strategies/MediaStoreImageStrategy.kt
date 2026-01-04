package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Images.Media
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.MediaFile

object MediaStoreImageStrategy : DataSourceStrategy {
    override val uri: Uri
        get() = Media.EXTERNAL_CONTENT_URI
    override val projection: Array<String>
        get() = arrayOf(
            Media._ID,
            Media.DATA,
            Media.DISPLAY_NAME,
            Media.MIME_TYPE,
            Media.DATE_MODIFIED,
            Media.SIZE
        )
    override val defaultSortOrder: String
        get() = "${Media.DATE_MODIFIED} DESC"

    override fun selection(): String =
        "1 = 1"

    override fun selectionArgs(): Array<String>? {
        return null
    }

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        val images = mutableListOf<MediaFile>()

        val idCol = cursor.getColumnIndexOrThrow(Media._ID)
        val pathCol = cursor.getColumnIndexOrThrow(Media.DATA)
        val nameCol = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
        val mimeTypeCol = cursor.getColumnIndexOrThrow(Media.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(Media.DATE_MODIFIED)
        val sizeCol = cursor.getColumnIndexOrThrow(Media.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val path = cursor.getString(pathCol)
            val date = longToLocalDate(cursor.getLong(dateCol))
            val name = cursor.getString(nameCol)
            val mimeType = cursor.getString(mimeTypeCol)
            val duration = 0
            val contentUri = ContentUris.withAppendedId(
                Media.EXTERNAL_CONTENT_URI,
                id
            )

            val size = formatSize(cursor.getLong(sizeCol))

            images.add(MediaFile(id, path, date, name, mimeType, duration, contentUri, size))
        }
        return images
    }
}