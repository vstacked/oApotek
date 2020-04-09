@file:Suppress("DEPRECATION")

package com.tugas.oapotek.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun isConnectedToNetwork(context: Context): Boolean{
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}