package com.tugas.oapotek.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugas.oapotek.R
import com.tugas.oapotek.model.CartList
import kotlinx.android.synthetic.main.activity_product_detail.*
import java.util.*
import kotlin.collections.HashMap

class ProductDetailActivity : AppCompatActivity() {
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val photoDetail = intent.getStringExtra(ProductActivity.photoDetail).toString()
        val judulDetail = intent.getStringExtra(ProductActivity.judulDetail).toString()
        val hargaDetail = intent.getStringExtra(ProductActivity.hargaDetail).toString()
        val deskripsiDetail = intent.getStringExtra(ProductActivity.deskripsiProduct).toString()
        val photo = findViewById<ImageView>(R.id.iv_product_detail)
        val judul = findViewById<TextView>(R.id.tv_product_detail_judul)
        val harga = findViewById<TextView>(R.id.tv_product_detail_harga)
        val deskripsi = findViewById<TextView>(R.id.tv_product_detail_deskripsi)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.show()
        supportActionBar?.title = judulDetail

        Picasso.get().load(photoDetail).into(photo)
        judul.text = judulDetail
        harga.text = "Rp.$hargaDetail"
        deskripsi.text = deskripsiDetail
        checkUser()
        initViews()
    }
    private fun checkUser() {
        if(FirebaseAuth.getInstance().uid.toString() == LoginActivity.uidAdmin){
            btn_beli_detail.visibility = View.GONE
        }else{
            btn_beli_detail.visibility = View.VISIBLE
        }
    }
    private fun initViews() {
        btn_beli_detail.setOnClickListener {
            uploadDataCartToFirebase()
        }
    }
    private fun uploadDataCartToFirebase() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val photoDetail = intent.getStringExtra(ProductActivity.photoDetail).toString()
        val uriPhoto = photoDetail.toUri()
        val judulDetail = intent.getStringExtra(ProductActivity.judulDetail).toString()
        val hargaDetail = intent.getStringExtra(ProductActivity.hargaDetail).toString()
        val idProduct = intent.getStringExtra(ProductActivity.idProductDetail).toString()
        val db = FirebaseDatabase.getInstance().getReference("cart/$uid/$idProduct")

        val builder = AlertDialog.Builder(this)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()

        val fireDb1 = FirebaseDatabase.getInstance().getReference("cart/")
        fireDb1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val coba3 = p0.child("/$uid/$idProduct/idProduct").value.toString()

                val quan = p0.child("/$uid/$idProduct/quantity").value.toString()
                if (quan != "null") {
                    q = quan.toInt()
                } else {
                    q = 1
                }
                db.setValue(
                    CartList(
                        idProduct,
                        uriPhoto.toString(),
                        judulDetail,
                        hargaDetail,
                        if (coba3 == "null") {
                            q.toString()
                        } else {
                            q.inc().toString()
                        }

                    )
                )
                    .addOnSuccessListener {
                        dialog!!.dismiss()
                        CartActivity.launchIntent(this@ProductDetailActivity)
                    }
                    .addOnFailureListener {
                        dialog!!.dismiss()
                        Toast.makeText(
                            this@ProductDetailActivity,
                            it.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        })
    }
    companion object {
        var q: Int = 1
    }
}
