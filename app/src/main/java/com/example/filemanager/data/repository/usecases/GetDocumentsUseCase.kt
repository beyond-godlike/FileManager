package com.example.filemanager.data.repository.usecases

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaItem
import com.example.filemanager.domain.sdk29AndUp

class GetDocumentsUseCase {
    //https://medium.com/@sendtosaeed2/android-fetch-all-files-from-local-storage-media-store-api-e9b914cd71e1
    suspend operator fun invoke(context: Context): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        val collection = sdk29AndUp {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_INTERNAL)
        } ?: MediaStore.Files.FileColumns.RELATIVE_PATH.toUri()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE
        )

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val date = Ext.longToLocalDate(cursor.getLong(dateColumn))
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val duration = 0
                val contentUri = Uri.withAppendedPath(collection, id.toString())

                val size = Ext.formatSize(cursor.getLong(sizeColumn))

                list.add(MediaItem(id, path, date, name, mimeType, duration, contentUri, size))
            }
            cursor.close()
        }

        return list
    }
}