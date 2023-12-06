package com.example.banafshetransportation.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.banafshetransportation.DataClasses.CoWorkersIDs
import com.example.banafshetransportation.ManagerMainActivity
import com.example.banafshetransportation.R
import com.example.banafshetransportation.Requests
import com.example.banafshetransportation.url
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject


class CoworkerIDsAdapter(context: Context?, private val data: ArrayList<CoWorkersIDs>) :
    RecyclerView.Adapter<CoworkerIDsAdapter.ViewHolder>() {
    val context = context

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.coworker_ids,
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
        private val name: TextView = itemView.findViewById(R.id.txtName)
        private val job: TextView = itemView.findViewById(R.id.txtJob)
        private val phone: TextView = itemView.findViewById(R.id.txtPhone)

        fun bindData(position: Int) {
            name.text = data[position].name
            phone.text = data[position].phone.toString()
            when (data[position].job) {
                "1" -> job.text = "مدیر"
                "2" -> job.text = "فروشنده"
                "3" -> job.text = "سرپرست کارگاه"
                "4" -> job.text = "راننده حمل"
            }

        }
    }
}


