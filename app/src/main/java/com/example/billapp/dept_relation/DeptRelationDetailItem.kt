package com.example.billapp.dept_relation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billapp.models.DeptRelation
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeptRelationDetailItem(
    viewModel: MainViewModel,
    deptRelation: DeptRelation,
    groupId: String,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var fromName by remember { mutableStateOf("") }
    var toName by remember { mutableStateOf("") }

    LaunchedEffect(deptRelation) {
        fromName = viewModel.getUserName(deptRelation.from)
        toName = viewModel.getUserName(deptRelation.to)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            //Text(text = "Transaction ID: ${deptRelation.groupTransactionId}")
            Text(text = "Transaction Name: ${deptRelation.name}")
            Text(text = "from:${fromName} -> to:${toName}")
            Text(text = "$${String.format("%.2f", deptRelation.amount)}")
        }
        IconButton(onClick = {
            showBottomSheet = true
        }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear Debt")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Clear Debt", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Are you sure you want to clear this debt?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showBottomSheet = false }) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        viewModel.deleteDeptRelation(groupId = groupId, deptRelationId = deptRelation.id)
                        viewModel.loadGroupDeptRelations(groupId)
                        showBottomSheet = false
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

