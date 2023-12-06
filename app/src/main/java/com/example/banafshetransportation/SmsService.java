package com.example.banafshetransportation;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class SmsService {

    private static final String TAG = SmsService.class.getSimpleName();
    private static final String ENDPOINT_URL = "http://api.ghasedaksms.com/v2/sms/send/simple";
    private static final String API_KEY = "CUQKHo1CZLAhoVZbOANjUpzD3xzdp6N0UOqEg1Z8BWE";

    private final RequestQueue mQueue;
    Context context;

    public SmsService(Context context) {
        mQueue = Volley.newRequestQueue(context);
        this.context=context;
    }

    public void sendSms(String message, String receptor, String sender) {
        String requestBody = "message=" + urlEncode(message) +
                " &receptor=" + urlEncode(receptor) +
                " &sender=" + urlEncode(sender)+" ";

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Sms sent successfully");
                        Toast.makeText(context, "پیامک با موفقیت ارسال شد.", Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error in sending sms: " + error.getMessage());
                        Toast.makeText(context, "خطای ارسال پیامک", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("apikey", API_KEY);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        mQueue.add(request);
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error in encoding string: " + e.getMessage());
            return s;
        }
    }

}
