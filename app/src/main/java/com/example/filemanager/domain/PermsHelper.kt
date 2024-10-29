package com.example.filemanager.domain

import android.Manifest
import android.os.Build

class PermsHelper {
    companion object {
        fun permissions(): List<String> {
            //Android is 13 (R) or above
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
            //Android 11: Build.VERSION_CODES.R (API level 30)
            else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}