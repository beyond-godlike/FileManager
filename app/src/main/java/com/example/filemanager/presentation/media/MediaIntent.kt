package com.example.filemanager.presentation.media

import com.example.filemanager.data.MediaType

sealed class MediaIntent {
    data class LoadMedia(val type: MediaType) : MediaIntent()
}