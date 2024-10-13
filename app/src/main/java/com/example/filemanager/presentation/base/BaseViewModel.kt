package com.example.filemanager.presentation.base

import android.content.Context
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    abstract fun dispatch(intent: Intent, context: Context)
}