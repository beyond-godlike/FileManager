package com.example.filemanager.presentation.images

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImagesViewModel: ViewModel()  {
    private val _imageItems = MutableStateFlow<ImageItemsState>(ImageItemsState.Empty)
    val imageItems: StateFlow<ImageItemsState> = _imageItems.asStateFlow()

    fun updateImages(images: List<ImageItem>) {
        _imageItems.value = ImageItemsState.Success(images)
    }

    fun loadImages(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.MIME_TYPE
                )

                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

                val cursor: Cursor? = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )
                val images = mutableListOf<ImageItem>()

                cursor?.use { it ->
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val path = it.getString(pathColumn)
                        val name = it.getString(nameColumn)
                        val mimeType = it.getString(mimeTypeColumn)
                        val contentUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )

                        if (mimeType.startsWith("image/")) {
                            images.add(ImageItem(id, path, name, mimeType, contentUri))
                        }
                    }
                }
                updateImages(images)
            }
        }
    }
}