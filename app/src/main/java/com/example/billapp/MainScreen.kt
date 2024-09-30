package com.example.billapp

import AvatarScreen
import ExposedDropdown
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.billapp.bonus.CurrencyConverterScreen
import com.example.billapp.bonus.ExchangeRateTableScreen
import com.example.billapp.dept_relation.DeptRelationsScreen
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
import com.example.billapp.sign.IntroScreen
import com.example.billapp.sign.SignInScreen
import com.example.billapp.sign.SignUpScreen
import com.example.billapp.sign.SplashScreen
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel,
    requestPermission: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

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
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (currentRoute != "intro" && currentRoute != "signin" && currentRoute != "signup" && currentRoute != "splash") { // 確認當前路由不是 IntroScreen
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
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier.padding(innerPadding)
            ) {
                // 這兩個要統一，要找到使用 main 的地方改成 home
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        },
                        viewModel = viewModel,
                        avatarViewModel = avatarViewModel
                    )
                }
                composable("main") {
                    HomeScreen(
                        navController = navController,
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        },
                        viewModel = viewModel,
                        avatarViewModel = avatarViewModel
                    )
                }

                composable("intro") { IntroScreen(navController) }
                // help me do this
                composable("signin") {
                    SignInScreen(viewModel = viewModel, navController = navController)
                }

                composable("signup") {
                    SignUpScreen(viewModel = viewModel, navController = navController)
                }
                composable("splash") {
                    SplashScreen(navController = navController, viewModel = viewModel)
                }

                composable("personal") {
                    PersonalUIScreen(
                        navController = navController,
                        viewModel = viewModel,
                    )
                }
                composable("add") {
                    ItemAdd(
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
                        avatarViewModel = avatarViewModel
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        navController = navController,
                        viewModel = viewModel,
                        avatarViewModel = avatarViewModel,
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
                composable("currency"){
                    CurrencyConverterScreen(navController = navController)
                }
                composable("exchangeRateTable") {
                    ExchangeRateTableScreen(
                        navController, "TWD",
                        listOf("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD")
                    )
                }
                composable("TEST"){
                    ExposedDropdown(navController = navController, viewModel = viewModel)
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


                composable("ItemAdd"){
                    ItemAdd(navController, viewModel)
                }
                composable("avatar"){
                    AvatarScreen(viewModel = avatarViewModel)
                }

                composable("deptRelationsScreen/{groupId}") { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                    DeptRelationsScreen(
                        viewModel = viewModel,
                        groupId = groupId,
                        onBackPress = { navController.popBackStack() }
                    )
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
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel
) {
    val user = viewModel.user.collectAsState().value
    val userImage = avatarViewModel.avatarUrl.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

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
            when (userImage) {
                "" -> PersonalDetailNull(user!!)
                else -> user?.let { PersonalDetail(it, userImage) }
            }
        }

        HorizontalDivider()

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
                showDialog = true
            }
        )

        // Logout Confirmation Dialog
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
                            onLogOut()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}



@Composable
fun PersonalDetail(user: User, image: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(150.dp) // Adjust the size as needed
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            AsyncImage(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_user_place_holder),
                contentDescription = "User Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(CircleShape)
            )
        }
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
