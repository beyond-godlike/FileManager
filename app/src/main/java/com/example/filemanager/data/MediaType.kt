package com.example.filemanager.data

import com.example.filemanager.R

enum class MediaType(val value: Int) {
    IMAGES(R.string.images),
    VIDEOS(R.string.video),
    AUDIOS(R.string.audio),
    DOWNLOADS(R.string.downloads),
    DOCUMENTS(R.string.documents),
    APPLICATIONS(R.string.applications)
}