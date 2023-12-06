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


class addStuffsAdapter(context: Context?) :
    RecyclerView.Adapter<addStuffsAdapter.ViewHolder>() {
    val context = context
    val data=ArrayList<Stuffs>()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.listofstuffs,
            parent, false
        )


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {

        return data.size+1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.etxtStuffName)
        private val desc: TextView = itemView.findViewById(R.id.etxtDescriptionStuff)
        private val num: TextView = itemView.findViewById(R.id.etxtNumberStuff)
        private val add:ImageView=itemView.findViewById(R.id.imgSubmitStuff)

        @SuppressLint("SuspiciousIndentation")
        fun bindData(position: Int) {
            add.setOnClickListener{
                if(position>=data.size){
                    data.add(Stuffs(name.text.toString(),num.text.toString().toInt(),desc.text.toString()))
                    add.setImageResource(R.drawable.ic_baseline_check_24)
                    notifyItemInserted(position+1)
                }

            }

        }


    }
}


