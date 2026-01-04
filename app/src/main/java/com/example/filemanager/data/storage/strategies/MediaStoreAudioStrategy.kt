package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaFile

object MediaStoreAudioStrategy : DataSourceStrategy {
    override val uri: Uri = Media.getContentUri(
        MediaStore.VOLUME_EXTERNAL
    )
    override val projection: Array<String> = arrayOf(
        Media._ID,
        Media.DATA,
        Media.DISPLAY_NAME,
        Media.DURATION,
        Media.MIME_TYPE,
        Media.DATE_MODIFIED,
        Media.SIZE
    )
    override val defaultSortOrder: String = "${Media.DATE_MODIFIED} DESC"
    override fun selection(): String =
        "1 = 1"

    override fun selectionArgs(): Array<String>? {
        return null
    }

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        val list = mutableListOf<MediaFile>()
        val idCol = cursor.getColumnIndexOrThrow(Media._ID)
        val pathCol = cursor.getColumnIndexOrThrow(Media.DATA)
        val nameCol = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
        val durationCol = cursor.getColumnIndexOrThrow(Media.DURATION)
        val mimeTypeCol = cursor.getColumnIndexOrThrow(Media.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(Media.DATE_MODIFIED)
        val sizeCol = cursor.getColumnIndexOrThrow(Media.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val path = cursor.getString(pathCol)
            val date = Ext.longToLocalDate(cursor.getLong(dateCol))
            val name = cursor.getString(nameCol)
            val mimeType = cursor.getString(mimeTypeCol)
            val duration = cursor.getInt(durationCol)
            val contentUri = ContentUris.withAppendedId(
                Media.EXTERNAL_CONTENT_URI,
                id
            )
            val size = Ext.formatSize(cursor.getLong(sizeCol))

            list.add(MediaFile(id, path, date, name, mimeType, duration, contentUri, size))
        }
        return list
    }
}