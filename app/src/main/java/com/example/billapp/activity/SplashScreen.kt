package com.example.billapp.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.firebase.FirestoreClass
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val montserratBold = FontFamily(Font(R.font.montserrat_bold))

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = montserratBold,
            fontSize = 60.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    LaunchedEffect(key1 = true) {
        delay(2500) // 2.5 seconds delay
        val currentUserID = FirestoreClass().getCurrentUserID()
        if (currentUserID.isNotEmpty()) {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("intro") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}