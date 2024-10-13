package com.example.filemanager.presentation.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.ImageItem
import com.example.filemanager.data.MediaRepository
import com.example.filemanager.presentation.base.BaseViewModel
import com.example.filemanager.presentation.base.Intent
import com.example.filemanager.presentation.home.HomeIntent
import com.example.filemanager.presentation.images.ImageItemsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    val repository: MediaRepository
) : BaseViewModel() {
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _imageItems = MutableStateFlow<ImageItemsState>(ImageItemsState.Empty)
    val imageItems: StateFlow<ImageItemsState> = _imageItems.asStateFlow()
    var textList: List<String> = listOf("image", "img", "img2", "img3", "img4", "img5", "img6", "img7", "img8", "img9", "img10")

    fun updateItems(items: List<ImageItem>) {
        _imageItems.value = ImageItemsState.Success(items)
        //textList = items.map { it.name }
        //textList = listOf("image", "img", "img2", "img3", "img4", "img5", "img6", "img7", "img8", "img9", "img10")
    }

    private val _items = MutableStateFlow(textList)
    val items = searchText
        .debounce(2000L)
        .combine(_items) { text, items ->
            if (text.isBlank()) {
                items
            } else {
                items.filter {
                    it.contains(text, ignoreCase = true)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _items.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onToogleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    override fun dispatch(intent: Intent, context: Context) {
        when (intent) {
            is SearchIntent.LoadImages -> {
                viewModelScope.launch {
                    updateItems(repository.loadImagesFromMediaStore(context))
                }
            }
        }
    }
}