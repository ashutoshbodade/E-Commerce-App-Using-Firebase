package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.OrdersAdapter
import com.octalgroup.mobilegurushop.Model.OrderModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_orders_view.*
import kotlin.collections.ArrayList

class OrdersViewActivity : AppCompatActivity() {
    val projectList = ArrayList<OrderModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_view)
        this.setTitle("ORDERS")
       loaddata()
    }

    private fun loaddata(){
        db.collection("orders").whereEqualTo("userid",userid()).orderBy("orderid", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e)
                    // Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                projectList.clear()

                for (doc in value!!) {
                    if (doc != null) {
                        val projects = doc.toObject(OrderModel::class.java)
                        projects.docid=doc.id.toString()
                        projectList.add(projects)
                    }
                }
                viewrecycler()
            }
    }

    private fun viewrecycler(){

        val adp = OrdersAdapter(this, projectList)
        adp.notifyDataSetChanged()
        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = adp
        load.visibility= View.GONE
        rvOrders.visibility= View.VISIBLE


    }
}
