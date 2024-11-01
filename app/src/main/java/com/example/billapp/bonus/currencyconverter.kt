package com.example.billapp.bonus

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.lightBrown
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.LightGray
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class ExchangeRateResponse(
    val conversion_rates: Map<String, Double>
)

interface ExchangeRateApiService {
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String
    ): ExchangeRateResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://v6.exchangerate-api.com/"

    val api: ExchangeRateApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApiService::class.java)
    }
}

@SuppressLint("DefaultLocale")
suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.getLatestRates("4d3d42c12ac2d28c5f08058b", fromCurrency)
            val fromRate = response.conversion_rates[fromCurrency] ?: 1.0
            val toRate = response.conversion_rates[toCurrency] ?: 1.0
            val convertedAmount = amount * (toRate / fromRate)
            String.format("%.2f", convertedAmount)
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(navController: NavController) {
    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("TWD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var result by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val currencies = listOf("TWD", "USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "貨幣轉換器",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBackgroundColor
                )
            )
        },
        containerColor = MainBackgroundColor,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("金額") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = lightBrown,
                            focusedLabelColor = lightBrown
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenu(
                            label = "從",
                            selectedCurrency = fromCurrency,
                            onCurrencySelected = { fromCurrency = it },
                            currencies = currencies,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val temp = fromCurrency
                                fromCurrency = toCurrency
                                toCurrency = temp
                            }
                        ) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = "Swap currencies",
                                tint = lightBrown
                            )
                        }

                        ExposedDropdownMenu(
                            label = "到",
                            selectedCurrency = toCurrency,
                            onCurrencySelected = { toCurrency = it },
                            currencies = currencies,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        result = convertCurrency(amount.toDoubleOrNull() ?: 0.0, fromCurrency, toCurrency)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = lightBrown),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("轉換", style = MaterialTheme.typography.bodyLarge)
            }

            AnimatedVisibility(
                visible = result.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "轉換結果",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = result,
                            style = MaterialTheme.typography.headlineMedium,
                            color = lightBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = { navController.navigate("exchangeRateTable") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGray
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("查看匯率表", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenu(
    label: String,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    currencies: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = lightBrown,
                focusedLabelColor = lightBrown,
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(BottomBackgroundColor)
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency, color = Color.White) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color.White
                    )
                )
            }
        }
    }
}

