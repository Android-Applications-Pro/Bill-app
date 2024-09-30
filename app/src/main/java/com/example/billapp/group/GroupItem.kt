package com.example.billapp.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.models.Group
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.Green
import com.example.billapp.ui.theme.Orange4
import com.example.billapp.ui.theme.Purple40
import com.example.billapp.ui.theme.Red
import com.example.billapp.viewModel.MainViewModel

@Composable
fun GroupItem(
    groupId: String,
    groupName: String,
    createdBy: String,
    totalDebt: Double,
    onClick: () -> Unit,
    imageId: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
            /*
            .border(
                width = 2.dp,  // Border thickness
                color = VeryDarkGray,  // Border color
                shape = RoundedCornerShape(8.dp)   // Apply the same rounded corner shape to the border
            ),
             */
        shape = RoundedCornerShape(16.dp), // Rounded corners
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBBB0A2)) // Card background color
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
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.width(24.dp))

                // Group name and created by text
                Column {
                    BasicText(
                        text = groupName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize // Larger font size
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 0.dp)
                    )
                    /*
                    BasicText(
                        text = "created by : $createdBy",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    )
                    */
                }
            }

            Spacer(modifier = Modifier.height(0.dp)) // Optional: space between sections


            Row(
                modifier = Modifier
                    .fillMaxWidth() // 填滿整個寬度
                    .background(Color(0xFFBBB0A2))
                    .padding(1.dp), // 外邊距
                horizontalArrangement = Arrangement.End // 內容向右對齊
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp) // Fixed width
                        .height(50.dp)
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp, bottom = 4.dp) // Inner padding
                        .shadow(
                            elevation = 8.dp,  // Shadow elevation height
                            shape = RoundedCornerShape(8.dp),  // Shape of the shadow (same as Box)
                            clip = false  // Whether to clip the content inside the shadow
                        )
                        .background(
                            color = when {
                                totalDebt < 0 -> Color(0xF3FF8B8B) // 負數時為紅色
                                totalDebt > 0 -> Green // 正數時為綠色
                                else -> Orange4 // 0 為淺黃色
                            },
                            shape = RoundedCornerShape(8.dp) // 圓角背景
                        ),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = when {
                            totalDebt < 0 -> "應付 : ${-totalDebt}" // 負數時為紅色
                            totalDebt > 0 -> "應收 : $totalDebt" // 正數時為綠色
                            else -> "帳務已結清" // 0 為淺黃色
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                        modifier = Modifier.padding(8.dp) // 調整文字的內邊距
                    )
                }
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
                    groupId = groupItem.id,
                    groupName = groupItem.name,
                    createdBy = groupItem.createdBy,
                    totalDebt = viewModel.calculateTotalDept(groupItem.id),
                    onClick = { onGroupClick(groupItem.id) },
                    imageId = groupItem.imageId
                )
            }
        }
    }
}