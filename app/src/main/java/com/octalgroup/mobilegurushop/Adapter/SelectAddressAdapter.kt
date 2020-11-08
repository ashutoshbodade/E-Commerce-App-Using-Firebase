package com.octalgroup.mobilegurushop.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.octalgroup.mobilegurushop.Model.AddressModel
import com.octalgroup.mobilegurushop.PlaceTrainOrder
import com.octalgroup.mobilegurushop.ProductView
import com.octalgroup.mobilegurushop.R

class SelectAddressAdapter(var c: Context, var list:ArrayList<AddressModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {


    override fun onClick(view: View) {
        val holder = view.tag as RecyclerView.ViewHolder

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_address, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(
            list[position].addressline!!,
            list[position].city!!,
            list[position].district!!,
            list[position].housenumber!!,
            list[position].postalcode!!,
            list[position].state!!
        )

        holder.view.setOnClickListener {
            val intent= Intent(c, PlaceTrainOrder::class.java)
            intent.putExtra("deliveryaddress", list[position].housenumber!! + ", " + list[position].addressline!! + ", "  + list[position].city!!
            + ", " + list[position].district!! + ", " + list[position].postalcode!! + ", " + list[position].state!!)
            c.startActivity(intent)
        }


    }



    inner class MyProjects(var view: View): RecyclerView.ViewHolder(view){

        var vvaddressline=view.findViewById<TextView>(R.id.txtAddress)

        var vvcity=view.findViewById<TextView>(R.id.txtCity)

        var vvdistrict=view.findViewById<TextView>(R.id.txtDistrict)

        var vvhousenumber=view.findViewById<TextView>(R.id.txtHouseNumber)

        var vvpostalcode=view.findViewById<TextView>(R.id.txtPinCode)

        var vvstate=view.findViewById<TextView>(R.id.txtState)




        fun bind(vaddressline: String, vcity:String, vdistrict:String, vhousenumber:String, vpostalcode:String, vstate:String)
        {

            vvaddressline.text = vaddressline

            vvcity.text = vcity

            vvdistrict.text = vdistrict

            vvhousenumber.text= vhousenumber

            vvpostalcode.text = vpostalcode

            vvstate.text = vstate



        }

    }

}