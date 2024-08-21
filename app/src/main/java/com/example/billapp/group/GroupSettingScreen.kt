package com.example.billapp.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.R
import com.example.billapp.viewModel.MainViewModel

@Composable
fun GroupSettingScreen(
    groupId: String,
    viewModel: MainViewModel,
    navController: NavController
) {
    val group by viewModel.getGroup(groupId).collectAsState(initial = null)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            androidx.compose.material.TopAppBar(
                title = { androidx.compose.material.Text(group?.name ?: "Group Detail") },
                navigationIcon = {
                    androidx.compose.material.IconButton(onClick = { navController.navigateUp() }) {
                        androidx.compose.material.Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 債務關係區塊
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .background(Color.White)
                    .border(2.dp, Color.Gray)
                    .padding(24.dp)
            ) {
                Text(
                    text = "債務關係",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
            }

            // 管理員區塊
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "管理員",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // 成員按鈕
                    Button(
                        onClick = {
                            // Navigate to 成員 screen
                            navController.navigate("memberListScreen")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(text = "成員")
                    }

                    // 群組邀請連結按鈕
                    Button(
                        onClick = {
                            // Navigate to 群組邀請連結 screen
                            navController.navigate("groupInviteLinkScreen")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "群組邀請連結")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Delete Group Button
                    Button(
                        onClick = {
                            group?.let {
                                viewModel.deleteGroup(it.id)
                                navController.navigateUp() // Navigate back after deletion
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red, // Background color
                            contentColor = Color.White  // Text and icon color
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete), // Your delete icon resource
                            contentDescription = "Delete Group"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "刪除群組")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingScreenPreview() {
    // Create a mock NavController
    val navController = rememberNavController()

    // Create a mock or default MainViewModel
    val viewModel = MainViewModel() // You may need to provide required parameters or use a factory if necessary

    // Call the composable you want to preview
    GroupSettingScreen(
        groupId = "mockGroupId",
        viewModel = viewModel,
        navController = navController
    )
}