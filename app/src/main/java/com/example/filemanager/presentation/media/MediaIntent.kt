package com.example.filemanager.presentation.media

import androidx.activity.result.IntentSenderRequest
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.MediaFile

sealed class MediaIntent {
    data class LoadMedia(val type: MediaType) : MediaIntent()
    data class DeleteFile(val file: MediaFile) : MediaIntent()
    data class HandleDeleteResult(val isSuccess: Boolean) : MediaIntent()
}

sealed class MediaFileEffect {
    data class ShowDeleteSuccess(val fileName: String) : MediaFileEffect()
    data class ShowDeleteError(val fileName: String, val errorMessage: String?) : MediaFileEffect()

    // For API 30+
    data class LaunchDeleteConfirmation(val intentSenderRequest: IntentSenderRequest?) :
        MediaFileEffect()
}