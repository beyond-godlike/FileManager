package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaFile

object MediaStoreApplicationStrategy : DataSourceStrategy {

    override val uri: Uri =
        MediaStore.Files.getContentUri("external")

    override val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.SIZE
    )

    override val defaultSortOrder =
        "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

    override fun selection(): String =
        "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?"

    override fun selectionArgs(): Array<String>? =
        arrayOf("%.apk")

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        val list = mutableListOf<MediaFile>()

        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val mimeTypeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
        val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)

            val contentUri = ContentUris.withAppendedId(uri, id)

            val name = cursor.getString(nameCol)
            val rawMime = cursor.getString(mimeTypeCol)
            val mimeType = if (name.endsWith(".apk", true)) {
                "application/vnd.android.package-archive"
            } else {
                rawMime ?: "unknown"
            }


            list.add(
                MediaFile(
                    id = id,
                    path = contentUri.toString(),
                    date = Ext.longToLocalDate(cursor.getLong(dateCol)),
                    name = name,
                    mimeType = mimeType,
                    duration = 0,
                    contentUri = contentUri,
                    size = Ext.formatSize(cursor.getLong(sizeCol))
                )
            )
        }

        return list
    }
}