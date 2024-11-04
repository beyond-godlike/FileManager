package com.example.filemanager.data
import android.net.Uri
import kotlinx.datetime.LocalDate

data class ImageItem(
    val id: Long,
    val path: String,
    val date: LocalDate,
    val name: String,
    val mimeType: String,
    val contentUri: Uri,
    val size: String
)