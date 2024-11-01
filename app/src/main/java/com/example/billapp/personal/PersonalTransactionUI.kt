@file:Suppress("DEPRECATION")

package com.example.billapp.personal

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.billapp.data.models.PersonalTransaction
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.billapp.R
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DismissValue.*
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalTransactionItem(
    transaction: PersonalTransaction,
    onItemClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = transaction.date?.toDate()
    val formattedDate = date?.let { dateFormat.format(it) } ?: "Unknown Date"

    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Use `Settled` as the initial state and `EndToStart` as the dismiss state.
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { newValue ->
            if (newValue == SwipeToDismissBoxValue.EndToStart) {
                showDialog = true
            }
            false
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "確認刪除", color = Color.White) },
            text = { Text(text = "你確定要刪除此交易紀錄嗎？", color = Color.White) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDialog = false
                        coroutineScope.launch {
                            dismissState.reset()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("確定", color = Color.Red)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            dismissState.reset()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("取消", color = Color.Black)
                }
            },
            containerColor = BottomBackgroundColor
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.fillMaxWidth(),
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val progress = dismissState.progress
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
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
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable(onClick = onItemClick),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBBB0A2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = formattedDate)
                        Column {
                            Text(text = transaction.name)
                        }
                        Text(
                            text = "${if (transaction.type == "收入") "+" else "-"}${String.format("%.0f", transaction.amount)}",
                            color = if (transaction.type == "收入") Color.Green else Color.Red
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun PersonalTransactionList(
    transactions: List<PersonalTransaction>,
    navController: NavController,
    viewModel: MainViewModel
) {
    LazyColumn {
        items(transactions) { transaction ->
            PersonalTransactionItem(
                transaction = transaction,
                onItemClick = {
                    navController.navigate("editTransaction/${transaction.transactionId}")
                },
                onDelete = {
                    viewModel.deleteTransaction(transaction.transactionId, transaction.type, transaction.amount)
                }
            )
        }
    }
}

//@Composable
//fun PersonalTransactionScreen(
//    navController: NavController,
//    viewModel: MainViewModel,
//    userId: String
//) {
//
//    val transactions by viewModel.userTransactions.collectAsState()
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Personal Transactions",
//            modifier = Modifier.padding(16.dp)
//        )
//
//        PersonalTransactionList(
//            transactions = transactions,
//            navController = navController,
//            viewModel = viewModel
//        )
//    }
//}