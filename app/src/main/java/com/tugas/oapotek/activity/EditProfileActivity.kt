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
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.model.User
import kotlinx.android.synthetic.main.account_edit_profile.view.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.ByteArrayOutputStream
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private val PICK_PHOTO = 100
    val PICK_CAMERA = 101
    private var PHOTO_URI: Uri? = null
    private var dialog:AlertDialog?=null
    var dialogView: View?=null

    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.launchIntent(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.show()
        supportActionBar?.title = "Edit Profile"

        initViews()
        loadProfile()
    }

    private fun initViews() {
        iv_edit_foto.setOnClickListener {
            getPhotoFromPhone()
        }
        btn_edit_apply.setOnClickListener {
            editProfile()
        }
        btn_edit_cancel.setOnClickListener {
            MainActivity.launchIntent(this)
        }
    }

    private fun loadProfile() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("user/$uid")

        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val photo = p0.child("photo").getValue().toString()
                val name = p0.child("name").value.toString()
                val address = p0.child("address").value.toString()

                Picasso.get().load(photo).into(iv_edit_foto)
                ti_edit_nama.setText(name)
                ti_edit_alamat.setText(address)
            }
        })
    }

    private fun editProfile() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("user/$uid")

        val builder = AlertDialog.Builder(this)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()

        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val saldo = p0.child("saldo").value.toString()
                val newName = ti_edit_nama.text.toString()
                val newAddress = ti_edit_alamat.text.toString()

                val photoName = UUID.randomUUID().toString()
                val uploadFirebase =
                    FirebaseStorage.getInstance().getReference("tgak/images/$photoName")
                val coba = (iv_edit_foto.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                coba.compress(Bitmap.CompressFormat.JPEG, 10, baos)
                val data = baos.toByteArray()
                uploadFirebase.putBytes(data)
                    .addOnSuccessListener {
                        uploadFirebase.downloadUrl.addOnSuccessListener {
                            val newPhoto = it.toString()

                            val dbUser = User(newPhoto, newName, newAddress, saldo)
                            fireDb.setValue(dbUser)
                                .addOnSuccessListener {
                                    dialog?.dismiss()
                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        "Data berhasil diubah",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent =
                                        Intent(this@EditProfileActivity, MainActivity::class.java)
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

        if (requestCode == PICK_PHOTO) {
            if (resultCode == Activity.RESULT_OK && data!!.data != null) {
                PHOTO_URI = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, PHOTO_URI)
                iv_edit_foto.setImageBitmap(bitmap)
            }
        }
    }

    companion object {
        fun launchIntent(context: Context) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
        }
    }
}
