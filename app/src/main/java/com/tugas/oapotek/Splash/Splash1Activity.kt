package com.tugas.oapotek.Splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import com.tugas.oapotek.R


class Splash1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash1)

        val background = object : Thread() {
            override fun run(){
                try{
                    Thread.sleep(2000)
                    val intent = Intent(baseContext, SplashActivity::class.java)
                    startActivity(intent)

                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()

    }
}
