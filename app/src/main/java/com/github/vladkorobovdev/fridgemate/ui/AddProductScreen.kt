package com.github.vladkorobovdev.fridgemate.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
    val context = LocalContext.current
    var barcode by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add product") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode (EAN)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Button(
                    onClick = {
                        if (barcode.isNotEmpty()) {
                            isLoading = true
                            viewModel.searchProductByBarcode(barcode) { foundName, foundImage ->
                                isLoading = false
                                if (foundName != null) {
                                    name = foundName
                                    imageUrl = foundImage
                                    Toast.makeText(context, "Found!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Find")
                }
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
                label = { Text("Product name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateString,
                onValueChange = { dateString = it },
                label = { Text("Date (dd.mm.yyyy)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (name.isNotEmpty() && dateString.isNotEmpty()) {
                        try {
                            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            val date = format.parse(dateString)?.time ?: throw Exception("Error")
                            viewModel.addProduct(name, date, barcode, imageUrl)
                            onNavigateBack()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Date format error!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}