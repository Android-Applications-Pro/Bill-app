package com.example.billapp.group

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.billapp.Avatar.GroupAvatarScreen
import com.example.billapp.R
import com.example.billapp.models.Group
import com.example.billapp.viewModel.MainViewModel

// 顯示在 Group，會放在底下 GroupList 中
@Composable
fun GroupItem(
    groupId: String,
    groupName: String,
    viewModel: MainViewModel,
    createdBy: String,
    onClick: () -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val groupAvatarUrl by viewModel.groupAvatarUrl.collectAsState()
    val groupCreationStatus by viewModel.groupCreationStatus.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // External padding
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), // Rounded corners
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Internal padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupAvatarScreen(groupId, viewModel)

            Spacer(modifier = Modifier.width(16.dp))

            // Group name and created by text
            Column {
                BasicText(
                    text = groupName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize // Larger font size
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                BasicText(
                    text = "created by : $createdBy",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun GroupList(
    viewModel: MainViewModel,
    groupItems: List<Group>,
    onGroupClick: (String) -> Unit,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(groupItems) { groupItem ->
            GroupItem(
                groupId = groupItem.id,
                viewModel = viewModel,
                createdBy = groupItem.createdBy,
                onClick = { onGroupClick(groupItem.id) },
                groupName = groupItem.name
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("CreateGroupScreen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // 設定按鈕高度
                    .padding(vertical = 4.dp)
            ) {
                Text("新增群組")
            }
        }
    }
}

@Preview
@Composable
fun GroupItemPreview()
{
    val viewModel = MainViewModel()
    val createdBy = "John Doe"
    val onClick = {}
    val groupName = "Sample Group"
    val groupId = "123"
    GroupItem(groupId, groupName, viewModel, createdBy, onClick)
}