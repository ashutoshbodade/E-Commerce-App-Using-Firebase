package com.octalgroup.mobilegurushop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.octalgroup.mobilegurushop.Adapter.AddressAdapter
import com.octalgroup.mobilegurushop.Model.AddressModel
import kotlinx.android.synthetic.main.activity_address_view.*


class AddressViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_view)

        val productsList = ArrayList<AddressModel>()

        db.collection("users").document(userid()).collection("address")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e)
                    // Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                productsList.clear()

                for (doc in value!!) {
                    if (doc != null) {
                        val projects = doc.toObject(AddressModel::class.java)
                        productsList.add(projects)
                    }
                }
                val adp = AddressAdapter(this, productsList)
                rvAddress.layoutManager = LinearLayoutManager(this.applicationContext)
                rvAddress.adapter = adp
                rvAddress.visibility = View.VISIBLE
            }


        btnAddNewAddress.setOnClickListener {
            startActivity(Intent(this, SaveNewAddressActivity::class.java))
            finish()
        }
    }
}