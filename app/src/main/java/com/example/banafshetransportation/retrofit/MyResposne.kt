package com.example.banafshetransportation.retrofit

data class MyResponse<T>(
    val data: T? = null,
    val message: String,
    val status: Int

)


