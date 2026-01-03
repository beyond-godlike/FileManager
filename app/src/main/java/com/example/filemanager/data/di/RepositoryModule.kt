package com.example.filemanager.data.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.example.filemanager.data.repository.FileBrowserRepository
import com.example.filemanager.data.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideFileBrowserRepository(
        contentResolver: ContentResolver
    ): FileBrowserRepository =
        FileBrowserRepository(
            contentResolver
        )

    @Provides
    @Singleton
    fun provideMediaRepository(
        fileBrowserRepository: FileBrowserRepository
    ): MediaRepository = MediaRepository(fileBrowserRepository)
}