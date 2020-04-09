@file:Suppress("DEPRECATION")

package com.tugas.oapotek.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tugas.oapotek.R
import com.tugas.oapotek.extensions.isConnectedToNetwork
import kotlinx.android.synthetic.main.activity_register.*

@Suppress("DEPRECATION", "UNUSED_ANONYMOUS_PARAMETER")
class RegisterActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    var context = this
    private var dialog:AlertDialog?=null
    private var dialogView: View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        initViews()
    }

    @SuppressLint("InflateParams")
    private fun initViews() {

        btn_register.setOnClickListener {
            it.closeKeyboard()
            if(!validateEmail() or !validatePassword() or !validateConfirmPassword() ){
                return@setOnClickListener
            }else {
                if(ti_password_register.editText?.text.toString() == ti_confirm_password_register.editText?.text.toString()){
                    val builder = AlertDialog.Builder(this)
                    dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
                    builder.setView(dialogView)
                    builder.setCancelable(false)
                    dialog = builder.create()

                    if(isConnectedToNetwork(this)){
                        RegisterUserToFirebase()
                    }else{
                        dialog?.show()
                        CheckConnectionOpenDialog()
                    }

                }else {
//                    et_confirm_password_register?.error = "Password Tidak Sama!"
                    ti_confirm_password_register.error = "Password Tidak Sama"

                }
            }
        }

        tv_login.setOnClickListener {
            LoginActivity.launchIntent(this)
        }

    }

    private fun View.closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun validateEmail(): Boolean{
        val email = ti_email_register.editText?.text.toString().trim()

        return if(email.isEmpty()){
            ti_email_register.error = "Email harus Diisi!"
            false
        }else{
            ti_email_register.error = null
            true
        }
    }

    private fun validatePassword(): Boolean{
        val password = ti_password_register.editText?.text.toString().trim()

        return if(password.isEmpty()){
            ti_password_register.error = "Password harus Diisi!"
            false
        }else{
            ti_password_register.error = null
            true
        }
    }

    private fun validateConfirmPassword(): Boolean{
        val password = ti_confirm_password_register.editText?.text.toString().trim()

        return if(password.isEmpty()){
            ti_confirm_password_register.error = "Confirm Password harus Diisi!"
            false
        }else{
            ti_confirm_password_register.error = null
            true
        }
    }

    @SuppressLint("InflateParams")
    private fun RegisterUserToFirebase() {
        val email = ti_email_register.editText?.text.toString().trim()
        val password = ti_password_register.editText?.text.toString().trim()

        //progressbar
        val builder = AlertDialog.Builder(this)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    dialog?.dismiss()
                    //val msg = it.result.toString()
                    //customToast(this, msg)
                    //Toast.makeText(this, it.result.toString(), Toast.LENGTH_SHORT).show()
                    BiodataActivity.launchIntent(this)
                }
            }
            .addOnFailureListener {
                dialog?.dismiss()
                val msg = it.message.toString()
                //customToastFail(this, msg)
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun CheckConnectionOpenDialog() {
        dialog?.dismiss()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pesan")
        builder.setMessage("Periksa koneksi Anda")
        builder.setPositiveButton("Retry"){dialog, which ->
            if(isConnectedToNetwork(this)){
                RegisterUserToFirebase()
            }else{
                CheckConnectionOpenDialog()
            }
        }
        builder.show()
    }

    companion object {
        fun launchIntent(context: Context){
            val intent = Intent(context, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
