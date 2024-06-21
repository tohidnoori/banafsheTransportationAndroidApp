package com.example.banafshetransportation.DataClasses

data class Resids(
    val custumerName:String,
    val date: String,
    val id: Int,
    val address: String,
    val phoneNumber: String,
    var residImage: String?=null,
    val price: Int,
    var listOfStufs: String,
    val randomGenerator: String,
    val tahvil:Int,
    val isNotCompleted:Int,
    val ResidID:Int
   // "custumerName","date", "id", "address","phoneNumber","price","listOfStufs","randomGenerator"
)
