package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.MediaFile

object MediaStoreVideoStrategy : DataSourceStrategy {
    override val uri: Uri
        get() = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    override val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.MIME_TYPE,
        MediaStore.Images.Media.DATE_MODIFIED
    )
    override val defaultSortOrder: String
        get() = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

    override fun selection(): String =
        "1 = 1"

    override fun selectionArgs(): Array<String>? {
        return null
    }

    override suspend fun fetchFiles(query: Cursor): List<MediaFile> {
        return query.use { parseVideos(it) }
    }

    private fun parseVideos(cursor: Cursor): List<MediaFile> {
        val videoList = mutableListOf<MediaFile>()
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
        val mimeTypeColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
        val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val path = cursor.getString(pathColumn)
            val name = cursor.getString(nameColumn)
            val duration = cursor.getInt(durationColumn)
            val size = formatSize(cursor.getLong(sizeColumn))

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id
            )
            val date = longToLocalDate(cursor.getLong(dateColumn))
            val mimeType = cursor.getString(mimeTypeColumn)

            val videoItem = when (idColumn) {
                0 -> MediaFile(id, path, date, name, mimeType, duration, contentUri, size)
                else -> null
            }
            videoItem?.let { videoList.add(it) }
        }

        return videoList
    }
}