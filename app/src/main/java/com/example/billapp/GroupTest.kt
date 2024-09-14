package com.example.billapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTest(
    navController: NavController,
    viewModel: MainViewModel,
    groupId: String
) {
    // Initialize scaffoldState
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.getGroupMembers(groupId)
    }

    val amount by viewModel.amount.collectAsState()
    val shareMethod by viewModel.shareMethod.collectAsState()
    val dividers by viewModel.dividers.collectAsState()
    val payers by viewModel.payers.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()

    var amountInput by remember { mutableStateOf(amount.toString()) }
    var expandedShareMethod by remember { mutableStateOf(false) }
    var expandedDividers by remember { mutableStateOf(false) }
    var expandedPayers by remember { mutableStateOf(false) }
    var showSeparateScreen by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Group Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD9C9BA))
                .padding(innerPadding)
        ) {
            TextField(
                value = amountInput,
                onValueChange = {
                    amountInput = it
                    it.toDoubleOrNull()?.let { validAmount ->
                        viewModel.setAmount(validAmount)
                    }
                },
                label = { Text("金額") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions {
                    keyboardController?.hide()
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedShareMethod,
                onExpandedChange = { expandedShareMethod = !expandedShareMethod }
            ) {
                TextField(
                    readOnly = true,
                    value = shareMethod,
                    onValueChange = { },
                    label = { Text("分帳方式") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedShareMethod) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedShareMethod,
                    onDismissRequest = { expandedShareMethod = false }
                ) {
                    listOf("均分", "比例", "調整", "金額", "份數").forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.setShareMethod(selectionOption)
                                expandedShareMethod = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedDividers,
                onExpandedChange = { expandedDividers = !expandedDividers }
            ) {
                TextField(
                    readOnly = true,
                    value = dividers.joinToString(", ") { member -> groupMembers.find { it.id == member }?.name ?: "" },
                    onValueChange = { },
                    label = { Text("分帳的人") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDividers) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedDividers,
                    onDismissRequest = { expandedDividers = false }
                ) {
                    groupMembers.forEach { user ->
                        val isSelected = dividers.contains(user.id)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            viewModel.toggleDivider(user.id)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(user.name)
                                }
                            },
                            onClick = {
                                viewModel.toggleDivider(user.id)
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedPayers,
                onExpandedChange = { expandedPayers = !expandedPayers }
            ) {
                TextField(
                    readOnly = true,
                    value = payers.joinToString(", ") { member -> groupMembers.find { it.id == member }?.name ?: "" },
                    onValueChange = { },
                    label = { Text("付錢的人") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayers) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedPayers,
                    onDismissRequest = { expandedPayers = false }
                ) {
                    groupMembers.forEach { user ->
                        val isSelected = payers.contains(user.id)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            viewModel.togglePayer(user.id)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(user.name)
                                }
                            },
                            onClick = {
                                viewModel.togglePayer(user.id)
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (amountInput.isNotBlank() && shareMethod.isNotBlank() && dividers.isNotEmpty() && payers.isNotEmpty()) {
                        showBottomSheet = true
                    } else {
                        showSnackbar = true
                    }
                },
                enabled = amountInput.isNotBlank() && shareMethod.isNotBlank() && dividers.isNotEmpty() && payers.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text("分帳")
            }

            Button(onClick = {
                // Trigger viewModel action to complete the transaction
                viewModel.addGroupTransaction(groupId)
                viewModel.loadGroupTransactions(groupId)
                viewModel.loadGroupDeptRelations(groupId)
                navController.popBackStack()
            }) {
                Text("完成")
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SeparateBottomSheetContent(
                        viewModel = viewModel,
                        groupId = groupId,
                        amount = amount.toFloat(),
                        onDismiss = { showBottomSheet = false },
                        onComplete = {
                            showBottomSheet = false
                        }
                    )
                }
            }
        }
    }

    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar("請填寫所有必要的欄位")
            showSnackbar = false
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GroupTestPreview() {
    val navController = rememberNavController()
    val viewModel = MainViewModel()
    GroupTest(navController, viewModel, "test")

}