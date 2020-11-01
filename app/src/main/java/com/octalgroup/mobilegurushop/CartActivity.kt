package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.CartAdapter
import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.CartItem
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.UpdateItemInCart
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : AppCompatActivity() {


    private val compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var cartDataSource: CartDataSource?=null
    private var mutableLiveDataCartItem: MutableLiveData<List<CartItem>>?=null
    private var recyclerViewState: Parcelable?=null

   /* init {

    }*/

    fun getMutableLiveDataCartItem():MutableLiveData<List<CartItem>>{
        if(mutableLiveDataCartItem == null)
            mutableLiveDataCartItem= MutableLiveData()
        getCartItems()
        return mutableLiveDataCartItem!!
    }

    fun initCartdataSource(context: Context){
        cartDataSource= LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    private fun getCartItems(){
        compositeDisposable.addAll(cartDataSource!!.getAllCart(userid())
            .subscribeOn(Schedulers.io())
            .subscribe({ cartItems ->
                mutableLiveDataCartItem!!.postValue(cartItems)
            },{t: Throwable? ->  mutableLiveDataCartItem!!.value = null}))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initCartdataSource(this)
        btnCheckout.setOnClickListener {
            val totalprice = txtTotalPrice.text.toString()

            val intent = Intent(this, PlaceOrderActivity::class.java)
           // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("totalprice",totalprice.substring(6))
            this.startActivity(intent)
        }

        rvCart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        rvCart.layoutManager=layoutManager
        rvCart.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))

        calculateTotalPrice()

        getMutableLiveDataCartItem().observe(this, Observer {
            if (it==null || it.isEmpty())
            {
                rvCart.visibility=View.GONE
                txtNoCart.visibility=View.VISIBLE
                btnCheckout.visibility=View.GONE
            }
            else
            {
                 rvCart.visibility=View.VISIBLE
                 txtNoCart.visibility=View.GONE
                 btnCheckout.visibility=View.VISIBLE
                 val adapter = CartAdapter(this,it)
                 rvCart.adapter=adapter
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe (sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCartEvent(event: UpdateItemInCart){
        if(event.cartItem != null){
            recyclerViewState=rvCart!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<Int>{

                    override fun onSuccess(t: Int) {
                        calculateTotalPrice()
                       rvCart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@CartActivity,"[UPDATE CART]"+e.message,Toast.LENGTH_LONG).show()
                    }

                })
        }
    }

    private fun calculateTotalPrice() {

        cartDataSource!!.sumPrice(userid())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Double>{
                override fun onSuccess(price: Double) {

                    txtTotalPrice.text = StringBuilder("Total: ")
                        .append(price)

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })

    }
}
