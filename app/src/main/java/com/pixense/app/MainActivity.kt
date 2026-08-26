package com.pixense.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixense.app.data.model.ThemeMode
import com.pixense.app.service.CameraCaptureService
import com.pixense.app.ui.screen.CameraAiScreen
import com.pixense.app.ui.theme.MyApplicationTheme
import com.pixense.app.ui.viewmodel.CameraAiViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {

    private val viewModel: com.pixense.app.ui.viewmodel.CameraAiViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()
            val useDarkTheme = when (themeMode) {
                _root_ide_package_.com.pixense.app.data.model.ThemeMode.SYSTEM -> isSystemDark
                _root_ide_package_.com.pixense.app.data.model.ThemeMode.LIGHT -> false
                _root_ide_package_.com.pixense.app.data.model.ThemeMode.DARK -> true
            }

            _root_ide_package_.com.pixense.app.ui.theme.MyApplicationTheme(darkTheme = useDarkTheme) {
                // Determine permissions based on API level
                val permissionsToRequest = buildList {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.READ_MEDIA_IMAGES)
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }

                val permissionsState =
                    rememberMultiplePermissionsState(permissions = permissionsToRequest)

                LaunchedEffect(permissionsState.allPermissionsGranted) {
                    if (permissionsState.allPermissionsGranted) {
                        if (viewModel.isAutoProcessEnabled.value) {
                            _root_ide_package_.com.pixense.app.service.CameraCaptureService.start(this@MainActivity)
                        }
                        viewModel.refreshLatestPhoto()
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    _root_ide_package_.com.pixense.app.ui.screen.CameraAiScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshLatestPhoto()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.refreshLatestPhoto()
    }
}

