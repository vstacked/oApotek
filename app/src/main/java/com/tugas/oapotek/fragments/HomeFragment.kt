package com.tugas.oapotek.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nex3z.notificationbadge.NotificationBadge
import com.tugas.oapotek.activity.TambahBarangActivity
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.activity.CartActivity
import com.tugas.oapotek.activity.LoginActivity
import com.tugas.oapotek.activity.ProductActivity
import com.tugas.oapotek.activity.ProductActivity.Companion.deskripsiProduct
import com.tugas.oapotek.activity.ProductActivity.Companion.hargaDetail
import com.tugas.oapotek.activity.ProductActivity.Companion.idProductDetail
import com.tugas.oapotek.activity.ProductActivity.Companion.judulDetail
import com.tugas.oapotek.activity.ProductActivity.Companion.photoDetail
import com.tugas.oapotek.activity.ProductDetailActivity
import com.tugas.oapotek.adapter.*
import com.tugas.oapotek.extensions.isConnectedToNetwork
import com.tugas.oapotek.model.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.topup_dialog.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    private lateinit var viewflipper: ViewFlipper
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null
    private var dialogResume: AlertDialog? = null
    private var dialogViewResume: View? = null
    lateinit var mSearchText: EditText
    lateinit var mRecyclerView: RecyclerView
    val adapterSearch = GroupAdapter<ViewHolder>()
    val adapterHome = GroupAdapter<ViewHolder>()
    val adapterHome2 = GroupAdapter<ViewHolder>()
    lateinit var badge: NotificationBadge
    private val count: Int = 0
    var image =
        intArrayOf(R.drawable.home_banner1) //    override fun onStart() {
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val builder = AlertDialog.Builder(view.context)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()
        if (isConnectedToNetwork(view.context)) {
            checkUser()
            fetchQuantity()
            badge = view.findViewById(R.id.nb_home) as NotificationBadge
            mSearchText = view.findViewById(R.id.et_home_search)
            mRecyclerView = view.findViewById(R.id.rv_home_searchresult)
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            if (mSearchText.text.isEmpty() || mSearchText.text.toString() == "") {
                adapterSearch.clear()
                mRecyclerView.adapter = adapterSearch
            }
            mSearchText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (mSearchText.text.isEmpty() || mSearchText.text.toString() == "") {
                        adapterSearch.clear()
                        mRecyclerView.adapter = adapterSearch
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (mSearchText.text.isEmpty() || mSearchText.text.toString() == "") {
                        adapterSearch.clear()
                        mRecyclerView.adapter = adapterSearch
                    }
                    val searchText = s.toString()
                    adapterSearch.clear()
                    mRecyclerView.adapter = adapterSearch
                    loadResultSearch(searchText)
                }

            })

            initViews()
            (activity as AppCompatActivity).supportActionBar?.hide()

            val uid = FirebaseAuth.getInstance().uid.toString()
            val fireDb = FirebaseDatabase.getInstance().getReference("user/$uid")

            fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        dialog?.dismiss()
                        val saldo = p0.child("saldo").value.toString()
                        tv_home_saldo.text = "Rp. $saldo"
                    }
                }
            })




//            for (i: Int in 0 until 1) {
//                flipImage(image[i])
//            }
        } else {
            dialog?.show()
            CheckConnectionOpenDialog()
        }
    }
    private fun checkUser() {
        if (FirebaseAuth.getInstance().uid.toString() != LoginActivity.uidAdmin) {
            btn_tambah_barang.visibility = View.GONE
            rl_home_cart.visibility = View.VISIBLE
            ll_home_saldo.visibility = View.VISIBLE
        } else {
            btn_tambah_barang.visibility = View.VISIBLE
            rl_home_cart.visibility = View.GONE
            ll_home_saldo.visibility = View.GONE
        }
    }
    private fun fetchQuantity() {

        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("/cart/$uid/")


        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var quantity = 0
                p0.children.forEach {
                    val Quantity = it.getValue(CartList::class.java)?.quantity
                    val value2 = Quantity?.toInt()
                    quantity += value2!!
                }
//                dialog?.dismiss()


//                refreshPage()
                if (quantity != 0) {
                    badge.setNumber(quantity)
                } else {
                    badge.setNumber(0)
                }

            }
        })
    }
    private fun loadResultSearch(searchText: String) {
        if (searchText.isEmpty()) {
            adapterSearch.clear()
            mRecyclerView.adapter = adapterSearch
        } else {

            val Database = FirebaseDatabase.getInstance().getReference("product/")
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
                                    adapterSearch.clear()
                                } else {


                                    p0.children.forEach { it1 ->
                                        val cart =
                                            it1.getValue(ProductList::class.java) as ProductList
                                        adapterSearch.clear()
                                        adapterSearch.add(SearchAdapter(cart))
                                    }
                                    adapterSearch.setOnItemClickListener { item, view ->
                                        val productDetail = item as SearchAdapter
                                        val intent =
                                            Intent(view.context, ProductDetailActivity::class.java)
                                        val idProduct = productDetail.product.idProduct
                                        val photo = productDetail.product.photo
                                        val judul = productDetail.product.judul
                                        val deskripsi = productDetail.product.deskripsi
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
    class ProductViewHolders(var view: View) : RecyclerView.ViewHolder(view)
    private fun CheckConnectionOpenDialog() {
        dialog?.dismiss()
        val builder = AlertDialog.Builder(view!!.context)
        builder.setTitle("Pesan")
        builder.setMessage("Periksa koneksi Anda")
        builder.setPositiveButton("Retry") { dialog, which ->
            if (isConnectedToNetwork(view!!.context)) {
                initViews()
                HomeFragment()
            } else {
                CheckConnectionOpenDialog()
            }
        }
        builder.show()
    }
    private fun initViews() {
        //dialog?.dismiss()
        iv_home_cart.setOnClickListener {
            CartActivity.launchIntent(view!!.context)
        }
        btn_home_topup.setOnClickListener {
            uploadSaldoToUser()
        }
        cv_satu.setOnClickListener {
//            Toast.makeText(context, "CV SATU", Toast.LENGTH_SHORT).show()
            //context?.let { it1 -> ProductActivity.launchIntent(it1) }
            val intent = Intent(context, ProductActivity::class.java)
            val kategori = "Obat"
            intent.putExtra("key", kategori)
            context?.startActivity(intent)
        }
        cv_dua.setOnClickListener {
//            Toast.makeText(context, "CV DUA", Toast.LENGTH_SHORT).show()
            //context?.let { it1 -> ProductActivity.launchIntent(it1) }
            val intent = Intent(context, ProductActivity::class.java)
            val kategori = "Vitamin"
            intent.putExtra("key", kategori)
            context?.startActivity(intent)
        }
        cv_tiga.setOnClickListener {
//            Toast.makeText(context, "CV TIGA", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ProductActivity::class.java)
            val kategori = "Herbal"
            intent.putExtra("key", kategori)
            context?.startActivity(intent)
        }
        cv_empat.setOnClickListener {
//            Toast.makeText(context, "CV EMPAT", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ProductActivity::class.java)
            val kategori = "Alat Kesehatan"
            intent.putExtra("key", kategori)
            context?.startActivity(intent)
        }
        cv_lima.setOnClickListener {
//            Toast.makeText(context, "CV LIMA", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ProductActivity::class.java)
            val kategori = "Suplemen"
            intent.putExtra("key", kategori)
            context?.startActivity(intent)
        }
        cv_enam.setOnClickListener {
//            Toast.makeText(context, "CV ENAM", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ProductActivity::class.java)
            val kategori = "Nutrisi"
            intent.putExtra("key", kategori)
            context?.startActivity(intent)
        }
        btn_tambah_barang.setOnClickListener {
            context?.let { it1 -> TambahBarangActivity.launchIntent(it1) }
        }
    }
    private fun uploadSaldoToUser() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("user/$uid")

//        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                if(p0.exists()){
//                    val saldo = p0.child("saldo").value.toString()
//                    tv_home_saldo.text = "Rp. $saldo"
//                }
//            }
//        })

        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                val photo = p0.child("photo").getValue().toString()
                val name = p0.child("name").getValue().toString()
                val address = p0.child("address").getValue().toString()
                val saldo = p0.child("saldo").value.toString()

                val dialogView =
                    LayoutInflater.from(view?.context).inflate(R.layout.topup_dialog, null)
                val builder = AlertDialog.Builder(view?.context)
                    .setView(dialogView)
                    .setTitle("Top Up")
                val alertDialog = builder.show()
                dialogView.et_home_nominal.setText(saldo)

                dialogView.btn_home_ok.setOnClickListener {
                    alertDialog.dismiss()
                    val nominal = dialogView.et_home_nominal.text.toString()
                    tv_home_saldo.text = "Rp. $nominal"
                    val dbUser = User(photo, name, address, nominal)
                    fireDb.setValue(dbUser)
                        .addOnSuccessListener {
                            Toast.makeText(view?.context, "Topup Success", Toast.LENGTH_LONG).show()
                        }

                }
                dialogView.btn_home_cancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        })
    }
    fun flipImage(i: Int) {
        val view = ImageView(context)
        view.setBackgroundResource(i)
        viewflipper.addView(view)
//        viewflipper.flipInterval(2000)
//        viewflipper.isAutoStart
//        viewflipper.animation
//        viewflipper.outAnimation


        viewflipper.setInAnimation(context, R.anim.leftin)
        viewflipper.setOutAnimation(context, R.anim.rightout)

        if (viewflipper.isFlipping) {
            viewflipper.stopFlipping()
        } else {
            viewflipper.startFlipping()
        }
    }
}
