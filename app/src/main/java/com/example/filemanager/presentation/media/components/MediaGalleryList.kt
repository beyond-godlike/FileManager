package com.example.filemanager.presentation.media.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.presentation.base.getThumbnail
import com.example.filemanager.presentation.media.MediaIntent
import com.example.filemanager.presentation.media.MediaViewModel
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.Typography
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MediaGalleryList(
    media: List<MediaFile>,
    paddings: PaddingValues,
    onDelete: (MediaFile) -> Unit
) {
    val ims = media.groupBy { it.date }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .padding(
                top = paddings.calculateTopPadding(),
                start = Dimens.defaultPadding,
                end = Dimens.defaultPadding,
            )
    ) {
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
                        context,
                        modifier = Modifier,
                        onDelete
                    )
                }
            }
        }
    }
}

@Composable
fun ListItemCard(
    item: MediaFile,
    context: Context,
    modifier: Modifier = Modifier,
    onDelete: (MediaFile) -> Unit
) {
    val thumbnail = getThumbnail(context, item, Size(100, 100))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.smallPadding),
        verticalAlignment = Alignment.CenterVertically

    ) {
        SmallImage(thumbnail, 48.dp)

        Spacer(modifier = Modifier.width(Dimens.defaultPadding))
        Column(
            modifier = Modifier.clickable {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    setDataAndType(item.contentUri, item.mimeType)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            }
        ) {
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
        OptionsMenu(
            onDelete = {
                onDelete(item)
            }
        )
    }
}

@Composable
fun SmallImage(bitmap: Bitmap, size: Dp) {
    GlideImage(
        imageModel = { bitmap },
        modifier = Modifier
            .width(size)
            .height(size),
    )

}

@Composable
fun OptionsMenu(onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Select") },
                onClick = {
                    // Handle Option 1 click
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Rename") },
                onClick = {
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    onDelete()
                    expanded = false
                }
            )
        }
    }
}
