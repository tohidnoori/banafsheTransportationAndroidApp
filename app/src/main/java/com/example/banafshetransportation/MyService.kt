package com.example.banafshetransportation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MyService : Service() {
    lateinit var socket:Socket

        override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()
        socket=Socket(this)
        socket.mSocket.connect()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Toast.makeText(this,"EJRA SHOD!",Toast.LENGTH_LONG).show()
        startForeground(67,MyApp.notifBuilder(67,"گالری مبل بنفشه","",this))
        val job= intent?.getIntExtra("job",50)
        socket=Socket(this)
        socket.mSocket.connect()
        if(socket.mSocket.connected()){
            Toast.makeText(this,"EJRA SHOD!",Toast.LENGTH_LONG).show()
        }else{
            socket.mSocket.connect()
            if (job != 4) {
                socket.mSocket.on("factor", socket.handlerMSG)
            }
            socket.mSocket.on("resid", socket.handlerMSG)
            if (socket.mSocket.connected()){
            Toast.makeText(this,"EJRA SHOD!!",Toast.LENGTH_LONG).show()}
        }
        if (job != 4) {
            socket.mSocket.on("factor", socket.handlerMSG)
        }
        socket.mSocket.on("resid", socket.handlerMSG)
        return START_STICKY
    }

    override fun onDestroy() {
        //stopForeground(true)
        super.onDestroy()
    }


}