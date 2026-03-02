package com.example.hw1_mobile_computing

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


fun sendShakeNotification(context: Context) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as ComponentActivity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            101
        )
        return
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val resultIntent = Intent(context, MainActivity::class.java)
    val resultPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(resultIntent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.android_logo)
        .setContentTitle("Shake Detected!")
        .setContentText("BOOM! You shook the phone.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(resultPendingIntent)
        .setAutoCancel(true)

    notificationManager.notify(NOTIFICATION_ID, builder.build())
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(CHANNEL_ID, "Shake Channel", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

class ShakeDetect(
    private val context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var isFirstReading = true
    private var lastShakeTime: Long = 0

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        if (isFirstReading) {
            lastX = x; lastY = y; lastZ = z
            isFirstReading = false
            return
        }

        val deltaX = Math.abs(lastX - x)
        val deltaY = Math.abs(lastY - y)
        val deltaZ = Math.abs(lastZ - z)

        val currentTime = System.currentTimeMillis()

        if ((deltaX > 0.8f || deltaY > 0.8f || deltaZ > 0.8f) && (currentTime - lastShakeTime > 1000)) {
            lastShakeTime = currentTime
            onShake()
        }

        lastX = x; lastY = y; lastZ = z
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}