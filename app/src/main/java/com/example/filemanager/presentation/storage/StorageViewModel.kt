package com.example.filemanager.presentation.storage

import android.os.Environment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class StorageViewModel : ViewModel() {
    val path = Environment.getExternalStorageDirectory().path //каждый раз новый
    val root = File(path)
    val filesAndFolders = root.listFiles()


    private val _storageItems = MutableStateFlow<StorageScreenState>(StorageScreenState.Empty)
    val storageItems: StateFlow<StorageScreenState> = _storageItems.asStateFlow()

    private fun updateItems(items: Array<File>?) {
        if(!items.isNullOrEmpty())
            _storageItems.value =  StorageScreenState.Success(items)
    }


    fun loadStorageItems() {
        // path = new path
        updateItems(filesAndFolders)
    }
}