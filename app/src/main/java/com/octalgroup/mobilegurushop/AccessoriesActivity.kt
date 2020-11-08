package com.octalgroup.mobilegurushop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.octalgroup.mobilegurushop.Adapter.AllProductsViewAdapter
import com.octalgroup.mobilegurushop.Model.ProductModel
import kotlinx.android.synthetic.main.activity_accessories.*


class AccessoriesActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accessories)
        val bundle: Bundle?= intent.extras
        val name =  bundle!!.getString("name")
        val title =  bundle.getString("title")
        this.setTitle(title)


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