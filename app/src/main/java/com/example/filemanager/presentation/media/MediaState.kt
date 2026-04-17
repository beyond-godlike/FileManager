package com.example.filemanager.presentation.media

import com.example.filemanager.data.repository.MediaFile
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class MediaState (
    val media: PersistentList<MediaFile> = persistentListOf(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val deletionResult: Boolean? = null, // Null means no result yet
    val error: String? = null
)