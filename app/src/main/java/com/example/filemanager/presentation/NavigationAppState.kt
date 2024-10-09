package com.example.filemanager.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.filemanager.presentation.home.HomeScreen
import com.example.filemanager.presentation.images.ImagesScreen
import com.example.filemanager.presentation.storage.StorageScreen
import com.example.filemanager.presentation.videos.VideosScreen

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object ImagesScreen : Screen("images")
    object VideosScreen : Screen("videos")
    object StorageScreen : Screen("storage")
}
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.ImagesScreen.route) {
            ImagesScreen(navController = navController)
        }
        composable(route = Screen.VideosScreen.route) {
            VideosScreen()
        }
        composable(route = Screen.StorageScreen.route) {
            StorageScreen()
        }
    }
}