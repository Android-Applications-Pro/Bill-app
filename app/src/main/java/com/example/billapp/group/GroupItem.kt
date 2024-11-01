package com.example.billapp.group

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.data.models.Group
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.ui.theme.theme.Purple40
import com.example.billapp.viewModel.MainViewModel

@SuppressLint("DefaultLocale")
@Composable
fun GroupItem(
    viewModel: MainViewModel,
    groupId: String,
    groupName: String,
    createdBy: String,
    onClick: () -> Unit,
    imageId: Int
) {
    val totalDebt by viewModel.totalDebtMap.collectAsState()

    // LaunchedEffect 確保在組件啟動時執行計算
    LaunchedEffect(groupId) {
        viewModel.calculateTotalDebtForGroup(groupId)
    }

    // 獲取該群組的債務總額，如果尚未計算出來則顯示 0
    val groupTotalDebt = totalDebt[groupId] ?: 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoxBackgroundColor)
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = getImageResourceById(imageId)),
                contentDescription = stringResource(id = R.string.image_contentDescription),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(color = Purple40),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                BasicText(
                    text = groupName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth() // 填滿整個寬度
                .background(BoxBackgroundColor)
                .padding(1.dp), // 外邊距
            horizontalArrangement = Arrangement.End // 內容向右對齊
        ) {
            Box(
                modifier = Modifier
                    .width(150.dp) // 固定寬度
                    .padding(4.dp) // 調整內邊距
                    .background(
                        color = Color(0xFFF8E9D7),
                        shape = RoundedCornerShape(8.dp) // 圓角背景
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                val (debtText, debtColor) = when {
                    groupTotalDebt < 0.0 -> "應付 : ${String.format("%.0f", -groupTotalDebt)}" to Color.Red
                    groupTotalDebt > 0.0 -> "應收 : ${String.format("%.0f", groupTotalDebt)}" to Color(0xFF228B22)
                    else -> "帳務已結清" to Color(0xFFC49700)
                }

                Text(
                    text = debtText,
                    color = debtColor,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun GroupList(
    viewModel: MainViewModel,
    groupItems: List<Group>,
    onGroupClick: (String) -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE4DFCB)) // 設置整個頁面的背景顏色
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(groupItems) { groupItem ->
                GroupItem(
                    viewModel = viewModel,
                    groupId = groupItem.id,
                    groupName = groupItem.name,
                    createdBy = groupItem.createdBy,
                    onClick = { onGroupClick(groupItem.id) },
                    imageId = groupItem.imageId
                )
            }
        }
    }
}