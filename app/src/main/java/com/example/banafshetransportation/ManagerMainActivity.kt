package com.example.banafshetransportation

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.URLDecoder
import java.util.Date


class ManagerMainActivity : AppCompatActivity() {
    lateinit var progressDialog: SweetAlertDialog
    var currentCaptureImagePath = "";
    lateinit var imageProcessor: MyImageProcessor
    var uploadImageIndex = 0;
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
    //val socket = Socket(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageProcessor = MyImageProcessor.instance
        binding = ActivityManagerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //socket.mSocket.connect()
        GoodPrefs.init(applicationContext)
        job = GoodPrefs.getInstance().getInt("job",50)
        binding.drawerName.text = GoodPrefs.getInstance().getString("name","")
//        binding.drawerName.text = "محمد علی رضایی"
        binding.drawer.openDrawer(binding.customDrawer)
        if (job != 4) {
            //socket.mSocket.on("factor", socket.handlerMSG)
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
        //socket.mSocket.on("resid", socket.handlerMSG)

//        val intent2 = Intent(this, MyService::class.java)
//        intent2.putExtra("job", job)
//        startService(intent2)

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
                            Log.i("salam", "login: ${body.data.toString()}")
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
        residAdapter = ResidsAdapter(this, ResidArrayList, listener = object : AdapterClick {
            override fun uploadPhoto(position: Int) {
                uploadImageIndex = position;
                permission()
            }
        })
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
    private fun uploadFile( path: String) {
        binding.imageLoading.visibility =  View.VISIBLE
        if (isNetworkAvailable(this@ManagerMainActivity)){
            lifecycleScope.launchWhenCreated {
                try{
                    val file = File(path)
                    var requestFile = RequestBody.create("image/*".toMediaType(), file)
                    val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    val response = RetrofitInstance.api.uploadFile(
                        "application/json",
                        filePart
                    )

                    withContext(Dispatchers.Main) {
                        binding.imageLoading.visibility =  View.INVISIBLE
                        if (response.isSuccessful) {
                            val body = response.body()!!
                            Log.i("kos", "login: ${body.message.toString()}")
                            if (body.status == 0) {
                                setSnackBar(binding.root,{},body.message)
                            } else {
                                setSnackBar(binding.root,{},"تصویر اپلود شد")
                                ResidArrayList[uploadImageIndex].residImage=body.data!!
                                residAdapter.notifyDataSetChanged()
                            }

                        } else {
                            setSnackBar(binding.root,{},"Profile picture could not be uploaded")
                            Log.i("kos", response.message())
                        }
                    }
                }catch (e:Exception){
                    binding.imageLoading.visibility =  View.INVISIBLE
                    setSnackBar(binding.root,{},"Profile picture could not be uploaded")
                    Log.i("kos", e.message.toString())
                }
            }
        }else{
            binding.imageLoading.visibility =  View.INVISIBLE
            setSnackBar(binding.root,{uploadFile(path)},"اینترنت متصل نیست")
        }
    }
    fun permission() {
        var permissions = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.CAMERA
            )
        }else{
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
        var granted = true
        for ( per in permissions){
            granted = ContextCompat.checkSelfPermission(
                applicationContext,
                per
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (granted) {
            var shouldShowRequestPermissionRationale= true;
            for ( per in permissions){
                shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this@ManagerMainActivity,
                    per
                )
            }
            if (!shouldShowRequestPermissionRationale) {
                ActivityCompat.requestPermissions(
                    this@ManagerMainActivity,
                    permissions,
                    101
                )
            }
        } else {
            Log.e("Else", "Else")
            showFileChooser(type="image/*")
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showFileChooser(type = "image/*")
            } else {
                Log.e("Permission", "Permission denied")
                // Handle the case where permission is denied
            }
        }
    }
    fun captureImageFile(): File {
        // Store image in dcim
        val file = File(
            Environment.getExternalStorageDirectory().toString() + "/DCIM/",
            "image" + Date().getTime() + ".png"
        )
        currentCaptureImagePath=file.absolutePath
        return file
    }
    private fun showFileChooser(type:String) {
        val intentGallery = Intent(Intent.ACTION_PICK)
        intentGallery.type = "image/*"

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureImageFile()))

        val galleryIntent = Intent.createChooser(intentGallery, "Choose Image")
        galleryIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intentCamera))

        startActivityForResult(galleryIntent, 1)
    }
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //binding.loadingAudio.visibility = View.VISIBLE;
        Log.i("kos","$resultCode $requestCode")
        if (resultCode == RESULT_OK ) {
            try {
                var which = "";
                val filePath=
                    if( data!=null&&data.data!=null){
                        which="gallery";
                        imageProcessor.getFilePathFromUri(applicationContext,data.data!!)
                    }else{
                        which="camera"
                        currentCaptureImagePath
                    }
                if (filePath!=null) {
                    if(File(filePath).length()/1024>1024){
                        //file is more than 1 mb
                        var a = imageProcessor.resizeImage(applicationContext, File(filePath));
                        Log.i("kos","file size : ${File(filePath).length()/1024}")
                        Log.i("kos","resized size : ${a.length()/1024}")
                        if(a.length()/1024>1024){
                            setSnackBar(binding.root,{},"This image is too big choose another.")
                            return;
                        }
                        uploadFile(a.absolutePath)
                    }else{
                        uploadFile(filePath)
                    }
                    if(which=="camera"){
                        setSnackBar(binding.root,{},"The file was received from Camera.")
                    }else{
                        setSnackBar(binding.root,{},"The file was received from gallery.")
                    }
                } else {
                    setSnackBar(binding.root,{},"Error loading file")
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun setSnackBar(coordinatorLayout: View?,retryFunc:()->Unit,message: String) {
        val snackbar = Snackbar
            .make(
                coordinatorLayout!!,
                message,
                Snackbar.LENGTH_INDEFINITE
            ).setDuration(2500)
        if(message=="اینترنت متصل نیست")
        {
            snackbar.setAction("دوباره") {
                snackbar.dismiss()
                retryFunc()
            }
            snackbar.setDuration(5000)
        }
        snackbar.setActionTextColor(Color.RED)
        val sbView = snackbar.view
        val textView =sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }


}