package com.example.filemanager.data.repository.usecases

import android.content.Context
import com.example.filemanager.data.repository.MediaFile

class GetAppsUseCase {
    operator fun invoke(context: Context): List<MediaFile> {
        val list = mutableListOf<MediaFile>()
        return list
    }
}