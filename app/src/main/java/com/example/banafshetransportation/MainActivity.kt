package com.example.banafshetransportation

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.banafshetransportation.databinding.ActivityMainBinding
import com.example.banafshetransportation.goodPerfs.GoodPrefs
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var job=50
    var name=""
    lateinit var path:String
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)
        GoodPrefs.init(applicationContext)
        if(GoodPrefs.getInstance().getInt("job",-1)>0){
            val intent= Intent(this,ManagerMainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnEnter.setOnClickListener{
            getCoworkers()
        }
    }

    lateinit var database: SQLiteDatabase

    fun getCoworkers() {
        val request: StringRequest = @SuppressLint("SuspiciousIndentation")
        object : StringRequest(
            Method.POST,
            url + "clientIn.php",
            Response.Listener { response ->
                try {
                    var json = JSONArray()
                    var jsonObject = JSONObject()
                    response.split("#").forEach {
                        jsonObject = JSONObject(it)
                        json.put(jsonObject)
                    }
                    for (i in 0 until json.length()) {
                        jsonObject = json[i] as JSONObject
                        if (json.length()>1) {
                            if(jsonObject.toString()!="{}"){
                                job=jsonObject.getInt("job")
                                name=jsonObject.getString("name")
                                GoodPrefs.getInstance().saveInt("job",job)
                                GoodPrefs.getInstance().saveString("name",name)
                                GoodPrefs.getInstance().saveString("phone",binding.etxtPhone.text.toString())
                                val intent= Intent(this,ManagerMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }else{
                            Toast.makeText(this,"شماره تلفن شما هنوز توسط مدیر تایید نشده است.",Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (Madareto: JSONException) {
                    Madareto.printStackTrace()

                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    this,
                    "خطا در اتصال به سرور!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            override fun getParams(): Map<String, String>? {
                val map = java.util.HashMap<String, String>()
                map.put("phone",binding.etxtPhone.text.toString())
                return map
            }
        }
        request.retryPolicy =
            DefaultRetryPolicy(
                18000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
        requestQueue.start()
    }
}