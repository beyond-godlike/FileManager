package com.example.filemanager.presentation.images.components

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.filemanager.data.ImageItem
import com.example.filemanager.presentation.base.getThumbnail
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.Typography


@Composable
fun ImageGalleryList(images: List<ImageItem>, paddings: PaddingValues) {
    val ims = images.groupBy { it.date }
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.padding(paddings)) {
        items(ims.entries.toList()) { entry ->
            val date = entry.key
            val imagesForDate = entry.value
            Text(
                text = date.toString(),
                style = Typography.labelMedium,
                modifier = Modifier.padding(top = Dimens.smallPadding, bottom = Dimens.smallPadding)
            )
            val h = (imagesForDate.size.dp * 64) + 32.dp
            LazyColumn(
                modifier = Modifier.height(h)
            ) {
                items(imagesForDate) { item ->
                    ListItemCard(
                        item,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun ListItemCard(
    item: ImageItem,
    modifier: Modifier = Modifier
) {
    val thumbnail = getThumbnail(LocalContext.current, item, Size(100, 100))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.smallPadding),
        verticalAlignment = Alignment.CenterVertically

    ) {
        SmallImage(thumbnail, 48.dp)

        Spacer(modifier = Modifier.width(Dimens.defaultPadding))
        Column {
            Text(
                text = item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxSize(0.8f)
            )
            Text(
                text = item.size
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* Handle options click */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
    }
}

@Composable
fun SmallImage(bitmap: Bitmap, size: Dp) {
    com.skydoves.landscapist.glide.GlideImage(
        imageModel = bitmap,
        contentDescription = "Image",
        modifier = Modifier
            .width(size)
            .height(size),
    )

}