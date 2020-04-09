package com.tugas.oapotek.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugas.oapotek.MainActivity

import com.tugas.oapotek.R
import com.tugas.oapotek.activity.CartActivity
import com.tugas.oapotek.activity.LoginActivity
import com.tugas.oapotek.adapter.HobbiesAdapter
import com.tugas.oapotek.adapter.Notification2Adapter
import com.tugas.oapotek.adapter.NotificationAdapter
import com.tugas.oapotek.model.CartList
import com.tugas.oapotek.model.Hobby
import com.tugas.oapotek.model.NotificationList
import com.tugas.oapotek.model.StatusBuy
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.status_spinner.view.*
import kotlinx.android.synthetic.main.t_notif_nama.view.*
import java.util.*
import kotlin.Comparator

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment() {

//    private var valueIdBuy = ""
val adapterProduct = GroupAdapter<ViewHolder>()
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null
    private var dialogResume: AlertDialog? = null
    private var dialogViewResume: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
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

    override fun onViewCreated(view: View,savedInstanceState: Bundle? ) {
        super.onViewCreated(view, savedInstanceState)
        loadNotif()
//        (activity as AppCompatActivity).supportActionBar?.show()
//        (activity as AppCompatActivity).supportActionBar?.title = "Notification"

//        kategori = view.findViewById(R.id.sp_status) as Spinner
            //        result = view.findViewById(R.id.tv_status_result) as TextView
    }

    fun loadNotif(){
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recycle_view.layoutManager = layoutManager

        val builder = AlertDialog.Builder(view?.context)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()

        val uid = FirebaseAuth.getInstance().uid.toString()
        if(FirebaseAuth.getInstance().uid.toString() == LoginActivity.uidAdmin){
            val fireDbPriority = FirebaseDatabase.getInstance().getReference("/notification/")
//                .orderByChild("status/statusBuy/time")
            fireDbPriority.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled (p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                @SuppressLint("SetTextI18n")
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        it.children.forEach {it1->
                            val query = it1.ref.orderByChild("status/statusBuy/time")
                            query.addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    p0.child("status").children.forEach { it1 ->
                                        if(it1.exists()){
                                            val cart = it1.getValue(StatusBuy::class.java) as StatusBuy
                                            adapterProduct.add(Notification2Adapter(cart))
                                        }
                                    }
                                    dialog?.dismiss()
                                    adapterProduct.setOnItemLongClickListener { item, view ->
                                        val productDetail = item as Notification2Adapter
                                        //  val intent =
                                        //Intent(view.context, ProductDetailActivity::class.java)
                                        val idBuy = productDetail.product.idBuy
                                        val judul = productDetail.product.judulArray
                                        val status = productDetail.product.status
                                        val time = productDetail.product.time
                                        val total = productDetail.product.total
                                        val uid = productDetail.product.uid
//                                        intent.putExtra(idProductDetail, idProduct)
//                                        intent.putExtra(photoDetail, photo)
//                                        intent.putExtra(judulDetail, judul)
//                                        intent.putExtra(hargaDetail, harga)
                                        //startActivity(intent)
                                        val kategories = arrayOf( "Complete", "Pending")
                                        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.status_spinner, null)
                                        dialogView.sp_status.adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1, kategories)

                                        dialogView.sp_status.setSelection(kategories.indexOf(status))
                                        dialogView.sp_status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                                //result.text = "Please Select Option"
                                            }

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                dialogView.tv_status_result.text = kategories[position]

                                            }

                                        }



                                        val builder = AlertDialog.Builder(view.context)
                                            .setView(dialogView)
                                            .setTitle("Status Action")
                                        val alertDialog = builder.show()
                                        dialogView.btn_status_ok.setOnClickListener {
                                            alertDialog.dismiss()
                                            val newStatus =
                                                dialogView.tv_status_result.text.toString()
                                            val db2 = FirebaseDatabase.getInstance()
                                                .getReference("notification/$uid/$idBuy/status/statusBuy")
                                            val db =
                                                StatusBuy(idBuy, total, newStatus, judul, time, uid)
                                            db2.setValue(db)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        view.context,
                                                        "Success",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    MainActivity.launchIntent(view.context)
                                                }


                                        }
                                        true
                                    }
                                    adapterProduct.setOnItemClickListener { item, view ->
                                        val productDetail = item as Notification2Adapter
                                        val idBuy = productDetail.product.idBuy
                                        val judul = productDetail.product.judulArray
                                        val status = productDetail.product.status
                                        val time = productDetail.product.time
                                        val total = productDetail.product.total
                                        val uid = productDetail.product.uid
//                                        intent.putExtra(idProductDetail, idProduct)
//                                        intent.putExtra(photoDetail, photo)
//                                        intent.putExtra(judulDetail, judul)
//                                        intent.putExtra(hargaDetail, harga)
                                        //startActivity(intent)

                                        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.t_notif_nama, null)
                                        dialogView.nama_obat.text=judul

                                        val builder = AlertDialog.Builder(view.context)
                                            .setView(dialogView)
                                            .setTitle("Nama Obat")
                                        val alertDialog = builder.show()
                                    }
                                }

                            })
                        }


                    }

                    recycle_view.adapter = adapterProduct
                }
            })
        }else{
            val fireDbPriority = FirebaseDatabase.getInstance().getReference("/notification/$uid/").orderByChild("status/statusBuy/time")
            fireDbPriority.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled (p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                @SuppressLint("SetTextI18n")
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        it.child("status").children.forEach { it1 ->
                            if(it1.exists()){
                                val cart = it1.getValue(StatusBuy::class.java) as StatusBuy
                                adapterProduct.add(NotificationAdapter(cart))
                            }
                        }
                        dialog?.dismiss()

                    }
                    adapterProduct.setOnItemClickListener { item, view ->
                        val productDetail = item as NotificationAdapter
                        val idBuy = productDetail.product.idBuy
                        val judul = productDetail.product.judulArray
                        val status = productDetail.product.status
                        val time = productDetail.product.time
                        val total = productDetail.product.total
                        val uid = productDetail.product.uid
//                                        intent.putExtra(idProductDetail, idProduct)
//                                        intent.putExtra(photoDetail, photo)
//                                        intent.putExtra(judulDetail, judul)
//                                        intent.putExtra(hargaDetail, harga)
                        //startActivity(intent)

                        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.t_notif_nama, null)
                        dialogView.nama_obat.text=judul

                        val builder = AlertDialog.Builder(view.context)
                            .setView(dialogView)
                            .setTitle("Nama Obat")
                        val alertDialog = builder.show()

                    }
                    recycle_view.adapter = adapterProduct
                    dialog?.dismiss()
                }
            })
        }
    }

    companion object{
        fun launchIntent(context: Context){
            val intent = Intent(context, NotificationFragment::class.java)
            context.startActivity(intent)
        }
    }
}
