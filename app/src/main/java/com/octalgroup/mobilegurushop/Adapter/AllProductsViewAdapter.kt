package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.CartItem
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.CountCartEvent
import com.octalgroup.mobilegurushop.Model.ProductModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.octalgroup.mobilegurushop.*
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AllProductsViewAdapter(var c: Context, var list: ArrayList<ProductModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    lateinit var mAuth: FirebaseAuth

    override fun onClick(view: View) {
        Toast.makeText(c, "hello", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_all_products_card, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MyProjects).bind(
            list[position].id,
            list[position].name.toString(),
            list[position].price.toString(),
            list[position].image.toString(),
            list[position].category.toString(),
            list[position].subcategory.toString()
        )



        holder.view.setOnClickListener {
            val intent= Intent(c, ProductView::class.java)
            intent.putExtra("id",list[position].id.toString())
            intent.putExtra("name",list[position].name.toString())
            intent.putExtra("price",list[position].price.toString())
            intent.putExtra("image",list[position].image.toString())
            intent.putExtra("category",list[position].category.toString())
            intent.putExtra("description",list[position].description.toString())
            intent.putExtra("subcategory",list[position].subcategory.toString())
            c.startActivity(intent)
        }


        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser!=null){
            holder.buttoncart.setOnClickListener {


                holder.buttoncart.visibility = View.INVISIBLE
                val docData = hashMapOf(
                    "uid" to userid().toString(),
                    "userphone" to userno().toString(),
                    "productid" to list[position].id.toInt(),
                    "productname" to list[position].name.toString(),
                    "productimage" to list[position].image.toString(),
                    "productprice" to list[position].price.toString(),
                    "productquantity" to 1,
                    "productcategory" to list[position].category.toString(),
                    "productsubcategory" to list[position].subcategory.toString()
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(userid()).collection("carttemp")
                    .document(list[position].id.toString())
                    .set(docData as Map<String, Any>)
                    .addOnSuccessListener { documentReference ->
                    }
                    .addOnFailureListener { e ->
                    }
                    .addOnCompleteListener {
                        holder.buttoncart.visibility = View.GONE
                        holder.buttonok.visibility = View.VISIBLE
                        Toast.makeText(c, "Added to cart", Toast.LENGTH_SHORT).show()

                        val saleref = db.collection("users").document(userid().toString())
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(saleref)
                            val newsale = snapshot.getDouble("cart")!! + 1
                            transaction.update(saleref, "cart", newsale)
                        }
                    }
            }
        }
        else{
            holder.buttoncart.setOnClickListener {
                Toast.makeText(c,"Log in to continue",Toast.LENGTH_SHORT).show()
                val intent= Intent(c, LogInActivity::class.java)
                c.startActivity(intent)
            }
        }






        Glide.with(c).load(list[position].image.toString()).into(holder.vimage)

    }



    inner class MyProjects(var view: View) : RecyclerView.ViewHolder(view) {

        internal var minteger = 1
        var vcid: String? = null
        var vcprice: String? = null
        var vcqty: String? = null
        var vcunit: String? = null

        init {

        }
        var vname = view.findViewById<TextView>(R.id.txtName)
        var vprice = view.findViewById<TextView>(R.id.txtPrice)
        var vqty = view.findViewById<TextView>(R.id.txtQty)
        var vimage = view.findViewById<ImageView>(R.id.imgProduct)
        var buttoncart = view.findViewById<ImageView>(R.id.btnaddtocart)
        var buttonok = view.findViewById<ImageView>(R.id.btnok)


        fun bind(id: Int, name: String, price: String, image: String, category: String, subcategory: String) {

            vname.text = name.toString()
            vprice.text = price + ".0 ₹"
            vqty.text = subcategory
            vcid = id.toString()
            vcprice = price

          /*  vcqty = qty
            vcunit = unit*/
            mAuth = FirebaseAuth.getInstance()
if(mAuth.currentUser!=null){
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userid()).collection("carttemp").document(id.toString())
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {

                val productname=document["productname"]

                if(productname!=null){
                    println("datas exist $id")
                    buttoncart.visibility = View.GONE
                    buttonok.visibility = View.VISIBLE
                }


            } else {
                println("datas not exist $id")

            }
        }
        .addOnFailureListener { exception ->
            println("datas failed exist $id")

        }
}




        }

    }

}