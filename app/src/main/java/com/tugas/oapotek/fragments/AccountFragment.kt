package com.tugas.oapotek.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.tugas.oapotek.MainActivity

import com.tugas.oapotek.R
import com.tugas.oapotek.activity.EditProfileActivity
import com.tugas.oapotek.activity.LoginActivity
import com.tugas.oapotek.adapter.AccountAdapter
import com.tugas.oapotek.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.account_edit_profile.view.*
import kotlinx.android.synthetic.main.activity_biodata.*
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {
    val adapterAccount = GroupAdapter<ViewHolder>()
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null
    private var dialogResume: AlertDialog? = null
    private var dialogViewResume: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

//    override fun onStart() {
//        super.onStart()
//        val builder = AlertDialog.Builder(view?.context)
//        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
//        builder.setView(dialogView)
//        builder.setCancelable(false)
//        dialog = builder.create()
//        dialog?.show()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        dialog?.dismiss()
//        val builder = AlertDialog.Builder(view?.context)
//        dialogViewResume = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
//        builder.setView(dialogViewResume)
//        builder.setCancelable(false)
//        dialogResume = builder.create()
//        dialogResume?.show()
//        val background = object : Thread() {
//            override fun run(){
//                try{
//                    sleep(2000)
//                    dialogResume?.dismiss()
//                } catch (e: Exception){
//                    e.printStackTrace()
//                }
//            }
//        }
//        background.start()
//
//    }
    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        (activity as AppCompatActivity).supportActionBar?.show()
//        (activity as AppCompatActivity).supportActionBar?.title = "Account"
        initViews()
        Profile()
    }

    private fun initViews() {
        btn_signout.setOnClickListener {
            SignOut()
        }
        tv_account_editprofile.setOnClickListener {
            context?.let { it1 -> EditProfileActivity.launchIntent(it1) }
        }
    }
    fun Profile() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("user/$uid")

        val builder = AlertDialog.Builder(view?.context)
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
                if(p0.exists()){
                val address = p0.child("address").getValue().toString()
                val name = p0.child("name").getValue().toString()
                val photo = p0.child("photo").getValue().toString()

                Picasso.get().load(photo).into(civ_account)
                tv_nama_account.text = name
                tv_alamat_account.text = address
                }
                dialog?.dismiss()
            }
        })
    }
    private fun SignOut() {
            FirebaseAuth.getInstance().signOut()
            ///LoginActivity.launchIntentClearTask(this)
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity?.startActivity(intent)
        }
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        super.onCreateOptionsMenu(menu)
//        val searchItem = menu.findItem(R.id.sv_home)
//        val searchView = searchItem.actionView as SearchView
//        searchView.queryHint = "Search View Hint"
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                //tastk here
//                return false
//            }
//        })
//        return true
//    }
}