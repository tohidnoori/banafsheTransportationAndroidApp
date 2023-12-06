package com.example.banafshetransportation.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.banafshetransportation.DataClasses.CoWorkersIDs
import com.example.banafshetransportation.DataClasses.Factors
import com.example.banafshetransportation.ManagerMainActivity
import com.example.banafshetransportation.ManagerMainActivity.Companion.job
import com.example.banafshetransportation.R
import com.example.banafshetransportation.Requests
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.text.DecimalFormat


class FactorsAdapter(context: Context?, private val data: ArrayList<Factors>) :
    RecyclerView.Adapter<FactorsAdapter.ViewHolder>() {
    val context = context
var state=0
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.factors,
            parent, false
        )


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {

        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtfactornum: TextView = itemView.findViewById(R.id.txtfactornum)
        private val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        private val txtmodel: TextView = itemView.findViewById(R.id.txtmodel)
        private val txtNumberOfSeats: TextView = itemView.findViewById(R.id.txtNumberOfSeats)
        private val txtWoodColor: TextView = itemView.findViewById(R.id.txtWoodColor)
//        private val txtCloth1: TextView = itemView.findViewById(R.id.txtCloth1)
//        private val txtCloth2: TextView = itemView.findViewById(R.id.txtCloth2)
//        private val txtCloth3: TextView = itemView.findViewById(R.id.txtCloth3)
        private val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        private val chbDaryaftShod:CheckBox=itemView.findViewById(R.id.checkBoxDaryaftShod)
        private val chbDarHaleEjra:CheckBox=itemView.findViewById(R.id.checkBoxDarhaleejra)
        private val chbAmadeTahvil:CheckBox=itemView.findViewById(R.id.checkBoxAmadetahvil)
        fun bindData(position: Int) {
            txtfactornum.text = data[position].id.toString()
            txtDate.text = "تاریخ: "+data[position].date.toString()
            txtmodel.text = "مدل مبل: "+data[position].model
            txtNumberOfSeats.text = "تعداد نشیمن:"+data[position].numberOfSeats.toString().convertNeshimans()
            txtWoodColor.text = " آقای/خانم:"+data[position].woodColor
//            txtCloth1.text = "پارچه تک نفره: "+data[position].cloth1
//            txtCloth2.text = "پارچه دو نفره: "+data[position].cloth2
//            txtCloth3.text = "پارچه سه نفره: "+data[position].cloth3
            txtDescription.text = "توضیحات: "+data[position].description

            if(job!=3){
                chbDaryaftShod.isEnabled=false
                chbAmadeTahvil.isEnabled=false
                chbDarHaleEjra.isEnabled=false
            }

            val old_state=data[position].state.toInt()
            when(old_state){
                0->chbDaryaftShod.isChecked=true
                1->chbDarHaleEjra.isChecked=true
                2->chbAmadeTahvil.isChecked=true
            }

            chbDaryaftShod.setOnCheckedChangeListener{buttonView,isChecked->
                if(isChecked){
                    chbDarHaleEjra.isChecked=false
                    chbAmadeTahvil.isChecked=false
                    state=0
                    if(context is (ManagerMainActivity) ){
                        context.changeState(data[position].id.toString(),state)
                    }
                }
            }
            chbDarHaleEjra.setOnCheckedChangeListener{buttonView,isChecked->
                if(isChecked){
                    chbDaryaftShod.isChecked=false
                    chbAmadeTahvil.isChecked=false
                    state=1
                    if(context is (ManagerMainActivity) ){
                        context.changeState(data[position].id.toString(),state)
                    }
                }
            }
            chbAmadeTahvil.setOnCheckedChangeListener{buttonView,isChecked->
                if(isChecked){
                    chbDarHaleEjra.isChecked=false
                    chbDaryaftShod.isChecked=false
                    state=2
                    if(context is (ManagerMainActivity) ){
                        context.changeState(data[position].id.toString(),state)
                    }
                }
            }

        }
        fun String.convertNeshimans(): String {
            var input = this;
            var a = ""
            for (i in 0 until input.length) {
                a+=input[i]+"+"
            }
            a = a.removeRange(a.length-1,a.length)
            return a
        }

    }
}


