package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Model.ProductModel
import com.octalgroup.mobilegurushop.Model.TrainCartModel
import com.octalgroup.mobilegurushop.R
import com.octalgroup.mobilegurushop.db
import com.octalgroup.mobilegurushop.userid
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.lang.StringBuilder


class CartTrainAdapter(var context: Context, var list: ArrayList<TrainCartModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener{

    val searchableList = ArrayList<ProductModel>()

    override fun onClick(view: View) {
        Toast.makeText(context, "hello", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(context).inflate(R.layout.list_cart_items, parent, false)
        return MyProjects(my_view)
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(
            list[position].productid,
            list[position].productname.toString(),
            list[position].productprice.toString(),
            list[position].productquantity.toString(),
            list[position].productcategory.toString(),
            list[position].productimage.toString(),
            list[position].productsubcategory.toString()
        )

        val price = StringBuilder(list[position].productprice.toString())
        val qty = StringBuilder(list[position].productquantity.toString())
        val qty1 =qty.toString()
        if ( qty1.toInt() ==1){
            holder.btn_product_minus.isEnabled = false
            holder.btn_product_minus.isClickable = false
        }

        val a:Int = list[position].productquantity.toInt()
        val b = list[position].productquantity





        holder.btn_product_minus.setOnClickListener {

            val a = holder.txt_product_quantitytotal.text.toString()
            var b = a.toInt()
            b -= 1
            if (b==1){
                holder.btn_product_minus.isEnabled = false
                holder.btn_product_minus.isClickable = false
            }
            val c = b*list[position].productprice!!.toInt()
            holder.txt_total_item_price.text="Total "+c.toString()+" ₹"
            holder.display(b, list[position].productid.toString())
        }

        holder.btn_product_plus.setOnClickListener {
            val a = holder.txt_product_quantitytotal.text.toString()
            var b = a.toInt()
            b += 1
            if (b>1){
                holder.btn_product_minus.isEnabled = true
                holder.btn_product_minus.isClickable = true
            }
            val c = b*list[position].productprice!!.toInt()
            holder.txt_total_item_price.text="Total "+c.toString()+" ₹"
            holder.display(b, list[position].productid.toString())

        }

        holder.btn_delete.setOnClickListener {

            db.collection("users").document(userid()).collection("carttemp").document(list[position].productid.toString())
                .delete()
                .addOnSuccessListener {
                    val saleref = db.collection("users").document(userid().toString())
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(saleref)
                        val newsale = snapshot.getDouble("cart")!! - 1
                        transaction.update(saleref, "cart", newsale)
                    }
                }
        }


        Glide.with(context).load(list[position].productimage.toString()).apply(RequestOptions.circleCropTransform()).into(holder.img_product_cart)

    }



    inner class MyProjects(var view: View ) : RecyclerView.ViewHolder(view) {

        var img_product_cart = view.findViewById(R.id.imgCartProduct) as ImageView

        var txt_product_name = view.findViewById(R.id.txtCartName) as TextView
        var txt_product_price = view.findViewById(R.id.txtCartPrice) as TextView
        var txt_product_quantity = view.findViewById(R.id.txtCartQty) as TextView
        var txt_product_quantitytotal = view.findViewById(R.id.txtProductTotalQty) as TextView
        var btn_product_minus = view.findViewById(R.id.decrease) as Button
        var btn_product_plus = view.findViewById(R.id.increase) as Button
        var btn_delete = view.findViewById(R.id.btnDelete) as ImageView
        var txt_total_item_price = view.findViewById(R.id.txtTotalItemPrice) as TextView




        fun bind(id: Int, varproductname: String, varproductprice: String, varproductquantity: String, varproductcategory: String, varproductimage: String, varproductsubcategory: String) {
            txt_product_name.text=varproductname

            txt_product_quantity.text= varproductsubcategory
            txt_product_quantitytotal.text= varproductquantity

            txt_product_price.text= "$varproductprice \u20B9"

            txt_total_item_price.text="Total "+varproductprice.toInt()*varproductquantity.toInt()+" ₹"
        }

        fun display(number: Int, pid:String) {
            txt_product_quantitytotal.text = number.toString()

            val saleref = db.collection("users").document(userid().toString()).collection("carttemp").document(pid.toString())
            db.runTransaction { transaction ->
                transaction.update(saleref, "productquantity", number.toInt())
            }
        }

    }



}