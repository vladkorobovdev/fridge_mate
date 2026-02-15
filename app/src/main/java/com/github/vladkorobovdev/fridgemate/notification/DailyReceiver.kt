package com.github.vladkorobovdev.fridgemate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DailyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, ExpirationService::class.java)
        context.startService(serviceIntent)
    }
}