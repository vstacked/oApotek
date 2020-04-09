// by Muhammad Rizki Nurlahid
// https://github.com/muhrizkin
// https://www.instagram.com/rizkinurlahid/
package com.tugas.oapotek

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugas.oapotek.activity.CartActivity
import com.tugas.oapotek.activity.LoginActivity
import com.tugas.oapotek.adapter.AccountAdapter
import com.tugas.oapotek.fragments.AccountFragment
import com.tugas.oapotek.fragments.HomeFragment
import com.tugas.oapotek.fragments.NotificationFragment
import com.tugas.oapotek.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_account.*

class MainActivity : AppCompatActivity() {

    lateinit var HomeFragment:HomeFragment
    lateinit var NotificationFragment:NotificationFragment
    lateinit var AccountFragment:AccountFragment
    private var backPressedTime:Long = 0
    private lateinit var backToast:Toast

    override fun onBackPressed() {
        if(backPressedTime+2000 > System.currentTimeMillis()){
            backToast.cancel()
            super.onBackPressed()
            return
        }else{
            backToast = Toast.makeText(this, "Press Again to Exit", Toast.LENGTH_SHORT)
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        //checkUserAccountSignIn()

        val bottomNavigation:BottomNavigationView = findViewById(R.id.nav_bot)

        HomeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, HomeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()


//        if(intent.getStringExtra(CartActivity.NotifFragKey) == ""){
//
//        }else{
//            NotificationFragment = NotificationFragment()
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.frame_layout, NotificationFragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
//        }

        bottomNavigation.setOnNavigationItemSelectedListener {item->
            when(item.itemId){
                R.id.item_home -> {
                    HomeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, HomeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.item_notification -> {
                        NotificationFragment = NotificationFragment()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, NotificationFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                }
                R.id.item_account -> {
                    AccountFragment = AccountFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, AccountFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }
    }

    private fun checkUserAccountSignIn() {
        if(FirebaseAuth.getInstance().uid.isNullOrEmpty()){
            LoginActivity.launchIntent(this)
        }
    }

    private fun initViews() {
    }

    companion object {
        fun launchIntent(context: Context){
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
