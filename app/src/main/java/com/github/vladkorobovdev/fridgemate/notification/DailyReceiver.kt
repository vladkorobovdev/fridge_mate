package com.github.vladkorobovdev.fridgemate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.vladkorobovdev.fridgemate.data.FridgeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DailyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                checkExpirationAndNotify(context)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun checkExpirationAndNotify(context: Context) {
        val database = FridgeDatabase.getDatabase(context)
        val dao = database.productDao()

        val now = System.currentTimeMillis()
        val threeDaysInMillis = TimeUnit.DAYS.toMillis(3)
        val targetDate = now + threeDaysInMillis

        val expiringProducts = dao.getExpiringProducts(targetDate)

        val validExpiring = expiringProducts.filter {
            it.expirationDate > (now - TimeUnit.DAYS.toMillis(1))
        }

        if (validExpiring.isNotEmpty()) {
            val count = validExpiring.size
            val text = "Attention! $count products are expiring soon."
            NotificationHelper.sendNotification(context, "Fridge Check", text)
        }
    }
}