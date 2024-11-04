@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.images

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.filemanager.R
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.images.components.ImageGalleryGrid
import com.example.filemanager.presentation.images.components.ImageGalleryList
import com.example.filemanager.presentation.theme.ui.Dimens

@Composable
fun ImagesScreen(navController: NavController) {
    val viewModel: ImagesViewModel = hiltViewModel()
    val imageItemsState = viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(ImagesIntent.LoadImages, context)
    }

    Scaffold(
        topBar = {
            ImagesTopAppBar(navController, viewModel)
        }
    ) { paddings ->
        when(viewModel.isGridLayout.value) {
            true -> ImageGalleryGrid(imageItemsState.value.images, paddings)
            false -> ImageGalleryList(imageItemsState.value.images, paddings)
        }
    }

}

@Composable
fun ImagesTopAppBar(navController: NavController, viewModel: ImagesViewModel) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.images),
                //style = Typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigate(Screen.HomeScreen.route)
                },
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Screen.SearchScreen.route)
                },
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(
                onClick = {
                    viewModel.toggleLayout()
                },
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "List"
                )
            }
            IconButton(
                onClick = {

                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Back"
                )
            }
        },

        modifier = Modifier.fillMaxWidth().padding(Dimens.defaultPadding)
    )
}

