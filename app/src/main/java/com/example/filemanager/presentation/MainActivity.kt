package com.example.filemanager.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.filemanager.domain.PermsHelper
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.FileManagerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileManagerTheme {
                val navController = rememberNavController()

                val permissions = remember { PermsHelper.permissions() }
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = permissions
                )
                val lifecycleOwner = LocalLifecycleOwner.current

                LaunchedEffect(key1 = lifecycleOwner) {
                    lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }
                }

                when {
                    permissionsState.allPermissionsGranted -> {
                        Box {
                            Navigation(navController = navController)
                        }
                    }

                    permissionsState.shouldShowRationale -> {
                        PermissionAlertDialog(
                            "Please, accept the permissions",
                            onClick = { permissionsState.launchMultiplePermissionRequest() }
                        )
                    }

                    !permissionsState.shouldShowRationale && !permissionsState.allPermissionsGranted -> {
                        PermissionAlertDialog(
                            "perms are denied permanently",
                            onClick = { openAppSettings() }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun PermissionAlertDialog(text: String,  onClick: () -> Unit) {
    Column(
       modifier = Modifier.fillMaxSize(),
       horizontalAlignment = Alignment.CenterHorizontally,
       verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            onClick()
        }, modifier = Modifier.padding(Dimens.smallPadding)
        ) {
            Text(text)
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}