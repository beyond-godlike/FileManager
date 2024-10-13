package com.example.filemanager.presentation.search

import com.example.filemanager.presentation.base.Intent

sealed class SearchIntent : Intent {
    object LoadImages : SearchIntent()
}