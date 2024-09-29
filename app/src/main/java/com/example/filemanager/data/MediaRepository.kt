package com.example.filemanager.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

class MediaRepository {
    suspend fun loadImagesFromMediaStore(context: Context): List<ImageItem> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        val currentTime = System.currentTimeMillis() / 1000 // Current time in seconds
        val tenDaysAgo = currentTime - (10 * 24 * 60 * 60) // 10 days ago in seconds

        val selection = "${MediaStore.Images.Media.DATE_MODIFIED} > ?"
        val selectionArgs = arrayOf(tenDaysAgo.toString())

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        val images = mutableListOf<ImageItem>()

        var counter = 0
        cursor?.use { it ->
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val path = it.getString(pathColumn)
                val name = it.getString(nameColumn)
                val mimeType = it.getString(mimeTypeColumn)
                val contentUri =
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                if (mimeType.startsWith("image/")) {
                    if(counter < 10) {
                        images.add(ImageItem(id, path, name, mimeType, contentUri))
                        counter++
                        continue
                    }
                }
            }
        }
        cursor?.close()
        return images
    }
}