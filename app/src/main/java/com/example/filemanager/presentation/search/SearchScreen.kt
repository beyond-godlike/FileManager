@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.images.ImageItemsState
import com.example.filemanager.presentation.theme.ui.Dimens

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: SearchViewModel = hiltViewModel()
    val imageItemsState = viewModel.imageItems.collectAsState()
    val context = LocalContext.current

    when (imageItemsState.value) {
        is ImageItemsState.Error -> {
            Text("Error loading data")
        }

        is ImageItemsState.Empty -> {
            viewModel.dispatch(SearchIntent.LoadImages, context)
        }

        is ImageItemsState.Success -> {
            MySearchScreen(viewModel, navController)

        }

    }
}

@Composable
fun MySearchScreen(viewModel: SearchViewModel, navController: NavController) {
    val searchText by viewModel.searchText.collectAsState()
    val itemsList by viewModel.items.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

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
        onActiveChange = { viewModel.onToogleSearch() },
        placeholder = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.defaultPadding))

    ) {
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(itemsList) { item ->
                    Text(
                        item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.defaultPadding)
                    )
                }
            }
        }
    }
}