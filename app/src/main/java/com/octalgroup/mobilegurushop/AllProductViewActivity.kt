package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.AllProductsViewAdapter
import com.octalgroup.mobilegurushop.Model.ProductModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_all_product_view.*
import kotlinx.android.synthetic.main.activity_all_product_view.fab

class AllProductViewActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_product_view)

        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser!=null){
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
            fab.setOnClickListener { view: View? ->
                startActivity(Intent(this, TrainCartActivity::class.java))
            }
        }
        else
        {
            fab.setOnClickListener { view: View? ->
                Toast.makeText(this,"Log in to continue", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LogInActivity::class.java))
            }
        }


        val bundle: Bundle?= intent.extras
        val id =  bundle!!.getString("id")
        val tname =  bundle.getString("tname")

        this.setTitle(tname)

             db.collection("products").whereEqualTo("available", true).whereEqualTo("category","Mobile").whereEqualTo("subcategory",tname)
            .get()
            .addOnSuccessListener { documents ->
                val productsList = ArrayList<ProductModel>()
                for (document in documents) {
                    if (document != null) {
                        val products = document.toObject(ProductModel::class.java)
                        productsList.add(products)
                    }
                }

                val adp = AllProductsViewAdapter(this, productsList)
                rvAllProducts.layoutManager = StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL)
                rvAllProducts.adapter = adp
                rvAllProducts.visibility = View.VISIBLE
                loading.visibility=View.GONE
            }
    }








}
