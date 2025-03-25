package com.example.filemanager.presentation.media

import com.example.filemanager.data.repository.MediaFile

data class MediaState (
    val media: List<MediaFile> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val deletionResult: Boolean? = null, // Null means no result yet
    val error: String? = null
)