package com.example.filemanager.presentation.images

import androidx.compose.runtime.Composable
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.filemanager.data.ImageItem
import com.example.filemanager.presentation.home.HomeViewModel

@Composable
fun ImagesScreen(navController: NavController) {
    val viewModel: ImagesViewModel = hiltViewModel()
    val imageItemsState = viewModel.imageItems.collectAsState()
    when (imageItemsState.value) {
        is ImageItemsState.Error -> {
            Text(text = "Error", Modifier.fillMaxSize())
        }

        is ImageItemsState.Empty -> {
            viewModel.loadImages(LocalContext.current)
        }

        is ImageItemsState.Success -> {
            ImagesScreen(
                (imageItemsState.value as ImageItemsState.Success).images
            )

        }
    }
}

@Composable
fun ImagesScreen(images: List<ImageItem>) {
    val ctx = LocalContext.current
    ImagesGrid(
        images,
        onItemClick = {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(it.contentUri, it.mimeType)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }//.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        })
}

@Composable
fun BitmapItemRow(imageItem: Bitmap) {
    com.skydoves.landscapist.glide.GlideImage(
        imageModel = imageItem,
        contentDescription = "Image",
        modifier = Modifier
            .width(100.dp)
            .height(150.dp).clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        //loading = {
        //    CircularProgressIndicator(modifier = Modifier.size(40.dp))
        //}
    )
}


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedCard(
    item: ImageItem,
    onItemClick: (ImageItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onItemClick(item) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp), //радиус скругления углов
        elevation = CardDefaults.cardElevation() // тень для эффекта глубины
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val thumbnail: Bitmap = LocalContext.current.contentResolver
                .loadThumbnail(item.contentUri, Size(640, 480), null)

            BitmapItemRow(thumbnail)
            Text(item.name)
            Text(item.contentUri.toString())
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun ImagesGrid(imageItems: List<ImageItem>, onItemClick: (ImageItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp) // Adaptive number of columns
    ) {
        itemsIndexed(imageItems) { _, item ->
            RoundedCard(item = item, onItemClick = onItemClick)
        }
    }
}
