package com.example.filemanager.presentation.videos

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.filemanager.data.repository.MediaItem

@Composable
fun VideosScreen(navController: NavHostController, s: String) {
    val context = LocalContext.current
    val viewModel: VideosViewModel = hiltViewModel()
    val videoItemsState = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVideos(context)
    }

    VideoScreen(videoItemsState.value.videos)
}

@Composable
fun VideoScreen(videos: List<MediaItem>) {
    val ctx = LocalContext.current
    VideosGrid(
        videos,
        onItemClick = {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(it.contentUri, it.mimeType)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }//.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        })
}

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosGrid(videos: List<MediaItem>, onItemClick: (MediaItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp) // Adaptive number of columns
    ) {
        itemsIndexed(videos) { _, item ->
            Card(
                modifier = Modifier.padding(8.dp),
                onClick = { onItemClick(item) }) { // Add some spacing
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    val thumbnail: Bitmap = LocalContext.current.contentResolver
                        .loadThumbnail(item.contentUri, Size(640, 480), null)
                    BitmapItemRow(thumbnail)
                    Text(item.name)
                    Text(item.size.toString())
                }
            }
        }
    }
}

@Composable
fun BitmapItemRow(imageItem: Bitmap) {
    com.skydoves.landscapist.glide.GlideImage(
        imageModel = imageItem,
        contentDescription = "Image",
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        //contentScale = ContentScale.Crop,
        loading = {
            CircularProgressIndicator(modifier = Modifier.size(40.dp))
        }
    )
}