package com.example.billapp.personal

import android.net.Uri
import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.MonthPickerDialog
import com.example.billapp.MyDatePickerDialog
import com.example.billapp.PieChart
import com.example.billapp.PieChartWithCategory
import com.example.billapp.R
import com.example.billapp.YearPickerDialog
import com.example.billapp.models.TransactionCategory
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.viewModel.MainViewModel
import com.google.firebase.annotations.concurrent.Background
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalUIScreen(
    navController: NavController,
    viewModel: MainViewModel

) {
    val user by viewModel.user.collectAsState()
    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var day by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    val transactions by viewModel.userTransactions.collectAsState()
    var selectedChart by remember { mutableStateOf("結餘") }
    var filteredRecords by remember { mutableStateOf(transactions) }
    var Type by remember { mutableStateOf("balance") }
    var dateType by remember { mutableStateOf("月") }
    var filteredIncome by remember { mutableStateOf(0f) }
    var filteredExpense by remember { mutableStateOf(0f) }
    var filteredBalance by remember { mutableStateOf(0f) }
    var filteredShopping by remember { mutableStateOf(0f) }
    var filteredEntertainment by remember { mutableStateOf(0f) }
    var filteredTransportation by remember { mutableStateOf(0f) }
    var filteredEducation by remember { mutableStateOf(0f) }
    var filteredLiving by remember { mutableStateOf(0f)}
    var filteredMedical by remember { mutableStateOf(0f)}
    var filteredInvestment by remember{ mutableStateOf(0f)}
    var filteredFood by remember { mutableStateOf(0f)}
    var filteredTravel by remember { mutableStateOf(0f)}
    var filteredOther by remember { mutableStateOf(0f)}
    var showDatePicker by remember { mutableStateOf(false) }
    //__________________________________________
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    // 添加选单状态
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("結餘") }


    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("首頁", "個人", "新增", "群組", "設定")
    val icons = listOf(
        R.drawable.baseline_home_24,
        R.drawable.baseline_person_24,
        R.drawable.baseline_add_24,
        R.drawable.baseline_groups_24,
        R.drawable.baseline_settings_24
    )

    // 根據選中的類型過濾記錄
    fun filterRecords() {
        val filtered = transactions.filter { transaction ->
            val calendar =
                Calendar.getInstance().apply { timeInMillis = transaction.date!!.toDate().time }
            when (dateType) {
                "年" -> calendar.get(Calendar.YEAR) == year
                "月" -> calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) + 1 == month
                "日" -> calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) + 1 == month && calendar.get(
                    Calendar.DAY_OF_MONTH
                ) == day
                "自訂" -> startDate?.let { calendar.timeInMillis >= it } == true && endDate?.let { calendar.timeInMillis <= it } == true

                else -> true
            }
        }
        filteredRecords = filtered.filter { transaction ->
            when (selectedChart) {
                "支出" -> transaction.type == "支出"
                "收入" -> transaction.type == "收入"
                "結餘" -> true
                else -> true
            }
        }

        // 計算篩選後的收入、支出和結餘
        filteredIncome = filtered.filter { it.type == "收入" }.sumOf { it.amount }.toFloat()
        filteredExpense = filtered.filter { it.type == "支出" }.sumOf { it.amount }.toFloat()
        filteredBalance = filteredIncome - filteredExpense

        /// 計算篩選後的各類別金額
        val categoryValues = listOf(
            TransactionCategory.SHOPPING,
            TransactionCategory.ENTERTAINMENT,
            TransactionCategory.TRANSPORTATION,
            TransactionCategory.EDUCATION,
            TransactionCategory.LIVING,
            TransactionCategory.MEDICAL,
            TransactionCategory.INVESTMENT,
            TransactionCategory.FOOD,
            TransactionCategory.TRAVEL,
            TransactionCategory.OTHER
        ).map { category ->
            filtered.filter { it.category == category  && it.type == "支出"}.sumOf { it.amount }.toFloat()
        }

        // 將 categoryValues 賦值給相應的變數
        filteredShopping = categoryValues[0]
        filteredEntertainment = categoryValues[1]
        filteredTransportation = categoryValues[2]
        filteredEducation = categoryValues[3]
        filteredLiving = categoryValues[4]
        filteredMedical = categoryValues[5]
        filteredInvestment = categoryValues[6]
        filteredFood = categoryValues[7]
        filteredTravel = categoryValues[8]
        filteredOther = categoryValues[9]
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

    // 更新日期
    fun updateDate(increment: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        when (dateType) {
            "年" -> calendar.add(Calendar.YEAR, increment)
            "月" -> calendar.add(Calendar.MONTH, increment)
            "日" -> calendar.add(Calendar.DAY_OF_MONTH, increment)
        }
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)

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

    Box()
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE4DFCB))
                .padding(16.dp),
        ) {
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA68A68),  // 设置按钮背景色
                        contentColor = Color(0xFF000000)    // 设置按钮文字颜色
                    )
                ) {
                    Text("${selectedType}分析")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("支出") }, // 使用 Composable 函數來顯示文字
                        onClick = {
                            selectedType = "支出"
                            selectedChart = "支出"
                            Type = "expanse"
                            filterRecords()
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("結餘") }, // 使用 Composable 函數來顯示文字
                        onClick = {
                            selectedType = "結餘"
                            selectedChart = "結餘"
                            Type = "balance"
                            filterRecords()
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("收入") }, // 使用 Composable 函數來顯示文字
                        onClick = {
                            selectedType = "收入"
                            selectedChart = "收入"
                            Type = "income"
                            filterRecords()
                            expanded = false
                        })
                }
            }
            // 顯示年月的 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { updateDate(-1) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "上一個"
                    )
                }

                Box(
                    modifier = Modifier
                        .clickable { showDatePicker = true }
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = when (dateType) {
                            "年" -> "$year"
                            "月" -> "$year/$month"
                            "日" -> "$year/$month/$day"
                            else -> "$year/$month/"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { updateDate(1) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "下一個"
                    )
                }
            }
            // 顯示年、月、日的 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    //.clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val selectedColor = Color(0xFF8A7059)
                val defaultColor = Color(0xFFD9C2A7)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp))
                        .background(if (dateType == "年") selectedColor else defaultColor)
                        .clickable { dateType = "年"; filterRecords() }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "年", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (dateType == "月") selectedColor else defaultColor)
                        .clickable { dateType = "月"; filterRecords() }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "月", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (dateType == "日") selectedColor else defaultColor)
                        .clickable { dateType = "日"; filterRecords() }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "日", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp))
                        .background(if (dateType == "自訂") selectedColor else defaultColor)
                        .clickable { dateType = "自訂"; filterRecords() }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "自訂", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
/*
            // 顯示已支出、結餘、收入的 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    //                .border(1.dp, Color.Gray)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val selectedColor = Color.Gray
                val defaultColor = Color.LightGray

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (Type == "expanse") selectedColor else defaultColor)
                        .clickable { switchData("支出"); Type = "expanse" }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "支出", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$filteredExpense",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (Type == "balance") selectedColor else defaultColor)
                        .clickable { switchData("結餘"); Type = "balance" }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "結餘", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$filteredBalance",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (Type == "income") selectedColor else defaultColor)
                        .clickable { switchData("收入"); Type = "income" }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "收入", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "$filteredIncome", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
            }

 */


            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally)
            ) {
                // PieChart
                val income = filteredIncome
                val expense = filteredExpense
                val total = filteredIncome + filteredExpense
                val balance = filteredBalance
                val categorylist = listOf(
                    filteredShopping, filteredEntertainment,
                    filteredTransportation, filteredEducation,
                    filteredLiving, filteredMedical,
                    filteredInvestment, filteredFood,
                    filteredTravel, filteredOther)
                PieChartWithCategory(
                    income = income,
                    expense = expense,
                    balance = balance,
                    total = total,
                    selectedCategory = Type,
                    categoryValues = categorylist
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
    if (showDatePicker) {

        if (dateType == "自訂") {


        } else {
            ShowPickerDialog(
                dateType = dateType,
                onDateSelected = { selectedDate ->
                    // 根據 dateType 更新 year, month, day
                    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate ?: 0L }
                    when (dateType) {
                        "年" -> year = calendar.get(Calendar.YEAR)
                        "月" -> {
                            year = calendar.get(Calendar.YEAR)
                            month = calendar.get(Calendar.MONTH) + 1
                        }

                        "日" -> {
                            year = calendar.get(Calendar.YEAR)
                            month = calendar.get(Calendar.MONTH) + 1
                            day = calendar.get(Calendar.DAY_OF_MONTH)
                        }
                    }
                    filterRecords()
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@Composable
fun ShowPickerDialog(
    dateType: String,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    when (dateType) {
        "年" -> YearPickerDialog(onYearSelected = { year ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
            }
            onDateSelected(calendar.timeInMillis)
        }, onDismiss = onDismiss)
        "月" -> MonthPickerDialog(onMonthSelected = { year, month ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
            }
            onDateSelected(calendar.timeInMillis)
        }, onDismiss = onDismiss)
        "日" -> MyDatePickerDialog(onDateSelected = onDateSelected, onDismiss = onDismiss)
    }
}


@Preview(showBackground = true)
@Composable
fun PersonalUIScreenPreview() {
    val navController = rememberNavController()
    val viewModel = MainViewModel()
    PersonalUIScreen(navController, viewModel)
}