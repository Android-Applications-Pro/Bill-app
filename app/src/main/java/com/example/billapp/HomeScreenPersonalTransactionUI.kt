package com.example.billapp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.billapp.models.PersonalTransaction
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.billapp.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenPersonalTransactionItem(
    transaction: PersonalTransaction,
    onItemClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    // Format the timestamp to a readable date format
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = transaction.date?.toDate() // Convert Firebase Timestamp to Java Date
    val formattedDate = date?.let { dateFormat.format(it) } ?: "Unknown Date"

    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                showDialog = true
            }
            false
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "確認刪除") },
            text = { Text(text = "你確定要刪除此交易紀錄嗎？") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text("確定")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        // 重置 dismissState 以確保交易項目恢復到正常狀態
                        coroutineScope.launch {
                            dismissState.reset()
                        }
                    }
                ) {
                    Text("取消")
                }
            }
        )
    } else {
        coroutineScope.launch {
            dismissState.reset()
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val progress = dismissState.progress.fraction
            val color = if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                Color.Red.copy(alpha = progress)
            } else {
                Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable(onClick = onItemClick),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = formattedDate)
                        Text(text = transaction.name)

                    }
                    Column (
                        modifier = Modifier.weight(1f)
                    ){
                        Text(text = transaction.type)
                        Text(
                            text = "${if (transaction.type == "收入") "+" else "-"}${transaction.amount}",
                            color = if (transaction.type == "收入") Color.Green else Color.Red
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.LightGray, shape = CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_edit_24),
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Red, shape = CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun HomeScreenPersonalTransactionList(
    transactions: List<PersonalTransaction>,
    navController: NavController,
    viewModel: MainViewModel,
    currentPage: Int,
    itemsPerPage: Int
) {
    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, transactions.size)
    val currentTransactions = transactions.subList(startIndex, endIndex)

    LazyColumn {
        items(currentTransactions) { transaction ->
            HomeScreenPersonalTransactionItem(
                transaction = transaction,
                onItemClick = {
                    navController.navigate("editTransaction/${transaction.transactionId}")
                },
                onDelete = {
                    viewModel.deleteTransaction(transaction.transactionId, transaction.type, transaction.amount)
                },
                onEdit = {
                    navController.navigate("editTransaction/${transaction.transactionId}")
                }
            )
        }
    }
}


