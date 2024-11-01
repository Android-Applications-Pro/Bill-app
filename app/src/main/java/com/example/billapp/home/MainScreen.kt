package com.example.billapp.home

import AvatarScreen
import ExposedDropdown
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.billapp.Achievement.AchievementsScreen
import com.example.billapp.Achievement.DetailedAchievementsScreen
import com.example.billapp.Achievement.DetailedBadgesScreen
import com.example.billapp.ProfileScreen
import com.example.billapp.QRCodeScannerScreen
import com.example.billapp.R
import com.example.billapp.SettingScreen
import com.example.billapp.bonus.CurrencyConverterScreen
import com.example.billapp.bonus.ExchangeRateTableScreen
import com.example.billapp.dept_relation.DebtRelationsScreen
import com.example.billapp.group.AddInvitationScreen
import com.example.billapp.group.CreateGroup
import com.example.billapp.group.GroupInviteLinkScreen
import com.example.billapp.group.GroupScreen
import com.example.billapp.group.GroupSettingScreen
import com.example.billapp.group.MemberListScreen
import com.example.billapp.data.models.User
import com.example.billapp.group.GroupTest
import com.example.billapp.personal.EditTransactionDetailScreen
import com.example.billapp.personal.PersonalUIScreen
import com.example.billapp.setting.AboutScreen
import com.example.billapp.setting.ContactUsScreen
import com.example.billapp.setting.LineTokenUpdateScreen
import com.example.billapp.sign.IntroScreen
import com.example.billapp.sign.SignInScreen
import com.example.billapp.sign.SignUpScreen
import com.example.billapp.sign.SplashScreen
import com.example.billapp.ui.theme.theme.Black
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import com.example.billapp.ui.theme.theme.DarkGray
import com.example.billapp.ui.theme.theme.White
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch

const val DETAILED_ACHIEVEMENTS_ROUTE = "detailed_achievements"
const val DETAILED_BADGES_ROUTE = "detailed_badges"

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
    val selectedIcons = listOf(
        R.drawable.capybara_icon_spaces,
        R.drawable.capybara_icon_spaces,
        R.drawable.capybara_icon_spaces,
        R.drawable.capybara_icon_spaces,
        R.drawable.capybara_icon_spaces
    )
    val unselectedIcons = listOf(
        R.drawable.home_icon_spaces,
        R.drawable.person_icon_spaces,
        R.drawable.add_icon_spaces,
        R.drawable.group_icon_spaces,
        R.drawable.setting_icon_spaces
    )

    Box{
        Scaffold(
            bottomBar = {
                if (currentRoute != "intro" && currentRoute != "signin" && currentRoute != "signup" && currentRoute != "splash") { // 確認當前路由不是 IntroScreen
                    NavigationBar(
                        containerColor = BottomBackgroundColor,
                        modifier = Modifier.height(80.dp)
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    // 檢查當前選中的索引是否與當前項目匹配
                                    val iconResId = if (selectedItem == index ) {
                                        // 使用選中的圖標
                                        selectedIcons[index]
                                    } else {
                                        unselectedIcons[index]
                                    }
                                    Icon(
                                        painter = painterResource(id = iconResId),
                                        contentDescription = item,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(2.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,  // 設定適當的文字大小
                                        fontWeight = FontWeight.Medium  // 設定字體粗細
                                    ) },
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
                                },
                                modifier = Modifier,

                                colors = NavigationBarItemColors(
                                    selectedIndicatorColor = Color.Transparent,
                                    selectedIconColor = Black,
                                    selectedTextColor = Black,
                                    unselectedIconColor = White,
                                    unselectedTextColor =  White,
                                    disabledIconColor = DarkGray,
                                    disabledTextColor = DarkGray
                                )
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
                    QRCodeScannerScreen(navController = navController)
                }

                composable("groupDetail/{groupId}") { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId")
                    groupId?.let {
                        GroupSettingScreen(
                            groupId = it,
                            viewModel = viewModel,
                            avatarViewModel = avatarViewModel,
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
                    MemberListScreen(navController, viewModel, avatarViewModel, groupId)
                }


                composable("ItemAdd"){
                    ItemAdd(navController, viewModel)
                }
                composable("avatar"){
                    AvatarScreen(viewModel = avatarViewModel)
                }

                composable("deptRelationsScreen/{groupId}") { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                    DebtRelationsScreen(
                        avatarViewModel = avatarViewModel,
                        viewModel = viewModel,
                        groupId = groupId,
                        onBackPress = { navController.popBackStack() }
                    )
                }

                composable("achievements") {
                    AchievementsScreen(
                        viewModel = viewModel,
                        navController = navController,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable(DETAILED_ACHIEVEMENTS_ROUTE) {
                    DetailedAchievementsScreen(
                        achievements = viewModel.achievements.collectAsState().value,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable(DETAILED_BADGES_ROUTE) {
                    DetailedBadgesScreen(
                        badges = viewModel.badges.collectAsState().value,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable("line_token_update") {
                    LineTokenUpdateScreen(navController = navController, viewModel = viewModel)
                }

            }
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
