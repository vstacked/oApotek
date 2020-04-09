package com.tugas.oapotek.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tugas.oapotek.R
import com.tugas.oapotek.model.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.product_list.view.*

class ProductAdapter(val product: ProductList) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.product_list
    }

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView

        Picasso.get().load(product.photo).into(itemView.iv_product_list)
        itemView.tv_product_list_judul.text = product.judul
        itemView.tv_product_list_harga.text = "Rp. "+product.harga

    }

}