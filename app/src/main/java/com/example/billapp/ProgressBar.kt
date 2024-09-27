package com.example.billapp

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ParallelogramProgressBar(
    TargetProgress: Float,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(0f) }

    // 使用 LaunchedEffect 來觸發動畫
    LaunchedEffect(TargetProgress) {
        progress = 0f
        delay(300) // 可選：添加延遲以確保動畫效果更明顯
        progress = TargetProgress
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp) // 設置進度條高度
            .padding(vertical = 4.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width * 0.9f, size.height)
                lineTo(size.width, 0f)
                lineTo(size.width * 0.1f, 0f)
                close()
            }

            clipPath(path) {
                drawRect(
                    color = Color.LightGray,
                    size = size
                )
                drawRect(
                    color = color,
                    size = Size(size.width * animatedProgress, size.height)
                )
            }
        }
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun RoundedCornerProgressBar(
    TargetProgress: Float,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(0f) }

    // 使用 LaunchedEffect 來觸發動畫
    LaunchedEffect(TargetProgress) {
        progress = 0f
        delay(300) // 可選：添加延遲以確保動畫效果更明顯
        progress = TargetProgress
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp) // 設置進度條高度
            .padding(vertical = 4.dp)
    ) {
        // 進度條背景框
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 繪製外部大圓角矩形作為背景框
            drawRoundRect(
                color = Color.LightGray,
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )
            // 繪製內部進度圓角矩形
            drawRoundRect(
                color = color,
                size = Size(size.width * animatedProgress, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )
        }
        // 中央顯示進度的文本
        Text(
            text = text,
            color = Color.Black, // 設置文本顏色為黑色
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
