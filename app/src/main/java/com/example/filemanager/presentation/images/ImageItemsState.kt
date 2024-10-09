package com.example.filemanager.presentation.images

import com.example.filemanager.data.ImageItem

sealed class ImageItemsState {
    object Error : ImageItemsState()
    object Empty : ImageItemsState()
    data class Success(val images: List<ImageItem>) : ImageItemsState()
}
