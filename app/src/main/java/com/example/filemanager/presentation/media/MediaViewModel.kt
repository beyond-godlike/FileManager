package com.example.filemanager.presentation.media

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(val repository: MediaRepository) : ViewModel() {
    private val _state = MutableStateFlow(MediaState())
    val state: StateFlow<MediaState> = _state.asStateFlow()

    private val _isGridLayout = mutableStateOf(true)
    val isGridLayout: State<Boolean> = _isGridLayout

    private val _effect = Channel<MediaFileEffect>(Channel.BUFFERED)
    val effect = _effect.consumeAsFlow()

    private var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>? = null
    private var currentFileToDeleteUri: Uri? = null
    private var currentFileToDeleteName: String? = null

    fun toggleLayout() {
        _isGridLayout.value = !_isGridLayout.value
    }

    // Call this from your View to set the launcher
    fun setDeleteResultLauncher(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        deleteResultLauncher = launcher
    }


    fun dispatch(intent: MediaIntent, context: Context) {
        when (intent) {
            is MediaIntent.LoadMedia -> {
                viewModelScope.launch {
                    when (intent.type) {
                        MediaType.IMAGES -> {
                            val images = repository.loadMedia(context, MediaType.IMAGES)
                            _state.update { it.copy(media = images, isLoading = false) }
                        }

                        MediaType.LAST_MEDIA -> {
                            val lastMedia = repository.loadMedia(context, MediaType.LAST_MEDIA)
                            _state.update { it.copy(media = lastMedia, isLoading = false) }
                        }

                        MediaType.VIDEOS -> {
                            val videos = repository.loadMedia(context, MediaType.VIDEOS)
                            _state.update { it.copy(media = videos, isLoading = false) }
                        }

                        MediaType.AUDIOS -> {
                            val audios = repository.loadMedia(context, MediaType.AUDIOS)
                            _state.update { it.copy(media = audios, isLoading = false) }
                        }

                        MediaType.DOWNLOADS -> {
                            val downloads = repository.loadMedia(context, MediaType.DOWNLOADS)
                            _state.update { it.copy(media = downloads, isLoading = false) }
                        }

                        MediaType.DOCUMENTS -> {
                            val documents = repository.loadMedia(context, MediaType.DOWNLOADS)
                            _state.update { it.copy(media = documents, isLoading = false) }
                        }

                        MediaType.APPLICATIONS -> {
                            val applications = repository.loadMedia(context, MediaType.APPLICATIONS)
                            _state.update { it.copy(media = applications, isLoading = false) }
                        }
                    }
                }
            }

            is MediaIntent.DeleteFile -> deleteFile(context, intent.file)
            is MediaIntent.HandleDeleteResult -> handleDeletionResult(intent.isSuccess)
        }
    }

    private fun deleteFile(context: Context, file: MediaFile) {

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, deletionResult = null, error = null) }
            deleteMediaApi30(context, listOf(file.contentUri))
            //performDirectDelete(context, file.contentUri)
        }
    }

    private fun performDirectDelete(context: Context, fileUri: Uri) {
        viewModelScope.launch {
            val contentResolver = context.contentResolver
            val rowsDeleted = contentResolver.delete(fileUri, null, null)

            if (rowsDeleted > 0) {
                _state.update { it.copy(isDeleting = false, deletionResult = true) }
                _effect.send(
                    MediaFileEffect.ShowDeleteSuccess(
                        getFileNameFromUri(context, fileUri) ?: "File"
                    )
                )
            } else {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        deletionResult = false,
                        error = "Failed to delete file."
                    )
                }
                _effect.send(
                    MediaFileEffect.ShowDeleteError(
                        getFileNameFromUri(context, fileUri) ?: "File", "Failed to delete file."
                    )
                )
            }
        }
    }

    private fun deleteMediaApi30(context: Context, uris: List<Uri>) {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, deletionResult = null, error = null) }
            val contentResolver = context.contentResolver
            val intentSender = MediaStore.createDeleteRequest(contentResolver, uris).intentSender
            val senderRequest = IntentSenderRequest.Builder(intentSender).build()
            _effect.send(MediaFileEffect.LaunchDeleteConfirmation(senderRequest))
            // The result will be handled when HandleDeleteResult is dispatched
        }
    }

    private fun handleDeletionResult(isSuccess: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = false, deletionResult = isSuccess) }
            currentFileToDeleteName?.let { name ->
                if (isSuccess) {
                    _effect.send(MediaFileEffect.ShowDeleteSuccess(name))
                } else {
                    _effect.send(
                        MediaFileEffect.ShowDeleteError(
                            name,
                            "Deletion was not successful."
                        )
                    )
                }
            }
            currentFileToDeleteUri = null
            currentFileToDeleteName = null
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        // Implement this using ContentResolver and Cursor
        return null
    }
}