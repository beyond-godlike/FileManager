package com.example.filemanager.presentation.storage

import java.io.File


sealed class StorageScreenState {
    object Error : StorageScreenState()
    object Empty : StorageScreenState()
    data class Success(val files: Array<File>) : StorageScreenState()
}