package com.example.filemanager.presentation.media.components

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.presentation.base.MediaThumbnail
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.Typography
import com.example.filemanager.presentation.utils.open
import kotlinx.collections.immutable.PersistentList

@Composable
fun MediaGalleryGrid(
    media: PersistentList<MediaFile>,
    paddings: PaddingValues
) {
    val grouped = remember(media) {
        media.groupBy { it.date.toString() }
            .toList()
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
            key = { it.first } // date
        ) { (date, imagesForDate) ->

            Text(
                text = date,
                style = Typography.labelMedium,
                modifier = Modifier.padding(
                    top = Dimens.defaultPadding,
                    bottom = Dimens.smallPadding
                )
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                items(
                    items = imagesForDate,
                    key = { it.id }
                ) { item ->
                    GridItemCard(item)
                }
            }
        }
    }
}

@Composable
fun GridItemCard(
    item: MediaFile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .padding(end = Dimens.smallPadding)
            .clickable {
                item.contentUri.open(context, item.mimeType)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation()
    ) {
        ImageWithText(item, item.size)
    }
}

@Composable
fun ImageWithText(item: MediaFile, text: String) {
    val context = LocalContext.current
    Box {
        val size = remember { Size(120, 120) }
        MediaThumbnail(item, context, size)
        Text(
            text = text,
            color = Color.White,
            style = Typography.titleSmall,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimens.smallPadding)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(4.dp)
        )
    }
}