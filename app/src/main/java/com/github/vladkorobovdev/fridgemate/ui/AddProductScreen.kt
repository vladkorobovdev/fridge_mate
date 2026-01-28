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

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add a product") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                            val date = format.parse(dateString)?.time ?: throw Exception("Invalid date")

                            viewModel.addProduct(name, date)
                            onNavigateBack()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Date Error! Format: 25.12.2025", Toast.LENGTH_SHORT).show()
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