package com.example.filemanager.data.repository

import com.example.filemanager.data.MediaType
import com.example.filemanager.data.storage.strategies.DataSourceStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreAllStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreApplicationStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreAudioStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreDocumentsStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreDownloadsStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreImageStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreLastMediaStrategy
import com.example.filemanager.data.storage.strategies.MediaStoreVideoStrategy

class MediaRepository(
    private val fileBrowserRepository: FileBrowserRepository
) {
    suspend fun loadMedia(mediaType: MediaType): List<MediaFile> {
        val strategy: DataSourceStrategy = when (mediaType) {
            MediaType.IMAGES -> MediaStoreImageStrategy
            MediaType.LAST_MEDIA -> MediaStoreLastMediaStrategy
            MediaType.VIDEOS -> MediaStoreVideoStrategy
            MediaType.AUDIOS -> MediaStoreAudioStrategy
            MediaType.DOWNLOADS -> MediaStoreDownloadsStrategy
            MediaType.DOCUMENTS -> MediaStoreDocumentsStrategy
            MediaType.APPLICATIONS -> MediaStoreApplicationStrategy
            MediaType.ALL -> MediaStoreAllStrategy
        }
        return fileBrowserRepository.loadFiles(strategy)
    }
}
