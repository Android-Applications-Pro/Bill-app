package com.example.billapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val keyboardController = LocalSoftwareKeyboardController.current

    // Helper to get a user's name by ID
    val getUserNameById: (String) -> String = { userId ->
        groupMembers.find { it.id == userId }?.name ?: "Unknown"
    }

    Scaffold(
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
            // Amount Input
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

            // Share Method Dropdown
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
                    listOf("均分", "比例", "份數", "自訂").forEach { selectionOption ->
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

            // Dividers Selection (Multiple Selection)
            ExposedDropdownMenuBox(
                expanded = expandedDividers,
                onExpandedChange = { expandedDividers = !expandedDividers }
            ) {
                TextField(
                    readOnly = true,
                    value = dividers.joinToString(", ") { getUserNameById(it) },
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
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                viewModel.toggleDivider(user.id)
                                // Don't close the dropdown after selecting
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payers Selection (Single Selection)
            ExposedDropdownMenuBox(
                expanded = expandedPayers,
                onExpandedChange = { expandedPayers = !expandedPayers }
            ) {
                TextField(
                    readOnly = true,
                    value = payers.joinToString(", ") { getUserNameById(it) },
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
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                viewModel.togglePayer(user.id)
                                expandedPayers = false // Close dropdown after selection
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    viewModel.addGroupTransaction(groupId)  // Pass the groupId to the function
                    navController.popBackStack()
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text("完成")
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