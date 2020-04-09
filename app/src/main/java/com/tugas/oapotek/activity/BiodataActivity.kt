package com.tugas.oapotek.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.model.User
import kotlinx.android.synthetic.main.activity_biodata.*
import java.io.ByteArrayOutputStream
import java.util.*

@Suppress("DEPRECATION")
class BiodataActivity : AppCompatActivity() {
    private val PICK_PHOTO = 100
    private var PHOTO_URI: Uri? = null
    private var dialog:AlertDialog?=null
    var dialogView:View?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biodata)
        supportActionBar?.hide()
        initViews()
    }
    @SuppressLint("InflateParams")
    private fun initViews() {
        iv_foto_biodata.setOnClickListener {
            getPhotoFromPhone()
        }

        btn_apply_biodata.setOnClickListener {
            it.closeKeyboard()
            if(!validateName() or !validateAddress()){
                return@setOnClickListener
            }else{
                val builder = AlertDialog.Builder(this)
                dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
                builder.setView(dialogView)
                builder.setCancelable(false)
                dialog = builder.create()
                dialog?.show()
                uploadFotoToFirebase()
            }
        }

        tv_lewati_biodata.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            dialog = builder.create()
            dialog?.show()
            uploadFotoToFirebase()
        }
    }

    private fun View.closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun validateAddress() : Boolean {
        val address = ti_alamat_biodata.editText?.text.toString()

        return when {
            address.isEmpty() -> {
                ti_alamat_biodata.error = "Alamat harus Diisi!"
                false
            }
            address.length > 300 -> {
                ti_alamat_biodata.error = "Alamat terlalu panjang!"
                false
            }
            else -> {
                ti_alamat_biodata.error = null
                true
            }
        }
    }

    private fun validateName() : Boolean {
        val name = ti_nama_biodata.editText?.text.toString()

        return when {
            name.isEmpty() -> {
                ti_nama_biodata.error = "Nama harus Diisi!"
                false
            }
            name.length > 30 -> {
                ti_nama_biodata.error = "Nama terlalu panjang!"
                false
            }
            else -> {
                ti_nama_biodata.error = null
                true
            }
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
                iv_foto_biodata.setImageBitmap(bitmap)
            }
        }
    }

    private fun uploadFotoToFirebase(){
        val photoName = UUID.randomUUID().toString()
        val uploadFirebase = FirebaseStorage.getInstance().getReference("tgak/images/$photoName")

//        iv_foto_biodata.isDrawingCacheEnabled =true
//        iv_foto_biodata.buildDrawingCache()
        val coba = (iv_foto_biodata.drawable as BitmapDrawable).bitmap
        val baos =ByteArrayOutputStream()
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
        val uid = FirebaseAuth.getInstance().uid
        val db = FirebaseDatabase.getInstance().getReference("user/$uid")
        val nama =ti_nama_biodata.editText?.text.toString()
        val alamat = ti_alamat_biodata.editText?.text.toString()
        if(nama != "" && alamat != ""){
            db.setValue(
                User(
                    photoURL,
                    ti_nama_biodata.editText?.text.toString(),
                    ti_alamat_biodata.editText?.text.toString(),
                    "0"
                ))
                .addOnSuccessListener {
                    MainActivity.launchIntent(this)
                    dialog?.dismiss()
                }
                .addOnFailureListener {

                }
        }else{
            db.setValue(
                User(
                    photoURL,
                    "Your Name",
                    "Your Address",
                    "0"
                ))
                .addOnSuccessListener {
                    MainActivity.launchIntent(this)
                    dialog?.dismiss()
                }
                .addOnFailureListener {

                }
        }


    }

    companion object {
        fun launchIntent(context: Context){
            val intent = Intent(context, BiodataActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
