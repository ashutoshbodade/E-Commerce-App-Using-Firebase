package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.ProductAdapter
import com.octalgroup.mobilegurushop.Adapter.ReviewAdapter
import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.CartItem
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.CountCartEvent
import com.octalgroup.mobilegurushop.Model.ReviewModel
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal
import java.math.RoundingMode

class ProductView : AppCompatActivity() {


    private val compositeDisposable: CompositeDisposable
    private val cartDataSource: CartDataSource

    private var TotalPrice: String? =null
    val TAG:String = this::class.toString()
    private var data: String? =null

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())
    }


    var adapter: ProductAdapter?=null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        if(adapter!=null)
            adapter!!.onStop()
        if (compositeDisposable!=null)
            compositeDisposable.clear()

        super.onStop()
    }


    override fun onResume() {
        super.onResume()
        countCartItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_view)

        val bundle: Bundle?= intent.extras
        val id =  bundle!!.getString("id")


        val productsList = ArrayList<ReviewModel>()
        val adp = ReviewAdapter(this, productsList)
        var rating:Double=0.2

        db.collection("reviews").whereEqualTo("productid",id!!.toInt()).whereEqualTo("uid", userid().toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document != null) {
                        rating =document["rating"] as Double
                        userRatingBar.rating=rating.toFloat()
                        userRating.text=rating.toString()
                        val review:String=document["review"] as String
                        userReview.text=review
                        val username:String=document["username"] as String
                        if (username.length!=0)
                        {
                          userTextImage.text=username.substring(0,1).toString()
                        }
                        userName.text=username
                    }
                }
            }
            .addOnCompleteListener {
                if(rating==0.2)
                {
                    writeReview.visibility=View.VISIBLE
                }
                else
                {
                    userReviewView.visibility=View.VISIBLE
                }
            }


        db.collection("reviews").whereEqualTo("productid",id.toInt()).orderBy("id", Query.Direction.DESCENDING).limit(4)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    if (document != null) {
                        val products = document.toObject(ReviewModel::class.java)
                        productsList.add(products)
                    }
                }
                rvReviews.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false )
                rvReviews.adapter = adp
                rvReviews.visibility = View.VISIBLE
            }


        fab.setOnClickListener { view: View? ->
            startActivity(Intent(this, CartActivity::class.java))
        }

        fab2.setOnClickListener {
            startActivity(Intent(this, TrainCartActivity::class.java))
        }


        calculateTotalPrice()


        db.collection("ratings").document(id.toString())
            .get()
            .addOnSuccessListener { document ->
                    if (document != null){

                        if(document["ratingaverage"] == null && document["totalratings"] == null){
                            productRatingCount.text= 0.toString()
                            productRating.rating=0.toFloat()
                            productTotalRatings.text="("+0.toInt()+")"
                        }
                        else
                        {
                            val rate:Double=document["ratingaverage"] as Double
                            val totalrate=document["totalratings"] as Double
                            val decimal = BigDecimal(rate).setScale(2, RoundingMode.HALF_EVEN)
                            productRatingCount.text= decimal.toString()
                            productRating.rating=rate.toFloat()
                            productTotalRatings.text="("+totalrate.toInt()+")"
                        }

                    }
                }


        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

            if (rating<3)
            {
                submitrating.text="Thanks please.. decribe How can we improve?"
            }
            else if (rating<=5)
            {
                submitrating.text="Thanks please.. decribe your best experience."
            }
            submitreview.visibility=View.VISIBLE
        }

        btnSubmitReview.setOnClickListener {
            val rating = ratingBar.rating.toFloat()
            val review = txtReview.text.toString()
            val username = username().toString()
            val uid = userid().toString()
            val productid= id!!.toInt()
            btnSubmitReview.isClickable=false
            btnSubmitReview.isActivated=false
            writeReview.visibility=View.GONE
            thanksView.visibility=View.VISIBLE



            val sfDocRef = db.collection("ratings").document(productid.toString())
            db.runTransaction { transaction ->
                val snapshot = transaction.get(sfDocRef)

                val newratings = snapshot.getDouble("totalratings")!! + 1
                val ratings = snapshot.getDouble("ratingaverage")!! + rating

                val average = ratings / 2

                transaction.update(sfDocRef, "totalratings", newratings)
                transaction.update(sfDocRef, "ratingaverage", average)

                // Success
                null
            }.addOnSuccessListener { Log.d(TAG, "Transaction success!") }
            .addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e)
                val docorderID = hashMapOf(
                    "totalratings" to 1.0,
                    "ratingaverage" to rating.toDouble()
                )
                db.collection("ratings").document(productid.toString())
                    .set(docorderID as Map<String, Any>)
                    .addOnSuccessListener { documentReference ->

                    }
            }

            val ratingno = db.collection("reviews").document("count")
            db.runTransaction { transaction ->
                val snapshot = transaction.get(ratingno)

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                val newreview = snapshot.getDouble("count")!! + 1
                transaction.update(ratingno, "count", newreview)

                val docorderID = hashMapOf(
                    "id" to newreview,
                    "rating" to rating,
                    "review" to review,
                    "username" to username,
                    "uid" to uid,
                    "productid" to productid
                )
                db.collection("reviews").document("review"+newreview)
                    .set(docorderID as Map<String, Any>)

                // Success
                null
            }.addOnSuccessListener {}.addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }




        }



        db.collection("products")
            .whereEqualTo("id", id.toInt())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document != null){

                        data=document["name"] as String
                        val name:String=document["name"] as String
                        this.setTitle(name)

                        val price:String=document["price"] as String
                        txtPrice.text= "₹ $price"

                        val mrp:String=document["mrp"] as String
                        txtMRP.text=mrp
                        txtMRP.paintFlags = txtMRP.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                        val qty:String=document["qty"] as String
                        val unit:String=document["unit"] as String

                        val image:String=document["image"] as String
                        Glide.with(this).load(image).centerCrop().into(imageView)
                        val dtype:String=document["dtype"] as String
                        val type:String=document["type"] as String

                        if(dtype=="normal") {

                            cartDataSource.getItemInCart(id.toString(),userid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object: SingleObserver<CartItem> {
                                    override fun onSubscribe(d: Disposable) {
                                    }
                                    override fun onError(e: Throwable) {

                                    }
                                    override fun onSuccess(t: CartItem) {
                                        btnAddToCart.text="ADDED TO CART"
                                        btnAddToCart.isActivated=false
                                        btnAddToCart.isClickable=false

                                        btnBuyNow.text="Proceed to check out"
                                    }
                                })

                            fab.visibility=View.VISIBLE
                            fab2.visibility=View.GONE
                            txtName.text=name+" ($qty $unit)"
                        }

                        else if(dtype=="train") {

                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(userid()).collection("traincarttemp").document(id.toString())
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {

                                        val productname=document["productname"]

                                        if(productname!=null){

                                            btnAddToCart.isActivated=false
                                            btnAddToCart.isClickable=false
                                            btnBuyNow.text="Proceed to check out"
                                            btnAddToCart.text="ADDED TO TRAIN CART"
                                        }


                                    } else {
                                        println("datas not exist $id")

                                    }
                                }
                                .addOnFailureListener { exception ->
                                    println("datas failed exist $id")

                                }
                            fab.visibility=View.GONE
                            fab2.visibility=View.VISIBLE
                            txtName.text=name+" ($qty $unit) *Food in Train Only"

                            val docRef = db.collection("users").document(userid())
                            docRef.addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    return@addSnapshotListener
                                }
                                if (snapshot != null && snapshot.exists()) {

                                    val traincart:Number=snapshot["traincart"] as Number
                                    fab2.count=traincart.toInt()
                                }
                            }

                        }




                        val description:String=document["description"] as String

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtDesc.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            txtDesc.setText(Html.fromHtml(description));
                        }

                        val a=price.toInt()
                        val b=mrp.toInt()
                        val c:Double=b.toDouble()/100
                        val d:Double=b.toDouble()-a
                        val e:Double=d/c
                        val f = BigDecimal(e).setScale(2, RoundingMode.HALF_EVEN)
                        txtDiscount.text=f.toString()+"% off"
                        productView.visibility=View.VISIBLE
                        loadingproductinfo.visibility=View.GONE
                        val cartItem = CartItem()

                        btnAddToCart.setOnClickListener {

                            if(dtype=="normal"){
                            cartItem.uid=userid()
                            cartItem.userPhone=userno()
                            cartItem.productId=id.toString()
                            cartItem.productName=name.toString()
                            cartItem.productImage=image.toString()
                            cartItem.productPrice=price.toDouble()
                            cartItem.productQuantity=1
                            cartItem.productSize= "$qty $unit"
                            cartItem.productType=type.toString()

                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    btnAddToCart.text="ADDED TO CART"
                                    org.greenrobot.eventbus.EventBus.getDefault().postSticky(CountCartEvent(true))
                                },{
                                        t: Throwable ->  Toast.makeText(this,"[Add to cart]"+t!!.message, Toast.LENGTH_SHORT).show()
                                }))

                            }

                            else if(dtype=="train"){

                                btnAddToCart.text="PLEASE WAIT"

                                val docData = hashMapOf(
                                    "uid" to userid().toString(),
                                    "userphone" to userno().toString(),
                                    "productid" to id.toInt(),
                                    "productname" to name.toString(),
                                    "productimage" to image.toString(),
                                    "productprice" to  price.toString(),
                                    "productquantity" to 1,
                                    "productsize" to qty.toString() +" "+ unit.toString(),
                                    "producttype" to  type.toString()
                                )

                                val db = FirebaseFirestore.getInstance()
                                db.collection("users").document(userid()).collection("traincarttemp")
                                    .document(id.toString())
                                    .set(docData as Map<String, Any>)
                                    .addOnSuccessListener { documentReference ->
                                    }
                                    .addOnFailureListener { e ->
                                    }
                                    .addOnCompleteListener {

                                        val saleref = db.collection("users").document(userid().toString())
                                        db.runTransaction { transaction ->
                                            val snapshot = transaction.get(saleref)
                                            val newsale = snapshot.getDouble("traincart")!! + 1
                                            transaction.update(saleref, "traincart", newsale)
                                        }

                                        btnAddToCart.text="ADDED TO TRAIN CART"
                                        Toast.makeText(this,"Added to train cart",Toast.LENGTH_SHORT).show()
                                        btnBuyNow.text="Proceed to checkout"
                                    }
                            }

                        }

                        btnBuyNow.setOnClickListener {
                            if(dtype=="normal") {
                                cartItem.uid = userid()
                                cartItem.userPhone = userno()
                                cartItem.productId = id.toString()
                                cartItem.productName = name.toString()
                                cartItem.productImage = image.toString()
                                cartItem.productPrice = price.toDouble()
                                cartItem.productQuantity = 1
                                cartItem.productSize = "$qty $unit"
                                cartItem.productType = type.toString()
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        btnAddToCart.text = "ADDED TO CART"
                                        val intent = Intent(this, PlaceOrderActivity::class.java)
                                        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        this.startActivity(intent)

                                        //holder.buttoncart.visibility=View.GONE
                                        // holder.buttonok.visibility=View.VISIBLE
                                        org.greenrobot.eventbus.EventBus.getDefault()
                                            .postSticky(CountCartEvent(true))
                                    }, { t: Throwable ->
                                        Toast.makeText(
                                            this,
                                            "[Add to cart]" + t!!.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                                )
                            }
                            else if(dtype=="train") {
                                val intent = Intent(this,SelectTrain::class.java)
                                startActivity(intent)
                            }

                        }


                    }

                }
            }
            .addOnFailureListener {

            }
            .addOnCompleteListener {
                if (data==null){
                    Toast.makeText(this,"No Data Found",Toast.LENGTH_SHORT).show()
                }
            }





    }

    private fun countCartItem(){
        cartDataSource.countItemInCart(userid())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Int> {
                override fun onSuccess(t: Int) {
                    fab.count= t
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

            })
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCountCartEvent(event: CountCartEvent)
    {
        if (event.isSuccess)
        {
            countCartItem()
        }
    }

    private fun calculateTotalPrice() {

        cartDataSource.sumPrice(userid())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Double>{
                override fun onSuccess(price: Double) {
                    TotalPrice=price.toString()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })

    }



}