package com.example.filemanager.data.storage.strategies

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.filemanager.data.repository.MediaFile

object MediaStoreApplicationStrategy : DataSourceStrategy {
    override val uri: Uri = MediaStore.Files.getContentUri(
        MediaStore.VOLUME_EXTERNAL
    )
    override val projection: Array<String> = emptyArray()
    override val defaultSortOrder: String = ""

    override fun selection(): String = "1 = 1"

    override fun selectionArgs(): Array<String>? {
        TODO("Not yet implemented")
    }

    override suspend fun fetchFiles(cursor: Cursor): List<MediaFile> {
        TODO("Not yet implemented")
    }
}