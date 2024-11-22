package com.example.filemanager.data.repository.usecases

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.MediaItem
import com.example.filemanager.domain.sdk29AndUp

class GetDownloadsUseCase {
    suspend operator fun invoke(context: Context): List<MediaItem> {
        val downloads = mutableListOf<MediaItem>()
        val collection = sdk29AndUp {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Downloads.RELATIVE_PATH.toUri()

        val projection = arrayOf(
            MediaStore.Downloads._ID,
            MediaStore.Downloads.DATA,
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads.DURATION,
            MediaStore.Downloads.MIME_TYPE,
            MediaStore.Downloads.DATE_MODIFIED,
            MediaStore.Downloads.SIZE
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_MODIFIED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.SIZE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Downloads.MIME_TYPE))
                val date = longToLocalDate(cursor.getLong(dateColumn))
                val duration = cursor.getInt(durationColumn)
                val contentUri = Uri.withAppendedPath(collection, id.toString())
                val size = formatSize(cursor.getLong(sizeColumn))
                Log.e("DWN", "ID: $id, Name: $name, duration: $duration")
                if(contentUri != null) {
                    downloads.add(MediaItem(id, path, date, name, mimeType, duration, contentUri, size))
                }
            }
        }
        return downloads
    }
}