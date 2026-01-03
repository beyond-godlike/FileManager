package com.example.filemanager.data.repository

import android.content.ContentResolver
import com.example.filemanager.data.storage.strategies.DataSourceStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileBrowserRepository(
    private val contentResolver: ContentResolver
) {
    suspend fun loadFiles(dataSourceStrategy: DataSourceStrategy): List<MediaFile> =
        withContext(Dispatchers.IO) {
            val query = contentResolver.query(
                dataSourceStrategy.uri,
                dataSourceStrategy.projection,
                dataSourceStrategy.selection(),
                dataSourceStrategy.selectionArgs(),
                dataSourceStrategy.defaultSortOrder
            )

            val result = query?.use { dataSourceStrategy.fetchFiles(query) } ?: emptyList()

            result
        }

}