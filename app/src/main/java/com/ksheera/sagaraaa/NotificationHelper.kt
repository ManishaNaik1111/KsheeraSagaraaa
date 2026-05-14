package com.ksheera.sagaraaa

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences(
            "milk_prefs", Context.MODE_PRIVATE
        )
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastEntry = prefs.getString("last_entry_date", "") ?: ""

        // Calculate days missed
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val daysMissed = if (lastEntry.isEmpty()) 1 else {
            try {
                val last = sdf.parse(lastEntry)!!
                val now = Date()
                val diff = now.time - last.time
                (diff / (1000 * 60 * 60 * 24)).toInt()
            } catch (e: Exception) { 1 }
        }

        if (daysMissed >= 1) {
            showMissedDayNotification(applicationContext, daysMissed)
        }
        return Result.success()
    }

    private fun showMissedDayNotification(context: Context, daysMissed: Int) {
        val channelId = "ksheera_daily_reminder"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Milk Entry Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you to add daily milk slip data"
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_income", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when {
            daysMissed == 1 -> "🐄 Don't forget today's milk entry!"
            daysMissed == 2 -> "⚠️ You missed yesterday's milk entry!"
            else -> "⚠️ You missed $daysMissed days of milk entries!"
        }
        val message = when {
            daysMissed == 1 -> "Tap to add today's milk slip data to KsheeraSagara."
            daysMissed == 2 -> "You can still add yesterday's entry. Tap to open the app."
            else -> "You can fill past entries. Tap to open Income Log."
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(1001, notification)
    }
}

object NotificationHelper {
    fun scheduleDailyReminder(context: Context) {
        // Schedule at 8 PM every day
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20) // 8 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(currentTime)) add(Calendar.DAY_OF_MONTH, 1)
        }
        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        val dailyWork = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_milk_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWork
        )
    }

    // Call this whenever a milk entry is saved
    fun updateLastEntryDate(context: Context) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        context.getSharedPreferences("milk_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("last_entry_date", today)
            .apply()
    }
}