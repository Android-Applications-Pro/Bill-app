package com.example.billapp.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.dept_relation.DeptRelationsScreen
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
            // Debt Relationship Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp), // Reduced padding here
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.Gray),
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(8.dp)) // Reduced padding inside the Card
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp), // Reduced padding inside the Row
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "債務關係",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp) // Reduced padding between Text and Button
                        )

                        Button(
                            onClick = {
                                navController.navigate("groupTest/$groupId")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp) // Reduced padding between Button and Text
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "新增交易",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    DeptRelationsScreen(viewModel = viewModel, groupId = groupId)
                }
            }

            // Administrator Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp), // Reduced padding here
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.Gray),
                backgroundColor = Color.LightGray
            ) {
                Column(
                    modifier = Modifier.padding(8.dp) // Reduced padding inside the Card
                ) {
                    Text(
                        text = "管理員",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp) // Reduced bottom padding
                    )

                    Button(
                        onClick = {
                            navController.navigate("memberListScreen/$groupId")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp) // Reduced bottom padding
                    ) {
                        Text(text = "成員")
                    }

                    Button(
                        onClick = {
                            navController.navigate("Group_Invite/$groupId")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "群組邀請連結")
                    }

                    Spacer(modifier = Modifier.height(8.dp)) // Reduced height

                    Button(
                        onClick = {
                            group?.let {
                                viewModel.deleteGroup(it.id)
                                navController.navigateUp()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete Group"
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // Reduced space between icon and text
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