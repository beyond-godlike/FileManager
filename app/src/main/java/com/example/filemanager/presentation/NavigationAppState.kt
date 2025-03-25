package com.example.filemanager.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.filemanager.data.MediaType
import com.example.filemanager.presentation.home.HomeScreen
import com.example.filemanager.presentation.media.MediaScreen
import com.example.filemanager.presentation.search.SearchScreen
import com.example.filemanager.presentation.storage.StorageScreen

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object ImagesScreen : Screen("images")
    object DocumentsScreen : Screen("documents")
    object DownloadsScreen : Screen("downloads")
    object AudioScreen : Screen("audio")
    object AppsScreen : Screen("apps")
    object VideoScreen : Screen("videos")
    object StorageScreen : Screen("storage")
    object SearchScreen : Screen("search")
}
@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.ImagesScreen.route) {
            MediaScreen(navController, MediaType.IMAGES)
        }
        composable(route = Screen.VideoScreen.route) {
            MediaScreen(navController, MediaType.VIDEOS)
        }
        composable(route = Screen.AudioScreen.route) {
            MediaScreen(navController, MediaType.AUDIOS)
        }
        composable(route = Screen.DocumentsScreen.route) {
            MediaScreen(navController, MediaType.DOCUMENTS)
        }
        composable(route = Screen.DownloadsScreen.route) {
            MediaScreen(navController, MediaType.DOWNLOADS)
        }
        composable(route = Screen.AppsScreen.route) {
            MediaScreen(navController, MediaType.APPLICATIONS)
        }
        composable(route = Screen.StorageScreen.route) {
            StorageScreen()
        }
        composable(route = Screen.SearchScreen.route) {
            SearchScreen(navController = navController)
        }
    }
}