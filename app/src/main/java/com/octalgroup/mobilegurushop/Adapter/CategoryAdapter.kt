package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.AllProductViewActivity
import com.octalgroup.mobilegurushop.Model.TypesModel
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class CategoryAdapter(var c: Context, var list:ArrayList<TypesModel>):
RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {


    override fun onClick(view: View) {
        val holder = view.tag as RecyclerView.ViewHolder
        Toast.makeText(c,"hello", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_categories, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(list[position].id,list[position].tname.toString())


        holder.view.setOnClickListener {
            val intent= Intent(c, AllProductViewActivity::class.java)
            intent.putExtra("id",list[position].id.toString())
            intent.putExtra("tname",list[position].tname.toString())
            c.startActivity(intent)
        }

        Glide.with(c).load(list[position].img.toString())
            .apply(RequestOptions.circleCropTransform()).into(holder.vtimgCategory)
    }


    inner class MyProjects(var view: View): RecyclerView.ViewHolder(view){

        var vtname=view.findViewById<TextView>(R.id.txtCategory)

        var vtimgCategory=view.findViewById<ImageView>(R.id.imgCategory)

        fun bind(id: Int, tname:String)
        {
            vtname.text=tname.toString()
        }

    }

}