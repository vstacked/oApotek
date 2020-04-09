package com.tugas.oapotek.activity

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.model.ProductList
import kotlinx.android.synthetic.main.activity_tambah_barang.*
import java.io.ByteArrayOutputStream
import java.util.*

class TambahBarangActivity : AppCompatActivity() {
    private val PICK_PHOTO = 100
    private var PHOTO_URI: Uri? = null
    var category:String?=null
    lateinit var kategori: Spinner
    lateinit var  result: TextView
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null
    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.launchIntent(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_barang)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.show()
        supportActionBar?.title = "Tambah Produk"

        kategori = findViewById(R.id.sp_kategori) as Spinner
        result = findViewById(R.id.tv_spinner) as TextView

        val kategories = arrayOf("Obat", "Vitamin", "Herbal", "Alat Kesehatan", "Suplemen", "Nutrisi")

        kategori.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, kategories)
        kategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                result.text = "Please Select Option"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                result.text = kategories[position]
                category = result.text as String
            }

        }
        initViews()
    }
    private fun initViews() {
        iv_contoh.setOnClickListener {
            getPhotoFromPhone()
        }
        btn_contoh.setOnClickListener {
            UploadFotoToFirebase()
        }
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
                iv_contoh.setImageBitmap(bitmap)
            }
        }
    }
    private fun UploadFotoToFirebase(){
        val photoName = UUID.randomUUID().toString()
        val uploadFirebase = FirebaseStorage.getInstance().getReference("tgak/product/$photoName")

//        iv_foto_biodata.isDrawingCacheEnabled =true
//        iv_foto_biodata.buildDrawingCache()
        val coba = (iv_contoh.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        coba.compress(Bitmap.CompressFormat.JPEG,10,baos)
        val data = baos.toByteArray()

        uploadFirebase.putBytes(data)
            .addOnSuccessListener {
                uploadFirebase.downloadUrl.addOnSuccessListener{
                    saveAllUserDataToDatabase(it.toString())

                }
            }
    }
    private fun saveAllUserDataToDatabase(photoURL: String) {
        val kategori = this.category
        val idProduct = UUID.randomUUID().toString()
        val db = FirebaseDatabase.getInstance().getReference("product/$kategori/$idProduct")

        val builder = AlertDialog.Builder(this)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()

        db.setValue(
            kategori?.let {
                ProductList(
                    idProduct,
                    photoURL,
                    et_contoh_nama.text.toString(),
                    et_contoh_harga.text.toString(),
                    it,
                    et_contoh_deskripsi.text.toString()
                )
            }
        )
            .addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                dialog?.dismiss()
                val intent =
                    Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            .addOnFailureListener {

            }
    }
    companion object {
        fun launchIntent(context: Context){
            val intent = Intent(context, TambahBarangActivity::class.java)
            context.startActivity(intent)
        }
    }
}
