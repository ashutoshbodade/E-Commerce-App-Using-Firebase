package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Database.*
import com.octalgroup.mobilegurushop.EventBus.UpdateItemInCart
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class CartAdapter (internal var context: Context,
                   internal var cartItems:List<CartItem>) :
RecyclerView.Adapter<CartAdapter.MyViewHolder>(){


    internal var compositeDisposable: CompositeDisposable
    internal var cartDataSource:CartDataSource

    init {
        compositeDisposable= CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val my_view = LayoutInflater.from(context).inflate(R.layout.list_cart_items, parent, false)
        return MyViewHolder(my_view)
    }



    override fun getItemCount(): Int {
      return cartItems.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context).load(cartItems[position].productImage).apply(RequestOptions.circleCropTransform()).into(holder.img_product_cart)

        holder.txt_product_name.text= StringBuilder(cartItems[position].productName!!)
        val price = StringBuilder(cartItems[position].productPrice.toString())
        holder.txt_product_price.text= "$price \u20B9"

        val qty = StringBuilder(cartItems[position].productQuantity.toString())
        val qty1 =qty.toString()
        if ( qty1.toInt() ==1){
            holder.btn_product_minus.isEnabled = false
            holder.btn_product_minus.isClickable = false
        }

        holder.txt_product_quantity.text= StringBuilder(cartItems[position].productSize.toString())
        holder.txt_product_quantitytotal.text= StringBuilder(cartItems[position].productQuantity.toString())

        val a:Int = cartItems[position].productQuantity.toInt()
        val b = cartItems[position].productPrice

        val c= a*b
        holder.txt_total_item_price.text="Total "+c.toString()+" ₹"


        holder.btn_product_minus.setOnClickListener {

            val a = holder.txt_product_quantitytotal.text.toString()
            var b = a.toInt()
            b -= 1
            if (b==1){
                holder.btn_product_minus.isEnabled = false
                holder.btn_product_minus.isClickable = false
            }
            val c = b*cartItems[position].productPrice
            holder.txt_total_item_price.text="Total "+c.toString()+" ₹"

            holder.display(b)

            EventBus.getDefault().postSticky(UpdateItemInCart(cartItems[position]))

            cartItems[position].productQuantity = b
        }

        holder.btn_product_plus.setOnClickListener {
            val a = holder.txt_product_quantitytotal.text.toString()
            var b = a.toInt()
            b += 1
            if (b>1){
                holder.btn_product_minus.isEnabled = true
                holder.btn_product_minus.isClickable = true
            }
            val c = b*cartItems[position].productPrice
            holder.txt_total_item_price.text="Total "+c.toString()+" ₹"
            holder.display(b)
            holder.visibility()

            EventBus.getDefault().postSticky(UpdateItemInCart(cartItems[position]))

            cartItems[position].productQuantity = b
        }


        holder.btn_delete.setOnClickListener {
            cartDataSource.deleteCart(cartItems[position])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<Int> {
                    override fun onSuccess(t: Int) {

                        Toast.makeText(context,cartItems[position].productName+" Removed",Toast.LENGTH_SHORT).show()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                    }
                })

            EventBus.getDefault().postSticky(UpdateItemInCart(cartItems[position]))

        }


    }



    inner class  MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        internal var minteger = 1
        lateinit var img_product_cart : ImageView
        lateinit var txt_product_name : TextView
        lateinit var txt_product_price : TextView
        lateinit var txt_product_quantity : TextView
        lateinit var txt_product_quantitytotal : TextView
        lateinit var btn_product_minus : Button
        lateinit var btn_product_plus : Button
        lateinit var btn_delete : ImageView
        lateinit var txt_total_item_price : TextView

        init
        {
            img_product_cart = itemView.findViewById(R.id.imgCartProduct) as ImageView
            txt_product_name = itemView.findViewById(R.id.txtCartName) as TextView
            txt_product_price = itemView.findViewById(R.id.txtCartPrice) as TextView
            txt_product_quantity = itemView.findViewById(R.id.txtCartQty) as TextView
            txt_product_quantitytotal = itemView.findViewById(R.id.txtProductTotalQty) as TextView
            btn_product_minus = itemView.findViewById(R.id.decrease) as Button
            btn_product_plus = itemView.findViewById(R.id.increase) as Button
            btn_delete = itemView.findViewById(R.id.btnDelete) as ImageView
            txt_total_item_price = itemView.findViewById(R.id.txtTotalItemPrice) as TextView

        }

        fun visibility() {
            val dis = txt_product_quantitytotal.text.toString()
            val di = dis.toInt()
            if (di <= 1) {
                btn_product_minus.isEnabled=false
                btn_product_minus.isActivated=false
            }
            else
            {
                btn_product_minus.isEnabled=true
                btn_product_minus.isActivated=true
            }
        }

        fun visibility1() {
            val dis = txt_product_quantitytotal.text.toString()
            val di = dis.toInt()
            if (di <= 2) {
                btn_product_minus.isEnabled=false
                btn_product_minus.isActivated=false
            }
            else
            {
               btn_product_minus.isEnabled=true
               btn_product_minus.isActivated=true
            }
        }



        fun display(number: Int) {
            txt_product_quantitytotal.text = number.toString()
        }


    }

}