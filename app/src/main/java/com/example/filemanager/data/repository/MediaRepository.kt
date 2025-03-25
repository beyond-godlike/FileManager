package com.example.filemanager.data.repository

import android.content.Context
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.usecases.GetAppsUseCase
import com.example.filemanager.data.repository.usecases.GetAudiosUseCase
import com.example.filemanager.data.repository.usecases.GetDocumentsUseCase
import com.example.filemanager.data.repository.usecases.GetDownloadsUseCase
import com.example.filemanager.data.repository.usecases.GetImagesUseCase
import com.example.filemanager.data.repository.usecases.GetLastMediaUseCase
import com.example.filemanager.data.repository.usecases.GetVideosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository(
    private val getLastMediaUseCase: GetLastMediaUseCase,
    private val getImagesUseCase: GetImagesUseCase,
    private val getVideosUseCase: GetVideosUseCase,
    private val getAudiosUseCase: GetAudiosUseCase,
    private val getDownloadsUseCase: GetDownloadsUseCase,
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val getApplicationsUseCase: GetAppsUseCase
) {
    // вызывать асинхронно и вернет state

    suspend fun loadMedia(context: Context, mediaType: MediaType): List<MediaFile> =
        withContext(Dispatchers.IO) {
            try {
                // Use a when expression to select the appropriate use case
                when (mediaType) {
                    MediaType.IMAGES -> getImagesUseCase(context)
                    MediaType.LAST_MEDIA -> getLastMediaUseCase(context)
                    MediaType.VIDEOS -> getVideosUseCase(context)
                    MediaType.AUDIOS -> getAudiosUseCase(context)
                    MediaType.DOWNLOADS -> getDownloadsUseCase(context)
                    MediaType.DOCUMENTS -> getDocumentsUseCase(context)
                    MediaType.APPLICATIONS -> getApplicationsUseCase(context)
                }
            } catch (e: Exception) {
                // Log the error and handle it appropriately
                println("Error loading ${mediaType.name}: ${e.message}")
                // rethrow the exception or return empty list as needed
                throw e
            }
        }
}
