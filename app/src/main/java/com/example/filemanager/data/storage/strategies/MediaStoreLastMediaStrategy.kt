package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Images.Media
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaFile

object MediaStoreLastMediaStrategy : DataSourceStrategy {
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
    val currentTime = System.currentTimeMillis() / 1000 // Current time in seconds
    val tenDaysAgo = currentTime - (10 * 24 * 60 * 60) // 10 days ago in seconds

    override val defaultSortOrder: String
        get() = "${Media.DATE_MODIFIED} DESC"

    override fun selection(): String = "${Media.DATE_MODIFIED} > ?"

    override fun selectionArgs(): Array<String>? {
        return arrayOf(tenDaysAgo.toString())
    }

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        val media = mutableListOf<MediaFile>()
        var counter = 0
        val idCol = cursor.getColumnIndexOrThrow(Media._ID)
        val pathCol = cursor.getColumnIndexOrThrow(Media.DATA)
        val nameCol = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
        val mimeTypeCol = cursor.getColumnIndexOrThrow(Media.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(Media.DATE_MODIFIED)
        val sizeCol = cursor.getColumnIndexOrThrow(Media.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val path = cursor.getString(pathCol)
            val date = Ext.longToLocalDate(cursor.getLong(dateCol))
            val name = cursor.getString(nameCol)
            val mimeType = cursor.getString(mimeTypeCol)
            val contentUri = ContentUris.withAppendedId(
                Media.EXTERNAL_CONTENT_URI,
                id
            )

            val size = Ext.formatSize(cursor.getLong(sizeCol))

            if (counter < 10) {
                media.add(MediaFile(id, path, date, name, mimeType, duration = 0, contentUri, size))
                counter++
                continue
            }
        }
        return media
    }

}