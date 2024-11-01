package com.example.billapp.dept_relation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import com.example.billapp.R
import com.example.billapp.data.models.DebtRelation
import com.example.billapp.ui.theme.theme.Black
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.Brown5
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupedDeptRelationItem(
    viewModel: MainViewModel,
    fromName: String,
    toName: String,
    fromUrl: String,
    toUrl: String,
    totalAmount: Double,
    debtRelations: List<DebtRelation>,
    groupId: String
) {
    var expanded by remember { mutableStateOf(false) }
    var showClearAllConfirmation by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    ) {
                        if (fromUrl.isNotEmpty()) {
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(fromUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.ic_user_place_holder),
                                contentDescription = "User Image",
                                error = painterResource(R.drawable.ic_user_place_holder),
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
                    Text(text = fromName, style = MaterialTheme.typography.bodyLarge)
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Arrow"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    ) {
                        if (toUrl.isNotEmpty()) {
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(toUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.ic_user_place_holder),
                                contentDescription = "User Image",
                                error = painterResource(R.drawable.ic_user_place_holder),
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
                    Text(text = toName, style = MaterialTheme.typography.bodyLarge)
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    TextButton(
                        onClick = {
                            showBottomSheet = true
                        },
                        modifier = Modifier.padding(top = 4.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Brown5
                        ),
                        border = BorderStroke(1.dp, Brown5) // 邊框設定
                    ) {
                        Text("一次结清", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (expanded) {
            debtRelations.forEach { relation ->
                DebtRelationDetailItem(
                    viewModel = viewModel,
                    debtRelation = relation,
                    groupId = groupId,
                )
            }
        }
    }

    if (showBottomSheet) {
        PaymentBottomSheet(
            onDismiss = { showBottomSheet = false },
            onConfirm = {
                showClearAllConfirmation = true
            },
            context = context
        )
    }

    if (showClearAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirmation = false },
            title = { Text("確認一次結清") },
            text = { Text("您確定要一次結清所有債務嗎？總金額: $${String.format("%.2f", totalAmount)}") },
            confirmButton = {
                Button(
                    onClick = {
                        debtRelations.forEach { relation ->
                            viewModel.deleteDebtRelation(groupId, relation.id)
                        }
                        viewModel.getGroupDebtRelations(groupId)
                        viewModel.calculateTotalDebtForGroup(groupId)
                        showClearAllConfirmation = false
                        showBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF93F37C)
                    )
                ) {
                    Text("確認")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearAllConfirmation = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF415C)
                    )
                ) {
                    Text("取消")
                }
            }
        )
    }
}
