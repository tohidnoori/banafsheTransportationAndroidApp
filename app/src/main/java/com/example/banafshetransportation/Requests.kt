package com.example.banafshetransportation

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLDecoder

const val url = "http://bnfgallery.ir/banafsheh/"

class Requests(context:Context,APIString:String) {
    companion object{
        lateinit var progressDialog: SweetAlertDialog
        fun progress(context: Context?) {
            progressDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            progressDialog.titleText = "لطفا صبر کنید."
            progressDialog.show()
        }}


    val context=context
    val APIString=APIString

       fun setData(jsonObject:JSONObject,requirements:ArrayList<String>,
           ondatasent: onDataSent
       ) {
           progress(context)

           val request: StringRequest = object : StringRequest(
               Method.POST, url + APIString,
               Response.Listener { response ->
                   println("ressss:"+response)
                   progressDialog.dismiss()
                   try {
                       if (response == "true") {
                           ondatasent.onSent(true)
                       } else ondatasent.onSent(false)
                   } catch (e: JSONException) {
                       e.printStackTrace()
                       Log.e("onResponse", "OnErrorResponse:$response", e)
                   }
               }, Response.ErrorListener { error ->
                   progressDialog.dismiss()
                   Toast.makeText(context, "خطا در اتصال به سرور!", Toast.LENGTH_SHORT).show()
                   Log.e("Connection", "error:", error)
               }) {
               override fun getParams(): Map<String, String>? {
                   val map: MutableMap<String, String> = HashMap()
                   for(i in 0 until requirements.size){
                       map[requirements[i]] = jsonObject.getString(requirements[i])
                   }
                   return map
               }
           }
           request.retryPolicy =
               DefaultRetryPolicy(
                   18000,
                   DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                   DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
               )
           request.setShouldCache(false)
           Volley.newRequestQueue(context).add(request)
       }
       interface onDataSent {
           fun onSent(result: Boolean)
       }


}




