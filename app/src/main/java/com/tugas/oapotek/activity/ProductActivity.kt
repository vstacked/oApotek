package com.tugas.oapotek.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.adapter.ProductAdapter
import com.tugas.oapotek.model.ProductList
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.t_admin_dialog.view.*
import kotlinx.android.synthetic.main.t_delete_dialog.view.*

class ProductActivity : AppCompatActivity() {
    val adapterProduct = GroupAdapter<ViewHolder>()
    lateinit var mSearchText: EditText
    lateinit var  mRecyclerView: RecyclerView
    val adapterSearch = GroupAdapter<ViewHolder>()
    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.launchIntent(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        supportActionBar?.hide()
        val value:String = intent.getStringExtra("key")
        mSearchText = findViewById(R.id.et_product_search)
        mRecyclerView = findViewById(R.id.rv_product_list)
        mSearchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                if (mSearchText.text.isEmpty() || mSearchText.text.toString() == "") {
//                    adapterSearch.clear()
//                    mRecyclerView.adapter = adapterSearch
////                    fetchUser()
//                }
            }
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (mSearchText.text.isEmpty() || mSearchText.text.toString() == "") {
//                    adapterSearch.clear()
//                    mRecyclerView.adapter = adapterSearch
////                    fetchUser()
//                }
                val searchText = s.toString()
//                adapterSearch.clear()
//                mRecyclerView.adapter = adapterProduct
//                fetchUser()
                loadResultSearch(searchText)
            }
        })
    fetchUser()
        val layoutManagerPopuler = GridLayoutManager(this, 2)
        layoutManagerPopuler.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = layoutManagerPopuler
    }
    private fun loadResultSearch(searchText: String) {
        if (searchText.isEmpty()) {
            adapterProduct.clear()
            adapterSearch.clear()
            mRecyclerView.adapter = adapterSearch
            fetchUser()
        } else {
            val value:String = intent.getStringExtra("key")
            val Database = FirebaseDatabase.getInstance().getReference("product")
            Database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val sort = it.ref.orderByChild("judul").startAt(searchText)
                            .endAt(searchText + "\uf8ff")
                        sort.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (mSearchText.text.isEmpty() || mSearchText.text.toString() == "") {
                                   // adapterSearch.clear()
//                                    fetchUser()
                                } else {


                                    p0.children.forEach { it1 ->
                                        val cart =
                                            it1.getValue(ProductList::class.java) as ProductList
                                        adapterSearch.clear()
                                        adapterProduct.clear()
                                        adapterSearch.add(ProductAdapter(cart))
                                    }
                                    adapterSearch.setOnItemClickListener { item, view ->
                                        val productDetail = item as ProductAdapter
                                        val intent =
                                            Intent(view.context, ProductDetailActivity::class.java)
                                        val idProduct = productDetail.product.idProduct
                                        val photo = productDetail.product.photo
                                        val deskripsi = productDetail.product.deskripsi
                                        val judul = productDetail.product.judul
                                        val harga = productDetail.product.harga
                                        intent.putExtra(idProductDetail, idProduct)
                                        intent.putExtra(deskripsiProduct, deskripsi)
                                        intent.putExtra(photoDetail, photo)
                                        intent.putExtra(judulDetail, judul)
                                        intent.putExtra(hargaDetail, harga)
                                        startActivity(intent)
                                    }
                                }
                                mRecyclerView.adapter = adapterSearch

                            }

                        })
                    }
                }

            })
        }

    }
    private fun fetchUser() {
                                                        //key dari HomeFragment
        val value:String = intent.getStringExtra("key")
            val fireDb = FirebaseDatabase.getInstance().getReference("/product/$value")


        fireDb.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled (p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val product = it.getValue(ProductList::class.java) as ProductList
                    adapterProduct.add(ProductAdapter(product))
                }
                adapterProduct.setOnItemClickListener { item, view ->
                    val productDetail = item as ProductAdapter
                    val intent = Intent(view.context, ProductDetailActivity::class.java)
                    val idProduct = productDetail.product.idProduct
                    val photo = productDetail.product.photo
                    val deskripsi = productDetail.product.deskripsi
                    val judul = productDetail.product.judul
                    val harga = productDetail.product.harga
                    intent.putExtra(idProductDetail, idProduct)
                    intent.putExtra(deskripsiProduct, deskripsi)
                    intent.putExtra(photoDetail, photo)
                    intent.putExtra(judulDetail, judul)
                    intent.putExtra(hargaDetail, harga)
                    startActivity(intent)
                }
                if(FirebaseAuth.getInstance().uid.toString() == LoginActivity.uidAdmin) {
                    adapterProduct.setOnItemLongClickListener { item, view ->
                        val productDetail = item as ProductAdapter
                        val idProduct = productDetail.product.idProduct

                        val dialogView =
                            LayoutInflater.from(view.context).inflate(R.layout.t_admin_dialog, null)
                        val builder = AlertDialog.Builder(view.context)
                            .setView(dialogView)
                            .setTitle("Action")
                        val alertDialog = builder.show()
                        dialogView.tv_admin_ubah.setOnClickListener {
                            alertDialog.dismiss()
                            val intent = Intent(view.context, UbahBarangActivity::class.java)
                            val idProduct = productDetail.product.idProduct
                            val kategori = value
                            intent.putExtra(idUbahProduct, idProduct)
                            intent.putExtra(kategoriProduct, kategori)
                            startActivity(intent)
                        }
                        dialogView.tv_admin_hapus.setOnClickListener {
                            alertDialog.dismiss()
                            val dialogViewDelete = LayoutInflater.from(view.context)
                                .inflate(R.layout.t_delete_dialog, null)
                            val builderDelete = AlertDialog.Builder(view.context)
                                .setView(dialogViewDelete)
                            val alertDialogDelete = builderDelete.show()
                            dialogViewDelete.btn_delete_delete.setOnClickListener {
                                alertDialogDelete.dismiss()
                                val db = FirebaseDatabase.getInstance()
                                    .getReference("product/$value/$idProduct")
                                db.removeValue()
                                    .addOnSuccessListener {
                                        MainActivity.launchIntent(view.context)
                                    }

                            }
                            dialogViewDelete.btn_delete_cancel.setOnClickListener {
                                alertDialogDelete.dismiss()
                            }
                        }

                        true
                    }
                }
                mRecyclerView.adapter = adapterProduct
            }

        })
    }
    companion object {
        val idProductDetail = "idProduct"
        val idUbahProduct = "idUbahProduct"
        val photoDetail = "photoDetail"
        val judulDetail = "judulDetail"
        val hargaDetail = "hargaDetail"
        val kategoriProduct = "kategoriProduct"
        val deskripsiProduct = "deskripsiProduct"
        fun launchIntent(context: Context){
            val intent = Intent(context, ProductActivity::class.java)
            context.startActivity(intent)
        }
    }
}
