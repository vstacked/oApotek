package com.tugas.oapotek.model

class User(val photo:String,
           val name:String,
           val address: String,
           val saldo: String) {

    constructor():this("","","","")
}