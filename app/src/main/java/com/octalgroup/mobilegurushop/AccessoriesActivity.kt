package com.octalgroup.mobilegurushop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.octalgroup.mobilegurushop.Adapter.AllProductsViewAdapter
import com.octalgroup.mobilegurushop.Model.ProductModel
import kotlinx.android.synthetic.main.activity_accessories.*


class AccessoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accessories)
        val bundle: Bundle?= intent.extras
        val name =  bundle!!.getString("name")
        val title =  bundle.getString("title")
        this.setTitle(title)


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

        db.collection("products").whereEqualTo("available", true).whereEqualTo("category",name)
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