package com.example.filemanager.presentation.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.data.repository.MediaRepository
import com.example.filemanager.presentation.base.BaseViewModel
import com.example.filemanager.presentation.base.Intent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    val repository: MediaRepository
) : BaseViewModel() {
    private var job: Job? = null

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _imageItems = MutableStateFlow<SearchItemsState>(SearchItemsState.Empty)
    val imageItems: StateFlow<SearchItemsState> = _imageItems.asStateFlow()

    private val _allImageItems = MutableStateFlow<List<MediaFile>>(emptyList())

    private fun updateItems(items: List<MediaFile>) {
        _imageItems.value = SearchItemsState.Success(items)
        _allImageItems.value = items
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val items = searchText
        .debounce(300L)
        .distinctUntilChanged()
        .onEach { _isSearching.update { true } }
        .flatMapLatest { text ->
            if (text.isBlank()) {
                _allImageItems
            } else {
                _allImageItems
                    .map { allItems ->
                        allItems.filter { it.name.contains(text, ignoreCase = true) }
                    }
                    .flowOn(Dispatchers.Default)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _allImageItems.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    override fun dispatch(intent: Intent, context: Context) {
        when (intent) {
            is SearchIntent.LoadImages -> {
                job?.cancel()
                job = viewModelScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            updateItems(repository.loadMedia(context, MediaType.LAST_MEDIA))
                        }
                    } catch (e: CancellationException) {
                        _imageItems.value = SearchItemsState.Error(e.message.toString())
                    } finally {
                        job = null
                    }
                }
            }
        }
    }
}