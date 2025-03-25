package com.example.filemanager.data.repository.usecases

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaFile

class GetLastMediaUseCase {
    suspend operator fun invoke(context: Context): List<MediaFile> {
        val media = mutableListOf<MediaFile>()
        val collection =
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )


        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE
        )
        val currentTime = System.currentTimeMillis() / 1000 // Current time in seconds
        val tenDaysAgo = currentTime - (10 * 24 * 60 * 60) // 10 days ago in seconds

        val selection = "${MediaStore.Images.Media.DATE_MODIFIED} > ?"
        val selectionArgs = arrayOf(tenDaysAgo.toString())

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        var counter = 0
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val date = Ext.longToLocalDate(cursor.getLong(dateColumn))
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val duration = 0
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val size = Ext.formatSize(cursor.getLong(sizeColumn))

                if (counter < 10) {
                    media.add(MediaFile(id, path, date, name, mimeType, duration, contentUri, size))
                    counter++
                    continue
                }
            }
            cursor.close()
        }
        return media
    }
}