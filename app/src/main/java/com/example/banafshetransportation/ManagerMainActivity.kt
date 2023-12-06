package com.example.banafshetransportation

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.banafshetransportation.Adapters.CoworkerIDsAdapter
import com.example.banafshetransportation.Adapters.FactorsAdapter
import com.example.banafshetransportation.Adapters.ResidsAdapter
import com.example.banafshetransportation.Adapters.addStuffsAdapter
import com.example.banafshetransportation.DataClasses.CoWorkersIDs
import com.example.banafshetransportation.DataClasses.Factors
import com.example.banafshetransportation.DataClasses.Resids
import com.example.banafshetransportation.databinding.ActivityManagerMainBinding
import com.example.banafshetransportation.goodPerfs.GoodPrefs
import com.example.banafshetransportation.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLDecoder


class ManagerMainActivity : AppCompatActivity() {
    lateinit var progressDialog: SweetAlertDialog
    fun progress(context: Context?) {
        progressDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog.titleText = "لطفا صبر کنید."
        progressDialog.show()
    }

    companion object {
        var job=50
        val factorItems = arrayListOf<String>(
            "model",
            "numberOfSeats",
            "id",
            "date",
            "woodColor",
            "description",
            "state",
            "cloth1",
            "cloth2",
            "cloth3",
            "FactorID"
        )
        val residItems = arrayListOf<String>(
            "custumerName",
            "date",
            "id",
            "address",
            "phoneNumber",
            "price",
            "listOfStufs",
            "randomGenerator",
            "tahvil",
            "ResidID"
        )
    }
    lateinit var binding: ActivityManagerMainBinding
    lateinit var coworkerAdapter: CoworkerIDsAdapter
    lateinit var factorAdapter: FactorsAdapter
    lateinit var addStuffsAdapter: addStuffsAdapter
    lateinit var residAdapter: ResidsAdapter
    var CoworkersArrayList = ArrayList<CoWorkersIDs>()
    var FactorArrayList = ArrayList<Factors>()
    var ResidArrayList = ArrayList<Resids>()
    val socket = Socket(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        socket.mSocket.connect()
        GoodPrefs.init(applicationContext)
        job = GoodPrefs.getInstance().getInt("job",50)
        binding.drawerName.text = GoodPrefs.getInstance().getString("name","")
//        binding.drawerName.text = "محمد علی رضایی"
        binding.drawer.openDrawer(binding.customDrawer)
        if (job != 4) {
            socket.mSocket.on("factor", socket.handlerMSG)
        }
        binding.imgClose.setOnClickListener{
            finish()
        }

        binding.toggleDrawer.setOnClickListener {
            if (binding.drawer.isDrawerOpen(binding.customDrawer)){
                binding.drawer.closeDrawer(binding.customDrawer)
            }else{
                binding.drawer.openDrawer(binding.customDrawer)
            }
        }
        socket.mSocket.on("resid", socket.handlerMSG)

        val intent2 = Intent(this, MyService::class.java)
        intent2.putExtra("job", job)
        startService(intent2)

        if (job == 4) {
            binding.btnManageFactors.visibility = View.GONE
            binding.btnManageCoworkers.visibility = View.GONE
            binding.drawerImage.setImageDrawable(getDrawable(R.drawable.driver))
            getResids2()
        } else
        if (job == 3) {
            binding.btnManageResids.visibility = View.GONE
            binding.btnManageCoworkers.visibility = View.GONE
            binding.drawerImage.setImageDrawable(getDrawable(R.drawable.craftsman))
            getFactors()
        } else
        if(job==1){
            binding.drawerImage.setImageDrawable(getDrawable(R.drawable.ceo))
        }else{
            binding.drawerImage.setImageDrawable(getDrawable(R.drawable.sellman))
        }

        binding.btnManageCoworkers.setOnClickListener {
            if (job == 1) {
                binding.btnManageFactorsTxt.setTextColor(Color.parseColor("#B6B7B8"))
                binding.btnManageResidsTxt.setTextColor(Color.parseColor("#B6B7B8"))
                binding.btnManageCoworkersTxt.setTextColor(Color.parseColor("#ffffff"))
                binding.fabAddCoworker.visibility = View.VISIBLE
//                binding.fabAddFactor.visibility = View.GONE
//                binding.fabAddResid.visibility = View.GONE
                //نمایش دیتای موچود در جدول همکاران از دیتابیس
                getCoworkers()
                //اضافه کردن همکار
                dialogOpener(this)
            } else {
                Toast.makeText(this, "ورود به این بخش برای شما مجاز نیست.", Toast.LENGTH_SHORT)
                    .show()
            }
            binding.drawer.closeDrawer(Gravity.RIGHT)
        }
        binding.btnManageFactors.setOnClickListener {
            binding.btnManageFactorsTxt.setTextColor(Color.parseColor("#ffffff"))
            binding.btnManageResidsTxt.setTextColor(Color.parseColor("#B6B7B8"))
            binding.btnManageCoworkersTxt.setTextColor(Color.parseColor("#B6B7B8"))
            if (job != 4) {
//                if (job != 3) {
//                    binding.fabAddFactor.visibility = View.VISIBLE
//                } else {
//                    binding.fabAddFactor.visibility = View.GONE
//                }
                binding.fabAddCoworker.visibility = View.GONE
                //binding.fabAddResid.visibility = View.GONE

                //نمایش دیتای موچود در جدول فاکتورها از دیتابیس
                getFactors()
                //اضافه کردن فاکتور
                //dialogOpenerFactors(binding.fabAddFactor,"setFactors.php",false, arrayListOf(Factors("",0,0,"هیچ تاریخی انتخاب نشده!","","","","","","",0)),0)
            } else {
                Toast.makeText(this, "ورود به این بخش برای شما مجاز نیست.", Toast.LENGTH_SHORT)
                    .show()
            }
            binding.drawer.closeDrawer(Gravity.RIGHT)
        }
        binding.btnManageResids.setOnClickListener {
            binding.btnManageFactorsTxt.setTextColor(Color.parseColor("#B6B7B8"))
            binding.btnManageResidsTxt.setTextColor(Color.parseColor("#ffffff"))
            binding.btnManageCoworkersTxt.setTextColor(Color.parseColor("#B6B7B8"))
            if (job == 2 || job == 1) {
//                if (job == 2 || job == 1) {
//                    binding.fabAddResid.visibility = View.VISIBLE
//                } else {
//                    binding.fabAddResid.visibility = View.GONE
//                }
                binding.fabAddCoworker.visibility = View.GONE
                //binding.fabAddFactor.visibility = View.GONE


//                dialogOpenerResids(
//                    binding.fabAddResid,
//                    "setResids.php",
//                    false,
//                    arrayListOf(
//                        Resids(
//                            "",
//                            "هنوز هیچ تاریخی انتخاب نشده!",
//                            0,
//                            "",
//                            "0",
//                            0,
//                            "{}",
//                            "0",
//                            0,
//                            0, 0
//                        )
//                    ),
//                    0
//                )
            }
            getResids2()
            binding.drawer.closeDrawer(binding.customDrawer)
        }
    }

    fun dialogOpener(context: Context) {
        //افزودن همکار
        binding.fabAddCoworker.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialoge_add_coworker)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.getWindow()?.getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.getWindow()?.setAttributes(lp)
            val EtxtName = dialog.findViewById<EditText>(R.id.etxtNameCoworker)
            val EtxtPhone = dialog.findViewById<EditText>(R.id.etxtPhoneCoworker)
            val EtxtJob = dialog.findViewById<EditText>(R.id.etxtJobCoworker)
            val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmitCoworker)
            btnSubmit.setOnClickListener {
                val requirements = arrayListOf<String>("name", "phone", "job")
                val jsonObject = JSONObject()
                jsonObject.put(requirements[0], EtxtName.text.toString())
                jsonObject.put(requirements[1], EtxtPhone.text.toString())
                jsonObject.put(requirements[2], EtxtJob.text.toString())

                val requests = Requests(
                    context,
                    "setCoWorkers.php"
                )

                requests.setData(jsonObject,
                    requirements, object : Requests.onDataSent {
                        override fun onSent(result: Boolean) {
                            dialog.dismiss()
                            if (result) {
                                CoworkersArrayList.add(
                                    CoWorkersIDs(
                                        EtxtName.text.toString(),
                                        EtxtPhone.text.toString().toLong(), EtxtJob.text.toString()
                                    )
                                )

                                coworkerAdapter.notifyDataSetChanged()
                            }
                        }
                    })
            }

            dialog.show()
        }

    }

    fun getCoworkers() {
        CoworkersArrayList.clear()
        val request: StringRequest = object : StringRequest(
            Method.POST,
            url + "getCoWorkers.php",
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
                        if (jsonObject.toString() != "{}") {
                            CoworkersArrayList.add(
                                CoWorkersIDs(
                                    URLDecoder.decode(
                                        jsonObject.getString("name").toString(),
                                        "UTF-8"
                                    ), jsonObject.getLong("phone"),
                                    URLDecoder.decode(
                                        jsonObject.getString("job").toString(),
                                        "UTF-8"
                                    )
                                )
                            )
                        }
                    }
                    setRecyclerViewCoworkers()
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

    private fun setRecyclerViewCoworkers() {
        CoworkersArrayList.reverse()
        coworkerAdapter = CoworkerIDsAdapter(this, CoworkersArrayList)
        binding.recycler.adapter = coworkerAdapter
        binding.recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        binding.recycler.scrollToPosition(CoworkersArrayList.size - 1)

    }



    fun getFactors() {
        progress(this)
        FactorArrayList.clear()
        val request: StringRequest = object : StringRequest(
            Method.POST,
            url + "getFactors.php",
            Response.Listener { response ->
                progressDialog.dismiss()
                try {
                    var json = JSONArray()
                    var jsonObject = JSONObject()
                    response.split("#").forEach {
                        jsonObject = JSONObject(it)
                        json.put(jsonObject)
                    }
                    for (i in 0 until json.length()) {
                        jsonObject = json[i] as JSONObject
                        if (jsonObject.toString() != "{}") {
                            //"model",
                            //            "numberOfSeats",
                            //            "id",
                            //            "date",
                            //            "woodColor",
                            //            "description",
                            //            "state",
                            //            "cloth1",
                            //            "cloth2",
                            //            "cloth3"
                            FactorArrayList.add(
                                Factors(
                                    URLDecoder.decode(
                                        jsonObject.getString(factorItems[0]).toString(),
                                        "UTF-8"
                                    ),
                                    jsonObject.getInt(factorItems[1]).toInt(),
                                    jsonObject.getInt(factorItems[2]).toInt(),
                                    URLDecoder.decode(
                                        jsonObject.getString(factorItems[3]).toString(),
                                        "UTF-8"
                                    ), URLDecoder.decode(
                                        jsonObject.getString(factorItems[4]).toString(),
                                        "UTF-8"
                                    ), URLDecoder.decode(
                                        jsonObject.getString(factorItems[5]).toString(),
                                        "UTF-8"
                                    ), URLDecoder.decode(
                                        jsonObject.getString(factorItems[6]).toString(),
                                        "UTF-8"
                                    ), URLDecoder.decode(
                                        jsonObject.getString(factorItems[7]).toString(),
                                        "UTF-8"
                                    ), URLDecoder.decode(
                                        jsonObject.getString(factorItems[8]).toString(),
                                        "UTF-8"
                                    ), URLDecoder.decode(
                                        jsonObject.getString(factorItems[9]).toString(),
                                        "UTF-8"
                                    ),jsonObject.getInt(factorItems[10]).toInt()
                                )
                            )
                        }
                    }
                    setRecyclerViewFactors()
                } catch (Madareto: JSONException) {
                    Madareto.printStackTrace()
                }
            },
            Response.ErrorListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "خطا در اتصال به سرور!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            override fun getParams(): Map<String, String>? {
                val map = java.util.HashMap<String, String>()
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

    private fun setRecyclerViewFactors() {
        FactorArrayList.reverse()
        factorAdapter = FactorsAdapter(this, FactorArrayList)
        binding.recycler.adapter = factorAdapter
        binding.recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycler.scrollToPosition(0)
        factorAdapter.notifyDataSetChanged()
    }


    fun recyclerStuffs(recyclerView: RecyclerView, listOfStuffs: String) {
        addStuffsAdapter = addStuffsAdapter(this)
        recyclerView.adapter = addStuffsAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.scrollToPosition(0)
        addStuffsAdapter.notifyDataSetChanged()
    }
    fun isNetworkAvailable(activity: Activity): Boolean {
        try {
            val connectivity =
                activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity == null) {
                return false
            } else {
                val info = connectivity.allNetworkInfo
                if (info != null) {
                    for (i in info.indices) {
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun getResids2() {
        progress(this)
        ResidArrayList.clear()
        if (isNetworkAvailable(this)){
            lifecycleScope.launchWhenCreated {
                try{ val response = RetrofitInstance.api.getResids(
                    "application/json",
                    phone = GoodPrefs.getInstance().getString("phone",""),
                    job = GoodPrefs.getInstance().getInt("job",50)
                )
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            progressDialog.dismiss()
                            val body = response.body()!!
                            Log.i("salam", "login: ${body.message.toString()}")
                            if (body.status == 0) {
                                Toast.makeText(
                                    applicationContext,
                                    "خطا در اتصال به سرور!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                ResidArrayList = body.data!!
                                setRecyclerViewResids()
                            }
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "خطا در اتصال به سرور!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("salam", response.message())
                        }
                    }
                }catch (e:Exception){
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "خطا در اتصال به سرور!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("salam", e.message.toString())
                }
            }
        }else{
            progressDialog.dismiss()
            Toast.makeText(
                applicationContext,
                "اینترنت نمتصل نیست",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun setRecyclerViewResids() {
        if(ResidArrayList.isEmpty()){
            binding.emptyText.text = "رسیدی برای شما وجود ندارد"
        }else{
            binding.emptyText.text = ""
        }
        ResidArrayList.reverse()
        residAdapter = ResidsAdapter(this, ResidArrayList)
        binding.recycler.adapter = residAdapter
        binding.recycler.layoutManager =LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycler.scrollToPosition(0)
        residAdapter.notifyDataSetChanged()
    }


    fun changeState(id: String, state: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("state", state)
        val requests = Requests(this, "updateState.php")
        requests.setData(jsonObject, arrayListOf("id", "state"), object : Requests.onDataSent {
            override fun onSent(result: Boolean) {
            }
        })
    }

    fun changeIsNotCompleted(id: String, isNotCompleted: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("isNotCompleted", isNotCompleted)
        val requests = Requests(this, "changeIsNotCompleted.php")
        requests.setData(
            jsonObject,
            arrayListOf("id", "isNotCompleted"),
            object : Requests.onDataSent {
                override fun onSent(result: Boolean) {
                }
            })
    }


}