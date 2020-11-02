package com.octalgroup.mobilegurushop
import com.octalgroup.android.Adapter.BannerAdapter
import com.octalgroup.mobilegurushop.Adapter.*
import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.CountCartEvent
import com.octalgroup.mobilegurushop.Model.Banner
import com.octalgroup.mobilegurushop.Model.ProductModel
import com.octalgroup.mobilegurushop.Model.ReviewModel
import com.octalgroup.mobilegurushop.Model.TypesModel
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.razorpay.Checkout
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.fab
import kotlinx.android.synthetic.main.activity_product_view.*
import kotlinx.android.synthetic.main.content_home.*
import org.greenrobot.eventbus.EventBus

import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    var adapter: ProductAdapter? = null
    val TAG: String = this::class.toString()


    override fun onStop() {
        EventBus.getDefault().unregister(this)
        if (adapter != null)
            adapter!!.onStop()
        super.onStop()
    }

    val productsList = ArrayList<ReviewModel>()
    val adp = ReviewAdapter(this, productsList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        Checkout.preload(applicationContext)

        FirebaseMessaging.getInstance().isAutoInitEnabled = true



        viewHome()

    }

    fun viewHome() {

        toolBar.inflateMenu(R.menu.menu_item)

       /* toolBar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener() {
            fun onMenuItemClick(item: MenuItem): Boolean {
                if (item.itemId == android.R.id.item1) {
                    // do something
                } else if (item.itemId == android.R.id.filter) {
                    // do something
                } else {
                    // do something
                }
                return false
            }


        })*/

        val docRef = db.collection("users").document(userid())
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val traincart:Number=snapshot["cart"] as Number
                fab.count=traincart.toInt()
            }
        }

        btnOrders.setOnClickListener {
            startActivity(Intent(this, OrdersViewActivity::class.java))
        }

        fab.setOnClickListener { view: View? ->
            startActivity(Intent(this, TrainCartActivity::class.java))
        }

        btnRestaurant.setOnClickListener {
            val intent = Intent(this, TypesActivity::class.java)
            intent.putExtra("title", "Mobile")
            intent.putExtra("name", "Mobile")
            this.startActivity(intent)
        }

        btnCake.setOnClickListener {
            val intent = Intent(this, TypesActivity::class.java)
            intent.putExtra("title", "Namkeens")
            intent.putExtra("name", "namkeen")
            this.startActivity(intent)
        }

        btnIceCream.setOnClickListener {
            val intent = Intent(this, TypesActivity::class.java)
            intent.putExtra("title", "Fast Food")
            intent.putExtra("name", "fastfood")
            this.startActivity(intent)
        }

        btnTrain.setOnClickListener {

            val intent = Intent(this, TypesActivity::class.java)
            intent.putExtra("title", "Confectionery")
            intent.putExtra("name", "confectionery")
            this.startActivity(intent)
        }

        db.collection("producttypes").limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val typesList = ArrayList<TypesModel>()
                for (document in documents) {
                    if (document != null) {
                        val types = document.toObject(TypesModel::class.java)
                        typesList.add(types)
                    }
                }

                val adp = CategoryAdapter(this, typesList)
                rvCategory.layoutManager =
                    LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                rvCategory.adapter = adp
                rvCategory.visibility = View.VISIBLE
                rvTypesProgressBar.visibility = View.GONE
            }




     /*   db.collection("reviews").orderBy("id", Query.Direction.DESCENDING).limit(3)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    if (document != null) {
                        val products = document.toObject(ReviewModel::class.java)
                        productsList.add(products)
                    }
                }
                rvReviews.layoutManager =
                    LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                rvReviews.adapter = adp
                rvReviews.visibility = View.VISIBLE
            }*/

        searchview.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@HomeActivity, query, Toast.LENGTH_SHORT).show()

                loadsearchproducts(query!!)

                viewcontent.visibility = View.GONE
                Log.i(TAG, "ASHU")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length > 0) {
                    loadsearchproducts(newText)
                    //Toast.makeText(this@HomeActivity,newText,Toast.LENGTH_SHORT).show()
                    viewcontent.visibility = View.GONE
                    rvSearch.visibility = View.VISIBLE

                } else {
                    //  Toast.makeText(this@HomeActivity,"EMPTY",Toast.LENGTH_SHORT).show()
                    rvSearch.visibility = View.GONE
                    viewcontent.visibility = View.VISIBLE

                }

                return true
            }

        })

        viewPagerh.setPageTransformer(true, ZoomOutPageTransformer())
        db.collection("banners")
            .get()
            .addOnSuccessListener { documents ->
                val bannerList = ArrayList<Banner>()
                for (document in documents) {
                    if (document != null) {
                        val banneritem = document.toObject(Banner::class.java)
                        bannerList.add(banneritem)
                        val handler = Handler()
                        var currentPage: Int = 0
                        if (viewPagerh == null) {

                        } else {
                            val update = Runnable {
                                viewPagerh.setCurrentItem(currentPage % bannerList.size, true)
                                currentPage++
                            }
                            val timer = Timer()
                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    handler.post(update)
                                }
                            }, 3000, 3000)
                        }
                        val adapter = BannerAdapter(this, bannerList)
                        viewPagerh.adapter = adapter
                    }
                }
            }

        loadofferimg()
        loadlatestproducts()

    }

    fun loadofferimg() {
        db.collection("offerimg")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document != null) {
                        val enable: Boolean = document["enable"] as Boolean
                        if (enable == true) {
                            val img: String = document["img"] as String
                            val titleview: String = document["title"] as String
                            val link: String = document["link"] as String

                            Glide.with(this).load(img).centerCrop().into(imgOffer)
                            imgOffer.setOnClickListener {
                                val intent = Intent(this, WebViewOffer::class.java)
                                intent.putExtra("title", titleview)
                                intent.putExtra("link", link)
                                this.startActivity(intent)
                            }
                        } else {
                            imgOffer.visibility = View.GONE
                        }

                    }

                }
            }
            .addOnFailureListener {

            }
    }

    fun loadlatestproducts() {
        //  db.collection("products").orderBy("id", Query.Direction.DESCENDING).limit(4).whereEqualTo("available", true)
        db.collection("products").orderBy("sale", Query.Direction.DESCENDING).limit(5)
            .whereEqualTo("available", true)
            .get()
            .addOnSuccessListener { documents ->
                val productsList = ArrayList<ProductModel>()
                for (document in documents) {
                    if (document != null) {
                        val products = document.toObject(ProductModel::class.java)
                        productsList.add(products)
                    }
                }

                val adp = LatestProductsAdapter(this, productsList)
                rvLatestProducts.layoutManager =
                    LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                rvLatestProducts.adapter = adp
                rvLatestProducts.visibility = View.VISIBLE
                viewhome.visibility = View.VISIBLE
            }
    }


    fun loadsearchproducts(query: String) {
        val productsList = ArrayList<ProductModel>()
        val adp = SearchProductAdapter(this@HomeActivity, productsList)

        if (productsList.size < 1) {

            db.collection("products").whereEqualTo("available", true)
                .get()
                .addOnSuccessListener { documents ->

                    for (document in documents) {
                        if (document != null) {
                            val products = document.toObject(ProductModel::class.java)
                            productsList.add(products)
                        }
                    }
                    adp.filter.filter(query.toString())
                    rvSearch.layoutManager =
                        LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    rvSearch.adapter = adp
                    rvSearch.visibility = View.VISIBLE
                }

        } else {
            adp.filter.filter(query.toString())
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        // builder.setIcon(R.drawable.joker)
        builder.setMessage("Do you want to Exit? ")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
            val a = Intent(Intent.ACTION_MAIN)
            a.addCategory(Intent.CATEGORY_HOME)
            a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(a)
            super@HomeActivity.onBackPressed()
        })
        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, id -> super@HomeActivity.onBackPressed() })
        builder.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.orders -> {
                startActivity(Intent(this, OrdersViewActivity::class.java))
                true
            }
            R.id.profile -> {
                startActivity(Intent(this, UserDetailsActivity::class.java))
                return true
            }
            R.id.logout -> {
                mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                startActivity(Intent(this, LogInActivity::class.java))
                Toasty.warning(
                    applicationContext,
                    "Logged out Successfully :)!",
                    Toast.LENGTH_SHORT,
                    true
                ).show();
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

   /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        /* val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }*/
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }*/


}

