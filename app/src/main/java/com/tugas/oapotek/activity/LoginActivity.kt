@file:Suppress("DEPRECATION")

package com.tugas.oapotek.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.adapter.AccountAdapter
import com.tugas.oapotek.extensions.isConnectedToNetwork
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION", "UNUSED_ANONYMOUS_PARAMETER")
class LoginActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    var context: LoginActivity = this
    private var dialog:AlertDialog?=null
    private var dialogView: View?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        checkUserAccountSignIn()
        initViews()
    }
    private fun checkUserAccountSignIn() {
        if(FirebaseAuth.getInstance().uid.isNullOrEmpty()){

        }else{
            MainActivity.launchIntent(this)
        }
    }
    private fun CheckConnectionOpenDialog() {
        dialog?.dismiss()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pesan")
        builder.setMessage("Periksa koneksi Anda")
        builder.setPositiveButton("Retry"){dialog, which ->
            if(isConnectedToNetwork(this)){
                LoginToFirebase()
            }else{
                CheckConnectionOpenDialog()
            }
        }
        builder.show()
    }
    @SuppressLint("InflateParams")
    private fun initViews() {

        tv_register.setOnClickListener{
            RegisterActivity.launchIntent(this)
        }

        btn_login.setOnClickListener {
            it.closeKeyboard()
        if(!validateEmail() or !validatePassword() ){
            return@setOnClickListener
        }else {
            val builder = AlertDialog.Builder(this)
            dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            dialog = builder.create()
                if(isConnectedToNetwork(this)){
                    LoginToFirebase()
                }else{
                    dialog?.show()
                    CheckConnectionOpenDialog()
                }
            }
        }
    }
    private fun View.closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    private fun validateEmail(): Boolean{
        val email = ti_email_login.editText?.text.toString().trim()

        return if(email.isEmpty()){
            ti_email_login.error = "Email harus Diisi!"
            false
        }else{
            ti_email_login.error = null
            true
        }
    }
    private fun validatePassword(): Boolean{
        val password = ti_password_login.editText?.text.toString().trim()

        return if(password.isEmpty()){
            ti_password_login.error = "Password harus Diisi!"
            false
        }else{
            ti_password_login.error = null
            true
        }
    }
    @SuppressLint("InflateParams")
    private fun LoginToFirebase() {
        val email = ti_email_login.editText?.text.toString().trim()
        val password = ti_password_login.editText?.text.toString().trim()

        //progressbar
        val builder = AlertDialog.Builder(this)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    dialog?.dismiss()
                    //val msg = it.result.toString()
                    //Toast.makeText(this, it.result.toString(), Toast.LENGTH_SHORT).show()
                    MainActivity.launchIntent(this)
                }
            }
            .addOnFailureListener {
                dialog?.dismiss()
                val msg = it.message.toString()
               Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
    }
    companion object {
        val uidAdmin = "ZrI7uhMjO5e0q6Etxh58BwBLXhV2"
        fun launchIntent(context: Context){
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
        fun launchIntentClearTask(context: Context){
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
