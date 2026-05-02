@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.media

import android.app.Activity
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.filemanager.data.MediaType
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.media.components.MediaGalleryGrid
import com.example.filemanager.presentation.media.components.MediaGalleryList
import com.example.filemanager.presentation.theme.ui.Dimens

@Composable
fun MediaScreen(
    navController: NavController,
    type: MediaType,
    viewModel: MediaViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isGrid by viewModel.isGridLayout.collectAsStateWithLifecycle()

    val intentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.dispatch(
            MediaIntent.HandleDeleteResult(result.resultCode == Activity.RESULT_OK),
            context
        )
    }

    LaunchedEffect(intentSenderLauncher) {
        viewModel.setDeleteResultLauncher(intentSenderLauncher)
    }

    LaunchedEffect(type) {
        viewModel.dispatch(MediaIntent.LoadMedia(type), context)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MediaFileEffect.ShowDeleteSuccess -> {
                    Log.d("Media", "Deleted: ${effect.fileName}")
                }

                is MediaFileEffect.ShowDeleteError -> {
                    Log.e("Media", "Error: ${effect.errorMessage}")
                }

                is MediaFileEffect.LaunchDeleteConfirmation -> {
                    effect.intentSenderRequest?.let(intentSenderLauncher::launch)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MediaTopAppBar(
                navController = navController,
                titleRes = type.value,
                onToggleLayout = viewModel::toggleLayout
            )
        }
    ) { paddings ->

        val onDeleteClick: (MediaFile) -> Unit = {
            viewModel.dispatch(MediaIntent.DeleteFile(it), context)
        }

        if (isGrid) {
            MediaGalleryGrid(state.media, paddings)
        } else {
            MediaGalleryList(
                media = state.media,
                paddings = paddings,
                onDelete = onDeleteClick
            )
        }
    }
}

@Composable
fun MediaTopAppBar(
    navController: NavController,
    titleRes: Int,
    onToggleLayout: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(titleRes), fontSize = 18.sp) },
        navigationIcon = {
            IconButton(
                onClick = { navController.navigate(Screen.HomeScreen.route) }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(
                onClick = { navController.navigate(Screen.SearchScreen.route) }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }

            IconButton(onClick = onToggleLayout) {
                Icon(Icons.Default.List, contentDescription = "Toggle layout")
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.defaultPadding)
    )
}