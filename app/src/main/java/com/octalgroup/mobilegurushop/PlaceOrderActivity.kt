package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.CartAdapter
import com.octalgroup.mobilegurushop.Adapter.ProductAdapter
import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.CartItem
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.UpdateItemInCart
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import es.dmoral.toasty.Toasty
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_place_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class PlaceOrderActivity : AppCompatActivity(), PaymentResultListener {
    var adapter: ProductAdapter?=null

    private var TotalPrice: String? =null
    private val compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var cartDataSource: CartDataSource?=null
    private var mutableLiveDataCartItem: MutableLiveData<List<CartItem>>?=null
    private var recyclerViewState: Parcelable?=null

    private var spicelevel:String=""

    /*val GOOGLE_PAY_PACKAGE_NAME =
        "com.google.android.apps.nbu.paisa.user"
    val GOOGLE_PAY_REQUEST_CODE = 123*/

    var orderprice:Int = 0
    var orderid:Int = 0
    var pickuplocation:String?=null
    var supportnumber:String?=null
    var userid:String?=null
    var userphone:String?=null
    var orderdate:String?=null
    var ordertime:String?=null

    var username:String?=null
    var orderstatus:String?=null
    var paymentmode:String?=null
    var paymentstatus:String?=null
    var paymentid:String?=null

    var pricetotal:Int=0

    var deliverycharges:Int=0
    var deliverychargesfinal:Int=0
    var abovefree:Int=0
    var inrupee:Double=0.0
    var minusprice:Double=0.0
    var points: Number=0
    var pointsused: Number=0

    init {
        initCartdataSource(this)
    }

    fun getMutableLiveDataCartItem(): MutableLiveData<List<CartItem>> {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        calculateTotalPrice()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())
        rvCartProducts!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        rvCartProducts.layoutManager=layoutManager
        rvCartProducts.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))


        getMutableLiveDataCartItem().observe(this, Observer {
            if (it==null || it.isEmpty())
            {
                rvCartProducts.visibility=View.GONE
                startActivity(Intent(this, CartActivity::class.java))
                finish()
            }
            else
            {
                val adapter = CartAdapter(this,it)
                rvCartProducts.adapter=adapter
            }
        })



        db.collection("numbers").document("points")
            .get()
            .addOnSuccessListener { document ->

                val pointsneeded: Number = document["pointredeemlimit"] as Number
                val ppp: Number = document["ppp"] as Number

                db.collection("users").document(userid())
                    .get()
                    .addOnSuccessListener { doc ->
                        points = doc["points"] as Number
                        if(points.toDouble() > pointsneeded.toDouble()){
                            redeempoints.visibility=View.VISIBLE
                            inrupee= ppp.toDouble() * points.toDouble()
                            redeempoints.text="Redeem Your Food Points $points \nand get discount of ${inrupee.toInt()} ₹"
                        }
                    }
                 }

                db.collection("numbers").document("data")
                    .get()
                    .addOnSuccessListener { document ->

                        pickuplocation = document["pickup"] as String
                        supportnumber = document["supportno"] as String
                        val cod: Boolean = document["cod"] as Boolean
                        val homedelivery: Boolean = document["homedelivery"] as Boolean

                        calculateTotalPrice()
                        txtSupport.text=supportnumber
                        loadFirst.visibility=View.GONE
                        layout.visibility=View.VISIBLE
                        txtPickup.text=pickuplocation
                        viewPickup.visibility=View.VISIBLE
                        rvCartProducts.visibility=View.VISIBLE

                        val lat = document["latitude"] as String
                        val log = document["longitude"] as String

                        pickUpLocation.setOnClickListener{
                            val gmmIntentUri:Uri = Uri.parse("geo:0,0?q=$lat,$log(pickuplocation)")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            startActivity(mapIntent)
                        }

                        if(cod==true)
                        {
                            btnCOD.visibility=View.VISIBLE
                        }
                        else
                        {
                            btnCOD.visibility=View.GONE
                        }

                        if(homedelivery==true)
                        {
                            rdoHome.visibility=View.VISIBLE
                        }
                        else
                        {
                            rdoPickup.isChecked=true
                            rdoHome.visibility=View.GONE
                            viewPickup.visibility=View.VISIBLE
                        }
                    }

        Checkout.preload(applicationContext)
                btnGooglePay.setOnClickListener {


                loadFirst.visibility=View.VISIBLE
                layout.visibility=View.GONE
                rvCartProducts.visibility=View.GONE


                    startPayment(orderprice.toInt())

                  /*  val uri = Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", "9511726841@paytm")
                        .appendQueryParameter("pn", "VYANKATESH MUKESH RATHOD")
                        //.appendQueryParameter("mc", "your-merchant-code")
                        //.appendQueryParameter("tr", "your-transaction-ref-id")
                        .appendQueryParameter("tn", "Products")
                        .appendQueryParameter("am", orderprice.toString())
                        .appendQueryParameter("cu", "INR")
                        //.appendQueryParameter("url", "your-transaction-url")
                        .build()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = uri
                    intent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
                    startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE)*/



                 }


                btnCOD.setOnClickListener {

                    bookedsave()



            loadFirst.visibility=View.VISIBLE
            layout.visibility=View.GONE
            rvCartProducts.visibility=View.GONE

            val sfDocRef = db.collection("numbers").document("orderid")
            db.runTransaction { transaction ->
                val snapshot = transaction.get(sfDocRef)

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                val newOrderid = snapshot.getDouble("orderid")!! + 1
                transaction.update(sfDocRef, "orderid", newOrderid)

                            orderid = newOrderid.toInt()
                            orderstatus = "Booked"
                            paymentmode = "COD"
                            paymentstatus = "Unpaid"
                            paymentid = ""
                            userid = userid()
                            userphone = userno()
                            orderdate = todayDate()
                            ordertime = Time()
                            username= com.octalgroup.mobilegurushop.username().toString()

                val instructions = txtInstructions.text.toString()

                            var deliverytype: String? = null

                            if (rdoHome.isChecked == true) {
                                deliverytype = "Home Delivery"
                                pickuplocation = txtAddress.text.toString()
                            } else if (rdoPickup.isChecked == true) {
                                deliverytype = "Pickup"
                            }

                            val docorderID = hashMapOf(
                                "orderid" to orderid.toInt(),
                                "userid" to userid.toString(),
                                "userphone" to userphone.toString(),
                                "deliveryaddress" to pickuplocation.toString(),
                                "supportnumber" to supportnumber.toString(),
                                "orderstatus" to orderstatus.toString(),
                                "paymentmode" to paymentmode.toString(),
                                "paymentstatus" to paymentstatus.toString(),
                                "paymentid" to paymentid.toString(),
                                "orderdate" to orderdate.toString(),
                                "ordertime" to ordertime.toString(),
                                "totalprice" to orderprice.toString(),
                                "productstotal" to pricetotal.toString(),
                                "deliverycharges" to deliverychargesfinal.toString(),
                                "deliverytype" to deliverytype.toString(),
                                "username" to username.toString(),
                                "date" to FieldValue.serverTimestamp(),
                                "instructions" to instructions,
                                "confirmdate" to "",
                                "confirmtime" to "",
                                "intransitdate" to "",
                                "intransittime" to "",
                                "deliverydate" to "",
                                "deliverytime" to "",
                                "cancleddate" to "",
                                "cancledtime" to "",
                                "cancledreason" to "",
                                "cancledby" to "",
                                "did" to "",
                                "dname" to "",
                                "dimage" to "",
                                "dnumber" to "",
                                "trainno" to "",
                                "trainname" to "",
                                "spicelevel" to spicelevel,
                                "pointsused" to pointsused.toInt(),
                                "pointsprice" to minusprice.toInt(),
                                "pointsearned" to 0
                            )

                                db.collection("orders").document("order"+orderid.toString())
                                .set(docorderID as Map<String, Any>)
                                .addOnSuccessListener { documentReference ->

                                    val docid: String = "order"+orderid.toString()

                                    getMutableLiveDataCartItem().observe(this, Observer {
                                        if (it == null || it.isEmpty()) {

                                        } else {
                                            val size = it.size

                                            for (x in 0 until size) {
                                                val productprice: Int = it[x].productPrice.toInt()
                                                val productquantity: Int = it[x].productQuantity.toInt()
                                                val producttotal: Int = productprice * productquantity
                                                val docData = hashMapOf(
                                                    "orderid" to orderid.toInt(),
                                                    "uid" to it[x].uid.toString(),
                                                    "userphone" to it[x].userPhone.toString(),
                                                    "productid" to it[x].productId.toInt(),
                                                    "productname" to it[x].productName.toString(),
                                                    "productimage" to it[x].productImage.toString(),
                                                    "productprice" to it[x].productPrice.toString(),
                                                    "productquantity" to it[x].productQuantity.toString(),
                                                    "productsize" to it[x].productSize.toString(),
                                                    "producttype" to it[x].productType.toString(),
                                                    "producttotal" to producttotal.toString()
                                                )
                                                db.collection("orders")
                                                    .document(docid)
                                                    .collection("orderitems")
                                                    .add(docData as Map<String, Any>)
                                                    .addOnSuccessListener { documentReference ->
                                                    }
                                                    .addOnFailureListener { e ->
                                                    }

                                                productplacedcount(it[x].productId.toString(), it[x].productQuantity.toInt() )
                                            }
                                            for (x in 0 until size) {
                                                cartDataSource!!.deleteCart(it[x])
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(object : SingleObserver<Int> {
                                                        override fun onSuccess(t: Int) {
                                                            println(x.toString() + " deleted")
                                                        }

                                                        override fun onSubscribe(d: Disposable) {
                                                        }

                                                        override fun onError(e: Throwable) {
                                                        }
                                                    })
                                                Toasty.success(
                                                    this,
                                                    "Order Placed Sucessfully",
                                                    Toast.LENGTH_LONG,
                                                    true
                                                ).show()
                                                loadFirst.visibility = View.GONE
                                                layout.visibility = View.VISIBLE
                                                startActivity(Intent(this, HomeActivity::class.java))
                                                finish()
                                            }

                                        }
                                    })

                                }
                                .addOnFailureListener { e ->
                                }


                    null
                }
                    .addOnSuccessListener { Log.d("TRANSACTION", "Transaction success!") }
                    .addOnFailureListener { e -> Log.w("FAILED", "Transaction failure.", e) }

        }

    }

    fun bookedsave(){
        val bookedadd = db.collection("stats").document("stats"+ todayDate())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(bookedadd)
            val newOrderid = snapshot.getDouble("booked")!! + 1
            transaction.update(bookedadd, "booked", newOrderid)
        }

        if(redeempoints.isChecked){
            val userupdate = db.collection("users").document(userid())
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userupdate)
                //val newOrderid = snapshot.getDouble("booked")!! + 1
                transaction.update(userupdate, "points",0)
            }
        }
    }

   /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GOOGLE_PAY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (data!!.getStringExtra("Status")=="SUCCESS")
                        {
                            /*val bundle: Bundle? = data.getExtras()
                            if (bundle != null) {
                                val keys = bundle.keySet()
                                val it: Iterator<String> = keys.iterator()
                                while (it.hasNext()) {
                                    val key = it.next()
                                        println("data "+key + " = " + bundle[key])
                                }
                            }*/

                            val sfDocRef = db.collection("numbers").document("orderid")
                            db.runTransaction { transaction ->
                                val snapshot = transaction.get(sfDocRef)
                                val newOrderid = snapshot.getDouble("orderid")!! + 1
                                transaction.update(sfDocRef, "orderid", newOrderid)
                                orderid = newOrderid.toInt()
                                // Success

                                orderstatus="Booked"
                                paymentmode="GPay"
                                paymentstatus="Paid"
                                paymentid=(data!!.getStringExtra("txnRef")).toString()
                                userid = userid()
                                userphone = userno()
                                orderdate = Date()
                                ordertime = Time()

                                var deliverytype:String?=null


                                if (rdoHome.isChecked == true) {
                                    deliverytype = "Home Delivery"
                                    pickuplocation = txtAddress.text.toString()
                                } else if (rdoPickup.isChecked == true) {
                                    deliverytype = "Pickup"
                                }

                                val docorderID = hashMapOf(
                                    "orderid" to orderid.toInt(),
                                    "userid" to userid.toString(),
                                    "userphone" to userphone.toString(),
                                    "deliveryaddress" to pickuplocation.toString(),
                                    "supportnumber" to supportnumber.toString(),
                                    "orderstatus" to orderstatus.toString(),
                                    "paymentmode" to paymentmode.toString(),
                                    "paymentstatus" to paymentstatus.toString(),
                                    "paymentid" to paymentid.toString(),
                                    "orderdate" to orderdate.toString(),
                                    "ordertime" to ordertime.toString(),
                                    "totalprice" to orderprice.toString(),
                                    "productstotal" to pricetotal.toString(),
                                    "deliverycharges" to deliverychargesfinal.toString(),
                                    "deliverytype" to deliverytype.toString()
                                )

                                db.collection("orders").document("order"+orderid.toString())
                                    .set(docorderID as Map<String, Any>)
                                    .addOnSuccessListener { documentReference ->

                                        val docid: String = "order"+orderid.toString()

                                        getMutableLiveDataCartItem().observe(this, Observer {
                                            if (it == null || it.isEmpty()) {
                                                Toast.makeText(this,"No Items in Cart",Toast.LENGTH_LONG).show()

                                            } else {
                                                val size = it.size

                                                for (x in 0 until size) {
                                                    val productprice: Int = it[x].productPrice.toInt()
                                                    val productquantity: Int = it[x].productQuantity.toInt()
                                                    val producttotal: Int = productprice * productquantity
                                                    val docData = hashMapOf(
                                                        "orderid" to orderid.toInt(),
                                                        "uid" to it[x].uid.toString(),
                                                        "userphone" to it[x].userPhone.toString(),
                                                        "productid" to it[x].productId.toInt(),
                                                        "productname" to it[x].productName.toString(),
                                                        "productimage" to it[x].productImage.toString(),
                                                        "productprice" to it[x].productPrice.toString(),
                                                        "productquantity" to it[x].productQuantity.toString(),
                                                        "productsize" to it[x].productSize.toString(),
                                                        "producttype" to it[x].productType.toString(),
                                                        "producttotal" to producttotal.toString()
                                                    )
                                                    db.collection("orders")
                                                        .document(docid)
                                                        .collection("orderitems")
                                                        .add(docData as Map<String, Any>)
                                                        .addOnSuccessListener { documentReference ->
                                                        }
                                                        .addOnFailureListener { e ->
                                                        }
                                                }
                                                for (x in 0 until size) {
                                                    cartDataSource!!.deleteCart(it[x])
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(object : SingleObserver<Int> {
                                                            override fun onSuccess(t: Int) {
                                                                println(x.toString() + " deleted")
                                                            }

                                                            override fun onSubscribe(d: Disposable) {
                                                            }

                                                            override fun onError(e: Throwable) {
                                                            }
                                                        })
                                                    Toasty.success(
                                                        this,
                                                        "Order Placed Sucessfully",
                                                        Toast.LENGTH_LONG,
                                                        true
                                                    ).show()
                                                    loadFirst.visibility = View.GONE
                                                    layout.visibility = View.VISIBLE
                                                    startActivity(Intent(this, HomeActivity::class.java))
                                                    finish()
                                                }

                                            }
                                        })

                                    }
                                    .addOnFailureListener { e ->
                                    }

                                null
                            }.addOnSuccessListener { Log.d("TRANSACTION", "Transaction success!")
                                //Toast.makeText(this,getRandomString(5)+"order",Toast.LENGTH_LONG).show()
                            }
                                .addOnFailureListener { e -> Log.w("FAILED", "Transaction failure.", e) }


                        }
                        else
                        {
                            loadFirst.visibility=View.GONE
                            layout.visibility=View.VISIBLE
                            Toast.makeText(this,"Payment Failed",Toast.LENGTH_SHORT).show()
                            rvCartProducts.visibility=View.VISIBLE
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(this,"Payment Cancled",Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }*/

    fun rbclick(view: View) {

        val radio: RadioButton = findViewById(rgroup.checkedRadioButtonId)

        if(radio.text=="Home Delivery"){

            txtTotal.textAlignment=View.TEXT_ALIGNMENT_CENTER

            loadFirst.visibility=View.VISIBLE
            db.collection("users")
                .whereEqualTo("uid",userid())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val address =document["uaddress"] as String
                        txtAddress.setText(address)
                        viewPickup.visibility=View.GONE
                        viewHomeDelivery.visibility=View.VISIBLE
                        loadFirst.visibility=View.GONE
                    }
                }

            db.collection("numbers").document("data")
                .get()
                .addOnSuccessListener { document ->
                    val free :String = document["abovefree"] as String
                    abovefree = free.toInt()
                    val delcharges :String = document["deliverycharges"] as String
                    deliverycharges = delcharges.toInt()
                    calculateTotalPrice()
                    txtFreeAbove.text = "Free Delivery on order above $free ₹"
                    viewdetails.visibility=View.VISIBLE
                }
        }

        else
        {
            viewHomeDelivery.visibility=View.GONE
            viewPickup.visibility=View.VISIBLE
            deliverycharges = 0
            calculateTotalPrice()
            viewdetails.visibility=View.GONE
            txtTotal.textAlignment=View.TEXT_ALIGNMENT_TEXT_END

        }

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCartEvent(event: UpdateItemInCart){
        if(event.cartItem != null){
            recyclerViewState=rvCartProducts!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<Int>{

                    override fun onSuccess(t: Int) {
                        calculateTotalPrice()
                        rvCartProducts!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@PlaceOrderActivity,"[UPDATE CART]"+e.message,Toast.LENGTH_LONG).show()

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
                    TotalPrice=price.toString()
                    val a:Double=TotalPrice!!.toDouble()

                    pricetotal = a.toInt()

                    if (pricetotal > abovefree)
                    {
                        orderprice=pricetotal-minusprice.toInt()


                        txtTotal.text="To Pay: ₹ "+orderprice.toString()

                        productstotal.text="Cart Total: ₹ ${pricetotal.toString()}"

                        deliverychargesfinal=0
                        txtDeliveryCharges.text="Delivery Charges: ₹ ${deliverychargesfinal.toString()}"
                    }
                    else if (pricetotal <= abovefree)
                    {
                        orderprice=pricetotal+deliverycharges-minusprice.toInt()

                        txtTotal.text="To Pay: ₹ "+orderprice.toString()
                        productstotal.text="Cart Total: ₹ ${pricetotal.toString()}"

                        deliverychargesfinal=deliverycharges
                        txtDeliveryCharges.text="Delivery Charges: ₹ ${deliverychargesfinal.toString()}"
                    }
                    else if (deliverycharges==0){
                        deliverychargesfinal=0
                        orderprice=pricetotal-minusprice.toInt()
                        txtTotal.text="To Pay: ₹ "+orderprice.toString()
                    }


                    //txtTotal.text="Total : "+price.toString()+" ₹"

                    //txtTotal.text = StringBuilder("Total: ")
                       // .append(price)



                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })

    }

    private fun startPayment(amount:Int) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Veera Da Dhaba")
            options.put("description", com.octalgroup.mobilegurushop.userid().toString())
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency","INR")
            options.put("amount",amount*100)

            val prefill = JSONObject()
            //prefill.put("email","test@razorpay.com")
            prefill.put("contact", userno().toString())
            options.put("prefill",prefill)
            co.open(activity,options)
        }
        catch (e: Exception){
            Toasty.error(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    val TAG:String = this::class.toString()

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            loadFirst.visibility=View.GONE
            layout.visibility=View.VISIBLE
            rvCartProducts.visibility=View.VISIBLE
            Toasty.error(this,"Payment failed $errorCode \n $response",Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e(TAG,"Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{

            bookedsave()

            val sfDocRef = db.collection("numbers").document("orderid")
            db.runTransaction { transaction ->
                val snapshot = transaction.get(sfDocRef)
                val newOrderid = snapshot.getDouble("orderid")!! + 1
                transaction.update(sfDocRef, "orderid", newOrderid)
                orderid = newOrderid.toInt()
                orderstatus="Booked"
                paymentmode="RazorPay"
                paymentstatus="Paid"
                paymentid=razorpayPaymentId.toString()
                userid = userid()
                userphone = userno()
                orderdate = todayDate()
                ordertime = Time()
                username= com.octalgroup.mobilegurushop.username().toString()
                val instructions = txtInstructions.text.toString()

                var deliverytype:String?=null

                if (rdoHome.isChecked == true) {
                    deliverytype = "Home Delivery"
                    pickuplocation = txtAddress.text.toString()
                } else if (rdoPickup.isChecked == true) {
                    deliverytype = "Pickup"
                }

                val docorderID = hashMapOf(
                    "orderid" to orderid.toInt(),
                    "userid" to userid.toString(),
                    "userphone" to userphone.toString(),
                    "deliveryaddress" to pickuplocation.toString(),
                    "supportnumber" to supportnumber.toString(),
                    "orderstatus" to orderstatus.toString(),
                    "paymentmode" to paymentmode.toString(),
                    "paymentstatus" to paymentstatus.toString(),
                    "paymentid" to paymentid.toString(),
                    "orderdate" to orderdate.toString(),
                    "ordertime" to ordertime.toString(),
                    "totalprice" to orderprice.toString(),
                    "productstotal" to pricetotal.toString(),
                    "deliverycharges" to deliverychargesfinal.toString(),
                    "deliverytype" to deliverytype.toString(),
                    "username" to username.toString(),
                    "date" to FieldValue.serverTimestamp(),
                    "instructions" to instructions,
                    "confirmdate" to "",
                    "confirmtime" to "",
                    "intransitdate" to "",
                    "intransittime" to "",
                    "deliverydate" to "",
                    "deliverytime" to "",
                    "cancleddate" to "",
                    "cancledtime" to "",
                    "cancledreason" to "",
                    "cancledby" to "",
                    "did" to "",
                    "dname" to "",
                    "dimage" to "",
                    "dnumber" to "",
                    "trainno" to "",
                    "trainname" to "",
                    "spicelevel" to spicelevel,
                    "pointsused" to pointsused.toInt(),
                    "pointsprice" to minusprice.toInt(),
                    "pointsearned" to 0

                )

                db.collection("orders").document("order"+orderid.toString())
                    .set(docorderID as Map<String, Any>)
                    .addOnSuccessListener { documentReference ->

                        val docid: String = "order"+orderid.toString()

                        getMutableLiveDataCartItem().observe(this, Observer {
                            if (it == null || it.isEmpty()) {
                                Toast.makeText(this,"No Items in Cart",Toast.LENGTH_LONG).show()

                            } else {
                                val size = it.size

                                for (x in 0 until size) {
                                    val productprice: Int = it[x].productPrice.toInt()
                                    val productquantity: Int = it[x].productQuantity.toInt()
                                    val producttotal: Int = productprice * productquantity
                                    val docData = hashMapOf(
                                        "orderid" to orderid.toInt(),
                                        "uid" to it[x].uid.toString(),
                                        "userphone" to it[x].userPhone.toString(),
                                        "productid" to it[x].productId.toInt(),
                                        "productname" to it[x].productName.toString(),
                                        "productimage" to it[x].productImage.toString(),
                                        "productprice" to it[x].productPrice.toString(),
                                        "productquantity" to it[x].productQuantity.toString(),
                                        "productsize" to it[x].productSize.toString(),
                                        "producttype" to it[x].productType.toString(),
                                        "producttotal" to producttotal.toString()
                                    )
                                    db.collection("orders")
                                        .document(docid)
                                        .collection("orderitems")
                                        .add(docData as Map<String, Any>)
                                        .addOnSuccessListener { documentReference ->
                                        }
                                        .addOnFailureListener { e ->
                                        }

                                    productplacedcount(it[x].productId.toString(), it[x].productQuantity.toInt())
                                }
                                for (x in 0 until size) {
                                    cartDataSource!!.deleteCart(it[x])
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object : SingleObserver<Int> {
                                            override fun onSuccess(t: Int) {
                                                println(x.toString() + " deleted")
                                            }

                                            override fun onSubscribe(d: Disposable) {
                                            }

                                            override fun onError(e: Throwable) {
                                            }
                                        })
                                    Toasty.success(
                                        this,
                                        "Order Placed Sucessfully",
                                        Toast.LENGTH_LONG,
                                        true
                                    ).show()
                                    loadFirst.visibility = View.GONE
                                    layout.visibility = View.VISIBLE
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                }

                            }
                        })

                    }
                    .addOnFailureListener { e ->
                    }

                null
            }.addOnSuccessListener { Log.d("TRANSACTION", "Transaction success!")
                //Toast.makeText(this,getRandomString(5)+"order",Toast.LENGTH_LONG).show()
            }
                .addOnFailureListener { e -> Log.w("FAILED", "Transaction failure.", e) }



            Toasty.success(this,"Payment Successful $razorpayPaymentId",Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e(TAG,"Exception in onPaymentSuccess", e)
        }
    }

    private fun productplacedcount(productid:String, qty:Int){

            val saleref = db.collection("products").document(productid.toInt().toString())
            db.runTransaction { transaction ->
            val snapshot = transaction.get(saleref)

            val newsale = snapshot.getDouble("sale")!! + qty
            transaction.update(saleref, "sale", newsale)

        }
    }

    fun rbclickspicy(view: View) {

        val radio2: RadioButton = findViewById(rgSpiceLevel.checkedRadioButtonId)

        if(radio2.text=="Low")
        {
            spicelevel="Low"
            Toast.makeText(this,spicelevel,Toast.LENGTH_SHORT).show()
        }
        else if(radio2.text=="Medium")
        {
            spicelevel="Medium"
            Toast.makeText(this,spicelevel,Toast.LENGTH_SHORT).show()
        }
        else if(radio2.text=="Spicy")
        {
            spicelevel="Spicy"
            Toast.makeText(this,spicelevel,Toast.LENGTH_SHORT).show()
        }
        else if(radio2.text=="Extra Spicy")
        {
            spicelevel="Extra Spicy"
            Toast.makeText(this,spicelevel,Toast.LENGTH_SHORT).show()
        }
        else
        {
            spicelevel="Not Selected"
            Toast.makeText(this,spicelevel,Toast.LENGTH_SHORT).show()
        }
    }

    fun redeempoints(view: View) {

        if(redeempoints.isChecked){
            pointsused= points
            minusprice=inrupee
            calculateTotalPrice()
        }
        else
        {
            pointsused=0
            minusprice=0.0
            calculateTotalPrice()
        }

    }


}


