package com.example.billapp.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.home.CustomKeyboard
import com.example.billapp.R
import com.example.billapp.home.StylishTextField
import com.example.billapp.home.evaluateExpression
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.ui.theme.theme.Orange1
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch

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
    var showCustomBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.getGroupMembers(groupId)
    }

    val name by viewModel.name.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val shareMethod by viewModel.shareMethod.collectAsState()
    val dividers by viewModel.dividers.collectAsState()
    val payers by viewModel.payers.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()

    var nameInput by remember { mutableStateOf(name) }
    var amountInput by remember { mutableStateOf(amount.toString()) }
    var expandedShareMethod by remember { mutableStateOf(false) }
    var expandedDividers by remember { mutableStateOf(false) }
    var expandedPayers by remember { mutableStateOf(false) }
    var showSeparateScreen by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    var toggleKeyboard by remember { mutableStateOf(false) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val currentUser = viewModel.user.collectAsState().value
    val userId by remember { mutableStateOf(currentUser?.id ?: "") }

    // 初始化時根據amount是否為整數決定顯示的內容
    LaunchedEffect(amount) {
        amountInput = if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            amount.toString()
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("新增交易") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Orange1)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackgroundColor)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 新增的交易名稱輸入欄位
            StylishTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    viewModel.setName(it) // 更新ViewModel中的交易名稱
                },
                label = "交易名稱",
                readOnly = false,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                StylishTextField(
                    value = amountInput,
                    onValueChange = { input ->
                        if (input.isEmpty()) {
                            amountInput = ""
                            viewModel.setAmount(0.0)
                        } else {
                            input.toDoubleOrNull()?.let { validAmount ->
                                viewModel.setAmount(validAmount)
                                // 根據是否為整數來決定顯示的內容
                                amountInput = if (validAmount % 1.0 == 0.0) {
                                    validAmount.toInt().toString()
                                } else {
                                    validAmount.toString()
                                }
                            } ?: run {
                                amountInput = input
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(2.dp, colorResource(id = R.color.colorAccent), RoundedCornerShape(8.dp)) // 使用 colorResource 取得顏色
                    .background(BoxBackgroundColor) // 背景顏色
                    .clickable {
                        if (amountInput.isNotBlank() && dividers.isNotEmpty() && payers.isNotEmpty()) {
                            showCustomBottomSheet = true
                        } else {
                            showSnackbar = true // 顯示錯誤訊息
                        }
                    }
            ) {
                Text(
                    text = if (shareMethod.isNotBlank()) "分帳方式 : $shareMethod" else "分帳方式 : 未選擇",
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Cursive,
                        color = Color.DarkGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Trigger viewModel action to complete the transaction
                    viewModel.addGroupTransaction(groupId)
                    viewModel.loadGroupTransactions(groupId)
                    viewModel.updateUserExperience(userId,5)
                    viewModel.getGroupDebtRelations(groupId)
                    viewModel.calculateTotalDebtForGroup(groupId)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange1// 更深的顏色
                ),
                modifier = Modifier
                    .align(Alignment.End) // 右對齊
                    .padding(16.dp) // 增加內邊距
            ) {
                Text("完成")
            }
        }
    }

    CustomBottomSheet(
        isVisible = showCustomBottomSheet,
        onDismiss = { showCustomBottomSheet = false }
    ) {
        SeparateBottomSheetContent(
            viewModel = viewModel,
            groupId = groupId,
            amount = amount.toFloat(),
            onDismiss = { showCustomBottomSheet = false },
            onComplete = {
                showCustomBottomSheet = false
            }
        )
    }

    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar("請填寫所有必要的欄位")
            showSnackbar = false
        }
    }
}

@Composable
fun CustomBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismiss)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .align(Alignment.BottomCenter)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .clickable(onClick = {})  // 防止點擊穿透
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    content()
                }
            }
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

