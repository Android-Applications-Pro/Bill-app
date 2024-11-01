package com.example.billapp

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.RGBLuminanceSource
import android.widget.FrameLayout
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.billapp.ui.theme.theme.BottomBackgroundColor
import com.example.billapp.ui.theme.theme.ButtonGrayColor
import com.example.billapp.ui.theme.theme.ButtonRedColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.ui.theme.theme.Orange1
import com.example.billapp.ui.theme.theme.PrimaryFontColor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    var hasCameraPermission by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(cameraPermissionState) {
        cameraPermissionState.launchPermissionRequest()
    }

    hasCameraPermission = cameraPermissionState.status.isGranted

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
                }
                decodeQRCodeFromBitmap(bitmap)?.let { qrCodeResult ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("qrCodeResult", qrCodeResult)
                    navController.popBackStack()
                } ?: run {
                    Log.e("QRCodeScannerScreen", "QR code not found in the selected image.")
                    showErrorDialog = true
                }
            } catch (e: Exception) {
                Log.e("QRCodeScannerScreen", "Error decoding QR code from image: ${e.message}")
                showErrorDialog = true
            }
        }
    }

    if (hasCameraPermission) {
        var barcodeView: CompoundBarcodeView? = null

        DisposableEffect(lifecycleOwner) {
            val observer = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    barcodeView?.resume()
                }

                override fun onPause(owner: LifecycleOwner) {
                    barcodeView?.pause()
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        val density = LocalDensity.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackgroundColor)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.Black)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(
                    factory = { context ->
                        CompoundBarcodeView(context).apply {
                            barcodeView = this
                            // 設置解碼器工廠以僅解碼 QR Code
                            barcodeView!!.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
                            decodeContinuous(object : BarcodeCallback {
                                override fun barcodeResult(result: BarcodeResult?) {
                                    result?.text?.let {
                                        navController.previousBackStackEntry?.savedStateHandle?.set("qrCodeResult", it)
                                        navController.popBackStack()
                                    }
                                }

                                override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
                            })
                            // 添加自定義文字
                            val textView = TextView(context).apply {
                                text = "請將群組QR code放在取景框內進行掃描"
                                setTextColor(android.graphics.Color.WHITE)
                                textSize = 16f
                                with(density) {
                                    setPadding(0, 0, 0, 16.dp.toPx().toInt())
                                }
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                                )
                            }
                            addView(textView)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Orange1),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("從相簿選擇照片", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGrayColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("返回", color = Color.White)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("需要相機權限來掃描 QR Code", color = PrimaryFontColor)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGrayColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("請求權限", color = Color.White)
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("確定", color = Color.Black)
                }
            },
            title = { Text("錯誤", color = Color.White) },
            text = { Text("無法解碼選擇的圖片中的 QR code，請重試。", color = Color.White) },
            containerColor = BottomBackgroundColor
        )
    }
}

fun decodeQRCodeFromBitmap(bitmap: Bitmap): String? {
    val intArray = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
    return try {
        val result: Result = QRCodeReader().decode(binaryBitmap)
        result.text
    } catch (e: Exception) {
        Log.e("QRCodeScannerScreen", "Error decoding QR code: ${e.message}")
        null
    }
}