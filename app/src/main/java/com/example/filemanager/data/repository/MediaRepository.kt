package com.example.filemanager.data.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.filemanager.domain.sdk29AndUp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MediaRepository {
    // вызывать асинхронно и вернет state
    suspend fun loadImagesFromMediaStore(context: Context): List<MediaItem> {
        val collection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

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
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        val images = mutableListOf<MediaItem>()

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
                val duration = 0
                val contentUri =
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                val size = formatSize(it.getLong(sizeColumn))

                if (mimeType.startsWith("image/")) {
                    if (counter < 10) {
                        images.add(MediaItem(id, path, date, name, mimeType, duration, contentUri, size))
                        counter++
                        continue
                    }
                }
            }
        }
        cursor?.close()
        return images
    }

    suspend fun loadAllImagesFromMediaStore(context: Context): List<MediaItem> {
        val images = mutableListOf<MediaItem>()
        val collection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI


        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE
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
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)



            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val date = longToLocalDate(cursor.getLong(dateColumn))
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val duration = 0
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val size = formatSize(cursor.getLong(sizeColumn))

                images.add(MediaItem(id, path, date, name, mimeType, duration, contentUri, size))
            }
            cursor.close()
        }
        return images
    }

    private fun longToLocalDate(timestamp: Long): LocalDate {
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

    suspend fun loadVideosFromMediaStore(context: Context): List<MediaItem> {
        val videoList = mutableListOf<MediaItem>()
        val collection = sdk29AndUp {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI


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
                // Get values of columns for a given video.
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
                    0 -> MediaItem(id, path, date, name, mimeType, duration, contentUri, size)
                    else -> null
                }
                videoItem?.let { videoList.add(it) }
            }
            cursor.close()
        }
        return videoList
    }

    fun loadAudios(context: Context): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        return list
    }

    fun loadDownloads(context: Context): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        return list
    }

    fun loadDocuments(context: Context): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        return list
    }

    fun loadApplications(context: Context): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        return list
    }
}