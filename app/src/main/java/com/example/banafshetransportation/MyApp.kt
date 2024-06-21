package com.example.banafshetransportation

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.banafshetransportation.goodPerfs.GoodPrefs

const val NOTIFICATION_ID = "Banafsheh"
class MyApp :Application(){
    companion object{
//        @SuppressLint("RemoteViewLayout")
//        fun notifBuilder(Nid:Int,NotifTitle:String,NotifText:String,context:Context):Notification {
//            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            val intent2 = context.packageManager.getLaunchIntentForPackage(context.packageName)
//            val pendingIntent = PendingIntent.getActivity(
//                context, 12,
//                intent2,
//                PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val notification = NotificationCompat.Builder(context, NOTIFICATION_ID)
//                .setLargeIcon(
//                    BitmapFactory.decodeResource(
//                    context.resources,
//                    R.drawable.chair
//                )
//                )
//                .setSmallIcon(R.drawable.chair)
//                .setContentTitle(NotifTitle)
//                .setContentText(NotifText)
//                .setContentIntent(pendingIntent)
//                .build()
//            notificationManager.notify(Nid, notification)
//
//            return notification
//        }
    }
    override fun onCreate() {
        super.onCreate()
        GoodPrefs.init(applicationContext)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_ID,
                "Banafsheh",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}