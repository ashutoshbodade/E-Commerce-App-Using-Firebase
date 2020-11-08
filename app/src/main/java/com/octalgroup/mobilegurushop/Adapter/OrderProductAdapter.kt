package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Model.OrderProductModel
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class OrderProductAdapter(var c: Context, var list: ArrayList<OrderProductModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    override fun onClick(view: View) {
        Toast.makeText(c, "hello", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_orders_product, parent, false)
        return MyProjects(my_view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(
            list[position].productname.toString(),
            list[position].productprice.toString(),
            list[position].productquantity.toString(),
            list[position].productsize.toString(),
            list[position].producttotal.toString(),
            list[position].producttype.toString()
        )

        Glide.with(c).load(list[position].productimage.toString())
            .apply(RequestOptions.circleCropTransform()).into(holder.v_productimage)
    }



    inner class MyProjects(var view: View) : RecyclerView.ViewHolder(view) {




        var v_productimage = view.findViewById<ImageView>(R.id.imgOrderProduct)

        var v_productname = view.findViewById<TextView>(R.id.txtOrderProductName)
        var v_productprice = view.findViewById<TextView>(R.id.txtOrderProductPrice)
        var v_productquantity = view.findViewById<TextView>(R.id.txtOrderProductQty)
        var v_productsize = view.findViewById<TextView>(R.id.txtOrderProductSize)
        var v_producttotalprice = view.findViewById<TextView>(R.id.txtOrderProductTotalPrice)



        fun bind(varproductname: String, varproductprice: String, varproductquantity: String, varproductsize: String, varproducttotal: String, varproducttype: String) {
            v_productname.text="$varproductname"
            v_productprice.text="$varproductprice ₹ "
            v_productquantity.text=varproductquantity
            v_productsize.text=varproductsize
            v_producttotalprice.text="$varproducttotal.0 ₹ "
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}