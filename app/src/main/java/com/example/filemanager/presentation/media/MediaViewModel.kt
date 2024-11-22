package com.example.filemanager.presentation.media

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(val repository: MediaRepository) : ViewModel() {
    private val _state = MutableStateFlow(MediaState())
    val state: StateFlow<MediaState> = _state.asStateFlow()

    private val _isGridLayout = mutableStateOf(true)
    val isGridLayout: State<Boolean> = _isGridLayout

    fun toggleLayout() {
        _isGridLayout.value = !_isGridLayout.value
    }
    fun dispatch(intent: MediaIntent, context: Context) {
        when (intent) {
            is MediaIntent.LoadMedia -> {
                viewModelScope.launch {
                    when (intent.type) {
                        MediaType.IMAGES -> {
                            val images = repository.loadAllImagesFromMediaStore(context)
                            _state.update { it.copy(media = images, isLoading = false) }
                        }

                        MediaType.VIDEOS -> {
                            val videos = repository.loadVideosFromMediaStore(context)
                            _state.update { it.copy(media = videos, isLoading = false) }
                        }

                        MediaType.AUDIOS -> {
                            val audios = repository.loadAudios(context)
                            _state.update { it.copy(media = audios, isLoading = false) }
                        }
                        MediaType.DOWNLOADS -> {
                            val downloads = repository.loadDownloads(context)
                            _state.update { it.copy(media = downloads, isLoading = false) }
                        }
                        MediaType.DOCUMENTS -> {
                            val documents = repository.loadDocuments(context)
                            _state.update { it.copy(media = documents, isLoading = false) }
                        }
                        MediaType.APPLICATIONS -> {
                            val applications = repository.loadApplications(context)
                            _state.update { it.copy(media = applications, isLoading = false) }
                        }
                    }
                }
            }
        }
    }
}