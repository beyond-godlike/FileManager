@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.media

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.filemanager.R
import com.example.filemanager.data.MediaType
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.media.components.MediaGalleryGrid
import com.example.filemanager.presentation.media.components.MediaGalleryList
import com.example.filemanager.presentation.theme.ui.Dimens

@Composable
fun MediaScreen(navController: NavController, type: MediaType) {
    val viewModel: MediaViewModel = hiltViewModel()
    val mediaState = viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(MediaIntent.LoadMedia(type), context)
    }

    Scaffold(
        topBar = {
            MediaTopAppBar(navController, viewModel)
        }
    ) { paddings ->
        when(viewModel.isGridLayout.value) {
            //передать state
            true -> MediaGalleryGrid(mediaState.value.media, paddings)
            false -> MediaGalleryList(mediaState.value.media, paddings)
        }
    }
}

@Composable
fun MediaTopAppBar(navController: NavController, viewModel: MediaViewModel) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.images), fontSize = 18.sp) },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigate(Screen.HomeScreen.route)
                }
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
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(
                onClick = {
                    viewModel.toggleLayout()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "List"
                )
            }
            IconButton(
                onClick = { }
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