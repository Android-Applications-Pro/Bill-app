package com.example.billapp.personal

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.PieChart
import com.example.billapp.viewModel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PersonalUIScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val user by viewModel.user.collectAsState()
    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    val transactions by viewModel.userTransactions.collectAsState()
    var selectedChart by remember { mutableStateOf("結餘") }
    var filteredRecords by remember { mutableStateOf(transactions) }

    // 根據選中的類型過濾記錄
    fun filterRecords() {
        filteredRecords = when (selectedChart) {
            "支出" -> transactions.filter { it.amount < 0 }
            "收入" -> transactions.filter { it.amount > 0 }
            "結餘" -> transactions
            else -> transactions
        }
    }

    LaunchedEffect(user) {
        user?.let {
            viewModel.loadUserTransactions()
            filterRecords()
        }
    }
    LaunchedEffect(transactions) {
        filterRecords()
    }


    // 更新到下一個月或上一個月
    fun updateMonth(increment: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        calendar.add(Calendar.MONTH, increment)
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1

        // 更新數據
        filterRecords()  // 過濾記錄
    }

    // 切換數據類型（支出、收入或結餘）
    fun switchData(type: String) {
        selectedChart = type
        filterRecords()
    }

    // 獲取顯示金額
    fun getDisplayAmount(): Float {
        return viewModel.getUserAmount()
    }

    // 格式化日期
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顯示年月的 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { updateMonth(-1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "上一個月"
                )
            }

            Text(
                text = "${year}年${month}月",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(onClick = { updateMonth(1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "下一個月"
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { switchData("支出") }) {
                Text(text = "月支出")
            }

            Button(onClick = { switchData("收入") }) {
                Text(text = "月收入")
            }

            Button(onClick = { switchData("結餘") }) {
                Text(text = "月結餘")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "當前金額: ${getDisplayAmount()}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
        ) {
            // PieChart
            val income = viewModel.getUserIncome()
            val expense = viewModel.getUserExpense()
            val total = income + expense
            val balance = income - expense
            PieChart(income = income, expense = expense, balance = balance, total = total)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = "收入和支出詳情",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PersonalTransactionList(
                transactions = filteredRecords,
                navController = navController
            )

        }
    }
}




@Preview(showBackground = true)
@Composable
fun PersonalUIScreenPreview() {
    val navController = rememberNavController()
    val viewModel = MainViewModel()
    PersonalUIScreen(navController, viewModel)
}