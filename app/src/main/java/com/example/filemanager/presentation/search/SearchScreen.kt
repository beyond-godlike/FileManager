@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.search

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.ImageSize.imageSizeMedium
import com.example.filemanager.presentation.theme.ui.Typography
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: SearchViewModel = hiltViewModel()
    val imageItemsState = viewModel.imageItems.collectAsState()
    val context = LocalContext.current.applicationContext

    when (imageItemsState.value) {
        is SearchItemsState.Error -> {
            Text((imageItemsState.value as SearchItemsState.Error).e, Modifier.fillMaxWidth())
        }

        is SearchItemsState.Empty -> {
            viewModel.dispatch(SearchIntent.LoadImages, context)
        }

        is SearchItemsState.Success -> {
            MySearchScreen(viewModel, navController)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchScreen(viewModel: SearchViewModel, navController: NavController) {
    val searchText by viewModel.searchText.collectAsState()
    val itemsList by viewModel.items.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    SearchBar(
        leadingIcon = {
            IconButton(
                onClick = { navController.navigate(Screen.HomeScreen.route) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = { },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        },
        shape = SearchBarDefaults.inputFieldShape,
        query = searchText,
        onQueryChange = viewModel::onSearchTextChange,
        onSearch = viewModel::onSearchTextChange,
        active = isSearching,
        onActiveChange = { viewModel.onToggleSearch() },
        placeholder = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.defaultPadding)),
        colors = SearchBarDefaults.colors(
            containerColor = Color.White
        )

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(itemsList) { item ->

                Row(modifier = Modifier.padding(Dimens.defaultPadding)) {
                    var thumbnail by remember { mutableStateOf<Bitmap?>(null) }
                    LaunchedEffect(item) {
                        scope.launch(Dispatchers.IO) {
                            val loadedThumbnail =
                                context.contentResolver.loadThumbnail(item.contentUri, Size(320, 240), null)
                            withContext(Dispatchers.Main) {
                                thumbnail = loadedThumbnail
                            }
                        }
                    }
                    if (thumbnail != null) {
                        IconRounded(imageItem = thumbnail!!)
                    }
                    Column(modifier = Modifier.padding(start = Dimens.defaultPadding)) {
                        Text(
                            item.name,
                            style = Typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            "",
                            style = Typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconRounded(imageItem: Bitmap) {
    GlideImage(
        imageModel = {imageItem},
        modifier = Modifier
            .width(imageSizeMedium)
            .height(imageSizeMedium)
            .clip(RoundedCornerShape(4.dp))
    )
}