package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.CartTrainAdapter
import com.octalgroup.mobilegurushop.Model.TrainCartModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_train_products.*

class TrainCartActivity : AppCompatActivity() {

    val cartitems = ArrayList<Int>()
    val productsList = ArrayList<TrainCartModel>()
    val adp = CartTrainAdapter(this, productsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_products)




        btnCheckoutTrain.setOnClickListener {
            val intent=Intent(this,PlaceTrainOrder::class.java)
            startActivity(intent)
        }


        db.collection("users").document(userid()).collection("carttemp")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e)
                    // Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                productsList.clear()
                cartitems.clear()

                for (document in value!!) {
                    if (document != null) {
                        val products = document.toObject(TrainCartModel::class.java)
                        productsList.add(products)

                        val price:String=document["productprice"] as String
                        val qty:Number=document["productquantity"] as Number
                        cartitems.add(price.toInt()*qty.toInt())
                    }
                }

                rvTProducts.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false )
                rvTProducts.adapter = adp
                rvTProducts.visibility = View.VISIBLE

                var totalprice:Int=0
                for (items in cartitems){
                    totalprice = totalprice + items
                }
                txtTotalPrice.text = StringBuilder("Total: ").append(totalprice.toString())
            }


    }
}
