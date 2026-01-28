package com.github.vladkorobovdev.fridgemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.vladkorobovdev.fridgemate.data.FridgeDatabase
import com.github.vladkorobovdev.fridgemate.ui.AddProductScreen
import com.github.vladkorobovdev.fridgemate.ui.ProductListScreen
import com.github.vladkorobovdev.fridgemate.viewmodel.FridgeViewModel
import com.github.vladkorobovdev.fridgemate.viewmodel.FridgeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FridgeDatabase.getDatabase(this)

        setContent {
            val viewModel: FridgeViewModel = viewModel(
                factory = FridgeViewModelFactory(database.productDao())
            )

            FridgeAppNavigation(viewModel)
        }
    }
}

@Composable
fun FridgeAppNavigation(viewModel: FridgeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            ProductListScreen(
                viewModel = viewModel,
                onNavigateToAdd = { navController.navigate("add") }
            )
        }
        composable("add") {
            AddProductScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}