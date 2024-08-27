package com.example.billapp.setting

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val packageName = context.packageName

    // Using remember to cache the result and avoid recomputation
    val packageInfo = remember {
        try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }


    val versionName = packageInfo?.versionName ?: "Unknown"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "關於我們") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Ca Bill Ba Ra",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "版本號: $versionName",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "開發者: Your Company Name",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "聯絡我們: cabillbara66@gmail.com",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "地址: 1234 Your Street, Your City, Your Country",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    val navController = rememberNavController()
    AboutScreen(navController = navController)
}