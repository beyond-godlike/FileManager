package com.example.filemanager.presentation.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.ImageItem
import com.example.filemanager.data.MediaRepository
import com.example.filemanager.presentation.base.BaseViewModel
import com.example.filemanager.presentation.base.Intent
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
import kotlin.text.contains

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
    var textList: List<ImageItem>? = null

    private val _allImageItems = MutableStateFlow<List<ImageItem>>(emptyList())

    fun updateItems(items: List<ImageItem>) {
        _imageItems.value = ImageItemsState.Success(items)
        _allImageItems.value = items
    }
    private val _items = MutableStateFlow(textList)

    val items = searchText
        .debounce(300L)
        .combine(_allImageItems) { text, allItems ->
            allItems.takeIf { text.isNotBlank() }
                ?.filter { it.name.contains(text, ignoreCase = true) }
                ?: allItems
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _allImageItems.value
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