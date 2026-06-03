package com.aat.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra(NotificationUtils.EXTRA_TITLE) ?: "AAT"
        val body = intent?.getStringExtra(NotificationUtils.EXTRA_BODY) ?: "Tienes un recordatorio pendiente."
        val id = intent?.getIntExtra(NotificationUtils.EXTRA_ID, 1001) ?: 1001
        NotificationUtils.showNotification(context, title, body, id)
    }
}
