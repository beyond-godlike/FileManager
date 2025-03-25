package com.example.filemanager.data.repository

import android.net.Uri
import kotlinx.datetime.LocalDate

data class MediaFile (
    val id: Long,
    val path: String,
    val date: LocalDate,
    val name: String,
    val mimeType: String,
    val duration: Int,
    val contentUri: Uri,
    val size: String
)