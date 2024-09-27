package com.example.billapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.billapp.group.GroupList
import com.example.billapp.models.Group
import com.example.billapp.models.User
import com.example.billapp.models.GroupTransaction
import com.example.billapp.personal.PersonalTransactionList
import com.example.billapp.ui.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.MainCardRedColor
import com.example.billapp.viewModel.MainViewModel
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: MainViewModel
) {
    val groups by viewModel.userGroups.collectAsState()
    // 获取最近两笔交易记录
    val transactions by viewModel.userTransactions.collectAsState()
    var filteredRecords by remember { mutableStateOf(transactions) }

    var selectedChart by remember { mutableStateOf("結餘") }
    val user by viewModel.user.collectAsState()
    fun filtered(){
        val filtered = transactions.filter {
            it.updatedAt != null

        }
            .sortedByDescending { it.updatedAt }
            .take(10)

        filteredRecords = filtered.filter { transaction ->
            when (selectedChart) {
                "支出" -> transaction.type == "支出"
                "收入" -> transaction.type == "收入"
                "結餘" -> true
                else -> true
            }
        }

    }

    LaunchedEffect(user) {
        user?.let {
            viewModel.loadUserTransactions()
            filtered()
        }
    }
    LaunchedEffect(transactions) {
        filtered()
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFD9C2A7))
        ) {
            val userName = "getUserName()"
            val income = viewModel.getUserIncome()
            val expense = viewModel.getUserExpense()
            val total = income + expense
            val balance = income - expense

            val level = viewModel.getUserLevel()
            val trustLevel = viewModel.getUserTrustLevel()

            HorizontalPager(
                state = pagerState,
                modifier =Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {page ->
                when(page){
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(BoxBackgroundColor)
//                    .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathLeft = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width * 0.5f, 0f)
                                lineTo(size.width * 0.4f, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            val pathRight = Path().apply {
                                moveTo(size.width * 0.5f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height)
                                lineTo(size.width * 0.4f, size.height)
                                close()
                            }
                            drawPath(pathLeft, BoxBackgroundColor)
                            drawPath(pathRight, MainCardRedColor)
                            drawLine(
                                color = Color.Gray,
                                start = Offset(size.width * 0.5f, 0f),
                                end = Offset(size.width * 0.4f, size.height),
                                strokeWidth = 4f
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.avatar_placeholder_2),
                                        contentDescription = "Character Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp) // 設置頭像大小
                                            .clip(CircleShape) // 設置頭像為圓形
                                            .background(Color.Gray) // 頭像背景顏色
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "AMY",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RoundedCornerProgressBar(
                                        TargetProgress = 1f,
                                        text = "信譽點數: $trustLevel/100",
                                        color = Color.Green,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RoundedCornerProgressBar(
                                        TargetProgress = 0.5f,
                                        text = "社交值: 等級: lv.$level",
                                        color = Color.Blue,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RoundedCornerProgressBar(
                                        TargetProgress = 0.083f,
                                        text = "血條: 2500/30000",
                                        color = Color.Red,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    1 -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(BoxBackgroundColor)
//                    .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathLeft = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width * 0.5f, 0f)
                                lineTo(size.width * 0.4f, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            val pathRight = Path().apply {
                                moveTo(size.width * 0.5f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height)
                                lineTo(size.width * 0.4f, size.height)
                                close()
                            }
                            drawPath(pathLeft, BoxBackgroundColor)
                            drawPath(pathRight, MainCardRedColor)
                            drawLine(
                                color = Color.Gray,
                                start = Offset(size.width * 0.5f, 0f),
                                end = Offset(size.width * 0.4f, size.height),
                                strokeWidth = 4f
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.avatar_placeholder_2),
                                        contentDescription = "Character Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp) // 設置頭像大小
                                            .clip(CircleShape) // 設置頭像為圓形
                                            .background(Color.Gray) // 頭像背景顏色
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "AMY",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f) // 確保 Box 是正方形
                                    .padding(8.dp)
                                    .clickable { navController.navigate("personal") },
                                contentAlignment = Alignment.Center
                            ) {
                                PieChart(
                                    income = income,
                                    expense = expense,
                                    balance = balance,
                                    total = total,
                                    modifier = Modifier.size(80.dp) // 調整圓餅圖大小
                                )

                                // 顯示支出、收入和結餘
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 4.dp, top = 4.dp) // 調整位置
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "支出: $expense",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(end = 4.dp, top = 4.dp) // 調整位置
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "收入: $income",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .offset(x = (-16).dp,y=(-32).dp)
                                        .padding(start = 4.dp, bottom = 4.dp) // 調整位置
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "結餘: $balance",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = (16).dp,y=(8).dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(
                                            onClick = { /*TODO*/ }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_navigate_before_24),
                                                contentDescription = "Previous Page"
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                                .padding(4.dp) // 內部 padding
                                        ) {
                                            Text(
                                                text = "2024/09",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        IconButton(
                                            onClick = { /*TODO*/ }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                                                contentDescription = "Next Page"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var currentPage by remember { mutableStateOf(1) }
        val itemsPerPage = 2
        val totalPages = (transactions.size + itemsPerPage - 1) / itemsPerPage

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(BoxBackgroundColor)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center // 水平置中
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .border(1.dp, Color.Gray)
                            .padding(8.dp) // 內部 padding
                    ) {
                        androidx.compose.material.Text(
                            text = "近期交易紀錄",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HomeScreenPersonalTransactionList(
                    transactions = transactions,
                    navController = navController,
                    viewModel = viewModel,
                    currentPage = currentPage,
                    itemsPerPage = itemsPerPage
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.IconButton(
                        onClick = {
                            if (currentPage > 1) {
                                currentPage--
                            }
                        },
                        enabled = currentPage > 1
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Previous Page"
                        )
                    }

                    androidx.compose.material.Text(
                        text = "$currentPage/$totalPages",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    androidx.compose.material.IconButton(
                        onClick = {
                            if (currentPage < totalPages) {
                                currentPage++
                            }
                        },
                        enabled = currentPage < totalPages
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                            contentDescription = "Next Page"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box (
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(BoxBackgroundColor)
                .padding(8.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center // 水平置中
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .border(1.dp, Color.Gray)
                            .padding(8.dp) // 內部 padding
                    ) {
                        Text(
                            text = "您的群組",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 顯示時間最近的 4 個 Group，並利用 4 宮格顯示
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(320.dp)  // Adjust the height as needed
                ) {
                    items(4) { index ->
                        if (index < groups.size) {
                            GroupItem(group = groups[index], onItemClick = {
                                navController.navigate("groupDetail/${groups[index].id}")
                            })
                        } else {
                            EmptyGroupSlot()
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// 只用於主頁
@Composable
fun GroupItem(group: Group, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = group.image,
                contentDescription = "Group Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = group.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun EmptyGroupSlot() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // 空的內容，只顯示背景色
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Create a mock NavController
    val navController = rememberNavController()
    // Create a mock or default MainViewModel
    val viewModel = MainViewModel() // You may need to provide required parameters or use a factory if necessary
    HomeScreen(navController = navController, onOpenDrawer = {}, viewModel = viewModel)
    // HomeScreen(navController = navController, onOpenDrawer = {})
}

@Preview(showBackground = true)
@Composable
fun GroupItemPreview(){
    val group = Group(
        id = "1",
        name = "Group 1",
        image = "https://example.com/image1.jpg",
        createdBy = "User 1",
    )
    GroupItem(group = group, onItemClick = {})
}




