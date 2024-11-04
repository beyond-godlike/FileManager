package com.example.filemanager.presentation.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.runtime.Composable
import com.example.filemanager.data.ImageItem


@Composable
fun loadThumbnail(uri: Uri, context: Context, size: Size): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(
        context.contentResolver.openInputStream(uri),
        null, options
    )
    val scale = (options.outWidth / size.width).coerceAtLeast(options.outHeight / size.height)
    options.inJustDecodeBounds = false
    options.inSampleSize = scale
    return BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
}
@Composable
fun getThumbnail(context: Context, item: ImageItem, size: Size) : Bitmap {
    val thumbnail: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver
            .loadThumbnail(item.contentUri, size, null)
    } else {
        loadThumbnail(item.contentUri, context, size)
    }
    return thumbnail!!
}