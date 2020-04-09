package com.tugas.oapotek.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tugas.oapotek.R
import com.tugas.oapotek.model.Hobby
import com.tugas.oapotek.model.Populer
import com.tugas.oapotek.model.Termahal
import kotlinx.android.synthetic.main.home_populer.view.*
import kotlinx.android.synthetic.main.home_termahal.view.*
import kotlinx.android.synthetic.main.list_item.view.*

class TermahalAdapter(val context: Context, val Termahal: List<Termahal>): RecyclerView.Adapter<TermahalAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_termahal, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return Termahal.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val termahal = Termahal[position]
        holder.setData(termahal, position)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var hobby:Termahal? = null

        fun setData(hobby: Termahal, pos: Int){
            itemView.iv_home_termahal.setImageResource(hobby.img)
            itemView.tv_home_termahal_judul.text = hobby.tittle
            itemView.tv_home_termahal_harga.text = hobby.harga

            this.hobby = hobby
        }

    }

}