package com.example.filemanager.presentation.base

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.filemanager.R
import com.example.filemanager.data.repository.MediaItem


//https://stackoverflow.com/questions/58030463/album-art-column-is-deprecated-from-api-29-and-so-on-how-to-obtain-path
@Composable
fun getThumbnail(context: Context, item: MediaItem, size: Size) : Bitmap {
    val thumbnail: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver
            .loadThumbnail(item.contentUri.normalizeScheme(), size, null)
    } else  {
        MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, item.id, MediaStore.Images.Thumbnails.MINI_KIND, null)
    }
    return thumbnail ?: ContextCompat.getDrawable(context, R.drawable.placeholder_image)!!.toBitmap()
}