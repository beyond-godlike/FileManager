package com.example.filemanager.presentation.media.components

import android.content.Context
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun MediaGalleryGrid(media: List<MediaFile>, paddings: PaddingValues) {
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
                modifier = Modifier.padding(
                    top = Dimens.defaultPadding,
                    bottom = Dimens.smallPadding
                )
            )
            LazyRow {
                items(imagesForDate) { item ->
                    GridItemCard(
                        item = item,
                        context = context,
                    )
                }
            }

        }
    }
}

@Composable
fun GridItemCard(
    item: MediaFile,
    context: Context,
    modifier: Modifier = Modifier
) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ImageWithText(item, item.size, context)
        }
    }
}

@Composable
fun ImageWithText(item: MediaFile, text: String, context: Context) {
    Box {
        MediaThumbnail(item, context, Size(120, 120))
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