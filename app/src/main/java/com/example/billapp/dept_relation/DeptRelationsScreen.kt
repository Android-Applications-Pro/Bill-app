package com.example.billapp.dept_relation

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

@Composable
fun DeptRelationsScreen(viewModel: MainViewModel, groupId: String, onBackPress: () -> Unit) {
    val deptRelation by viewModel.deptRelations.collectAsState()
    val groupIdDeptRelations by viewModel.groupIdDeptRelations.collectAsState()

    // Load transactions and calculate dept relations when the screen is opened
    LaunchedEffect(groupId) {
        viewModel.loadGroupDeptRelations(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Dept Relations") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        DeptRelationList(
            viewModel = viewModel,
            deptRelations = groupIdDeptRelations,
            groupId = groupId,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
