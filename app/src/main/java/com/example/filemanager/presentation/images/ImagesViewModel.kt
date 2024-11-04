package com.example.filemanager.presentation.images

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.MediaRepository
import com.example.filemanager.presentation.home.MediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(val repository: MediaRepository) : ViewModel() {
    private val _state = MutableStateFlow(MediaState())
    val state: StateFlow<MediaState> = _state.asStateFlow()

    private val _isGridLayout = mutableStateOf(true)
    val isGridLayout: State<Boolean> = _isGridLayout

    fun toggleLayout() {
        _isGridLayout.value = !_isGridLayout.value
    }
    fun dispatch(intent: ImagesIntent, context: Context) {
        when (intent) {
            is ImagesIntent.LoadImages -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    try {
                        val images = repository.loadAllImagesFromMediaStore(context)
                        _state.update { it.copy(images = images, isLoading = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message, isLoading = false) }
                    }
                }
            }
        }
    }
}