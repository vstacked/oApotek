package com.tugas.oapotek.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugas.oapotek.R
import com.tugas.oapotek.model.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item2.view.*
import kotlinx.android.synthetic.main.product_list.view.*

class Notification2Adapter(val product: StatusBuy) : Item<ViewHolder>(){

    override fun getLayout(): Int {
        return R.layout.list_item2
    }

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView

        val status = product.status
        if(status == "Pending"){
            itemView.tv_item2_status.setBackgroundColor(Color.parseColor("#FFFF0000"))
        }else{
            itemView.tv_item2_status.setBackgroundColor(Color.parseColor("#008CFF"))
        }

        //Picasso.get().load(product.photo).into(itemView.iv_product_list)
        //itemView.tv_li2_title.text = notificationList.judul
        val uid = product.uid
        val fireDb = FirebaseDatabase.getInstance().getReference("user/$uid")

        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    val name = p0.child("name").value.toString()

                    itemView.tv_item2_from.text = name
                }
            }
        })


        itemView.tv_item2_title.text = product.judulArray
        itemView.tv_item2_total.text = "Rp. "+product.total
        itemView.tv_item2_status.text = product.status



    }

}