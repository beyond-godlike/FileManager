package com.example.filemanager.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.filemanager.data.repository.Ext.Companion.formatSize
import com.example.filemanager.data.repository.Ext.Companion.longToLocalDate
import com.example.filemanager.data.repository.usecases.GetAppsUseCase
import com.example.filemanager.data.repository.usecases.GetAudiosUseCase
import com.example.filemanager.data.repository.usecases.GetDocumentsUseCase
import com.example.filemanager.data.repository.usecases.GetDownloadsUseCase
import com.example.filemanager.data.repository.usecases.GetImagesUseCase
import com.example.filemanager.data.repository.usecases.GetVideosUseCase
import com.example.filemanager.domain.sdk29AndUp

class MediaRepository(
    private val getImagesUseCase: GetImagesUseCase,
    private val getVideosUseCase: GetVideosUseCase,
    private val getAudiosUseCase: GetAudiosUseCase,
    private val getDownloadsUseCase: GetDownloadsUseCase,
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val getApplicationsUseCase: GetAppsUseCase
) {
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

    suspend fun loadImages(context: Context): List<MediaItem> = getImagesUseCase(context)
    suspend fun loadVideos(context: Context): List<MediaItem> = getVideosUseCase(context)

    suspend fun loadAudios(context: Context): List<MediaItem> = getAudiosUseCase(context)

    suspend fun loadDownloads(context: Context): List<MediaItem> = getDownloadsUseCase(context)

    suspend fun loadDocuments(context: Context): List<MediaItem> = getDocumentsUseCase(context)

    suspend fun loadApplications(context: Context): List<MediaItem> = getApplicationsUseCase(context)

}