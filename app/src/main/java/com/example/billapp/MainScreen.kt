package com.example.billapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.billapp.group.AddInvitationScreen
import com.example.billapp.group.CreateGroup
import com.example.billapp.group.GroupInviteLinkScreen
import com.example.billapp.group.GroupScreen
import com.example.billapp.group.GroupSettingScreen
import com.example.billapp.models.User
import com.example.billapp.personal.EditTransactionDetailScreen
import com.example.billapp.personal.PersonalUIScreen
import com.example.billapp.setting.AboutScreen
import com.example.billapp.setting.ContactUsScreen
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogOut: () -> Unit,
    viewModel: MainViewModel,
    requestPermission: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("首頁", "個人", "新增", "群組", "設定")
    val icons = listOf(
        R.drawable.baseline_home_24,
        R.drawable.baseline_person_24,
        R.drawable.baseline_add_24,
        R.drawable.baseline_groups_24,
        R.drawable.baseline_settings_24
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                val onCloseDrawer: () -> Unit = {
                    scope.launch {
                        drawerState.close()
                    }
                }
                DrawerContent(navController, onCloseDrawer, onLogOut, viewModel)
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = icons[index]),
                                    contentDescription = item
                                )
                            },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                when (index) {
                                    0 -> navController.navigate("home")
                                    1 -> navController.navigate("personal")
                                    2 -> navController.navigate("add")
                                    3 -> navController.navigate("group")
                                    4 -> navController.navigate("settings")
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        },
                        viewModel = viewModel,
                    )
                }
                composable("personal") {
                    PersonalUIScreen(
                        navController = navController,
                        viewModel = viewModel,
                    )
                }
                composable("add") {
                    PersonalTest(
                        navController = navController,
                        viewModel = viewModel,
                    )
                }
                composable("group") {
                    GroupScreen(
                        navController = navController,
                        viewModel = viewModel,
                    )
                }
                composable("settings") {
                    SettingScreen(
                        navController = navController,
                        viewModel = viewModel,
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        navController = navController,
                        viewModel = viewModel,
                        requestPermission = requestPermission
                    )
                }
                composable("CreateGroupScreen") {
                    CreateGroup(navController = navController,viewModel = viewModel)
                }
                composable("contact_us"){
                    ContactUsScreen(navController = navController, viewModel = viewModel)
                }
                composable("about"){
                    AboutScreen(navController = navController)
                }
                composable("Join_Group"){
                    AddInvitationScreen(navController = navController, viewModel = viewModel)
                }
                composable(
                    route = "Group_Invite/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getString("groupId")
                    groupId?.let {
                        GroupInviteLinkScreen(groupId = it, navController = navController)
                    }
                }

                composable("editTransaction/{transactionId}") { backStackEntry ->
                    val transactionId = backStackEntry.arguments?.getString("transactionId")
                    transactionId?.let {
                        EditTransactionDetailScreen(
                            navController = navController,
                            transactionId = it
                        )
                    }
                }

                composable("qrCodeScanner") {
                    QRCodeScannerScreen(
                        onScanResult = { result ->
                            navController.previousBackStackEntry?.savedStateHandle?.set("groupLink", result)
                            navController.navigateUp()
                        },
                        onBack = {
                            navController.navigateUp()
                        }
                    )
                }

                composable("groupDetail/{groupId}") { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId")
                    groupId?.let {
                        GroupSettingScreen(
                            groupId = it,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
                composable(
                    route = "groupTest/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                    GroupTest(navController, viewModel, groupId)
                }
                composable("memberListScreen/{groupId}") { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                    MemberListScreen(navController, viewModel, groupId)
                }
            }
        }
    }
}


@Composable
fun DrawerContent(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    onLogOut: () -> Unit,
    viewModel: MainViewModel
) {
    val user by viewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            val currentUser = user
            when (user?.image) {
                "" -> PersonalDetailNull(user!!)
                else -> currentUser?.let { PersonalDetail(it) }
            }
        }

        Divider()

        // Menu items
        Spacer(modifier = Modifier.height(16.dp))
        NavigationDrawerItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_nav_user), contentDescription = null) },
            label = { Text("My Profile") },
            selected = false,
            onClick = {
                navController.navigate("profile")
                onCloseDrawer()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        NavigationDrawerItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_nav_sign_out), contentDescription = null) },
            label = { Text("Sign Out") },
            selected = false,
            onClick = {
                onLogOut()
            }
        )
    }
}


@Composable
fun PersonalDetail(user: User) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = coil.request.ImageRequest.Builder(LocalContext.current)
                .data(user.image)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_user_place_holder),
            contentDescription = "User Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(user.name)
    }
}

@Composable
fun PersonalDetailNull(user: User) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_user_place_holder),
            contentDescription = "User Image",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(user.name)
    }
}
