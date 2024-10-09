package com.example.filemanager.data

import android.net.Uri

data class VideoItem(
    val id: Long,
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int,
    val mimeType: String,
)
