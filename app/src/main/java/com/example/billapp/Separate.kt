package com.example.billapp


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.R
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.example.billapp.models.User

@Composable
fun SeparateBottomSheetContent(
    viewModel: MainViewModel,
    groupId: String,
    amount: Float,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    val shareMethod by viewModel.shareMethod.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val dividers by viewModel.dividers.collectAsState()
    val payers by viewModel.payers.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.getGroupMembers(groupId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )  {
        Text("分帳選項", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Share method selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("均分", "比例", "調整", "金額", "份數").forEach { method ->
                Button(
                    onClick = { viewModel.updateShareMethod(method) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (shareMethod == method) colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(method, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on selected share method
        when (shareMethod) {
            "均分" -> EvenSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "比例" -> ProportionalSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "調整" -> AdjustableSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "金額" -> ExactAmountSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "份數" -> SharesSplitContent(viewModel, payers, dividers, groupMembers, amount)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onDismiss) {
                Text("取消")
            }
            Button(onClick = onComplete) {
                Text("完成")
            }
        }
    }
}

@Composable
fun SeparateScreen(
    navController: NavController,
    viewModel: MainViewModel,
    groupId: String,
    amount: Float,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    LaunchedEffect(groupId) {
        viewModel.getGroupMembers(groupId)
    }

    val shareMethod by viewModel.shareMethod.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val dividers by viewModel.dividers.collectAsState()
    val payers by viewModel.payers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9C9BA))
            .padding(16.dp)
    ) {
        // Title
        Text("分帳選項", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.updateShareMethod("均分") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (shareMethod == "均分") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("均分")
            }
            Button(
                onClick = { viewModel.updateShareMethod("比例") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (shareMethod == "比例") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("比例")
            }
            Button(
                onClick = { viewModel.updateShareMethod("調整") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (shareMethod == "調整") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("調整")
            }
            Button(
                onClick = { viewModel.updateShareMethod("金額") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (shareMethod == "金額") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("金額")
            }
            Button(
                onClick = { viewModel.updateShareMethod("份數") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (shareMethod == "份數") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("份數")
            }
        }


        // Content based on selected share method
        when (shareMethod) {
            "均分" -> EvenSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "比例" -> ProportionalSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "調整" -> AdjustableSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "金額" -> ExactAmountSplitContent(viewModel, payers, dividers, groupMembers, amount)
            "份數" -> SharesSplitContent(viewModel, payers, dividers, groupMembers, amount)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onDismiss) {
                Text("取消")
            }
            Button(onClick = {
                onComplete()
                navController.popBackStack()
            }) {
                Text("完成")
            }
        }
    }
}

@Composable
fun EvenSplitContent(
    viewModel: MainViewModel,
    payers: List<String>,
    dividers: List<String>,
    groupMembers: List<User>,
    amount: Float
) {
    Column {
        Text(
            text = "均分",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "依照人數均分。",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "總金額: ${"%.2f".format(amount)}",
                fontSize = 20.sp,
                color = Color.Black
            )
        }

        val perPersonAmount = amount / dividers.size
        LazyColumn {
            items(dividers) { memberId ->
                val member = groupMembers.find { it.id == memberId }
                member?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "$${"%.2f".format(perPersonAmount)}",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ProportionalSplitContent(
    viewModel: MainViewModel,
    payers: List<String>,
    dividers: List<String>,
    groupMembers: List<User>,
    amount: Float
) {
    val percentages = viewModel.userPercentages.collectAsState().value

    Column {
        Text(
            text = "按比例",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "依照每人比例分配",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "總金額: ${"%.2f".format(amount)}",
                fontSize = 20.sp,
                color = Color.Black
            )
        }

        LazyColumn {
            items(dividers) { memberId ->
                val member = groupMembers.find { it.id == memberId }
                member?.let {
                    val percentage = percentages[memberId] ?: 0f
                    val calculatedAmount = percentage / 100 * amount

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "$${"%.2f".format(calculatedAmount)}",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                        OutlinedTextField(
                            value = percentage.toString(),
                            onValueChange = { newPercentage ->
                                if (newPercentage.isEmpty() || newPercentage.toFloatOrNull() != null) {
                                    val percentageValue = newPercentage.toFloatOrNull() ?: 0f
                                    val updatedPercentages = percentages.toMutableMap().apply {
                                        put(memberId, percentageValue)
                                    }
                                    viewModel.updateUserPercentages(updatedPercentages)
                                }
                            },
                            label = { Text("%") },
                            modifier = Modifier
                                .width(80.dp)
                                .padding(horizontal = 8.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AdjustableSplitContent(
    viewModel: MainViewModel,
    payers: List<String>,
    dividers: List<String>,
    groupMembers: List<User>,
    amount: Float
) {
    val userAdjustments = viewModel.userAdjustments.collectAsState().value

    Column {
        Text(
            text = "按調整",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "扣除每人額外的花費後，剩下部分所有人均分。",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "總金額: ${"%.2f".format(amount)}",
                fontSize = 20.sp,
                color = Color.Black
            )
        }

        LazyColumn {
            items(dividers) { memberId ->
                val member = groupMembers.find { it.id == memberId }
                member?.let {
                    val adjustment = userAdjustments[memberId] ?: 0f
                    val remainingAmount = amount - userAdjustments.values.sum()
                    val perPersonAmount = remainingAmount / dividers.size
                    val finalAmount = adjustment + perPersonAmount

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "$${"%.2f".format(finalAmount)}",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                        OutlinedTextField(
                            value = adjustment.toString(),
                            onValueChange = { newAmount ->
                                if (newAmount.isEmpty() || newAmount.toFloatOrNull() != null) {
                                    val adjustmentValue = newAmount.toFloatOrNull() ?: 0f
                                    val updatedAdjustments = userAdjustments.toMutableMap().apply {
                                        put(memberId, adjustmentValue)
                                    }
                                    viewModel.updateUserAdjustments(updatedAdjustments)
                                }
                            },
                            label = { Text("金額") },
                            modifier = Modifier
                                .width(100.dp)
                                .padding(horizontal = 8.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun ExactAmountSplitContent(
    viewModel: MainViewModel,
    payers: List<String>,
    dividers: List<String>,
    groupMembers: List<User>,
    amount: Float
) {
    val userEnteredAmounts = viewModel.userExactAmounts.collectAsState().value

    Column {
        Text(
            text = "按確切金額",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "按照每人實際花費分配",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "總金額: ${"%.2f".format(amount)}",
                fontSize = 20.sp,
                color = Color.Black
            )
        }

        LazyColumn {
            items(dividers) { memberId ->
                val member = groupMembers.find { it.id == memberId }
                member?.let {
                    val enteredAmount = userEnteredAmounts[memberId] ?: 0f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "$${"%.2f".format(enteredAmount)}",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically)
                        )

                        OutlinedTextField(
                            value = enteredAmount.toString(),
                            onValueChange = { newAmount ->
                                if (newAmount.isEmpty() || newAmount.toFloatOrNull() != null) {
                                    val amountValue = newAmount.toFloatOrNull() ?: 0f
                                    val updatedAmounts = userEnteredAmounts.toMutableMap().apply {
                                        put(memberId, amountValue)
                                    }
                                    viewModel.updateUserExactAmounts(updatedAmounts)
                                }
                            },
                            label = { Text("金額") },
                            modifier = Modifier
                                .width(100.dp)
                                .padding(horizontal = 8.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun SharesSplitContent(
    viewModel: MainViewModel,
    payers: List<String>,
    dividers: List<String>,
    groupMembers: List<User>,
    amount: Float
) {
    val userShares = viewModel.userShares.collectAsState().value

    Column {
        Text(
            text = "按份數",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "按照每人持有的份數分配",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "總金額: ${"%.2f".format(amount)}",
                fontSize = 20.sp,
                color = Color.Black
            )
        }

        LazyColumn {
            items(dividers) { memberId ->
                val member = groupMembers.find { it.id == memberId }
                member?.let {
                    val shares = userShares[memberId] ?: 0
                    val totalShares = userShares.values.sum()
                    val calculatedAmount = if (totalShares > 0) shares / totalShares.toFloat() * amount else 0f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "$${"%.2f".format(calculatedAmount)}",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                        OutlinedTextField(
                            value = shares.toString(),
                            onValueChange = { newShares ->
                                if (newShares.isEmpty() || newShares.toIntOrNull() != null) {
                                    val shareValue = newShares.toIntOrNull() ?: 0
                                    val updatedShares = userShares.toMutableMap().apply {
                                        put(memberId, shareValue)
                                    }
                                    viewModel.updateUserShares(updatedShares)
                                }
                            },
                            label = { Text("份數") },
                            modifier = Modifier
                                .width(80.dp)
                                .padding(horizontal = 8.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}
