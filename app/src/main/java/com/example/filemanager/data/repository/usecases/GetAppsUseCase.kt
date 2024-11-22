package com.example.filemanager.data.repository.usecases

import android.content.Context
import com.example.filemanager.data.repository.MediaItem

class GetAppsUseCase {
    suspend operator fun invoke(context: Context): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        return list
    }
}