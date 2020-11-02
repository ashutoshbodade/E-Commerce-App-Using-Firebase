package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.AllProductsViewAdapter
import com.octalgroup.mobilegurushop.Adapter.ProductAdapter
import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.CountCartEvent
import com.octalgroup.mobilegurushop.Model.ProductModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_product_view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AllProductViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_product_view)

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
