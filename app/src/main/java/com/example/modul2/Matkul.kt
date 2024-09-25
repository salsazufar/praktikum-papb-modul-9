//package com.example.modul2
//
//data class Matkul(
//    val hari : String,
//    val jam : String,
//    val matkul : String,
//    val praktikum : Boolean,
//    val ruang : String
//)

package com.example.modul2

import com.google.firebase.firestore.PropertyName

data class Matkul(
    @PropertyName("matkul") val matkul: String = "",
    @PropertyName("hari") val hari: String = "",
    @PropertyName("jam") val jam: String = "",
    @PropertyName("ruangan") val ruangan: String = "",
    @PropertyName("praktikum") val praktikum: Boolean = false
)