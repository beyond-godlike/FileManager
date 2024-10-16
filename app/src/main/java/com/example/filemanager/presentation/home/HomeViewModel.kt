package com.example.filemanager.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val repository: MediaRepository) : ViewModel() {
    private val _state = MutableStateFlow(MediaState())
    val state: StateFlow<MediaState> = _state.asStateFlow()

    //private val _isSearching = MutableStateFlow(false)
    //val isSearching = _isSearching.asStateFlow()


    fun dispatch(intent: HomeIntent, context: Context) {
        when (intent) {
            is HomeIntent.LoadImages -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    try {
                        val images = repository.loadImagesFromMediaStore(context)
                        _state.update { it.copy(images = images, isLoading = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message, isLoading = false) }
                    }
                }
            }
        }
    }
}