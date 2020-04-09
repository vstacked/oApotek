package com.tugas.oapotek.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.activity.CartActivity
import com.tugas.oapotek.activity.ProductDetailActivity
import com.tugas.oapotek.model.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_cart.view.*
import kotlinx.android.synthetic.main.list_cart.view.*
import kotlinx.android.synthetic.main.t_delete_dialog.view.*

class CartAdapter(val cart: CartList) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.list_cart
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView

        Picasso.get().load(cart.photo).into(itemView.civ_cart)
        itemView.tv_cart_judul.text = cart.judul
        itemView.tv_cart_harga.text = "Rp. "+cart.harga
        itemView.tv_cart_count.text = cart.quantity

        val uid = FirebaseAuth.getInstance().uid
        val idProduct = cart.idProduct
        val db = FirebaseDatabase.getInstance().getReference("cart/$uid/$idProduct")
        itemView.btn_cart_plus.setOnClickListener {
            val a = itemView.tv_cart_count.text.toString()
            val b = a.toInt() + 1
            itemView.tv_cart_count.text = b.toString()
            val db1 = CartList(cart.idProduct, cart.photo, cart.judul, cart.harga, b.toString())
            db.setValue(db1)
                .addOnSuccessListener {
                    val intent = Intent(itemView.context, CartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val a = viewHolder.itemView.context as Activity
                    a.overridePendingTransition(0, 0)
                    itemView.context.startActivity(intent)
                    a.overridePendingTransition(0, 0)
//                    val q = (viewHolder.itemView.context as Activity) as MainActivity
//                    q.onBackPressed()
                }
        }
        itemView.btn_cart_minus.setOnClickListener {
            val a = itemView.tv_cart_count.text.toString()
            val b = a.toInt() - 1
            itemView.tv_cart_count.text = b.toString()
            if(a == "1"){
                val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.t_delete_dialog, null)
                val builder = AlertDialog.Builder(itemView.context)
                    .setView(dialogView)
                    .setTitle("Alert")
                val alertDialog = builder.show()
                dialogView.btn_delete_delete.setOnClickListener {
                    alertDialog.dismiss()
                    val db = FirebaseDatabase.getInstance().getReference("cart/$uid/$idProduct")
                    db.removeValue()

                    val intent = Intent(itemView.context, CartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val a = viewHolder.itemView.context as Activity
                    a.overridePendingTransition(0, 0)
                    itemView.context.startActivity(intent)
                    a.overridePendingTransition(0, 0)

                }
                dialogView.btn_delete_cancel.setOnClickListener {
                    alertDialog.dismiss()
                    val intent = Intent(itemView.context, CartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val a = viewHolder.itemView.context as Activity
                    a.overridePendingTransition(0, 0)
                    itemView.context.startActivity(intent)
                    a.overridePendingTransition(0, 0)
                }
            }else{
                val db1 = CartList(cart.idProduct, cart.photo, cart.judul, cart.harga, b.toString())
                db.setValue(db1)
                    .addOnSuccessListener {
                        val intent = Intent(itemView.context, CartActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        val a = viewHolder.itemView.context as Activity
                        a.overridePendingTransition(0, 0)
                        itemView.context.startActivity(intent)
                        a.overridePendingTransition(0, 0)
                    }
            }
        }

    }


    companion object {
        val totalprice = "totalprice"

    }

}