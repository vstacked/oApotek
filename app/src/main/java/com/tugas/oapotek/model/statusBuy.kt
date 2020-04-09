package com.tugas.oapotek.model

class StatusBuy(
    val idBuy:String,
    val total:String,
    val status: String,
    val judulArray: String,
    val time: Long,
    val uid:String){

    constructor():this("","","", "", 0,"")


}