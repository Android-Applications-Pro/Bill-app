package com.example.billapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.models.TransactionCategory
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalTest(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Initialize the category to ensure it has a default value
    LaunchedEffect(Unit) {
        viewModel.setCategory(TransactionCategory.OTHER)
    }
    val transactionType by viewModel.transactionType.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val category by viewModel.category.collectAsState()
    val name by viewModel.name.collectAsState()
    val note by viewModel.note.collectAsState()
    var selectedCategory by remember { mutableStateOf(TransactionCategory.OTHER) }
    var amountInput by remember { mutableStateOf(amount.toString()) }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var isBottomSheetVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9C9BA))
            .padding(top = 65.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clickable {
                if (isBottomSheetVisible) {
                    coroutineScope.launch {
                        isBottomSheetVisible = false
                        bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }
    ) {

        // 收入/支出切换按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "支出",
                color = if (transactionType == "支出") colorResource(id = R.color.colorAccent) else Color.Gray,
                fontSize = 18.sp
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.setTransactionType("收入") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "收入") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                    )
                ) {
                    Text("收入")
                }

                Button(
                    onClick = { viewModel.setTransactionType("支出") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "支出") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                    )
                ) {
                    Text("支出")
                }
            }


            Text(
                "收入",
                color = if (transactionType == "收入") colorResource(id = R.color.colorAccent) else Color.Gray,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

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

        // Category Dropdown and Name Input
        TransactionCategoryDropdown2(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                if (isValidCategory(category)) {
                    selectedCategory = category
                } else {
                    selectedCategory = TransactionCategory.OTHER
                }
            }
        )

        TextField(
            value = name,
            onValueChange = { viewModel.setName(it) },
            label = { Text("名稱") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Note Input
        TextField(
            value = note,
            onValueChange = { viewModel.setNote(it) },
            label = { Text("備註") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                viewModel.addPersonalTransaction()
                amountInput = ""
                viewModel.setAmount(0.0)
                viewModel.setCategory(TransactionCategory.OTHER)
                viewModel.setName("")
                viewModel.setNote("")
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

@Composable
fun TransactionCategoryDropdown2(
    selectedCategory: TransactionCategory,
    onCategorySelected: (TransactionCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = selectedCategory.name,
        modifier = Modifier
            .clickable { expanded = true }
            .padding(16.dp)
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        TransactionCategory.entries.forEach { category ->
            DropdownMenuItem(
                onClick = {
                    onCategorySelected(category)
                    expanded = false
                },
                text = {
                    Text(text = category.name)
                }
            )
        }
    }
}


fun isValidAmount(amount: String): Boolean {
    return amount.toDoubleOrNull() != null && amount.isNotEmpty()
}

fun isValidCategory(category: TransactionCategory): Boolean {
    return TransactionCategory.values().contains(category)
}

@Preview
@Composable
fun ItemAddPreview() {
    val navController = rememberNavController()
    val viewModel = MainViewModel()
    PersonalTest(navController, viewModel)
}





