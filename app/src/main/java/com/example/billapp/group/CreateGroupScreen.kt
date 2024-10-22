package com.example.billapp.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.viewModel.MainViewModel
import com.example.billapp.viewModel.GroupCreationStatus
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.Brown5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroup(
    viewModel: MainViewModel,
    navController: NavController
) {
    var groupName by remember { mutableStateOf("") }
    var selectedImageId by remember { mutableStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val user by viewModel.user.collectAsState()
    val userId by remember { mutableStateOf(user?.id ?: "") }

    val groupCreationStatus by viewModel.groupCreationStatus.collectAsState()

    Scaffold(
        modifier = Modifier
            .background(color = MainBackgroundColor),
        topBar = {
            TopAppBar(
                title = { Text("創建群組", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp()}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Brown5 // 設定背景顏色為 Brown5
                )
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MainBackgroundColor)
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { showBottomSheet = true }
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getImageResourceById(selectedImageId)),
                    contentDescription = "Group Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Brown5, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createGroupWithImageId(groupName, selectedImageId)
                    viewModel.updateUserExperience(userId,10)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown5  // Use containerColor instead of backgroundColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Group")
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Select Group Image", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    CreateGroupPresetImages(
                        onImageSelected = { id ->
                            selectedImageId = id
                            showBottomSheet = false
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(groupCreationStatus) {
        if (groupCreationStatus == GroupCreationStatus.SUCCESS) {
            navController.navigateUp()
            viewModel.resetGroupCreationStatus()
        }
    }
}

@Composable
fun CreateGroupPresetImages(
    onImageSelected: (Int) -> Unit
) {
    val presets = listOf(1, 2, 3, 4)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(presets) { imageId ->
            Image(
                painter = painterResource(id = getImageResourceById(imageId)),
                contentDescription = "Preset Group Image $imageId",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { onImageSelected(imageId) }
            )
        }
    }
}

fun getImageResourceById(imageId: Int): Int {
    return when (imageId) {
        1 -> R.drawable.image_group_travel
        2 -> R.drawable.image_group_house
        3 -> R.drawable.image_group_dining
        4 -> R.drawable.image_group_shopping
        else -> R.drawable.ic_board_place_holder
    }
}