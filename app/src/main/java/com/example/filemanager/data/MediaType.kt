package com.example.filemanager.data

import com.example.filemanager.R

enum class MediaType(val value: Int) {
    IMAGES(R.string.images),
    LAST_MEDIA(R.string.last_videos),
    VIDEOS(R.string.video),
    AUDIOS(R.string.audio),
    DOWNLOADS(R.string.downloads),
    DOCUMENTS(R.string.documents),
    APPLICATIONS(R.string.applications)
}