package com.github.vladkorobovdev.fridgemate.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.vladkorobovdev.fridgemate.data.FridgeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ExpirationService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            checkExpirationDate()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private suspend fun checkExpirationDate() {
        val database = FridgeDatabase.getDatabase(applicationContext)
        val dao = database.productDao()

        val now = System.currentTimeMillis()
        val threeDaysInMillis = TimeUnit.DAYS.toMillis(3)
        val targetDate = now + threeDaysInMillis

        val expiringProducts = dao.getExpiringProducts(targetDate)

        val validExpiring = expiringProducts.filter { it.expirationDate > (now - TimeUnit.DAYS.toMillis(1)) }

        if (validExpiring.isNotEmpty()) {
            val count = validExpiring.size
            val text = "Attention! $count products are expiring soon."
            NotificationHelper.sendNotification(applicationContext, "Fridge Check", text)
        }
    }
}