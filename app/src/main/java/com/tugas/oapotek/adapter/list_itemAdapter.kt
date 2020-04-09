package com.tugas.oapotek.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugas.oapotek.R
import com.tugas.oapotek.model.Hobby
import kotlinx.android.synthetic.main.list_item.view.*

class HobbiesAdapter(val context: Context, val hobbies: List<Hobby>): RecyclerView.Adapter<HobbiesAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return hobbies.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val hobby = hobbies[position]
        holder.setData(hobby, position)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var hobby:Hobby? = null

        fun setData(hobby: Hobby?, pos: Int){
            //itemView.tv_li_title.text = hobby!!.tittle

            this.hobby = hobby
        }

//        init{
//            itemView.iv_li_delete.setOnClickListener {
//                val msg: String = "Hobby is: " + hobby!!.tittle
//
//                val intent = Intent()
//                intent.action = Intent.ACTION_SEND
//                intent.putExtra(Intent.EXTRA_TEXT, msg)
//                intent.type = "text/plain"
//                context.startActivity(Intent.createChooser(intent, "Please Select App :"))
//            }
//        }
    }

}