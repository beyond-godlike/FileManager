package com.example.filemanager.presentation.home

import com.example.filemanager.R
import com.example.filemanager.presentation.Screen

data class CategoryItem(
    val icon: Int,
    val title: Int,
    val description: String,
    val route: String
)

val categories = listOf(
    CategoryItem(R.drawable.download_24, R.string.downloads, "", Screen.HomeScreen.route),
    CategoryItem(R.drawable.image_24, R.string.images, "", Screen.ImagesScreen.route),
    CategoryItem(R.drawable.video_24, R.string.video, "", Screen.VideoScreen.route),
    CategoryItem(R.drawable.audio_24, R.string.audio, "0 mb", Screen.ImagesScreen.route),
    CategoryItem(R.drawable.document_24, R.string.documents, "", Screen.HomeScreen.route),
    CategoryItem(R.drawable.apps_24, R.string.applications, "", Screen.HomeScreen.route)
)
val storageCategories = listOf(
    CategoryItem(R.drawable.storage_24, R.string.storage, "", Screen.StorageScreen.route),
    CategoryItem(R.drawable.sd_storage_24, R.string.sd_card, "", Screen.StorageScreen.route)
)