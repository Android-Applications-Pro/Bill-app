package com.example.billapp

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectImageScreen(navController: NavController, imageViewModel: ImageViewModel = viewModel()) {
    val images = remember {
        listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7,
            R.drawable.image8,
            R.drawable.image9,
            R.drawable.image10
        )
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageViewModel.setImageUri(it, context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Image") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Button(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    Text("Upload Image")
                }
            }
            if (imageViewModel.selectedImageBitmap.value != null) {
                item {
                    Image(
                        bitmap = imageViewModel.selectedImageBitmap.value!!,
                        contentDescription = "Uploaded Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedImageUri", imageViewModel.selectedImageUri.value)
                                navController.navigateUp()
                            }
                    )
                }
            }
            items(images) { image ->
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "Selectable Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable {
                            imageViewModel.selectedImageBitmap.value = null
                            imageViewModel.selectedImageUri.value = null
                            navController.previousBackStackEntry?.savedStateHandle?.set("selectedImage", image)
                            navController.navigateUp()
                        }
                )
            }
        }
    }
}