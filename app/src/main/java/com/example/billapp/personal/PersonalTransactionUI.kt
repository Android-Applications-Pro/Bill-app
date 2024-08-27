package com.example.billapp.personal

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.billapp.models.PersonalTransaction
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PersonalTransactionItem(
    transaction: PersonalTransaction,
    onItemClick: () -> Unit
) {
    // Format the timestamp to a readable date format
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val date = transaction.date?.toDate() // Convert Firebase Timestamp to Java Date
    val formattedDate = dateFormat.format(date!!)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onItemClick),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formattedDate
            )
            Column {
                Text(text = transaction.name)
            }
            Text(
                text = "${if (transaction.type == "收入") "+" else "-"}${transaction.amount}",
                color = if (transaction.type == "收入") androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}

@Composable
fun PersonalTransactionList(
    transactions: List<PersonalTransaction>,
    navController: NavController
) {
    LazyColumn {
        items(transactions) { transaction ->
            PersonalTransactionItem(
                transaction = transaction,
                onItemClick = {
                    navController.navigate("editTransaction/${transaction.transactionId}")
                }
            )
        }
    }
}

@Composable
fun PersonalTransactionScreen(
    navController: NavController,
    viewModel: MainViewModel,
    userId: String
) {
    // Load transactions when the screen is composed
    viewModel.loadUserTransactions()

    val transactions by viewModel.userTransactions.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Personal Transactions",
            modifier = Modifier.padding(16.dp)
        )

        PersonalTransactionList(
            transactions = transactions,
            navController = navController
        )
    }
}

