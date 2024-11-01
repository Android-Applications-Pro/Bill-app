package com.example.billapp.bonus

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.billapp.lightBrown
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateTableScreen(navController: NavController, baseCurrency: String, currencies: List<String>) {
    var exchangeRates by remember { mutableStateOf<Map<String, Double>?>(null) }
    var lastUpdateTime by remember { mutableStateOf<Long?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // LaunchedEffect 部分保持不變
    LaunchedEffect(baseCurrency) {
        coroutineScope.launch {
            val lastUpdate = DataStoreManager.getLastUpdateTime(context).first()
            lastUpdateTime = lastUpdate
            val currentTime = System.currentTimeMillis()
            if (currentTime - (lastUpdate ?: 0L) > TimeUnit.DAYS.toMillis(1)) {
                try {
                    val response = RetrofitInstance.api.getLatestRates("4d3d42c12ac2d28c5f08058b", baseCurrency)
                    val rates = response.conversion_rates.filterKeys { it in currencies }
                    exchangeRates = rates
                    rates.forEach { (currency, rate) ->
                        DataStoreManager.saveExchangeRate(context, currency, rate)
                    }
                    DataStoreManager.saveLastUpdateTime(context, currentTime)
                    lastUpdateTime = currentTime
                } catch (e: Exception) {
                    exchangeRates = null
                }
            } else {
                val rates = mutableMapOf<String, Double>()
                currencies.forEach { currency ->
                    val rate = DataStoreManager.getExchangeRate(context, currency).first()
                    if (rate != null) {
                        rates[currency] = rate
                    }
                }
                exchangeRates = rates
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "匯率表格",
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
        containerColor = MainBackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = BoxBackgroundColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TWD 對其他幣值的匯率",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (exchangeRates == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = lightBrown)
                        }
                    } else {
                        exchangeRates?.forEach { (currency, rate) ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8E9D7)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = getCurrencyName(currency),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = currency,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black
                                        )
                                    }
                                    Text(
                                        text = String.format("%.4f", 1 / rate),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = lightBrown,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            lastUpdateTime?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = Date(it)
                    Text(
                        text = "最後更新：${dateFormat.format(date)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun getCurrencyName(currencyCode: String): String { return when (currencyCode) { "TWD" -> "新台幣" "USD" -> "美元" "EUR" -> "歐元" "JPY" -> "日圓" "GBP" -> "英鎊" "AUD" -> "澳幣" "CAD" -> "加幣" "CHF" -> "瑞士法郎" "CNY" -> "人民幣" "SEK" -> "瑞典克朗" "NZD" -> "紐西蘭元" else -> currencyCode } }