package com.example.filemanager.data.storage.strategies

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.filemanager.data.repository.Ext
import com.example.filemanager.data.repository.MediaFile

object MediaStoreDocumentsStrategy : DataSourceStrategy {
    //https://medium.com/@sendtosaeed2/android-fetch-all-files-from-local-storage-media-store-api-e9b914cd71e1
    override val uri: Uri = MediaStore.Files.getContentUri(
        MediaStore.VOLUME_EXTERNAL
    )
    override val projection: Array<String> = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.SIZE
    )
    override val defaultSortOrder: String = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

    override fun selection(): String = "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ?"

    override fun selectionArgs(): Array<String>? {
        return arrayOf("application/pdf")
    }

    override suspend fun fetchFiles(query: Cursor): List<MediaFile> {
        val list = mutableListOf<MediaFile>()
        //if (!hasStoragePermissions(context)) {
        //    return emptyList()
        //}
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val date = Ext.longToLocalDate(cursor.getLong(dateColumn))
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val duration = 0
                val contentUri = ContentUris.appendId(
                    uri.buildUpon(),
                    id
                ).build()
                val size = Ext.formatSize(cursor.getLong(sizeColumn))

                Log.e("DWN", "ID: $id, Name: $name")
                    //if (mimeType.contains(FileType.PDF.ext) || mimeType.contains(FileType.TXT.ext)) {
                    list.add(MediaFile(id, path, date, name, mimeType, duration, contentUri, size))
                //}
            }
        }
        return list
    }
}