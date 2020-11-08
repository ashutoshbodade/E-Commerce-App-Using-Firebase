package com.octalgroup.mobilegurushop


import com.octalgroup.mobilegurushop.Adapter.TypesAdapter
import com.octalgroup.mobilegurushop.Model.TypesModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_types.*


class TypesActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_types)
        val bundle: Bundle?= intent.extras
       val name =  bundle!!.getString("name")
        val title =  bundle.getString("title")
        this.setTitle(title)
        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser!=null){
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
        }
        else
        {
            fabtrain.setOnClickListener {
                Toast.makeText(this,"Log in to continue", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LogInActivity::class.java))
            }
        }




        loadtypes(name!!)


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
