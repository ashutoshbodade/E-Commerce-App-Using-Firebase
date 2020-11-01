package com.octalgroup.mobilegurushop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    val TAG:String = this::class.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

    /*
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = 2020
        cal[Calendar.MONTH] = Calendar.APRIL
        cal[Calendar.DAY_OF_MONTH] = 28
        val dateRepresentation = cal.time
        println("DATES "+dateRepresentation)


        val cal2 = Calendar.getInstance()
        cal[Calendar.YEAR] = 2020
        cal[Calendar.MONTH] = Calendar.APRIL
        cal[Calendar.DAY_OF_MONTH] = 29
        val dateRepresentation2 = cal2.time

        db.collection("orders").whereGreaterThan("date",dateRepresentation).whereLessThan("date",dateRepresentation2)
            .get()
            .addOnSuccessListener { documents ->
                val projectList = ArrayList<OrderModel>()
                for (document in documents) {
                    if (document != null) {
                        val types = document.toObject(OrderModel::class.java)
                        projectList.add(types)
                        println("FIRESTORE DATA ORDER ID"+document["orderid"])
                    }
                }

            }*/

    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser == null)
        {
            startActivity(Intent(this, WelcomeActivity::class.java))
            startActivity(intent)
        }
        else
        {
            intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
