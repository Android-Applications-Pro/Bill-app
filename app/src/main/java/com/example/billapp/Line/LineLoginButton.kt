package com.example.billapp.Line

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.billapp.R

@Composable
fun LineLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00B900)),
        modifier = Modifier.size(width = 200.dp, height = 48.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.line_icon),
            contentDescription = "Line Logo",
            modifier = Modifier.size(24.dp)
        )
    }
}