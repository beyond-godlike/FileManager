package com.example.filemanager.presentation.home

import com.example.filemanager.R
import com.example.filemanager.presentation.Screen

data class CategoryItem(
    val icon: Int,
    val title: String,
    val description: String,
    val route: String
)

val categories = listOf(
    CategoryItem(R.drawable.image_24, "Images", "", Screen.ImagesScreen.route),
    CategoryItem(R.drawable.video_24, "Video", "", Screen.VideosScreen.route),
    CategoryItem(R.drawable.audio_24, "Audio", "0 mb", Screen.ImagesScreen.route)
)
val storageCategories = listOf(
    CategoryItem(R.drawable.storage_24, "Storage", "", Screen.StorageScreen.route),
    CategoryItem(R.drawable.sd_storage_24, "SD Card", "", Screen.StorageScreen.route)
)