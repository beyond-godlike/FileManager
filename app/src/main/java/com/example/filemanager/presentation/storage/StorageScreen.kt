package com.example.filemanager.presentation.storage

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

@Composable
fun StorageScreen() {
    val viewModel: StorageViewModel = hiltViewModel()
    val state = viewModel.storageItems.collectAsState()
    when (state.value) {
        is StorageScreenState.Success -> {
            FilesScreen(
                (state.value as StorageScreenState.Success).files
            )
        }
        is StorageScreenState.Error -> {

        }
        is StorageScreenState.Empty -> {
            viewModel.loadStorageItems()
        }

    }
}

@Composable
fun FilesScreen(files: Array<File>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp) // Adaptive number of columns
    ) {
        itemsIndexed(files) { _, item ->
            //RoundedCard(item = item, onItemClick = onItemClick)
            Text(item.name)
        }
    }
}