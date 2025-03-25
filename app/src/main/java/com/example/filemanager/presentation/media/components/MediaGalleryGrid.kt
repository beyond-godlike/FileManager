package com.example.filemanager.presentation.media.components

import android.content.Context
import android.content.Intent
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.filemanager.presentation.base.getThumbnail
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.Typography
import com.skydoves.landscapist.glide.GlideImage

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
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    setDataAndType(item.contentUri, item.mimeType)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
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
            // todo constants
            val thumbnail = getThumbnail(LocalContext.current, item, Size(480, 480))
            ImageWithText(thumbnail, item.size)
        }
    }
}

@Composable
fun ImageWithText(imageItem: Any, text: String) {
    Box {
        GlideImage(
            { imageItem },
            modifier = Modifier
                .width(120.dp)
                .height(120.dp),
        )
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