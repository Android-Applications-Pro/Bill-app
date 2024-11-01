package com.example.billapp.dept_relation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.ui.theme.theme.Orange1
import com.example.billapp.viewModel.AvatarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtRelationsScreen(
    avatarViewModel: AvatarViewModel,
    viewModel: MainViewModel,
    groupId: String,
    onBackPress: () -> Unit
) {
    val deptRelation by viewModel.debtRelations.collectAsState()
    val groupIdDeptRelations by viewModel.groupIdDebtRelations.collectAsState()

    // Load transactions and calculate dept relations when the screen is opened
    LaunchedEffect(groupId) {
        viewModel.getGroupDebtRelations(groupId)
        viewModel.calculateTotalDebtForGroup(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "債務關係",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange1
                )
            )
        },
        modifier = Modifier.background(BoxBackgroundColor)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(MainBackgroundColor) // 設置頁面的背景顏色
                .padding(innerPadding)
                .fillMaxSize() // 確保 Box 佔滿整個可用空間
        ) {
            DeptRelationList(
                avatarViewModel = avatarViewModel,
                viewModel = viewModel,
                debtRelations = groupIdDeptRelations,
                groupId = groupId,
                modifier = Modifier
            )
        }
    }
}
