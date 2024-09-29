package com.example.filemanager.presentation.home

import com.example.filemanager.data.ImageItem

sealed class HomeIntent {
    object LoadImages : HomeIntent()
}