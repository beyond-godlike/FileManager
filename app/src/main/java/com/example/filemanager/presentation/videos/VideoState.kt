package com.example.filemanager.presentation.videos

import com.example.filemanager.data.repository.MediaItem

data class VideoState(
    val videos: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
