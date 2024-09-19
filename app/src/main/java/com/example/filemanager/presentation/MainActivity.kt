package com.example.filemanager.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.example.filemanager.presentation.home.HomeScreen
import com.example.filemanager.presentation.theme.ui.FileManagerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileManagerTheme {

                val navController = rememberNavController()

                val permissionsState = rememberMultiplePermissionsState(
                    permissions = getListAccordingToVersion()
                )

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )

                when {
                    permissionsState.allPermissionsGranted -> {
                        Box(modifier = Modifier.padding(26.dp)) {
                            Navigation(navController = navController)
                            HomeScreen(navController)
                        }
                    }

                    permissionsState.shouldShowRationale -> {
                        PermissionAlertDialog("Please, accept the permissions")
                    }
                    !permissionsState.shouldShowRationale && !permissionsState.allPermissionsGranted -> {
                        PermissionAlertDialog("perms are denied permanently")
                    }
                }
            }

        }
    }
}

@Composable
fun getListAccordingToVersion(): List<String> {
    var list: List<String> = listOf()

    //Android is 13 (R) or above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        list = listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    }
    // Android 12: Build.VERSION_CODES.S (API level 31)
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S) {
        list = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
    }
    //Android 11: Build.VERSION_CODES.R (API level 30)
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
        list = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }
    //Android 10: Build.VERSION_CODES.Q (API level 29) or lower
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        list = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    return list
}

@Composable
fun PermissionAlertDialog(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text)
    }
}