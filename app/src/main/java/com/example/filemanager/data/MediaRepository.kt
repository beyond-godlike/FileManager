package com.example.filemanager.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


class MediaRepository {
    suspend fun loadImagesFromMediaStore(context: Context): List<ImageItem> {
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
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val path = it.getString(pathColumn)
                val date = longToLocalDate(it.getLong(dateColumn))
                val name = it.getString(nameColumn)
                val mimeType = it.getString(mimeTypeColumn)
                val contentUri =
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                val size = formatSize(it.getLong(sizeColumn))

                if (mimeType.startsWith("image/")) {
                    if(counter < 10) {
                        images.add(ImageItem(id, path, date, name, mimeType, contentUri, size))
                        counter++
                        continue
                    }
                }
            }
        }
        cursor?.close()
        return images
    }

    suspend fun loadAllImagesFromMediaStore(context: Context): List<ImageItem> {
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

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        val images = mutableListOf<ImageItem>()

        var counter = 0
        cursor?.use { it ->
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val path = it.getString(pathColumn)
                val date = longToLocalDate(it.getLong(dateColumn))
                val name = it.getString(nameColumn)
                val mimeType = it.getString(mimeTypeColumn)
                val contentUri =
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                val size = formatSize(it.getLong(sizeColumn))

                if (mimeType.startsWith("image/")) {
                        images.add(ImageItem(id, path, date, name, mimeType, contentUri, size))
                        //continue
                }
            }
        }
        cursor?.close()
        return images
    }

    private fun longToLocalDate(timestamp: Long): LocalDate {
        //val instant = Instant.fromEpochMilliseconds(timestamp)
        val instant = Instant.fromEpochSeconds(timestamp)
        instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return instant.toLocalDateTime(TimeZone.UTC).date
    }

    @SuppressLint("DefaultLocale")
    private fun formatSize(sizeInBytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var unitIndex = 0

        var size = sizeInBytes.toDouble()
        while (size > 1024) {
            size /= 1024
            unitIndex++
        }

        return String.format("%.2f %s", size, units[unitIndex])
    }

}
