package com.example.filemanager.presentation.media

import com.example.filemanager.data.repository.MediaItem

data class MediaState (
    val media: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)