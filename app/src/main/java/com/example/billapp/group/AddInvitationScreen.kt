package com.example.billapp.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.R
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import com.example.billapp.ui.theme.theme.Brown6
import com.example.billapp.ui.theme.theme.ButtonRedColor
import com.example.billapp.ui.theme.theme.ItemAddMainColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.ui.theme.theme.MainCardRedColor
import com.example.billapp.ui.theme.theme.Orange1
import com.example.billapp.ui.theme.theme.Orange2
import com.example.billapp.ui.theme.theme.PrimaryFontColor
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvitationScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var groupLink by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val currentUser = viewModel.user.collectAsState().value

    val userId by remember { mutableStateOf(currentUser?.id ?: "") }

    // Collect group exists and user in group states
    val groupExistsState by viewModel.groupExistsState.collectAsState()
    val userInGroupState by viewModel.userInGroupState.collectAsState()

    // Handle QR code scan result
    val qrCodeResult = navController.currentBackStackEntry?.savedStateHandle?.get<String>("qrCodeResult")
    qrCodeResult?.let {
        groupLink = it
        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("qrCodeResult")
    }

    // Effect to handle group existence and user group membership
    LaunchedEffect(groupExistsState, userInGroupState) {
        when {
            groupExistsState == false -> {
                dialogMessage = "查無此ID"
                showDialog = true
                viewModel.resetGroupStates()
            }
            groupExistsState == true && userInGroupState == true -> {
                dialogMessage = "您已加入該群組"
                showDialog = true
                viewModel.resetGroupStates()
            }
            groupExistsState == true && userInGroupState == false -> {
                viewModel.assignUserToGroup(groupLink, userId)
                viewModel.updateUserExperience(userId, 10)
                dialogMessage = "成功加入群組"
                showDialog = true
                viewModel.resetGroupStates()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("加入群組", color = PrimaryFontColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("qrCodeScanner")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                            contentDescription = "掃描 QR code",
                            tint = PrimaryFontColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Orange1)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackgroundColor)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainBackgroundColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MainBackgroundColor,
                        focusedIndicatorColor = Orange2,
                        unfocusedIndicatorColor = ItemAddMainColor,
                        errorIndicatorColor = MaterialTheme.colorScheme.error,
                    ),
                    value = groupLink,
                    onValueChange = {
                        groupLink = it
                        isError = groupLink.isBlank()
                    },
                    label = { Text("群組連結", color = PrimaryFontColor) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )
                if (isError) {
                    Text(
                        text = "群組連結不能為空",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (groupLink.isNotBlank()) {
                            viewModel.checkGroupExists(groupLink)
                            viewModel.checkUserInGroup(groupLink, userId)
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange1,
                        contentColor = Color.White
                    ),
                    enabled = groupLink.isNotBlank()
                ) {
                    Text("完成")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                viewModel.resetGroupStates()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                    showDialog = false
                    viewModel.resetGroupStates()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("確定", color = Color.Black)
                }
            },
            title = { Text("提示", color = Color.White) },
            text = { Text(dialogMessage, color = Color.White) },
            containerColor = BottomBackgroundColor
        )
    }
}

@Preview
@Composable
fun AddInvitationScreenPreview() {
    // Create a mock NavController
    val navController = rememberNavController()
    // Create a mock or default MainViewModel
    val viewModel = MainViewModel() // You may need to provide required parameters or use a factory if necessary
    AddInvitationScreen(navController = navController, viewModel = viewModel)
}