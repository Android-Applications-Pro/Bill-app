package com.example.billapp

import AvatarScreen
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.ButtonGreenColor
import com.example.billapp.ui.theme.theme.ButtonRedColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import com.example.billapp.home.StylishTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel,
    requestPermission: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf(viewModel.getUserBudget().toString()) }
    var isEditing by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email
            budget = it.budget.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "個人檔案",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBackgroundColor
                )
            )
        },
        containerColor = MainBackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar Section
            // Avatar Section
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(vertical = 8.dp)
                    .background(MainBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                AvatarScreen(avatarViewModel)
            }

            // Profile Info Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StylishTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "名稱",
                        readOnly = !isEditing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    )

                    StylishTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "電子信箱",
                        readOnly = !isEditing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    )

                    StylishTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = "預算",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    )
                }
            }

            Button(
                onClick = {
                    if (isEditing) {
                        user?.let {
                            val updatedUser = it.copy(
                                name = name,
                                email = email,
                                budget = budget.toIntOrNull() ?: 0
                            )
                            viewModel.updateUserProfile(updatedUser)
                            imageUri?.let { uri -> avatarViewModel.uploadAvatar(uri) }
                            viewModel.updateUserBudget(updatedUser.budget)
                        }
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) ButtonRedColor else ButtonGreenColor
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                AnimatedContent(
                    targetState = isEditing,
                    label = "button_label"
                ) { editing ->
                    Text(
                        if (editing) "儲存變更" else "編輯資料",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}