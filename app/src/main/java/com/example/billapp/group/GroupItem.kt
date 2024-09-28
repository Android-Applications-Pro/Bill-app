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
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.models.Group
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.Purple40
import com.example.billapp.ui.theme.Green
import com.example.billapp.ui.theme.Gray
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.VeryDarkGray


@Composable
fun GroupItem(groupName: String, createdBy: String, totalDebt: Float, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 8.dp)
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
            // First Row for image and group name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 32.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.Center, // Horizontally center the content
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular ImageView
                Image(
                    painter = painterResource(id = R.drawable.ic_board_place_holder),
                    contentDescription = stringResource(id = R.string.image_contentDescription),
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(color = Purple40),
                    contentScale = ContentScale.Crop
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

            // Second Row for "總欠債"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), // Outer padding
                horizontalArrangement = Arrangement.End // Align content to the right
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
                                totalDebt > 0 -> Green      // Green if totalDebt is greater than 0
                                totalDebt == 0f -> Gray      // Gray if totalDebt is 0
                                else -> ButtonRedColor                 // Red if totalDebt is less than 0
                            },
                            shape = RoundedCornerShape(8.dp)    // Rounded background color
                        ),
                    contentAlignment = Alignment.Center   // Align content to the center
                ) {
                    Text(
                        text = "總欠債:\n$totalDebt NTD",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
fun GroupList(
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
                    groupName = groupItem.name,
                    createdBy = groupItem.createdBy,
                    totalDebt = 0f, // 假設你有這個數據，這裡使用示例值
                    onClick = { onGroupClick(groupItem.id) }
                )
            }
            /*
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("CreateGroupScreen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text("新增群組")
            }
        }
        */
        }
    }
}

@Preview
@Composable
fun GroupItemPreview()
{
    GroupItem("Travel","Jason",10000f,{})
}