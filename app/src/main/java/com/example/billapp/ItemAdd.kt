package com.example.billapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylishTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val capybaraBrown = colorResource(id = R.color.colorAccent)

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontFamily = FontFamily.Cursive,
            color = capybaraBrown
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, capybaraBrown, RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorResource(id = R.color.colorLight), // 聚焦時的背景色
            unfocusedContainerColor = colorResource(id = R.color.colorLight), // 未聚焦時的背景色
            focusedIndicatorColor = capybaraBrown, // 聚焦時
            unfocusedIndicatorColor = Color.Transparent // 未聚焦時
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuField(
    label: String,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("選項 1", "選項 2", "選項 3")

    val capybaraBrown = colorResource(id = R.color.colorAccent)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.colorLight), // 聚焦时的背景色
                unfocusedContainerColor = colorResource(id = R.color.colorLight), // 未聚焦時的背景色
                focusedIndicatorColor = capybaraBrown, // 聚焦時
                unfocusedIndicatorColor = Color.Transparent // 未聚焦時
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(2.dp, capybaraBrown, RoundedCornerShape(8.dp))  // 添加深色外框
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onItemSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomKeyboard(
    onKeyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val capybaraBrown = colorResource(id = R.color.colorAccent)
    val capybaraLight = colorResource(id = R.color.colorLight)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val buttonModifier = Modifier
            .weight(1f)
            .padding(4.dp)

        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = capybaraLight
        )

        // 鍵盤按鈕
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier, onClick = { onKeyClick("7") }, colors = buttonColors) {
                Text("7", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("8") }, colors = buttonColors) {
                Text("8", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("9") }, colors = buttonColors) {
                Text("9", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("÷") }, colors = buttonColors) {
                Text("÷", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier, onClick = { onKeyClick("4") }, colors = buttonColors) {
                Text("4", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("5") }, colors = buttonColors) {
                Text("5", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("6") }, colors = buttonColors) {
                Text("6", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("×") }, colors = buttonColors) {
                Text("×", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier, onClick = { onKeyClick("1") }, colors = buttonColors) {
                Text("1", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("2") }, colors = buttonColors) {
                Text("2", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("3") }, colors = buttonColors) {
                Text("3", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("-") }, colors = buttonColors) {
                Text("-", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier, onClick = { onKeyClick(".") }, colors = buttonColors) {
                Text(".", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("0") }, colors = buttonColors) {
                Text("0", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = onDeleteClick, colors = buttonColors) {
                Text("⌫", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = { onKeyClick("+") }, colors = buttonColors) {
                Text("+", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier, onClick = onClearClick, colors = buttonColors) {
                Text("AC", color = capybaraBrown)
            }
            Button(modifier = buttonModifier, onClick = onOkClick, colors = buttonColors) {
                Text("OK", color = capybaraBrown)
            }
        }
    }
}

@Composable
fun ItemAdd() {
    var selectedTab by remember { mutableStateOf("個人") }
    var groupName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var payer by remember { mutableStateOf("") }
    var splitMethod by remember { mutableStateOf("") }
    var showKeyboard by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9C9BA))  // 設置頁面的背景顏色
            .padding(16.dp)  // 在背景上應用 padding
    ) {
        // 選項卡 (個人/群組)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { selectedTab = "個人" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "個人") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                )
            ) {
                Text("個人")
            }
            Button(
                onClick = { selectedTab = "群組" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "群組") colorResource(id = R.color.colorAccent) else colorResource(id = R.color.primary_text_color)
                )
            ) {
                Text("群組")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 當前選項卡的內容
        if (selectedTab == "群組") {
            StylishTextField(
                value = TextFieldValue(groupName),
                onValueChange = { groupName = it.text },
                label = "群組名稱"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 金額輸入
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            StylishTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    showKeyboard = true
                },
                label = "金額",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showKeyboard = true
                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 類別、名稱、付款人、分帳方式
        DropDownMenuField(
            label = "類別",
            selectedItem = category,
            onItemSelected = { category = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropDownMenuField(
            label = "名稱",
            selectedItem = name,
            onItemSelected = { name = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropDownMenuField(
            label = "付款人",
            selectedItem = payer,
            onItemSelected = { payer = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropDownMenuField(
            label = "分帳方式",
            selectedItem = splitMethod,
            onItemSelected = { splitMethod = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        // 自製鍵盤
        if (showKeyboard) {
            CustomKeyboard(
                onKeyClick = { key ->
                    amount = TextFieldValue(amount.text + key)
                },
                onDeleteClick = {
                    if (amount.text.isNotEmpty()) {
                        amount = TextFieldValue(amount.text.dropLast(1))
                    }
                },
                onClearClick = {
                    amount = TextFieldValue("")
                },
                onOkClick = {
                    showKeyboard = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}