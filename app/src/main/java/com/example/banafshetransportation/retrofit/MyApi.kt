package com.example.banafshetransportation.retrofit

import com.example.banafshetransportation.DataClasses.Resids
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MyApi {
    @GET("getResids2.php")
    suspend fun getResids(
        @Query("content-type") contentType: String,
        @Query("phone") phone: String,
        @Query("job") job: Int,
    ): Response<MyResponse<ArrayList<Resids>>>

    @Multipart
    @POST("uploadFile.php")
    suspend fun uploadFile(
        @Query("content-type") contentType: String,
        @Part file: MultipartBody.Part
    ): Response<MyResponse<String>>

}