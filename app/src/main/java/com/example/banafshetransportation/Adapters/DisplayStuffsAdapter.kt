package com.example.banafshetransportation.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.banafshetransportation.DataClasses.CoWorkersIDs
import com.example.banafshetransportation.DataClasses.Stuffs
import com.example.banafshetransportation.ManagerMainActivity
import com.example.banafshetransportation.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class DisplayStuffsAdapter(context: Context?,data:ArrayList<Stuffs>) :
    RecyclerView.Adapter<DisplayStuffsAdapter.ViewHolder>() {
    val context = context
    val data=data

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.stufss,
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
        private val name: TextView = itemView.findViewById(R.id.txtStufName)
        private val desc: TextView = itemView.findViewById(R.id.txtStuffDesc)
        private val num: TextView = itemView.findViewById(R.id.txtNumStuff)

        fun bindData(position: Int) {
            name.text="عنوان کالا: "+data[position].stuffName
            desc.text="توضیحات: "+data[position].stuffDesc
            num.text="تعداد: "+data[position].numStuff.toString()

        }


    }
}


