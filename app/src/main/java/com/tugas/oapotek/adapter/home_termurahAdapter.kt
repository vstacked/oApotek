package com.tugas.oapotek.adapter

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
import kotlinx.android.synthetic.main.home_termurah.view.*
import kotlinx.android.synthetic.main.product_list.view.*
import kotlinx.android.synthetic.main.search_view.view.*

class TermurahAdapter(val product: ProductList) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.home_termurah
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView

        Picasso.get().load(product.photo).into(itemView.iv_home_termurah)
        itemView.tv_home_termurah_judul.text = product.judul
        itemView.tv_home_termurah_harga.text = product.harga

    }

}