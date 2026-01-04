package com.example.filemanager.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.example.filemanager.domain.PermsHelper
import com.example.filemanager.presentation.theme.ui.Dimens
import com.example.filemanager.presentation.theme.ui.FileManagerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileManagerTheme {
                val navController = rememberNavController()

                // Состояние для обычных разрешений (Фото, Видео)
                val permissionsState = rememberMultiplePermissionsState(PermsHelper.permissions())

                // Состояние конкретно для MANAGE_EXTERNAL_STORAGE
                var hasAllFilesAccess by remember { mutableStateOf(isAllFilesAdmin()) }

                // Проверка при возвращении в приложение (например, из настроек)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            hasAllFilesAccess = isAllFilesAdmin()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                LaunchedEffect(Unit) {
                    permissionsState.launchMultiplePermissionRequest()
                }

                // Логика отображения
                when {
                    // Если даны и обычные, и спец. разрешение
                    permissionsState.allPermissionsGranted && hasAllFilesAccess -> {
                        Navigation(navController = navController)
                    }

                    // Если обычные даны, но нет доступа ко всем файлам (для документов)
                    permissionsState.allPermissionsGranted && !hasAllFilesAccess -> {
                        PermissionAlertDialog(
                            text = "Для отображения документов нужен доступ ко всем файлам",
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                        data = Uri.parse("package:${packageName}")
                                    }
                                    startActivity(intent)
                                }
                            }
                        )
                    }

                    // Логика для обычных разрешений (твоя прежняя)
                    permissionsState.shouldShowRationale -> {
                        PermissionAlertDialog("Нужны базовые разрешения", onClick = { permissionsState.launchMultiplePermissionRequest() })
                    }

                    !permissionsState.allPermissionsGranted -> {
                        PermissionAlertDialog("Разрешения отклонены навсегда", onClick = { openAppSettings() })
                    }
                }
            }
        }
    }
}



fun isAllFilesAdmin(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        true // На версиях ниже 11 это разрешение не нужно
    }
}
@Composable
fun PermissionAlertDialog(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                onClick()
            }, modifier = Modifier.padding(Dimens.smallPadding)
        ) {
            Text(text)
        }
    }
}
fun Activity.openAppSettings() {
    val uri = Uri.parse("package:$packageName")
    Intent(
        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,
        uri
    ).also(::startActivity)
}