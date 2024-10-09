package com.example.filemanager.presentation.videos

import com.example.filemanager.data.VideoItem

sealed class VideoItemsState {
    object Error : VideoItemsState()
    object Loading : VideoItemsState()
    object Empty : VideoItemsState()
    data class Success(val videos: List<VideoItem>) : VideoItemsState()
}