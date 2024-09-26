import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import coil.compose.rememberAsyncImagePainter
import com.example.billapp.R
import com.example.billapp.viewModel.GroupCreationStatus
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    var groupName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val groupCreationStatus by viewModel.groupCreationStatus.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Create Group", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { showBottomSheet = true }
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = if (selectedImageUri != null) {
                        rememberAsyncImagePainter(selectedImageUri)
                    } else {
                        painterResource(id = R.drawable.ic_board_place_holder)
                    },
                    contentDescription = "Group Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createGroup(groupName, selectedImageUri, context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Group")
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                CreateGroupPresetAvatars(
                    onImageSelected = { uri ->
                        selectedImageUri = uri
                        showBottomSheet = false
                    },
                    launcher = launcher
                )
            }
        }
    }

    LaunchedEffect(groupCreationStatus) {
        if (groupCreationStatus == GroupCreationStatus.SUCCESS) {
            navController.navigateUp()
            viewModel.resetGroupCreationStatus()
        }
    }
}

@Composable
fun CreateGroupPresetAvatars(
    onImageSelected: (Uri?) -> Unit,
    launcher: ManagedActivityResultLauncher<String, Uri?>
) {
    val presets = listOf(
        R.drawable.image_group_travel,
        R.drawable.image_group_house,
        R.drawable.image_group_dining,
        R.drawable.image_group_shopping
    )

    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_photo),
                    contentDescription = "Choose from gallery",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        items(presets) { preset ->
            Image(
                painter = painterResource(id = preset),
                contentDescription = "Preset Group Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .clickable {
                        val uri = Uri.parse("android.resource://${context.packageName}/$preset")
                        onImageSelected(uri)
                    }
            )
        }
    }
}