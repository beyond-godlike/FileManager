package com.example.filemanager.presentation.home

import androidx.compose.runtime.Immutable
import com.example.filemanager.R
import com.example.filemanager.data.MediaType

@Immutable
data class CategoryItem(
    val icon: Int,
    val title: Int,
    val description: String,
    val mediaType: MediaType
)

val categories = listOf(
    CategoryItem(R.drawable.download_24, R.string.downloads, "", MediaType.DOWNLOADS),
    CategoryItem(R.drawable.image_24, R.string.images, "", MediaType.IMAGES),
    CategoryItem(R.drawable.video_24, R.string.video, "", MediaType.VIDEOS),
    CategoryItem(R.drawable.audio_24, R.string.audio, "0 mb", MediaType.AUDIOS),
    CategoryItem(R.drawable.document_24, R.string.documents, "", MediaType.DOCUMENTS),
    CategoryItem(R.drawable.apps_24, R.string.applications, "", MediaType.APPLICATIONS),

)
val storageCategories = listOf(
    CategoryItem(R.drawable.storage_24, R.string.storage, "", MediaType.AUDIOS),
    CategoryItem(R.drawable.sd_storage_24, R.string.sd_card, "", MediaType.AUDIOS)
)