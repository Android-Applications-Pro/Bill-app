package com.example.billapp.dept_relation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billapp.models.DeptRelation

@Composable
fun DeptRelationList(deptRelations: Map<String, List<DeptRelation>>) {
    val groupedDeptRelations = deptRelations.values.flatten().groupBy { Pair(it.from, it.to) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        groupedDeptRelations.forEach { (pair, relations) ->
            item {
                GroupedDeptRelationItem(
                    from = pair.first,
                    to = pair.second,
                    totalAmount = relations.sumOf { it.amount },
                    deptRelations = relations
                )
            }
        }
    }
}

@Preview
@Composable
fun DeptRelationListPreview() {
    val deptRelations = mapOf(
        "id1" to listOf(
            DeptRelation(from = "John Doe", to = "Jane Smith", amount = 100.0),
            DeptRelation(from = "John Doe", to = "Jane Smith", amount = 50.0),
        ),
        "id2" to listOf(
            DeptRelation(from = "Alice Johnson", to = "Bob Brown", amount = 75.0),
            DeptRelation(from = "Alice Johnson", to = "Bob Brown", amount = 25.0),
        )
    )
    DeptRelationList(deptRelations = deptRelations)
}