package com.github.vladkorobovdev.fridgemate

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.vladkorobovdev.fridgemate.data.FridgeDatabase
import com.github.vladkorobovdev.fridgemate.notification.DailyReceiver
import com.github.vladkorobovdev.fridgemate.notification.NotificationHelper
import com.github.vladkorobovdev.fridgemate.ui.AddProductScreen
import com.github.vladkorobovdev.fridgemate.ui.ProductListScreen
import com.github.vladkorobovdev.fridgemate.viewmodel.FridgeViewModel
import com.github.vladkorobovdev.fridgemate.viewmodel.FridgeViewModelFactory
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        scheduleDailyCheck(this)

        val database = FridgeDatabase.getDatabase(this)

        setContent {
            val viewModel: FridgeViewModel = viewModel(
                factory = FridgeViewModelFactory(database.productDao())
            )

            val context = LocalContext.current
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            FridgeAppNavigation(viewModel)
        }
    }

    private fun scheduleDailyCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        ///////////////////// TEST

//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, DailyReceiver::class.java)
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val triggerTime = System.currentTimeMillis() + (15 * 1000)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (alarmManager.canScheduleExactAlarms()) {
//                alarmManager.setExact(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
//            }
//        } else {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
//        }
//
//        println("TEST: Alarm set for 15 seconds from now")
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