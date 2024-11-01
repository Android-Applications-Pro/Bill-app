package com.example.billapp.dept_relation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.billapp.data.models.DebtRelation
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel

@Composable
fun DeptRelationList(
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel,
    debtRelations: Map<String, List<DebtRelation>>,
    groupId: String,
    modifier: Modifier
) {
    val optimizedDeptRelations = remember(debtRelations) {
        optimizeDebtRelations(debtRelations.values.flatten())
    }
    val user by viewModel.user.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MainBackgroundColor)
    ) {
        val filteredDeptRelations = optimizedDeptRelations.filter { (pair, _) ->
            user!!.id == pair.first || user!!.id == pair.second
        }

        if (filteredDeptRelations.isEmpty()) {
            item {
                Text(
                    text = "查無欠債關係",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            filteredDeptRelations.forEach { (pair, amount) ->
                item {
                    var fromName by remember { mutableStateOf("") }
                    var toName by remember { mutableStateOf("") }
                    var fromUrl by remember { mutableStateOf("") }
                    var toUrl by remember { mutableStateOf("") }

                    LaunchedEffect(pair.first, pair.second) {
                        fromName = viewModel.getUserName(pair.first)
                        toName = viewModel.getUserName(pair.second)
                        fromUrl = avatarViewModel.loadAvatar(pair.first).toString()
                        toUrl = avatarViewModel.loadAvatar(pair.second).toString()
                    }

                    GroupedDeptRelationItem(
                        viewModel = viewModel,
                        fromName = fromName,
                        toName = toName,
                        fromUrl = fromUrl,
                        toUrl = toUrl,
                        totalAmount = amount,
                        debtRelations = debtRelations.values.flatten().filter {
                            (it.from == pair.first && it.to == pair.second) ||
                                    (it.from == pair.second && it.to == pair.first)
                        },
                        groupId = groupId
                    )
                }
            }
        }
    }
}

private fun optimizeDebtRelations(relations: List<DebtRelation>): Map<Pair<String, String>, Double> {
    val debtMap = mutableMapOf<Pair<String, String>, Double>()

    relations.forEach { relation ->
        val key = if (relation.from < relation.to) {
            Pair(relation.from, relation.to)
        } else {
            Pair(relation.to, relation.from)
        }

        val currentAmount = debtMap.getOrDefault(key, 0.0)
        if (relation.from < relation.to) {
            debtMap[key] = currentAmount + relation.amount
        } else {
            debtMap[key] = currentAmount - relation.amount
        }
    }

    return debtMap.filter { it.value != 0.0 }.mapValues { (key, value) ->
        if (value > 0) {
            Pair(key.first, key.second) to value
        } else {
            Pair(key.second, key.first) to -value
        }
    }.map { it.value }.toMap()
}