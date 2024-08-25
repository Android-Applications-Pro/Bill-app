package com.example.billapp

import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billapp.viewModel.MainViewModel
import java.util.*
import kotlin.math.absoluteValue

data class FinanceRecord(
    val date: String,   // 記錄的日期
    val amount: Float,  // 記錄的金額（正值代表收入，負值代表支出）
    val note: String    // 記錄的備註
)

@Composable
fun PersonalUIScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }

    // 模擬三個月的收入與支出數據
    val financeData = mapOf(
        Pair(2024, 5) to listOf(
            FinanceRecord("2024-05-01", -200f, "買衣服"),
            FinanceRecord("2024-05-03", -50f, "買咖啡"),
            FinanceRecord("2024-05-10", 5000f, "工資"),
            FinanceRecord("2024-05-15", -300f, "買鞋"),
            FinanceRecord("2024-05-20", -100f, "超市購物")
        ),
        Pair(2024, 6) to listOf(
            FinanceRecord("2024-06-01", 5500f, "工資"),
            FinanceRecord("2024-06-05", -150f, "餐廳吃飯"),
            FinanceRecord("2024-06-10", -200f, "電影院"),
            FinanceRecord("2024-06-20", -300f, "買書"),
            FinanceRecord("2024-06-25", -250f, "旅行")
        ),
        Pair(2024, 7) to listOf(
            FinanceRecord("2024-07-01", 5200f, "工資"),
            FinanceRecord("2024-07-05", -100f, "健身房"),
            FinanceRecord("2024-07-12", -200f, "朋友聚會"),
            FinanceRecord("2024-07-15", -150f, "買禮物"),
            FinanceRecord("2024-07-20", -300f, "家居用品")
        )
    )

    var selectedChart by remember { mutableStateOf("支出") }
    var records by remember { mutableStateOf(financeData[Pair(year, month)] ?: emptyList()) }
    var filteredRecords by remember { mutableStateOf(records) }

    // 根據選中的類型過濾記錄
    fun filterRecords() {
        filteredRecords = when (selectedChart) {
            "支出" -> records.filter { it.amount < 0 }
            "收入" -> records.filter { it.amount > 0 }
            "結餘" -> records
            else -> records
        }
    }

    // 更新到下一個月或上一個月
    fun updateMonth(increment: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        calendar.add(Calendar.MONTH, increment)
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1

        // 更新數據
        records = financeData[Pair(year, month)] ?: emptyList()
        filterRecords()  // 過濾記錄
    }

    // 切換數據類型（支出、收入或結餘）
    fun switchData(type: String) {
        selectedChart = type
        filterRecords()
    }

    // 獲取顯示金額
    fun getDisplayAmount(): Float {
        return filteredRecords.sumOf { it.amount.toDouble() }.toFloat()
    }

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
            PieChart(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                data = filteredRecords,
                displayAmount = getDisplayAmount()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "收入和支出詳情", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            filteredRecords.forEach { record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(
                                "edit_detail_screen/${record.date}/${record.amount}/${Uri.encode(record.note)}"
                            )

                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = record.date, modifier = Modifier.weight(1f))
                    Text(text = "${record.amount}", modifier = Modifier.weight(1f))
                    Text(text = record.note, modifier = Modifier.weight(2f))
                }
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun PieChart(modifier: Modifier = Modifier, data: List<FinanceRecord>, displayAmount: Float) {
    Canvas(modifier = modifier) {
        var totalIncome = 0f
        var totalSpend = 0f

        for (record in data) {
            if (record.amount > 0) {
                totalIncome += record.amount
            } else {
                totalSpend += record.amount.absoluteValue
            }
        }

        val total = totalIncome + totalSpend
        val startAngle = -90f

        val spendFraction = if (total > 0) totalSpend / total else 0f
        val incomeFraction = if (total > 0) totalIncome / total else 0f

        drawArc(
            color = Color.Red,
            startAngle = startAngle,
            sweepAngle = spendFraction * 360f,
            useCenter = true,
            size = size,
            style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
        )

        drawArc(
            color = Color.Green,
            startAngle = startAngle + spendFraction * 360f,
            sweepAngle = incomeFraction * 360f,
            useCenter = true,
            size = size,
            style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
        )

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "$displayAmount",
                size.width / 2,
                size.height / 2,
                Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = Paint.Align.CENTER
                    textSize = 40.sp.toPx()
                }
            )
        }
    }
}