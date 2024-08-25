package com.example.billapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDetailScreen(navController: NavController, record: FinanceRecord) {
    var date by remember { mutableStateOf(record.date) }
    var amount by remember { mutableStateOf(TextFieldValue(record.amount.toString())) }
    var note by remember { mutableStateOf(record.note) }
    var showKeyboard by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("編輯記錄") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "編輯記錄",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("日期") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("金額") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showKeyboard = true
                        keyboardController?.hide() // 隱藏預設鍵盤
                    },
                readOnly = true // 禁用預設鍵盤
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("備註") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (showKeyboard) {
                CustomKeyboard(
                    onKeyClick = { key ->
                        amount = TextFieldValue(amount.text + key)
                    },
                    onDeleteClick = {
                        if (amount.text.isNotEmpty()) {
                            amount = TextFieldValue(amount.text.dropLast(1))
                        }
                    },
                    onClearClick = {
                        amount = TextFieldValue("")
                    },
                    onOkClick = {
                        showKeyboard = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            Button(
                onClick = { /* 保存邏輯 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
    }
}