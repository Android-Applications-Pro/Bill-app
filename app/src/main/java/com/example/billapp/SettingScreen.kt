package com.example.billapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
            .fillMaxHeight()
            .background(Color(0xFFE4DFCB))
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

        SettingButton(
            text = "帳號",
            icon = painterResource(id = R.drawable.baseline_person_24),
            onClick = {
                navController.navigate("profile")
            }
        )

        SettingButton(
            text = "聯絡我們",
            icon = painterResource(id = R.drawable.baseline_call_24),
            onClick = {
                navController.navigate("contact_us")
            }
        )

        SettingButton(
            text = "問題回報",
            icon = painterResource(id = R.drawable.baseline_report_problem_24),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/gi6Fyew6qfFyVAn29"))
                context.startActivity(intent)
            }
        )

        SettingButton(
            text = "關於",
            icon = painterResource(id = R.drawable.baseline_speaker_notes_24),
            onClick = {
                navController.navigate("about")
            }
        )
    }
}

@Composable
fun SettingButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .size(height = 60.dp, width = 200.dp), // 設定按鈕高度和寬度
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFBCBCBC))
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
