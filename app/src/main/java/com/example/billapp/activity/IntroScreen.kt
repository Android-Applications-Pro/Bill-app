package com.example.billapp.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billapp.R

@Composable
fun IntroScreen(navController: NavController) {
    val montserratBold = FontFamily(Font(R.font.montserrat_bold))

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.intro_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontFamily = montserratBold,
                fontSize = 50.sp,
                color = Color(0xFF0C90F1),
                modifier = Modifier.padding(top = 40.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.capybara),
                contentDescription = stringResource(id = R.string.image_contentDescription),
                modifier = Modifier.padding(top = 30.dp)
            )

            Text(
                text = stringResource(id = R.string.let_s_get_started_text),
                fontFamily = montserratBold,
                fontSize = 25.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 25.dp)
            )

            Button(
                onClick = { navController.navigate("signIn") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C90F1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
            ) {
                Text(stringResource(id = R.string.sign_in), color = Color.White)
            }

            Button(
                onClick = { navController.navigate("signUp") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 15.dp)
            ) {
                Text(stringResource(id = R.string.sign_up), color = Color(0xFF0C90F1))
            }
        }
    }
}