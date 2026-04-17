package com.example.filemanager.data.repository

import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
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