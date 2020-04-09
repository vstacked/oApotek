package com.tugas.oapotek.activity

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugas.oapotek.MainActivity
import com.tugas.oapotek.R
import com.tugas.oapotek.adapter.CartAdapter
import com.tugas.oapotek.fragments.NotificationFragment
import com.tugas.oapotek.model.CartList
import com.tugas.oapotek.model.NotificationList
import com.tugas.oapotek.model.StatusBuy
import com.tugas.oapotek.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.t_dialog.view.*
import kotlinx.android.synthetic.main.topup_dialog.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
class CartActivity : AppCompatActivity() {
    val adapterCart = GroupAdapter<ViewHolder>()
    var idBuy: String = ""
    var idCart: String = ""
    var photoCart: String = ""
    var judulCart: String = ""
    var hargaCart: String = ""
    var quantityCart: String = ""
    var totalCart: String = ""
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.tugas.oapotek.activity"
    private val description = "Notification"
    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.launchIntent(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.show()
        supportActionBar?.title = "Cart"
        btn_beli_cart.isClickable = tv_cart_total.text != "Rp. 0"
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fetchCart()
        initViews()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_cart.layoutManager = layoutManager
    }
    @SuppressLint("CommitTransaction")
    private fun initViews() {
        btn_beli_cart.setOnClickListener {
            showDialog()
//            NotificationFragment.launchIntent(this)
//            val intent = Intent(this, NotificationFragment::class.java)
//            val idBuy = idBuy
//            val idProduct = idCart
//            val bundle = Bundle()
//            bundle.putString(idBuyIntent, idBuy)
//            bundle.putString(idProductIntent, idProduct)
//

//            intent.putExtra(idBuyIntent, idBuy)
//            intent.putExtra(idProductIntent, idProduct)
//            startActivity(intent)
//            val fragmentManager = supportFragmentManager
            //MainActivity.launchIntent(this)
//            NotificationFragment = NotificationFragment()
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.frame_layout, NotificationFragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
            //MainActivity.launchIntent(this)

//            val intent = Intent(this, MainActivity::class.java)
//            val value = "NotificationFragment"
//            intent.putExtra(NotifFragKey, value)
//            startActivity(intent)
        }
    }private fun uploadToNotification() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val idProduct = idCart
        val photoDetail = photoCart
        val uriPhoto = photoDetail.toUri()
        val judulDetail = judulCart
        val hargaDetail = hargaCart
        val quantity = quantityCart
        val totalCart = totalCart
        val idBuy = idBuy

//        val idBuy = idBuy //diganti dadi random text, goodluck :)
        val db =
            FirebaseDatabase.getInstance().getReference("notification/$uid/$idBuy/status/statusBuy")
        val db1 = FirebaseDatabase.getInstance()
            .getReference("notification/$uid/$idBuy/product/$idProduct/")
        //val db1 = FirebaseDatabase.getInstance().getReference("notification/$uid/$idBuy")

        val builder = AlertDialog.Builder(this)
        dialogView = layoutInflater.inflate(R.layout.progressbar_fullscreen, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()

        db1.setValue(
            NotificationList(
                idProduct,
                uriPhoto.toString(),
                judulDetail,
                hargaDetail,
                quantity
            )
        )


        val judulArray = tv_cart_coba.text.toString()
        val time = System.currentTimeMillis()
        db.setValue(
            StatusBuy(
                idBuy,
                totalCart,
                "Pending",
                judulArray,
                time,
                uid
            )
        )
            .addOnSuccessListener {
                dialog?.dismiss()
                //sendNotificationToAdmin()
                val db = FirebaseDatabase.getInstance().getReference("cart/$uid/")
                db.removeValue()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

//                        val Notification= NotificationFragment.newInstance(idBuy)
//                        supportFragmentManager
//                            .beginTransaction()
//                            .replace(R.id.frame_layout, Notification)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                            .commit()


//                        intent.putExtra("IdBuy", idBuy)
//                        val idBuyTv = findViewById<TextView>(R.id.tv_cobaa)
//                        idBuyTv.text = idBuy
//                        val b = NotificationFragment.newInstance(idBuy)
//                        replaceFragment(b)
//                        val kategori = "Obat"
//                        intent.putExtra("key", kategori)
//                        val value = "NotificationFragment"
//                        intent.putExtra(NotifFragKey, value)
//                        startActivity(intent)
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
            }

    }
    private fun sendNotificationToAdmin() {
        if(FirebaseAuth.getInstance().uid.toString() != LoginActivity.uidAdmin){
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0 ,intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val contentView = RemoteViews(packageName, R.layout.notification_custom_layout)
        contentView.setTextViewText(R.id.tv_notification_title, "Title")
        contentView.setTextViewText(R.id.tv_notification_content, "Content")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel  = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.CYAN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.drawable.junction_satu)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.junction_satu))
                .setContentIntent(pendingIntent)
        }else{
            builder = Notification.Builder(this)
                .setContent(contentView)
                .setSmallIcon(R.drawable.junction_satu)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.junction_satu))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
        }
    }private fun fetchCart() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("/cart/$uid/")


        fireDb?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                var sum = 0
                p0.children.forEach {
                    val cart = it.getValue(CartList::class.java) as CartList
                    adapterCart.add(CartAdapter(cart))
                    val Harga = it.getValue(CartList::class.java)?.harga
                    val Quantity = it.getValue(CartList::class.java)?.quantity
                    val value1 = Harga?.toInt()
                    val value2 = Quantity?.toInt()
                    val rumus = value1!! * value2!!
                    sum += rumus
//
//                    idCart = it.getValue(CartList::class.java)?.idProduct.toString()
//                    photoCart = it.getValue(CartList::class.java)?.photo.toString()
//                    judulCart = it.getValue(CartList::class.java)?.judul.toString()
//                    hargaCart = it.getValue(CartList::class.java)?.harga.toString()
//                    quantityCart = it.getValue(CartList::class.java)?.quantity.toString()
                }
//                totalCart = sum.toString()
                tv_cart_total.text = "Rp. $sum"
                rv_cart.adapter = adapterCart
            }
        })
    }private fun uploadfetchCart() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("/cart/$uid/")


        fireDb?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var sum = 0
                    idBuy = UUID.randomUUID().toString()
                    p0.children.forEach {
                        //                    val cart = it.getValue(CartList::class.java) as CartList
//                    adapterCart.add(CartAdapter(cart))
                        val Harga = it.getValue(CartList::class.java)?.harga
                        val Quantity = it.getValue(CartList::class.java)?.quantity
                        val value1 = Harga?.toInt()
                        val value2 = Quantity?.toInt()
                        val rumus = value1!! * value2!!
                        sum += rumus

                        idCart = it.getValue(CartList::class.java)?.idProduct.toString()
                        photoCart = it.getValue(CartList::class.java)?.photo.toString()
                        judulCart = it.getValue(CartList::class.java)?.judul.toString()
                        hargaCart = it.getValue(CartList::class.java)?.harga.toString()
                        quantityCart = it.getValue(CartList::class.java)?.quantity.toString()
                        uploadToNotification()

                        val array = arrayOf(judulCart)
                        for (element in array) {
                            tv_cart_coba.text = tv_cart_coba.text.toString() + element + ", "
                        }
                    }
                    totalCart = sum.toString()
                    uploadToNotification()


                    tv_cart_total.text = "Rp. $sum"
                }
            }
        })
    }private fun showDialog() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val fireDb = FirebaseDatabase.getInstance().getReference("user/$uid")

        fireDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val photo = p0.child("photo").value.toString()
                val name = p0.child("name").value.toString()
                val address = p0.child("address").value.toString()
                val saldo = p0.child("saldo").value.toString()

                val dialogView = LayoutInflater.from(this@CartActivity).inflate(R.layout.t_dialog, null)
                val builder = AlertDialog.Builder(this@CartActivity)
                    .setView(dialogView)
                    .setTitle("Alert")
                val alertDialog = builder.show()

                dialogView.btn_sure_ok.setOnClickListener {
                    alertDialog.dismiss()

                    val fireDbCart = FirebaseDatabase.getInstance().getReference("/cart/$uid/")
                    fireDbCart.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                var sumB=0
                                p0.children.forEach {
                                    val Harga = it.getValue(CartList::class.java)?.harga
                                    val Quantity = it.getValue(CartList::class.java)?.quantity
                                    val value1 = Harga?.toInt()
                                    val value2 = Quantity?.toInt()
                                    val rumus = value1!! * value2!!
                                    sumB += rumus
                                }
                                if(saldo.toInt()>sumB){
                                    val sisaSaldo = saldo.toInt() - sumB
                                    val nominal = sisaSaldo.toString()
                                    val dbUser = User(photo, name, address, nominal)
                                    fireDb.setValue(dbUser)
                                    uploadfetchCart()
                                }else{
                                    Toast.makeText(this@CartActivity, "Saldo Anda kurang", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    })
                }
                dialogView.btn_sure_cancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        })
    }
    companion object {
        val idBuyIntent = "idBuyIntent"
        val idProductIntent = ""
        val NotifFragKey = "NotifFragKey"
        fun launchIntent(context: Context) {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        }
    }
}
