package com.tugas.oapotek.model

class ProductList(val idProduct:String,
                  val photo: String,
                   val judul: String,
                   val harga: String,
                   val kategori: String,
                  val deskripsi: String) {

    constructor():this("","","","","", "")

}