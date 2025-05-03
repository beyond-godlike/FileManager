@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.media

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.media.components.MediaGalleryGrid
import com.example.filemanager.presentation.media.components.MediaGalleryList
import com.example.filemanager.presentation.theme.ui.Dimens

@Composable
fun MediaScreen(navController: NavController, type: MediaType) {
    val viewModel: MediaViewModel = hiltViewModel()
    val mediaState = viewModel.state.collectAsState()
    val context = LocalContext.current.applicationContext

    val intentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.dispatch(
            MediaIntent.HandleDeleteResult(result.resultCode == Activity.RESULT_OK),
            context
        )
    }
    viewModel.setDeleteResultLauncher(intentSenderLauncher)

    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(MediaIntent.LoadMedia(type), context)

        viewModel.effect.collect { effect ->
            when (effect) {
                is MediaFileEffect.ShowDeleteSuccess -> {
                    println("Deletion successful: ${effect.fileName}")
                }

                is MediaFileEffect.ShowDeleteError -> {
                    println("Deletion error: ${effect.fileName} - ${effect.errorMessage}")
                }

                is MediaFileEffect.LaunchDeleteConfirmation -> {
                    effect.intentSenderRequest?.let { request ->
                        intentSenderLauncher.launch(request)
                    }
                }
                // Handle other effects
            }
        }
    }
    val onDeleteClick: (MediaFile) -> Unit = remember {
        { file ->
            viewModel.dispatch(MediaIntent.DeleteFile(file), context)
        }
    }

    Scaffold(
        topBar = {
            MediaTopAppBar(navController, viewModel, type.value)
        }
    ) { paddings ->
        when (viewModel.isGridLayout.value) {
            //передать state
            true -> MediaGalleryGrid(mediaState.value.media, paddings)
            false -> MediaGalleryList(mediaState.value.media, paddings, onDelete = {
                onDeleteClick(it)
            })
        }
    }
}

@Composable
fun MediaTopAppBar(navController: NavController, viewModel: MediaViewModel, value: Int) {
    TopAppBar(
        title = { Text(text = stringResource(value), fontSize = 18.sp) },
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

        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.defaultPadding)
    )
}