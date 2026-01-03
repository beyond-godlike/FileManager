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
    object MediaScreen : Screen("media/{mediaType}") {
        fun createRoute(type: MediaType) = "media/${type.name}"
    }

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
        composable(route = Screen.MediaScreen.route) { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString("mediaType")
            val mediaType = MediaType.entries.find { it.name == typeName } ?: MediaType.ALL

            MediaScreen(navController, mediaType)
        }
        composable(route = Screen.StorageScreen.route) {
            StorageScreen()
        }
        composable(route = Screen.SearchScreen.route) {
            SearchScreen(navController = navController)
        }
    }
}