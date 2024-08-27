package com.example.billapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PieChart(income: Float, expense: Float, balance: Float, total: Float) {
    val incomeAngle = (income / total) * 360f
    val expenseAngle = (expense / total) * 360f

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Text(
                text = "收入: $income",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            )
            Text(
                text = "支出: $expense",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
            )
            Canvas(modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
            ) {
                withTransform({
                    rotate(270f)
                }) {
                    drawArc(
                        color = Color.Green,
                        startAngle = 0f,
                        sweepAngle = incomeAngle,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx())
                    )
                    drawArc(
                        color = Color.Red,
                        startAngle = incomeAngle,
                        sweepAngle = expenseAngle,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx())
                    )
                }
            }
            Text(
                text = "結餘: $balance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    PieChart(income = 100f, expense = 50f, balance = 50f, total = 150f)
}