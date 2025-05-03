package com.example.filemanager.data.repository.usecases

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.MediaFile

class GetVideosUseCase {
    operator fun invoke(context: Context): List<MediaFile> {
        val videoList = mutableListOf<MediaFile>()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        //MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)


        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
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
            cursor.close()
        }
        return videoList
    }
}