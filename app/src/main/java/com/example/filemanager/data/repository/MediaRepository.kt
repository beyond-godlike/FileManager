package com.example.filemanager.data.repository

import android.content.Context
import com.example.filemanager.data.repository.usecases.GetAppsUseCase
import com.example.filemanager.data.repository.usecases.GetAudiosUseCase
import com.example.filemanager.data.repository.usecases.GetDocumentsUseCase
import com.example.filemanager.data.repository.usecases.GetDownloadsUseCase
import com.example.filemanager.data.repository.usecases.GetImagesUseCase
import com.example.filemanager.data.repository.usecases.GetLastMediaUseCase
import com.example.filemanager.data.repository.usecases.GetVideosUseCase

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
    suspend fun loadImages(context: Context): List<MediaItem> = getImagesUseCase(context)
    suspend fun loadLastMedia(context: Context): List<MediaItem> = getLastMediaUseCase(context)
    suspend fun loadVideos(context: Context): List<MediaItem> = getVideosUseCase(context)

    suspend fun loadAudios(context: Context): List<MediaItem> = getAudiosUseCase(context)

    suspend fun loadDownloads(context: Context): List<MediaItem> = getDownloadsUseCase(context)

    suspend fun loadDocuments(context: Context): List<MediaItem> = getDocumentsUseCase(context)

    suspend fun loadApplications(context: Context): List<MediaItem> = getApplicationsUseCase(context)

}