package com.example.filemanager.presentation.images

import com.example.filemanager.presentation.base.Intent

sealed class ImagesIntent : Intent {
    object LoadImages : ImagesIntent()
}