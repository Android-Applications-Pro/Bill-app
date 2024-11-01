package com.example.billapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.billapp.data.models.User
import com.example.billapp.setting.LineTokenUpdateScreen
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch

val lightBrown = Color(0xFF6D4C41)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val onCloseDrawer: () -> Unit = {
        scope.launch {
            drawerState.close()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "設定",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBackgroundColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .background(MainBackgroundColor)

        ) {
            ProfileCard(viewModel,navController,avatarViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            NotificationSwitch()
            SettingsList(navController,context)
            Spacer(modifier = Modifier.weight(1f))
//            LineTokenUpdateScreen(navController,viewModel)
            LogoutButton(viewModel, navController, onCloseDrawer)
        }
    }
}

@Composable
fun ProfileCard(
    viewModel: MainViewModel,
    navController: NavController,
    avatarViewModel: AvatarViewModel
) {
    val user = viewModel.user.collectAsState().value
    val userImage = avatarViewModel.avatarUrl.collectAsState().value

    val level by remember { mutableStateOf(viewModel.getUserLevel()) }
    val trustLevel by remember { mutableStateOf(viewModel.getUserTrustLevel()) }
    val budget by remember { mutableStateOf(viewModel.getUserBudget().toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                width = 2.dp,
                color = lightBrown,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = BoxBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileImage(userImage)
                Spacer(modifier = Modifier.width(16.dp))
                UserInfo(
                    user = user,
                    trustLevel = trustLevel,
                    budget = budget,
                    onEditClick = { navController.navigate("profile") }
                )
            }
        }
    }
}

@Composable
private fun ProfileImage(userImage: String?) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
    ) {
        if (userImage != null) {
            AsyncImage(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data(userImage)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_user_place_holder),
                contentDescription = "User Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_user_place_holder),
                contentDescription = "User Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun UserInfo(
    user: User?,
    trustLevel: Int,
    budget: String,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(lightBrown.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = lightBrown
                )
            }
        }

        TrustLevelIndicator(trustLevel)

        Text(
            text = "預算(血量上限)：$budget",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun TrustLevelIndicator(trustLevel: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "信譽點數",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$trustLevel/100",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = trustLevel / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                trustLevel >= 80 -> Color.Green
                trustLevel >= 50 -> Color.Yellow
                else -> Color.Red
            }
        )
    }
}


@Composable
fun NotificationSwitch() {
    var isChecked by remember { mutableStateOf(false) }
    val lightBrown = Color(0xFFD3BA9E)
    val darkBrown = Color(0xFF4F372F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = lightBrown)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification Icon",
                tint = darkBrown,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (isChecked) "通知已開啟" else "通知已關閉",
                style = MaterialTheme.typography.bodyLarge,
                color = darkBrown,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))  // Add a spacer to ensure proper alignment
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4F372F),
                    uncheckedThumbColor = Color.Gray,
                    checkedTrackColor = Color(0xFFB67B6C),
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}




@Composable
fun SettingsList(navController: NavController, context: Context) {
    val settingsItems = listOf(
        //SettingsItem("個人資料修改", R.drawable.baseline_person_24) { navController.navigate("profile") },
        SettingsItem("聯絡我們", R.drawable.baseline_call_24) { navController.navigate("contact_us") },
        SettingsItem("問題回報", R.drawable.baseline_report_problem_24) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/gi6Fyew6qfFyVAn29"))
            context.startActivity(intent)
        },
        SettingsItem("關於", R.drawable.baseline_speaker_notes_24) { navController.navigate("about") },
        SettingsItem("Line Token 更新", R.drawable.baseline_wallet_24) {
            navController.navigate("line_token_update")
        }
    )


    settingsItems.forEach { item ->
        SettingsListItem(item)
    }
}

@Composable
fun SettingsListItem(item: SettingsItem) {
    val lightBrown = Color(0xFFD3BA9E)
    val darkBrown = Color(0xFF4F372F)

    Button(
        onClick = item.onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = lightBrown),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = item.iconResId),
                contentDescription = null,
                tint = darkBrown,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = darkBrown,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LogoutButton(viewModel: MainViewModel?, navController: NavController?, onCloseDrawer: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = { showDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
    ) {
        Text("登出", color = MaterialTheme.colorScheme.onError)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = "Confirm Logout")
            },
            text = {
                Text("Are you sure you want to log out?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onCloseDrawer()
                        viewModel?.logOut {
                            navController?.navigate("intro") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Yes", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                ) {
                    Text("No", color = Color.Red)
                }
            },
            containerColor = BottomBackgroundColor
        )
    }
}


@Composable
fun ProgressBar(label: String, progress: Float, text: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

data class SettingsItem(
    val title: String,
    val iconResId: Int,
    val onClick: () -> Unit
)

