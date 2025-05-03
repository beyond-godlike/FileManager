package com.example.filemanager.data.repository.usecases

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.domain.PermsHelper.Companion.hasStoragePermissions

class GetDocumentsUseCase {
    //https://medium.com/@sendtosaeed2/android-fetch-all-files-from-local-storage-media-store-api-e9b914cd71e1
    operator fun invoke(context: Context): List<MediaFile> {
        if (!hasStoragePermissions(context)) {
            return emptyList()
        }

        val list = mutableListOf<MediaFile>()
        val collection =
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

        Log.e("DWN", "ID: $collection")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ?"
        val selectionArgs = arrayOf("application/pdf")

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
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
                val contentUri = ContentUris.appendId(
                    collection.buildUpon(),
                    id
                ).build()
                val size = Ext.formatSize(cursor.getLong(sizeColumn))

                Log.e("DWN", "ID: $id, Name: $name")
                //if(mimeType.contains(FileType.PDF.ext) || mimeType.contains(FileType.TXT.ext)) {
                    list.add(MediaFile(id, path, date, name, mimeType, duration, contentUri, size))
                //}
            }
            cursor.close()
        }

        return list
    }
}