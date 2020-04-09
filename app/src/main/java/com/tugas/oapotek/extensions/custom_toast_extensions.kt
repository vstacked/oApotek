package com.tugas.oapotek.extensions

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.tugas.oapotek.R
import kotlinx.android.synthetic.main.custom_toast.*

fun Activity.customToast(context: Context, message: String){

    //  val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = layoutInflater.inflate(R.layout.custom_toast, ll_toast)


        val toastText = layout.findViewById<TextView>(R.id.tv_toast)
//        val toastIv = layout.findViewById<ImageView>(R.id.toast_iv)
//
        toastText.text = message.toString()
//        toastIv.setImageResource(R.drawable.ic_toasticon)

    val toast = Toast(applicationContext)

    val bg = ContextCompat.getDrawable(context, R.drawable.custom_toast_background)
    bg?.colorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(context, R.color.custom_toast),
        PorterDuff.Mode.MULTIPLY)
    layout.background = bg

    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.BOTTOM, 0, 20)
    toast.view = layout
    toast.show()

}

fun Activity.customToastFail(context: Context, message: String){

    //  val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = layoutInflater.inflate(R.layout.custom_toast, ll_toast)


    val toastText = layout.findViewById<TextView>(R.id.tv_toast)
//        val toastIv = layout.findViewById<ImageView>(R.id.toast_iv)
//
    toastText.text = message
//        toastIv.setImageResource(R.drawable.ic_toasticon)

    val toast = Toast(applicationContext)

    val bg = ContextCompat.getDrawable(context, R.drawable.custom_toast_background)
    bg?.colorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(context, R.color.custom_toast_fail),
        PorterDuff.Mode.MULTIPLY)
    layout.background = bg

    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.BOTTOM, 0, 20)
    toast.view = layout
    toast.show()

}