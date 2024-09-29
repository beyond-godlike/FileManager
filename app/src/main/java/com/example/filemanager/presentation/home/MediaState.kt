package com.example.filemanager.presentation.home

import com.example.filemanager.data.ImageItem

data class MediaState(
    val images: List<ImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
