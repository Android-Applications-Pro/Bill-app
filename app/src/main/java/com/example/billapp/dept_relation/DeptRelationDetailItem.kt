package com.example.billapp.dept_relation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billapp.models.DeptRelation

@Composable
fun DeptRelationDetailItem(deptRelation: DeptRelation, onClearDebt: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Transaction ID: ${deptRelation.groupTransactionId}")
            Text(text = "$${String.format("%.2f", deptRelation.amount)}")
        }
        IconButton(onClick = onClearDebt) {
            Icon(Icons.Default.Clear, contentDescription = "Clear Debt")
        }
    }
}

@Preview
@Composable
fun DeptRelationDetailItemPreview()
{
    val deptRelation = DeptRelation(
        from = "John Doe",
        to = "Jane Smith",
        amount = 100.0,
        groupTransactionId = "transaction123"
    )
    val onClearDebt = {}
    DeptRelationDetailItem(deptRelation = deptRelation, onClearDebt = onClearDebt)
}