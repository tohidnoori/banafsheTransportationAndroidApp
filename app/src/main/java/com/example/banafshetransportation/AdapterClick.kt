package com.example.banafshetransportation

import com.example.banafshetransportation.DataClasses.Resids
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AdapterClick {
    fun uploadPhoto(position:Int)
}