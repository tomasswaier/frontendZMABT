package com.example.frontendzmabt.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.frontendzmabt.MainActivity
import com.example.frontendzmabt.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.jvm.java


import android.app.NotificationChannel
import android.os.Build

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "default", // MUST match backend
            "Default Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "General notifications"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        println("🔥 Firebase service created meooooow")
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("MESSAGE RECEIVED: ${remoteMessage.data}")
        remoteMessage.data["placeId"]?.let { placeId ->
            showNotification(placeId.toInt())
        }
    }

    override fun onNewToken(token: String) {
        // send this token to your backend
        println("FCM TOKEN: $token")
    }

    private fun showNotification(placeId: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("placeId", placeId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        println("Eiiii at least sum:"+placeId)

        val notification = NotificationCompat.Builder(this, "default")
            .setContentTitle("New Place")
            .setContentText("Tap to open")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}