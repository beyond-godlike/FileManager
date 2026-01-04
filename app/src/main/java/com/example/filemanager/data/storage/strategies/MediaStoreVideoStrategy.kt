package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Video.Media
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.MediaFile

object MediaStoreVideoStrategy : DataSourceStrategy {
    override val uri: Uri
        get() = Media.EXTERNAL_CONTENT_URI
    override val projection = arrayOf(
        Media._ID,
        Media.DATA,
        Media.DISPLAY_NAME,
        Media.DURATION,
        Media.SIZE,
        Media.MIME_TYPE,
        Media.DATE_MODIFIED
    )
    override val defaultSortOrder: String
        get() = "${Media.DISPLAY_NAME} ASC"

    override fun selection(): String =
        "1 = 1"

    override fun selectionArgs(): Array<String>? {
        return null
    }

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        val videoList = mutableListOf<MediaFile>()
        val idCol = cursor.getColumnIndexOrThrow(Media._ID)
        val pathCol = cursor.getColumnIndexOrThrow(Media.DATA)
        val nameCol = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
        val durationCol = cursor.getColumnIndexOrThrow(Media.DURATION)
        val sizeCol = cursor.getColumnIndexOrThrow(Media.SIZE)
        val mimeTypeCol = cursor.getColumnIndexOrThrow(Media.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(Media.DATE_MODIFIED)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val path = cursor.getString(pathCol)
            val name = cursor.getString(nameCol)
            val duration = cursor.getInt(durationCol)
            val size = formatSize(cursor.getLong(sizeCol))

            val contentUri = ContentUris.withAppendedId(
                Media.EXTERNAL_CONTENT_URI,
                id
            )
            val date = longToLocalDate(cursor.getLong(dateCol))
            val mimeType = cursor.getString(mimeTypeCol)

            val videoItem = when (idCol) {
                0 -> MediaFile(id, path, date, name, mimeType, duration, contentUri, size)
                else -> null
            }
            videoItem?.let { videoList.add(it) }
        }

        return videoList
    }
}