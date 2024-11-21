package com.example.filemanager.presentation.videos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(val repository: MediaRepository): ViewModel()  {
    private val _state = MutableStateFlow(VideoState())
    val state: StateFlow<VideoState> = _state.asStateFlow()

    fun loadVideos(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val videos = repository.loadVideosFromMediaStore(context)
                _state.update { it.copy(videos, isLoading = false) }
            }
        }
    }
}