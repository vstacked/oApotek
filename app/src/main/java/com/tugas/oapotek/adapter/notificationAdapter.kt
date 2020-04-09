package com.tugas.oapotek.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode
import com.squareup.picasso.Picasso
import com.tugas.oapotek.R
import com.tugas.oapotek.activity.CartActivity
import com.tugas.oapotek.model.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.product_list.view.*

class NotificationAdapter(val product: StatusBuy) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.list_item
    }

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView

        val status = product.status
        if(status == "Pending"){
            itemView.tv_li_status.setBackgroundColor(Color.parseColor("#FFFF0000"))
        }else{
            itemView.tv_li_status.setBackgroundColor(Color.parseColor("#008CFF"))
        }

        //Picasso.get().load(product.photo).into(itemView.iv_product_list)
        itemView.tv_li_title.text = product.judulArray
        itemView.tv_li_status.text = product.status
        itemView.tv_li_total.text = "Rp. " + product.total

//        val itemView1 = Notification2Adapter()
//
//
//                                val array = arrayOf(notificationList.judul)
//                        for(element in array){
//                            itemView.tv_li_title.text = itemView.tv_li_title.text.toString() + element + ","
//                        }
        //itemView.tv_li_title.text = notificationList.judul

//        val uid = FirebaseAuth.getInstance().uid.toString()
//        val fireDbPriority = FirebaseDatabase.getInstance().getReference("/notification/$uid/")
//        fireDbPriority.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled (p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//            override fun onDataChange(p0: DataSnapshot) {
//
//                p0.children.forEach {
//                    var judul = ""
//                    it.child("product").children.forEach { it2 ->
//                        val product1 = it2.getValue(NotificationList::class.java) as NotificationList
//                        //d.text = product1.judul
//                        judul += product1.judul
//                        val array = arrayOf(product1.judul)
//                        for(element in array){
//                            itemView.tv_li_title.text = itemView.tv_li_title.text.toString() + element + ","
//                        }
//                    }
//
//                }
//
//            }
//        })
    }

}