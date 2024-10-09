@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.filemanager.presentation.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.filemanager.presentation.Screen
import com.example.filemanager.presentation.base.NavigationItem
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import com.example.filemanager.data.ImageItem
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val imageItemsState = viewModel.state.collectAsState()
    val context = LocalContext.current

    // Since key1 = Unit is used, the LaunchedEffect will not be retriggered
    // when the composable recomposes due to configuration changes (e.g., screen rotation).
    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(HomeIntent.LoadImages, context)
    }

    ReadyHomeScreen(imageItemsState.value.images, navController, viewModel)
}

@Suppress("UNUSED_EXPRESSION")
@Composable
fun ReadyHomeScreen(
    images: List<ImageItem>,
    navController: NavController,
    viewModel: HomeViewModel
) {
    val items = listOf(
        NavigationItem(
            title = "Очистка",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = Screen.ImagesScreen.route
        ),
        NavigationItem(
            title = "Корзина",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = Screen.HomeScreen.route
        ),
        NavigationItem(
            title = "Настройки",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = Screen.HomeScreen.route
        ),
        NavigationItem(
            title = "Справка/Отзыв",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = Screen.HomeScreen.route
        )
    )


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
                                Text(text = item.title)
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
                                    contentDescription = item.title
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
            //Collecting states from ViewModel
            val searchText by viewModel.searchText.collectAsState()
            val isSearching by viewModel.isSearching.collectAsState()

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
                                        onClick = { /*TODO*/ },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    }
                                },
                                query = searchText,
                                onQueryChange = { viewModel::onSearchTextChange },
                                onSearch = {/*viewModel::onSearchTextChange*/ },
                                active = isSearching,
                                placeholder = {
                                    Text(
                                        "Searching for: $searchText",
                                        fontSize = 14.sp
                                    )
                                },
                                onActiveChange = { viewModel.onToogleSearch() },
                                content = {
                                    Text(searchText, fontSize = 14.sp)
                                }
                            )
                        }
                    )
                }

            ) {
                MainScreen(navController = navController, paddings = it, images)
            }
        }
    }
}

@Composable
fun loadThumbnail(uri: Uri, context: Context): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(
        context.contentResolver.openInputStream(uri),
        null, options
    )
    val scale = (options.outWidth / 320).coerceAtLeast(options.outHeight / 240)
    options.inJustDecodeBounds = false
    options.inSampleSize = scale
    return BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
}

@Composable
fun RowImages(images: List<ImageItem>) {
    LazyRow {
        items(images) { item ->
            val thumbnail: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                LocalContext.current.contentResolver
                    .loadThumbnail(item.contentUri, Size(320, 240), null)
            } else {
                loadThumbnail(item.contentUri, LocalContext.current)
            }
            ImageRounded(thumbnail!!)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun ImageRounded(imageItem: Bitmap) {
    com.skydoves.landscapist.glide.GlideImage(
        imageModel = imageItem,
        contentDescription = "Image",
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MainScreen(navController: NavController, paddings: PaddingValues, images: List<ImageItem>) {
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
                .padding(top = 24.dp)
        ) {
            Text(
                text = "Недавние",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "Показать все",
                fontSize = 14.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        // Handle the click event here
                    }
                    .align(Alignment.CenterEnd)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Spacer(modifier = Modifier.height(16.dp))
            RowImages(images)
        }
        // да, так и должно быть. там будет куча подразделов а эта хуета (возможно) сеткой

        Text(
            text = "Категории",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(categories.chunked(2)) { row ->
                LazyRow {
                    items(row) { item ->
                        ImageWithText(
                            painterResource(item.icon),
                            title = item.title,
                            description = item.description,
                            modifier = Modifier.padding(16.dp),
                            onClick = { item.route.let { navController.navigate(it) } }
                        )
                    }
                }
            }
        }

        // todo подборки
        Text(
            text = "Все хранилище",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(storageCategories.chunked(2)) { row ->
                LazyRow {
                    items(row) { item ->
                        ImageWithText(
                            painterResource(item.icon),
                            title = item.title,
                            description = item.description,
                            modifier = Modifier.padding(16.dp),
                            onClick = { navController.navigate(item.route) }
                        )
                    }
                }
            }
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
                .size(width = 24.dp, height = 24.dp)
                .align(Alignment.CenterVertically),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 14.sp
            )
        }
    }
}