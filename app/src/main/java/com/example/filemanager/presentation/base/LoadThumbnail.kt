package com.example.filemanager.presentation.base

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.filemanager.R
import com.example.filemanager.data.repository.MediaFile
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MediaThumbnail(item: MediaFile, context: Context, size: Size) {
    var isLoading by remember(item.contentUri) { mutableStateOf(true) }

    val thumbnailState = produceState<Bitmap?>(initialValue = null, item.contentUri) {
        isLoading = true
        value = withContext(Dispatchers.IO) {
            try {
                context.contentResolver.loadThumbnail(
                    item.contentUri,
                    Size(size.width * 2, size.height * 2),
                    null
                )
            } catch (e: Exception) {
                null
            }
        }
        isLoading = false
    }

    val placeholderRes = when {
        item.mimeType.contains("pdf") -> R.drawable.document_24
        item.mimeType.contains("zip") || item.mimeType.contains("rar") -> R.drawable.ic_archive
        item.mimeType.startsWith("audio") -> R.drawable.ic_music
        item.mimeType.contains("apk") -> R.drawable.apps_24
        else -> R.drawable.placeholder_image
    }

    val bitmap = thumbnailState.value

    if (isLoading) {
        Box(
            modifier = Modifier.size(size.width.dp, size.height.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
    } else {
        ImageRounded(bitmap, size, placeholderRes)
    }
}

@Composable
fun ImageRounded(imageItem: Bitmap?, size: Size, placeholderRes: Int) {
    GlideImage(
        imageModel = { imageItem },
        modifier = Modifier
            .size(size.width.dp, size.height.dp)
            .clip(RoundedCornerShape(16.dp)),
        loading = {
            Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        },
        failure = {
            Image(
                painter = painterResource(placeholderRes),
                contentDescription = null,
                modifier = Modifier
                    .size(size.width.dp, size.height.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    )
}