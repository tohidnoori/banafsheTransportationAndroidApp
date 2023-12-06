package com.example.banafshetransportation.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.banafshetransportation.*
import com.example.banafshetransportation.DataClasses.CoWorkersIDs
import com.example.banafshetransportation.DataClasses.Factors
import com.example.banafshetransportation.DataClasses.Resids
import com.example.banafshetransportation.DataClasses.Stuffs
import com.example.banafshetransportation.ManagerMainActivity.Companion.factorItems
import com.example.banafshetransportation.ManagerMainActivity.Companion.job
import com.example.banafshetransportation.databinding.ResidsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLDecoder
import java.text.DecimalFormat
import java.util.Locale


class ResidsAdapter(Context: Context?, private val data: ArrayList<Resids>) :
    RecyclerView.Adapter<ResidsAdapter.ViewHolder>() {
    var display=true
    val context = Context
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ResidsBinding.inflate(LayoutInflater.from(context),parent,false);


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.imgSubmitCode.setOnClickListener{
            if(holder.binding.etxtCode.text.toString()==data[position].randomGenerator){
                val requests=Requests(context!!,"updateTahvil.php")
                val jsonObject=JSONObject()
                jsonObject.put("tahvil",1)
                jsonObject.put("id",data[position].id)
                requests.setData(jsonObject, arrayListOf("tahvil","id"),object : Requests.onDataSent {
                    override fun onSent(result: Boolean) {
                        holder.binding.etxtCode.setText("رسید کالا تایید شد.")
                        holder.binding.etxtCode.isEnabled=false
                        var animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                        holder.binding.imgSubmitCode.animation = animation
                        holder.binding.imgSubmitCode.visibility = View.GONE
                    }
                })
            }else{
                Toast.makeText(context,"کد تایید صحیح نمی باشد.",Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding.isNotCompleted.setOnCheckedChangeListener{buttonView,isChecked->
            if(isChecked){
                val ISNOTCOMPLETED=1
                if(context is (ManagerMainActivity) ){
                    context.changeIsNotCompleted(data[position].id.toString(),ISNOTCOMPLETED)
                }
            }else{
                val ISNOTCOMPLETED=0
                if(context is (ManagerMainActivity) ){
                    context.changeIsNotCompleted(data[position].id.toString(),ISNOTCOMPLETED)
                }
            }
        }
        holder.binding.etxtCode.setText("")
        holder.binding.etxtCode.isEnabled = true
        holder.binding.imgSubmitCode.visibility=View.VISIBLE
        if(data[position].tahvil==1){
            holder.binding.etxtCode.setText("رسید کالا تایید شد");
            holder.binding.etxtCode.isEnabled=false
             holder.binding.imgSubmitCode.visibility=View.GONE
        }
        holder.bindData(holder.adapterPosition)
    }

    override fun getItemCount(): Int {

        return data.size
    }

    inner class ViewHolder(val binding: ResidsBinding) : RecyclerView.ViewHolder(binding.root) {
        val stuffss=ArrayList<String>()
        lateinit var displayStuffsAdapter: DisplayStuffsAdapter


        @SuppressLint("SuspiciousIndentation")
        fun bindData(position: Int) {
            binding.txtResidnum.text=data[position].id.toString()
            binding.txtDateResid.text="تاریخ : "+data[position].date
            binding.txtCustumerName.text=data[position].custumerName
            binding.txtPrice.text=data[position].price.addAutomaticThousandSeparator()
            binding.txtCustumerPhone.text=data[position].phoneNumber
            binding.txtAddress.text=data[position].address

//            binding.txtResidnum.setOnClickListener {
//                Toast.makeText(context,data[position].tahvil.toString(),Toast.LENGTH_SHORT).show()
//                Toast.makeText(context,(data[position].tahvil).toString(),Toast.LENGTH_SHORT).show()
//            }

            if(data[position].isNotCompleted!=0){
                binding.isNotCompleted.isChecked=true
            }
            if(job!=4){
                binding.isNotCompleted.isEnabled=false
            }
            val requests=Requests(context!!,"deleteResid.php")
            val jsonObject=JSONObject()
            jsonObject.put("table","resid")
            jsonObject.put("ResidID",data[position].ResidID)
            val reqierments= arrayListOf<String>("table","ResidID")
            getStuff(position)

//            if(job!=4){
//                binding.imgSubmitCode.visibility=View.GONE
//                if(data[position].tahvil==1){
//                    binding.etxtCode.setText("رسید کالا تایید شد.")
//                    binding.etxtCode.isEnabled=false
//                }else{
//                    binding.etxtCode.isEnabled=false
//                    binding.etxtCode.setText("کالا هنوز تحویل داده نشده است.")
//                }
//            }
        }

        @SuppressLint("SuspiciousIndentation")
        fun getStuff(position: Int)
            {
                stuffss.clear()
                stuffss.add(
                    data[position].listOfStufs
                )
                setRecyclerDisplayStuffs(binding.recyclerShowResid)
            }


        fun setRecyclerDisplayStuffs(recycler: RecyclerView) {
            var jsonObject = JSONObject()
            val arrayList = ArrayList<Stuffs>()
            stuffss.forEach {
                val array = it.split("#")
                println(it)
                for (i in 0..array.size -1) {
                    var text = array[i].replace("@@@", "}")
                    text = text.replace("***", "{")
                    text = text.replace("&", ",")
                    text = text.replace("=", ":")
                    jsonObject = JSONObject(text)
                    println(text)
                    if(jsonObject.toString()!="{}"){
                    arrayList.add(
                        Stuffs(
                            (URLDecoder.decode(
                                jsonObject.getString("name").toString(),
                                "UTF-8"
                            )), jsonObject.getInt("num"), URLDecoder.decode(
                                jsonObject.getString("desc").toString(),
                                "UTF-8"
                            )
                        )
                    )
                        println("arraylist: "+arrayList.toString())
                }}
                displayStuffsAdapter = DisplayStuffsAdapter(context, arrayList)
                recycler.adapter = displayStuffsAdapter
                recycler.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recycler.scrollToPosition(0)
            }
        }
        fun Int.addAutomaticThousandSeparator(): String {
            val format = DecimalFormat("#,###")
            format.isDecimalSeparatorAlwaysShown = false
            return format.format(this).toString().replace(",","/") +" تومان "
        }

    }
}


