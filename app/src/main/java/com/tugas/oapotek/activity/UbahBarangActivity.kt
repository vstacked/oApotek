package com.tugas.oapotek.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.model.ProductList
import kotlinx.android.synthetic.main.activity_ubah_barang.*
import java.io.ByteArrayOutputStream
import java.lang.reflect.Array
import java.util.*

class UbahBarangActivity : AppCompatActivity() {

    private val PICK_PHOTO = 100
    private var PHOTO_URI: Uri? = null
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null

    lateinit var  result: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_barang)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.show()
        supportActionBar?.title = "Ubah Barang"

        result = findViewById(R.id.tv_ubah_spinner)
        initViews()
        loadBarang()
    }
    private fun loadBarang() {
        val idProduct = intent.getStringExtra(ProductActivity.idUbahProduct).toString()
        val kategorii = intent.getStringExtra(ProductActivity.kategoriProduct).toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("product/$kategorii/$idProduct")
        fireDb.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val photo = p0.child("photo").value.toString()
                val judul = p0.child("judul").value.toString()
                val deskripsi = p0.child("deskripsi").value.toString()
                val harga = p0.child("harga").value.toString()
                val kategoriDb = p0.child("kategori").value.toString()

                Picasso.get().load(photo).into(iv_ubah_gambar)
                et_ubah_nama.setText(judul)
                et_ubah_harga.setText(harga)
                et_ubah_dekripsi.setText(deskripsi)
//                kategori.setSelection(kategories.indexOf(kategoriDb))
                result.text = kategoriDb
            }

        })
    }
    private fun initViews() {
        iv_ubah_gambar.setOnClickListener {
            getPhotoFromPhone()
        }
        btn_ubah.setOnClickListener {
            ubahBarang()
        }
    }
    private fun ubahBarang() {
        val idProduct = intent.getStringExtra(ProductActivity.idUbahProduct).toString()
        val kategorii = intent.getStringExtra(ProductActivity.kategoriProduct).toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("product/$kategorii/$idProduct")

        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val newJudul = et_ubah_nama.text.toString()
                val newHarga = et_ubah_harga.text.toString()
                val kategori = result.text.toString()
                val newDeskripsi = et_ubah_dekripsi.text.toString()

                val photoName = UUID.randomUUID().toString()
                val uploadFirebase =
                    FirebaseStorage.getInstance().getReference("tgak/images/$photoName")
                val coba = (iv_ubah_gambar.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                coba.compress(Bitmap.CompressFormat.JPEG, 10, baos)
                val data = baos.toByteArray()
                uploadFirebase.putBytes(data)
                    .addOnSuccessListener {
                        uploadFirebase.downloadUrl.addOnSuccessListener {
                            val newPhoto = it.toString()

                            val builder = AlertDialog.Builder(this@UbahBarangActivity)
                            dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
                            builder.setView(dialogView)
                            builder.setCancelable(false)
                            dialog = builder.create()
                            dialog?.show()

                            val dbBarang = ProductList(idProduct, newPhoto, newJudul, newHarga, kategori, newDeskripsi)
                            fireDb.setValue(dbBarang)
                                .addOnSuccessListener {
                                    dialog?.dismiss()
                                    Toast.makeText(
                                        this@UbahBarangActivity,
                                        "Data berhasil diubah",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent =
                                        Intent(this@UbahBarangActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    overridePendingTransition(0, 0)
                                    startActivity(intent)
                                    overridePendingTransition(0, 0)
                                }
                        }
                    }
            }
        })
    }
    private fun getPhotoFromPhone() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_PHOTO){
            if(resultCode == Activity.RESULT_OK  && data!!.data != null){
                PHOTO_URI = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, PHOTO_URI)
                iv_ubah_gambar.setImageBitmap(bitmap)
            }
        }
    }
//    private fun UploadFotoToFirebase(){
//        val photoName = UUID.randomUUID().toString()
//        val uploadFirebase = FirebaseStorage.getInstance().getReference("tgak/product/$photoName")
//
////        iv_foto_biodata.isDrawingCacheEnabled =true
////        iv_foto_biodata.buildDrawingCache()
//        val coba = (iv_ubah_gambar.drawable as BitmapDrawable).bitmap
//        val baos = ByteArrayOutputStream()
//        coba.compress(Bitmap.CompressFormat.JPEG,10,baos)
//        val data = baos.toByteArray()
//
//        uploadFirebase.putBytes(data)
//            .addOnSuccessListener {
//                uploadFirebase.downloadUrl.addOnSuccessListener{
//                    saveAllUserDataToDatabase(it.toString())
//
//                }
//            }
//    }
//
//    private fun saveAllUserDataToDatabase(photoURL: String) {
//        val kategori = this.category
//        val idProduct = UUID.randomUUID().toString()
//        val db = FirebaseDatabase.getInstance().getReference("product/$kategori/$idProduct")
//
//        db.setValue(
//            kategori?.let {
//                ProductList(
//                    idProduct,
//                    photoURL,
//                    et_ubah_nama.text.toString(),
//                    et_ubah_harga.text.toString(),
//                    it
//                )
//            }
//        )
//            .addOnSuccessListener {
//                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
//            }
//            .addOnFailureListener {
//
//            }
//    }
    companion object{
        fun launchIntent(context: Context){
            val intent = Intent(context, UbahBarangActivity::class.java)
            context.startActivity(intent)
        }
    }
}
