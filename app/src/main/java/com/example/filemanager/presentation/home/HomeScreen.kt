@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.home

import android.util.Size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.filemanager.R
import com.example.filemanager.data.repository.MediaFile
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.base.MediaThumbnail
import com.example.filemanager.presentation.base.NavigationItem
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.ImageSize
import com.example.filemanager.presentation.theme.ui.Typography
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val imageItemsState = viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(HomeIntent.LoadMedia)
    }

    ReadyHomeScreen(imageItemsState.value.media, navController, viewModel)
}

@Composable
fun ReadyHomeScreen(
    images: PersistentList<MediaFile>,
    navController: NavController,
    viewModel: HomeViewModel
) {

    val items = remember {
        listOf(
            NavigationItem(
                R.string.clean,
                Icons.Filled.Home,
                Icons.Outlined.Star,
                Screen.MediaScreen.route
            ),
            NavigationItem(
                R.string.trash,
                Icons.Filled.Settings,
                Icons.Outlined.Delete,
                Screen.HomeScreen.route
            ),
            NavigationItem(
                R.string.settings,
                Icons.Filled.Settings,
                Icons.Outlined.Settings,
                Screen.HomeScreen.route
            ),
            NavigationItem(
                R.string.help,
                Icons.Filled.Settings,
                Icons.Outlined.Info,
                Screen.HomeScreen.route
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var selectedItemIndex by rememberSaveable {
            mutableIntStateOf(0)
        }
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .requiredWidth(220.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Text(text = stringResource(item.titleRes))
                            },
                            selected = index == selectedItemIndex,
                            onClick = {
                                navController.navigate(item.route)
                                selectedItemIndex = index
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = stringResource(item.titleRes)
                                )
                            },
                            badge = {
                                item.badgeCount?.let {
                                    Text(text = item.badgeCount.toString())
                                }
                            },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            },
            drawerState = drawerState
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            SearchBar(
                                leadingIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu"
                                        )
                                    }
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { navController.navigate(Screen.SearchScreen.route) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    }
                                },
                                query = "",
                                onQueryChange = { navController.navigate(Screen.SearchScreen.route) },
                                onSearch = { },
                                active = false,
                                onActiveChange = { navController.navigate(Screen.SearchScreen.route) },
                                content = {
                                    Text("Searching for:", fontSize = 14.sp)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                },
                modifier = Modifier.padding(Dimens.largePadding)

            ) {
                MainScreen(navController = navController, paddings = it, images)
            }
        }
    }
}

@Composable
fun RowImages(images: PersistentList<MediaFile>) {
    LazyRow {
        items(
            items = images,
            key = { it.id }
        ) { item ->
            MediaThumbnail(item, LocalContext.current, Size(120, 150))
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    paddings: PaddingValues,
    images: PersistentList<MediaFile>
) {
    Column(
        modifier = Modifier.padding(
            start = paddings.calculateStartPadding(LayoutDirection.Ltr),
            top = paddings.calculateTopPadding(),
            end = paddings.calculateEndPadding(LayoutDirection.Ltr),
            bottom = paddings.calculateBottomPadding()
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.largePadding)
        ) {
            Text(
                text = stringResource(R.string.recent),
                style = Typography.labelMedium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = stringResource(R.string.show_all),
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        navController.navigate(Screen.MediaScreen.route)
                    }
                    .align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        RowImages(images)

        Text(
            text = stringResource(R.string.categories),
            style = Typography.labelMedium,
            modifier = Modifier.padding(top = Dimens.largePadding, bottom = Dimens.defaultPadding)
        )

        CategoriesGrid(categories, navController)

        // todo подборки
        Text(
            text = stringResource(R.string.all_storage),
            style = Typography.labelMedium,
            modifier = Modifier.padding(top = Dimens.largePadding, bottom = Dimens.defaultPadding)
        )
        CategoriesGrid(storageCategories, navController)
    }
}

@Composable
fun CategoriesGrid(categories: List<CategoryItem>, navController: NavController) {
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(Dimens.smallPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { item ->
            ImageWithText(
                painterResource(item.icon),
                title = context.getString(item.title),
                description = item.description,
                onClick = {
                    item.mediaType.let {
                        navController.navigate(Screen.MediaScreen.createRoute(it))
                    }
                }
            )
        }
    }
}

@Composable
fun ImageWithText(
    image: Painter,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable { onClick() }
    ) {
        Icon(
            image,
            contentDescription = "Image",
            modifier = Modifier
                .size(ImageSize.imageSizeSmall)
                .align(Alignment.CenterVertically),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(Dimens.defaultPadding))
        Column {
            Text(
                text = title,
                style = Typography.labelSmall
            )
            Text(
                text = description,
                fontSize = 11.sp
            )
        }
    }
}