package com.example.billapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billapp.viewModel.MainViewModel

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "設定",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = Color.Black
        )
        Button(onClick = {
            navController.navigate("profile")
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .size(height = 60.dp, width = 200.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Text(text = "帳號",
                fontSize = 18.sp,
                color = Color.Black)
        }
        Button(onClick = {
            navController.navigate("contact_us")
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .size(height = 60.dp, width = 200.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Text(text = "聯絡我們",
                fontSize = 18.sp,
                color = Color.Black)
        }
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/gi6Fyew6qfFyVAn29"))
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .size(height = 60.dp, width = 200.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Text(
                text = "問題回報",
                fontSize = 18.sp,
                color = Color.Black,
            )
        }
        Button(onClick = {
            navController.navigate("about")
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .size(height = 60.dp, width = 200.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Text(text = "關於",
                fontSize = 18.sp,
                color = Color.Black)
        }
    }
}

@Composable
fun SettingButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .size(height = 60.dp, width = 200.dp), // 設定按鈕高度和寬度
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(Color.LightGray)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.Black
        )

    }
}