package com.example.filemanager.presentation.search

import com.example.filemanager.data.repository.MediaItem

sealed class SearchItemsState {
    data class Error(val e: String) : SearchItemsState()
    object Empty : SearchItemsState()
    data class Success(val images: List<MediaItem>) : SearchItemsState()
}