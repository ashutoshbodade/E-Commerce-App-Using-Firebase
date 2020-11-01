package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.TrainsAdapter
import com.octalgroup.mobilegurushop.Model.TrainsModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_select_train.*

class SelectTrain : AppCompatActivity() {

    val projectList = ArrayList<TrainsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_train)
        this.setTitle("Select Train")
        loaddata()
    }


    private fun loaddata(){
        db.collection("trains")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e)
                    // Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                projectList.clear()
                rvTrains.visibility= View.VISIBLE

                for (doc in value!!) {
                    if (doc != null) {
                        val projects = doc.toObject(TrainsModel::class.java)
                        projectList.add(projects)
                    }
                }
                viewrecycler()
            }
    }

    private fun viewrecycler(){
        val adp = TrainsAdapter(this, projectList)
        adp.notifyDataSetChanged()
        rvTrains.layoutManager = LinearLayoutManager(this)
        rvTrains.adapter = adp
        loading.visibility= View.GONE
        rvTrains.visibility= View.VISIBLE
    }
}
