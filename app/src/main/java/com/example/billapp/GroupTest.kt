package com.example.billapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.viewModel.MainViewModel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

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


    var toggleKeyboard by remember { mutableStateOf(false) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                StylishTextField(
                    value = amountInput,
                    onValueChange = {
                        amountInput = it
                        it.toDoubleOrNull()?.let { validAmount ->
                            viewModel.setAmount(validAmount)
                            // 根據是否為整數來決定顯示的內容
                            amountInput = if (validAmount % 1.0 == 0.0) {
                                validAmount.toInt().toString()
                            } else {
                                validAmount.toString()
                            }
                        }
                    },
                    label = "金額",
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            toggleKeyboard = !toggleKeyboard
                            isBottomSheetVisible = toggleKeyboard
                        }
                )
            }

            AnimatedVisibility(visible = isBottomSheetVisible) {
                CustomKeyboard(
                    onKeyClick = { key ->
                        amountInput += key
                        // 驗證並更新金額輸入
                        amountInput.toDoubleOrNull()?.let { validAmount ->
                            viewModel.setAmount(validAmount)
                        }
                    },
                    onDeleteClick = {
                        if (amountInput.isNotEmpty()) {
                            amountInput = amountInput.dropLast(1)
                            amountInput.toDoubleOrNull()?.let { validAmount ->
                                viewModel.setAmount(validAmount)
                            }
                        }
                    },
                    onClearClick = {
                        amountInput = ""
                        viewModel.setAmount(0.0)
                    },
                    onOkClick = {
                        coroutineScope.launch {
                            isBottomSheetVisible = false
                            toggleKeyboard = false
                        }
                    },
                    onEqualsClick = {
                        // 計算 amountInput
                        val result = evaluateExpression(amountInput)
                        amountInput = result.toString()
                        viewModel.setAmount(result)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedShareMethod,
                onExpandedChange = { expandedShareMethod = !expandedShareMethod }
            ) {
                StylishTextField(
                    readOnly = true,
                    value = shareMethod,
                    onValueChange = { },
                    label = "分帳方式" ,
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedShareMethod) },
//                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
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
                StylishTextField(
                    readOnly = true,
                    value = dividers.joinToString(", ") { member -> groupMembers.find { it.id == member }?.name ?: "" },
                    onValueChange = { },
                    label = "分帳的人" ,
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDividers) },
//                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
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
                StylishTextField(
                    readOnly = true,
                    value = payers.joinToString(", ") { member -> groupMembers.find { it.id == member }?.name ?: "" },
                    onValueChange = { },
                    label = "付錢的人",
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayers) },
//                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
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

