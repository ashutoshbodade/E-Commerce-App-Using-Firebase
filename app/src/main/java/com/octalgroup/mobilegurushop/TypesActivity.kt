package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.ProductAdapter
import com.octalgroup.mobilegurushop.Adapter.TypesAdapter
import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.CountCartEvent
import com.octalgroup.mobilegurushop.Model.TypesModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_types.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TypesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_types)
        val bundle: Bundle?= intent.extras
       val name =  bundle!!.getString("name")
        val title =  bundle.getString("title")
        this.setTitle(title)



            val docRefs = db.collection("users").document(userid())
            docRefs.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val traincart:Number=snapshot["cart"] as Number
                    fabtrain.count=traincart.toInt()
                }
            }


        fabtrain.visibility=View.VISIBLE
        fabtrain.setOnClickListener {
            startActivity(Intent(this, TrainCartActivity::class.java))
        }
        loadtypes(name!!)
        closednotice.visibility = View.GONE
        close.visibility = View.GONE

    }


    private  fun loadtypes(name:String){

        db.collection("producttypes").whereEqualTo("type",name)
            .get()
            .addOnSuccessListener { documents ->
                val typesList = ArrayList<TypesModel>()
                for (document in documents) {
                    if (document != null) {
                        val types = document.toObject(TypesModel::class.java)
                        typesList.add(types)
                    }
                }

                val adp = TypesAdapter(this, typesList)
                rvTypes.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false )
                rvTypes.adapter = adp
                rvTypes.visibility = View.VISIBLE
                rvTypesProgressBar.visibility = View.GONE
            }
    }


}
