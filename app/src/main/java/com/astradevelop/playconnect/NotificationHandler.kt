package com.astradevelop.playconnect

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class NotificationHandler {
    fun scheduleNotification(context: Context, year: Int, month: Int, day: Int, hour: Int, minute: Int, title: String, body: String, match: String, type: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val triggerTime = calendar.timeInMillis

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }

        val code = System.currentTimeMillis().toInt()

        val repo = NotificationRepository(context)
        repo.insertNotification(code, title, body, match, type, triggerTime.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            code,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }

    }
    fun scheduleNotificationV2(context: Context, timestamp: Timestamp ,title: String, body: String, match: String) {
        val triggerTime = timestamp.toDate().time
        val twoHoursInMillis = 2 * 60 * 60 * 1000
        val adjustedTime = triggerTime.minus(twoHoursInMillis)

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }

        val code = System.currentTimeMillis().toInt()

        val repo = NotificationRepository(context)
        repo.insertNotification(code, title, body, match, "1", adjustedTime.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            code,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    adjustedTime,
                    pendingIntent
                )
            }
        } else {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                adjustedTime,
                pendingIntent
            )
        }

    }

    fun scheduleNotificationV3(context: Context, timestamp: Timestamp ,title: String, body: String, match: String) {
        val triggerTime = timestamp.toDate().time
        val hourInMillis = 60 * 60 * 1000
        val adjustedTime = triggerTime.plus(hourInMillis)

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }

        val code = System.currentTimeMillis().toInt()

        val repo = NotificationRepository(context)
        repo.insertNotification(code, title, body, match, "2", adjustedTime.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            code,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    adjustedTime,
                    pendingIntent
                )
            }
        } else {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                adjustedTime,
                pendingIntent
            )
        }

    }

    fun reScheduleNotification(context: Context, timestamp: Long, title: String, body: String, code: Int) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            code,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timestamp,
                    pendingIntent
                )
            }
        } else {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timestamp,
                pendingIntent
            )
        }

    }

    fun scheduleNotificationTournament(context: Context, timestamp: Timestamp ,title: String, body: String, match: String) {
        val triggerTime = timestamp.toDate().time
        val twoHoursInMillis = 2 * 60 * 60 * 1000
        val adjustedTime = triggerTime.minus(twoHoursInMillis)

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }

        val code = System.currentTimeMillis().toInt()

        val repo = NotificationRepository(context)
        repo.insertNotification(code, title, body, match, "1", adjustedTime.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            code,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    adjustedTime,
                    pendingIntent
                )
            }
        } else {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                adjustedTime,
                pendingIntent
            )
        }

    }

}