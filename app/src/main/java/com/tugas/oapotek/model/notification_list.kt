package com.tugas.oapotek.model

class NotificationList(
    val idProduct:String,
    val photo: String,
    val judul: String,
    val harga: String,
    val quantity: String){

    constructor():this("","","","","")

}