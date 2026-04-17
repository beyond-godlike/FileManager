package com.example.filemanager.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.MediaRepository
import com.example.filemanager.presentation.media.MediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val repository: MediaRepository) : ViewModel() {
    private val _state = MutableStateFlow(MediaState())
    val state: StateFlow<MediaState> = _state.asStateFlow()

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadMedia -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    try {
                        val images = repository.loadMedia(MediaType.LAST_MEDIA)
                        _state.update { it.copy(
                            media = images.toPersistentList(),
                            isLoading = false
                        ) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message, isLoading = false) }
                    }
                }
            }
        }
    }
}