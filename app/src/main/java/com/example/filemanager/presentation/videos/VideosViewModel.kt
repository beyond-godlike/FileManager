package com.example.filemanager.presentation.videos

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.data.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosViewModel: ViewModel()  {
    private val _videoItems = MutableStateFlow<VideoItemsState>(VideoItemsState.Empty)
    val videoItems: StateFlow<VideoItemsState> = _videoItems.asStateFlow()

    fun updatesVideo(videos: List<VideoItem>) {
        _videoItems.value = VideoItemsState.Success(videos)
    }

    fun loadVideos(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val videoList = mutableListOf<VideoItem>()
                val collection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Video.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                        )
                    } else {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }

                val projection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.MIME_TYPE
                )

                val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

                val query = context.contentResolver.query(
                    collection,
                    projection,
                    null,
                    null,
                    sortOrder
                )
                query?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val duration = cursor.getInt(durationColumn)
                        val size = cursor.getInt(sizeColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val mimeType =
                            cursor.getString(mimeTypeColumn)

                        val videoItem = when (idColumn) {
                            0 -> VideoItem(id, contentUri, name, duration, size, mimeType)
                            else -> null
                        }
                        videoItem?.let { videoList.add(it) }
                    }
                    updatesVideo(videoList)
                }
            }
        }
    }
}