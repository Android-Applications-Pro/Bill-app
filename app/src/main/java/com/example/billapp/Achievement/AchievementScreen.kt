package com.example.billapp.Achievement

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.home.DETAILED_ACHIEVEMENTS_ROUTE
import com.example.billapp.home.DETAILED_BADGES_ROUTE
import com.example.billapp.lightBrown
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.viewModel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: MainViewModel,
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    val achievements by viewModel.achievements.collectAsState()
    val badges by viewModel.badges.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "成就系統",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBackgroundColor
                )
            )
        },
        containerColor = MainBackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 成就區塊
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = BoxBackgroundColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "成就進度",
                            style = MaterialTheme.typography.titleLarge,
                            color = lightBrown
                        )
                        TextButton(
                            onClick = { navController.navigate(DETAILED_ACHIEVEMENTS_ROUTE) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = lightBrown
                            )
                        ) {
                            Text("查看全部")
                        }
                    }
                    AchievementsSection(
                        achievements = achievements,
                        onViewAllClick = { navController.navigate(DETAILED_ACHIEVEMENTS_ROUTE) }
                    )
                }
            }

            // 徽章區塊
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = BoxBackgroundColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "獲得徽章",
                            style = MaterialTheme.typography.titleLarge,
                            color = lightBrown
                        )
                        TextButton(
                            onClick = { navController.navigate(DETAILED_BADGES_ROUTE) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = lightBrown
                            )
                        ) {
                            Text("查看全部")
                        }
                    }
                    BadgesSection(
                        badges = badges,
                        onViewAllClick = { navController.navigate(DETAILED_BADGES_ROUTE) }
                    )
                }
            }
        }
    }
}