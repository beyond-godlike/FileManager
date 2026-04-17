package com.example.filemanager.presentation.media.components

import android.util.Size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.presentation.base.MediaThumbnail
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.Typography
import com.example.filemanager.presentation.utils.open
import kotlinx.collections.immutable.PersistentList

@Composable
fun MediaGalleryList(
    media: PersistentList<MediaFile>,
    paddings: PaddingValues,
    onDelete: (MediaFile) -> Unit
) {
    val grouped = remember(media) {
        media.groupBy { it.date }.toList()
    }

    LazyColumn(
        modifier = Modifier.padding(
            top = paddings.calculateTopPadding(),
            start = Dimens.defaultPadding,
            end = Dimens.defaultPadding,
        )
    ) {
        items(
            items = grouped,
            key = { it.first }
        ) { (date, imagesForDate) ->

            Text(
                text = date.toString(),
                style = Typography.labelMedium,
                modifier = Modifier.padding(
                    top = Dimens.smallPadding,
                    bottom = Dimens.smallPadding
                )
            )
            Column {
                imagesForDate.forEach { item ->
                    ListItemCard(
                        item = item,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
fun ListItemCard(
    item: MediaFile,
    modifier: Modifier = Modifier,
    onDelete: (MediaFile) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SmallImage(item)

        Spacer(modifier = Modifier.width(Dimens.defaultPadding))

        Column(
            modifier = Modifier.clickable {
                item.contentUri.open(context, item.mimeType)
            }
        ) {
            Text(
                text = item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Text(text = item.size)
        }

        Spacer(modifier = Modifier.weight(1f))

        OptionsMenu(
            onDelete = { onDelete(item) }
        )
    }
}

@Composable
fun SmallImage(item: MediaFile) {
    val size = remember { Size(48, 48) }
    val context = LocalContext.current

    MediaThumbnail(item, context, size)
}

@Composable
fun OptionsMenu(onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
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
