// MainActivity.kt
package com.example.billapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.billapp.viewModel.MainViewModel


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 權限獲得，可以進行相關操作
            } else {
                // 權限被拒絕，可以顯示一個解釋或提示
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 禁止螢幕旋轉
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        installSplashScreen()
        setContent {
            MainScreen(
                viewModel = viewModel,
                requestPermission = { permission ->
                    requestPermissionLauncher.launch(permission)
                }
            )
        }
    }
}

