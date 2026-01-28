package com.github.vladkorobovdev.fridgemate.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.vladkorobovdev.fridgemate.ui.camera.CameraPreviewScreen
import com.github.vladkorobovdev.fridgemate.viewmodel.FridgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: FridgeViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var isCameraOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraOpen = true
        } else {
            Toast.makeText(context, "Camera permission is required for scanning!", Toast.LENGTH_SHORT).show()
        }
    }

    if (isCameraOpen) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreviewScreen(onBarcodeScanned = { scannedCode ->
                barcode = scannedCode
                isCameraOpen = false

                isLoading = true
                viewModel.searchProductByBarcode(scannedCode) { foundName, foundImage ->
                    isLoading = false
                    if (foundName != null) {
                        name = foundName
                        imageUrl = foundImage
                    }
                }
            })

            Button(
                onClick = { isCameraOpen = false },
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    } else {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Add product") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Icon(Icons.Default.Info, contentDescription = "Scan")
                    }
                }

                Button(
                    onClick = {
                        if (barcode.isNotEmpty()) {
                            isLoading = true
                            viewModel.searchProductByBarcode(barcode) { foundName, foundImage ->
                                isLoading = false
                                if (foundName != null) {
                                    name = foundName
                                    imageUrl = foundImage
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Find data online")
                }

                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Photo",
                        modifier = Modifier.size(100.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateString,
                    onValueChange = { dateString = it },
                    label = { Text("Expires on (dd.MM.yyyy)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (name.isNotEmpty() && dateString.isNotEmpty()) {
                            try {
                                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                val date = format.parse(dateString)?.time ?: throw Exception()
                                viewModel.addProduct(name, date, barcode, imageUrl)
                                onNavigateBack()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Date format error!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Product")
                }
            }
        }
    }
}