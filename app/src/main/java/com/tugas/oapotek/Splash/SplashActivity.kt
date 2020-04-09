package com.tugas.oapotek.Splash

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.activity.BiodataActivity
import com.tugas.oapotek.activity.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        val background = object : Thread() {
            override fun run(){
                try{
                    Thread.sleep(2000)
                    val intent = Intent(baseContext, LoginActivity::class.java)
                    startActivity(intent)

                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
    private fun setAnimation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

        }else{

        }
    }
}
