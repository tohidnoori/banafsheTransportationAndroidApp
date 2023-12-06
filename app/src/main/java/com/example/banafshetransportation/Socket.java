package com.example.banafshetransportation;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class Socket {
    JSONObject jsonObject;

    public Socket(Context context) {
        this.context = context;
    }

    Context context;


    io.socket.client.Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://mybackend.iran.liara.run");
        } catch (URISyntaxException e) {
        }
    }

    public Handler handler = new Handler();
    public Emitter.Listener handlerMSG = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    jsonObject = (JSONObject) args[0];
                    try {
                        System.out.println(jsonObject.toString());
                        if (jsonObject.has("model")) {
                            MyApp.Companion.notifBuilder(90,"یک فاکتور با شماره " + jsonObject.getString("id") + " ثبت شد.",
                                    jsonObject.getString("model") + " " + jsonObject.getString("numberOfSeats") + " نفره", context);
                        }
                        else {
                            MyApp.Companion.notifBuilder(85,"یک رسید با شماره " + jsonObject.getString("id") + " ثبت شد.",
                                    jsonObject.getString("custumerName") + " در تاریخ " + jsonObject.getString("date") , context);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
