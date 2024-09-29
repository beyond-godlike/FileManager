package com.example.filemanager.data
import android.net.Uri

data class ImageItem(
    val id: Long,
    val path: String,
    val name: String,
    val mimeType: String,
    val contentUri: Uri
)