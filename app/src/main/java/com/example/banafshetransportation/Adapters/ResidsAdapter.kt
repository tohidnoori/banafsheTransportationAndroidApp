package com.example.banafshetransportation.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.banafshetransportation.*
import com.example.banafshetransportation.DataClasses.Resids
import com.example.banafshetransportation.DataClasses.Stuffs
import com.example.banafshetransportation.ManagerMainActivity.Companion.job
import com.example.banafshetransportation.databinding.ResidsBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.net.URLDecoder
import java.text.DecimalFormat


class ResidsAdapter(activity1: Activity?, private var data: ArrayList<Resids>,private val listener:AdapterClick) :
    RecyclerView.Adapter<ResidsAdapter.ViewHolder>() {
    var display=true
    val activity = activity1
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ResidsBinding.inflate(LayoutInflater.from(activity),parent,false);


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.imgSubmitCode.setOnClickListener{
            if(data[position].residImage!=null &&data[position].residImage!!.isNotEmpty()){
                if(holder.binding.etxtCode.text.toString()==data[position].randomGenerator){
                    val requests=Requests(activity!!,"updateTahvil.php")
                    val jsonObject=JSONObject()
                    jsonObject.put("tahvil",1)
                    jsonObject.put("id",data[position].id)
                    jsonObject.put("residImage",data[position].residImage)
                    requests.setData(jsonObject, arrayListOf("tahvil","id","residImage"),object : Requests.onDataSent {
                        override fun onSent(result: Boolean) {
                            holder.binding.etxtCode.setText("رسید کالا تایید شد.")
                            holder.binding.etxtCode.isEnabled=false
                            var animation = AnimationUtils.loadAnimation(activity, R.anim.fade_out)
                            holder.binding.imgSubmitCode.animation = animation
                            holder.binding.imgSubmitCode.visibility = View.GONE
                        }
                    })
                }else{
                    Toast.makeText(activity,"کد تایید صحیح نمی باشد.",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity,"عکس رسید را اپلود کنید",Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding.isNotCompleted.setOnCheckedChangeListener{buttonView,isChecked->
            if(isChecked){
                val ISNOTCOMPLETED=1
                if(activity is (ManagerMainActivity) ){
                    activity.changeIsNotCompleted(data[position].id.toString(),ISNOTCOMPLETED)
                }
            }else{
                val ISNOTCOMPLETED=0
                if(activity is (ManagerMainActivity) ){
                    activity.changeIsNotCompleted(data[position].id.toString(),ISNOTCOMPLETED)
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

            if(data[position].residImage!=null && data[position].residImage!!.isNotEmpty()){
                //load image
                binding.addImageIcon.visibility = View.GONE
                binding.residImageView.visibility = View.VISIBLE
                binding.imageLoading.visibility = View.VISIBLE
                Picasso.get()
                    .load(data[position].residImage)
                    .into(binding.residImageView, object : Callback {
                        override fun onSuccess() {
                            binding.imageLoading.setVisibility(View.GONE)
                        }

                        override fun onError(e: Exception) {
                            binding.imageLoading.setVisibility(View.GONE)
                        }
                    })
            }else{
                binding.addImageIcon.visibility = View.VISIBLE
                binding.residImageView.visibility = View.GONE
            }
            (binding.addImage).setOnClickListener {
                listener.uploadPhoto(position)
            }
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
            val requests=Requests(activity!!,"deleteResid.php")
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
                            )), jsonObject.getString("num"), URLDecoder.decode(
                                jsonObject.getString("desc").toString(),
                                "UTF-8"
                            )
                        )
                    )
                        println("arraylist: "+arrayList.toString())
                }}
                displayStuffsAdapter = DisplayStuffsAdapter(activity, arrayList)
                recycler.adapter = displayStuffsAdapter
                recycler.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
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


