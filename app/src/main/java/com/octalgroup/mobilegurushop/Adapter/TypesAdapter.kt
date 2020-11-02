package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Model.ProductModel
import com.octalgroup.mobilegurushop.Model.TypesModel
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore


class TypesAdapter(var c: Context, var list:ArrayList<TypesModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {


    override fun onClick(view: View) {
        val holder = view.tag as RecyclerView.ViewHolder
        Toast.makeText(c,"hello", Toast.LENGTH_LONG).show()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_types, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MyProjects).bind(list[position].id,list[position].tname.toString())

        var expandedPosition = 1

        holder.vtdropdown.setOnClickListener {
            if (expandedPosition == 1){
                holder.vtrvproduct.visibility=View.GONE
                expandedPosition = -1
                holder.vtdropdownup.visibility=View.VISIBLE
                holder.vtdropdowndown.visibility=View.GONE

            }
            else
            {
                holder.vtrvproduct.visibility=View.VISIBLE
                expandedPosition = 1
                holder.vtdropdownup.visibility=View.GONE
                holder.vtdropdowndown.visibility=View.VISIBLE
            }
        }


    }



    inner class MyProjects(var view: View): RecyclerView.ViewHolder(view){

    var ptypename:String?=null


    fun recycler(){
    val db = FirebaseFirestore.getInstance()
    db.collection("products")

        .whereEqualTo("subcategory",ptypename).whereEqualTo("available",true)
        .get()
        .addOnSuccessListener { documents ->
            val productsList = ArrayList<ProductModel>()
            for (document in documents) {
                if (document != null) {
                    val products = document.toObject(ProductModel::class.java)
                    productsList.add(products)
                }
            }

            val adp = AllProductsViewAdapter(c, productsList)
            vtrvproduct.layoutManager = StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL)
            vtrvproduct.adapter = adp
            vtrvProductsProgressBar.visibility=View.GONE
            }
        }



        var vtname=view.findViewById<TextView>(R.id.txtTypeName)
        var vtrvproduct=view.findViewById<RecyclerView>(R.id.rvProducts)
        var vtrvProductsProgressBar=view.findViewById<ProgressBar>(R.id.rvProductsProgressBar)
        var vtdropdown=view.findViewById<TableLayout>(R.id.dropdown)
        var vtdropdownup=view.findViewById<ImageView>(R.id.dropdownup)
        var vtdropdowndown=view.findViewById<ImageView>(R.id.dropdowndown)




        fun bind(id: Int, tname:String)
        {
            vtname.text=tname.toString()
            ptypename=tname.toString()
            recycler()
        }

    }

}