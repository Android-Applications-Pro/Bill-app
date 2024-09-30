package com.example.billapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billapp.ui.theme.DarkerGray
import com.example.billapp.ui.theme.PieGreenColor
import com.example.billapp.ui.theme.PieRedColor

@Composable
fun PieChart(income: Float, expense: Float, balance: Float, total: Float, modifier: Modifier = Modifier) {
    val incomeAngle = (income / total) * 360f
    val expenseAngle = (expense / total) * 360f
    var noneAngle by remember { mutableStateOf(360f) }

    if(income == 0f){
        if(expense == 0f){
            noneAngle = 360f
        }else{
            noneAngle = 0f
        }
    }else{
        noneAngle = 0f
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(80.dp)) { // 調整圓餅圖大小
            withTransform({
                rotate(270f)
            }) {
                drawArc(
                    color = PieGreenColor,
                    startAngle = 0f,
                    sweepAngle = incomeAngle,
                    useCenter = true, // 使用中心點
                    style = Fill // 改為實心
                )
                drawArc(
                    color = PieRedColor,
                    startAngle = incomeAngle,
                    sweepAngle = expenseAngle,
                    useCenter = true, // 使用中心點
                    style = Fill // 改為實心
                )
                drawArc(
                    color = DarkerGray,
                    startAngle = incomeAngle,
                    sweepAngle = noneAngle,
                    useCenter = true, // 使用中心點
                    style = Fill // 改為實心
                )
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PieChartWithCategory(income: Float, expense: Float, balance: Float, total: Float, selectedCategory: String, categoryValues: List<Float>){
    if(selectedCategory == "balance"){
        val incomeAngle = (income / total) * 360f
        val expenseAngle = (expense / total) * 360f
        var noneAngle by remember { mutableStateOf(360f) }
        if(balance != 0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }
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
                        drawArc(
                            color = Color.LightGray,
                            startAngle = incomeAngle,
                            sweepAngle = noneAngle,
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
    }else if(selectedCategory == "income"){
        val incomeAngle = income *  360f
        var noneAngle by remember { mutableStateOf(360f) }
        if(income!=0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {

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
                            color = Color.LightGray,
                            startAngle = incomeAngle,
                            sweepAngle = noneAngle,
                            useCenter = false,
                            style = Stroke(width = 20.dp.toPx())
                        )
                    }
                }
                Text(
                    text = "收入: $income",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }else if(selectedCategory == "expanse-2"){
        val expenseAngle = expense * 360f
        var noneAngle by remember { mutableStateOf(360f) }
        if(expense!=0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                ) {
                    withTransform({
                        rotate(270f)
                    }) {
                        drawArc(
                            color = Color.Red,
                            startAngle = 0f,
                            sweepAngle = expenseAngle,
                            useCenter = false,
                            style = Stroke(width = 20.dp.toPx())
                        )
                        drawArc(
                            color = Color.LightGray,
                            startAngle = expenseAngle,
                            sweepAngle = noneAngle,
                            useCenter = false,
                            style = Stroke(width = 20.dp.toPx())
                        )
                    }
                }
                Text(
                    text = "支出: $expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }else if (selectedCategory == "expanse") {
        val categoryNames = listOf(
            "shopping",
            "entertainment",
            "transportation",
            "education",
            "living",
            "medical",
            "investment",
            "food",
            "travel",
            "other"
        )

        val categoryColorMap: List<Color> = listOf(
            Color.Magenta,
            Color.Cyan,
            Color.Yellow,
            Color.Blue,
            Color.Gray,
            Color(207, 159, 255),
            Color(242, 140, 40),
            Color(193, 154, 107),
            Color(255, 192, 203),
            Color.Red
        )

        val angles = categoryValues.map { (it / expense) * 360f }
        var noneAngle by remember { mutableStateOf(360f) }
        if(expense != 0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }

        var startAngle = 0f

        val pagerState = rememberPagerState(pageCount = { 2 })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp)
        ) { page ->
            when(page){
                0 -> Box {
                    Column() {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                if(expense != 0f){
                                    Canvas(
                                        modifier = Modifier
                                            .size(200.dp)
                                            .align(Alignment.Center)
                                    ) {
                                        withTransform({
                                            rotate(270f)
                                        }) {
                                            angles.forEachIndexed { index, sweepAngle ->
                                                drawArc(
                                                    color = categoryColorMap[index],
                                                    startAngle = startAngle,
                                                    sweepAngle = sweepAngle,
                                                    useCenter = false,
                                                    style = Stroke(width = 20.dp.toPx())
                                                )
                                                startAngle += sweepAngle
                                            }
                                        }
                                    }
                                }else{
                                    Canvas(modifier = Modifier
                                        .size(200.dp)
                                        .align(Alignment.Center)
                                    ) {
                                        withTransform({
                                            rotate(270f)
                                        }) {
                                            drawArc(
                                                color = Color.LightGray,
                                                startAngle = 0f,
                                                sweepAngle = noneAngle,
                                                useCenter = false,
                                                style = Stroke(width = 20.dp.toPx())
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "支出: $expense",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 圖例
                        LazyColumn {
                            itemsIndexed(categoryValues) { index, value ->
                                if (value > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(color = categoryColorMap[index])
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        BasicText(text = categoryNames[index])
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> Box(
                    contentAlignment = Alignment.Center
                ){
                    if(expense != 0f){
                        // 圖例
                        Column {
                            categoryValues.forEachIndexed { index, value ->
                                if (value > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(color = categoryColorMap[index])
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        BasicText(text = "${categoryNames[index]}: $value")
                                    }
                                }
                            }
                        }
                    }else{
                        Text(text = "無交易紀錄")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    PieChart(income = 100f, expense = 50f, balance = 50f, total = 150f)
}