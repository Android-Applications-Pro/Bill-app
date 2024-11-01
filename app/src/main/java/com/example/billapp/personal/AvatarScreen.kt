import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.billapp.R
import com.example.billapp.lightBrown
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import com.example.billapp.viewModel.AvatarViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarScreen(viewModel: AvatarViewModel) {
    val avatarUrl by viewModel.avatarUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadAvatar()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // Avatar Image
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(100.dp),
                color = lightBrown
            )
        } else {
            when {
                avatarUrl == null -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_place_holder),
                        contentDescription = "Default Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, lightBrown, CircleShape)
                            .clickable { showBottomSheet = true }
                    )
                }
                avatarUrl?.startsWith("android.resource://") == true -> {
                    val resourceId = avatarUrl?.substringAfterLast("/")?.toIntOrNull()
                        ?: R.drawable.ic_user_place_holder
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Preset Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, lightBrown, CircleShape)
                            .clickable { showBottomSheet = true }
                    )
                }
                else -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, lightBrown, CircleShape)
                            .clickable { showBottomSheet = true }
                    )
                }
            }
        }

        // Edit Icon
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Avatar",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomEnd)
                .clickable { showBottomSheet = true }
                .background(lightBrown, CircleShape)
                .padding(4.dp)
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion { showBottomSheet = false }
            },
            sheetState = sheetState,
            containerColor = BottomBackgroundColor,
            modifier = Modifier.fillMaxHeight(0.7f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "選擇頭像",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                PresetAvatars(viewModel)

                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion { showBottomSheet = false }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = lightBrown),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("取消")
                }
            }
        }
    }
}


@Composable
fun PresetAvatars(viewModel: AvatarViewModel) {
    val presets = listOf(
        R.drawable.image1,
        R.drawable.image10,
        R.drawable.image11,
        R.drawable.image12,
        R.drawable.image7,
        R.drawable.image8,
        R.drawable.image9
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_photo),
                    contentDescription = "Choose from gallery",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        items(presets) { preset ->
            Image(
                painter = painterResource(id = preset),
                contentDescription = "Preset Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { viewModel.usePresetAvatar(preset) }
            )
        }
    }
}