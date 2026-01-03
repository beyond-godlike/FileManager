package com.example.filemanager.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import leakcanary.LeakCanary

@HiltAndroidApp
class FileManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LeakCanary.config = LeakCanary.config.copy(
            dumpHeap = true,  // включить/выключить дампы
            retainedVisibleThreshold = 5 // сколько задержанных объектов до уведомления
        )
    }
}