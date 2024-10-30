package com.example.billapp.sign

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.ui.theme.theme.Brown2
import com.example.billapp.ui.theme.theme.Brown3
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDataLoaded by viewModel.isDataLoaded.collectAsState()  // 新增狀態

    LaunchedEffect(authState) {
        if (authState is MainViewModel.AuthState.Authenticated) {
            val user = (authState as MainViewModel.AuthState.Authenticated).user
            // 在導航之前先加載所有必要數據
            viewModel.loadInitialData(user.id)
        }
    }

    // 監聽數據加載完成狀態
    LaunchedEffect(isDataLoaded) {
        if (isDataLoaded) {
            navController.navigate("home") {
                popUpTo("signIn") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.signinbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF404040)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.sign_in_description_text),
                fontSize = 16.sp,
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Brown3)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(id = R.string.email)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = TextStyle(fontSize = 16.sp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(id = R.string.password)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textStyle = TextStyle(fontSize = 16.sp),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = { viewModel.signIn(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Brown2),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = stringResource(id = R.string.sign_in),
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            when (authState) {
                is MainViewModel.AuthState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    )
                }
                is MainViewModel.AuthState.Authenticated -> {
                    LaunchedEffect(authState) {
                        navController.navigate("splash") {
                            popUpTo("signIn") { inclusive = true }
                        }
                    }
                }
                is MainViewModel.AuthState.Error -> {
                    Text(
                        text = error ?: "Unknown error occurred",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                else -> {}
            }
        }
    }
}


private fun validateForm(email: String, password: String): Boolean {
    return email.isNotBlank() && password.isNotBlank()
}
