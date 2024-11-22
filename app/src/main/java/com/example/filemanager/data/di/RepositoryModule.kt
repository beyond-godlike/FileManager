package com.example.filemanager.data.di

import android.app.Application
import android.content.Context
import com.example.filemanager.data.repository.MediaRepository
import com.example.filemanager.data.repository.usecases.GetAppsUseCase
import com.example.filemanager.data.repository.usecases.GetAudiosUseCase
import com.example.filemanager.data.repository.usecases.GetDocumentsUseCase
import com.example.filemanager.data.repository.usecases.GetDownloadsUseCase
import com.example.filemanager.data.repository.usecases.GetImagesUseCase
import com.example.filemanager.data.repository.usecases.GetLastMediaUseCase
import com.example.filemanager.data.repository.usecases.GetVideosUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideGetLastMediaUseCase(): GetLastMediaUseCase = GetLastMediaUseCase()

    @Provides
    @Singleton
    fun provideGetImagesUseCase(): GetImagesUseCase = GetImagesUseCase()

    @Provides
    @Singleton
    fun provideGetVideosUseCase(): GetVideosUseCase = GetVideosUseCase()

    @Provides
    @Singleton
    fun provideGetDocumentsUseCase(): GetDocumentsUseCase = GetDocumentsUseCase()

    @Singleton
    @Provides
    fun provideGetAudiosUseCase(): GetAudiosUseCase = GetAudiosUseCase()

    @Singleton
    @Provides
    fun provideGetDownloadsUseCase(): GetDownloadsUseCase = GetDownloadsUseCase()

    @Singleton
    @Provides
    fun provideGetAppsUseCase(): GetAppsUseCase = GetAppsUseCase()


    @Provides
    @Singleton
    fun provideMediaRepository(
        getLastMediaUseCase: GetLastMediaUseCase,
        getImagesUseCase: GetImagesUseCase,
        getVideosUseCase: GetVideosUseCase,
        getAudiosUseCase: GetAudiosUseCase,
        getDownloadsUseCase: GetDownloadsUseCase,
        getDocumentsUseCase: GetDocumentsUseCase,
        getAppsUseCase: GetAppsUseCase
    ): MediaRepository = MediaRepository(
        getLastMediaUseCase,
        getImagesUseCase,
        getVideosUseCase,
        getAudiosUseCase,
        getDownloadsUseCase,
        getDocumentsUseCase,
        getAppsUseCase
    )
}