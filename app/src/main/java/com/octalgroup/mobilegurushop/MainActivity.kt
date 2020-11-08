package com.octalgroup.mobilegurushop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
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
        checkForUpdates()
    }


    private fun checkForUpdates() {
        //1
        val appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        val appUpdateInfo = appUpdateManager.appUpdateInfo
        appUpdateInfo.addOnSuccessListener {
            //2
            if(it.availableVersionCode() <= BuildConfig.VERSION_CODE)
            {
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
            else
            {
                Toast.makeText(this,"Update app to continue shopping ....",Toast.LENGTH_SHORT).show()

                val uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
                var intent = Intent(Intent.ACTION_VIEW, uri)

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

                if (intent.resolveActivity(this.packageManager) != null) {
                    startActivity(intent)
                } else {
                    intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))
                    if (intent.resolveActivity(this.packageManager) != null) {
                        startActivity(intent)
                        finish()
                        finishAffinity()
                    } else {
                        Toast.makeText(this, "No play store or browser app", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }

    }

}
