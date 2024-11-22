package com.example.filemanager.presentation.home

import com.example.filemanager.presentation.base.Intent

sealed class HomeIntent : Intent {
    object LoadMedia : HomeIntent()
}