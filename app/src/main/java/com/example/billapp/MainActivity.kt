// MainActivity.kt
package com.example.billapp

import DailyExperienceWorker
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import com.example.billapp.ui.theme.custom_jf_Typography
import java.util.concurrent.TimeUnit
import androidx.work.Constraints



class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val avatarViewModel: AvatarViewModel by viewModels()

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
        if (viewModel.isUserLoggedIn.value) {
            setupDailyExperienceWork()
        }
        setContent {
            MaterialTheme (
                typography = custom_jf_Typography
            ) {
                MainScreen(
                    viewModel = viewModel,
                    avatarViewModel = avatarViewModel,
                    requestPermission = { permission ->
                        requestPermissionLauncher.launch(permission)
                    }
                )
            }
        }
    }
    private fun setupDailyExperienceWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyExperienceWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "dailyExperienceIncrease",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}

