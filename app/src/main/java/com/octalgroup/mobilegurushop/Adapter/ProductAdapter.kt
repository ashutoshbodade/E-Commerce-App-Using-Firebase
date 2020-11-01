package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Database.CartDataSource
import com.octalgroup.mobilegurushop.Database.CartDatabase
import com.octalgroup.mobilegurushop.Database.CartItem
import com.octalgroup.mobilegurushop.Database.LocalCartDataSource
import com.octalgroup.mobilegurushop.EventBus.CountCartEvent
import com.octalgroup.mobilegurushop.Model.ProductModel
import com.octalgroup.mobilegurushop.ProductView
import com.octalgroup.mobilegurushop.R
import com.octalgroup.mobilegurushop.userid
import com.octalgroup.mobilegurushop.userno
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_product.view.*

class ProductAdapter(var c: Context, var list: ArrayList<ProductModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, Filterable {

    val searchableList = ArrayList<ProductModel>()

    override fun onClick(view: View) {
        Toast.makeText(c, "hello", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_product, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private val compositeDisposable:CompositeDisposable
    private val cartDataSource:CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(c).cartDAO())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(
            list[position].id,
            list[position].name.toString(),
            list[position].price.toString(),
            list[position].qty.toString(),
            list[position].unit.toString(),
            list[position].image.toString(),
            list[position].type.toString(),
            list[position].dtype.toString()
        )



        holder.vimage.setOnClickListener {
            val intent= Intent(c,ProductView::class.java)
            intent.putExtra("id",list[position].id.toString())
            intent.putExtra("name",list[position].name.toString())
            intent.putExtra("price",list[position].price.toString())
            intent.putExtra("qty",list[position].qty.toString())
            intent.putExtra("unit",list[position].unit.toString())
            intent.putExtra("image",list[position].image.toString())
            intent.putExtra("type",list[position].type.toString())
            intent.putExtra("description",list[position].description.toString())
            intent.putExtra("dtype",list[position].dtype.toString())
            c.startActivity(intent)
        }

        holder.buttoncart.setOnClickListener {
            if(list[position].dtype.toString()=="normal") {
            val cartItem = CartItem()
            cartItem.uid=userid()
            cartItem.userPhone=userno()
            cartItem.productId=list[position].id.toString()
            cartItem.productName=list[position].name.toString()
            cartItem.productImage=list[position].image.toString()
            cartItem.productPrice=list[position].price!!.toDouble()
            cartItem.productQuantity=1
            cartItem.productSize=list[position].qty!!.toString() +" "+list[position].unit.toString()
            cartItem.productType=list[position].type.toString()
            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(c,"Added to cart", Toast.LENGTH_SHORT).show()
                    holder.buttoncart.visibility=View.GONE
                    holder.buttonok.visibility=View.VISIBLE
                    org.greenrobot.eventbus.EventBus.getDefault().postSticky(CountCartEvent(true))
                },{
                    t: Throwable ->  Toast.makeText(c,"[Add to cart]"+t!!.message, Toast.LENGTH_SHORT).show()
                })) }
            else if (list[position].dtype.toString()=="train")
            {
                holder.buttoncart.visibility= View.INVISIBLE
                val docData = hashMapOf(
                    "uid" to userid().toString(),
                    "userphone" to userno().toString(),
                    "productid" to list[position].id.toInt(),
                    "productname" to list[position].name.toString(),
                    "productimage" to  list[position].image.toString(),
                    "productprice" to  list[position].price.toString(),
                    "productquantity" to 1,
                    "productsize" to list[position].qty.toString() +" "+list[position].unit.toString(),
                    "producttype" to  list[position].type.toString()
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(userid()).collection("traincarttemp")
                    .document(list[position].id.toString())
                    .set(docData as Map<String, Any>)
                    .addOnSuccessListener { documentReference ->
                    }
                    .addOnFailureListener { e ->
                    }
                    .addOnCompleteListener {
                        holder.buttoncart.visibility= View.GONE
                        holder.buttonok.visibility= View.VISIBLE
                        Toast.makeText(c,"Added to train cart",Toast.LENGTH_SHORT).show()
                        val saleref = db.collection("users").document(userid().toString())
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(saleref)
                            val newsale = snapshot.getDouble("traincart")!! + 1
                            transaction.update(saleref, "traincart", newsale)
                        }
                    }

            }
        }

        Glide.with(c).load(list[position].image.toString())
            .apply(RequestOptions.circleCropTransform()).into(holder.vimage)

    }

    fun onStop()
    {
        if (compositeDisposable!=null)
            compositeDisposable.clear()
    }


    inner class MyProjects(var view: View ) : RecyclerView.ViewHolder(view) {

        internal var minteger = 1
        var vcid: String? = null
        var vcprice: String? = null
        var vcqty: String? = null
        var vcunit: String? = null


        init {

            itemView.btnaddtocart.setOnClickListener {
                /*
                Toast.makeText(c, vcid, Toast.LENGTH_SHORT).show()
                itemView.btnaddtocart.visibility = View.GONE
                itemView.viewQtyNo.visibility = View.VISIBLE
                itemView.txtQtyNo.text = "1"
                visibility()
                val view2 = LayoutInflater.from(c).inflate(R.layout.activity_home, aqib, false)
                val vdata = view2.findViewById<TextView>(R.id.txtData)
                vdata.text="ashu"
                */
            }





            itemView.increase.setOnClickListener {
                increaseInteger()
                visibility()
                calculateqtyprice()
            }

            itemView.decrease.setOnClickListener {
                decreaseInteger()
                visibility()
                calculateqtyprice()
            }

        }


        fun visibility() {
            val dis = displayInteger.text.toString()
            val di = dis.toInt()
            if (di <= 0) {
                itemView.btnaddtocart.visibility = View.VISIBLE
                itemView.viewQtyNo.visibility = View.GONE
            }
        }

        fun increaseInteger() {
            minteger += 1
            display(minteger)

        }

        fun decreaseInteger() {
            minteger -= 1
            display(minteger)
        }


        private fun display(number: Int) {
            displayInteger.text = number.toString()
        }


        fun calculateqtyprice() {
            val qtyproduct = displayInteger.text.toString()
            val qtyproductint = qtyproduct.toInt()

            if (qtyproductint > 0) {

                val totalprice = qtyproductint * vcprice!!.toInt()
                vprice.text = totalprice.toString() + ".0 ₹"
                val totalqty = qtyproductint * vcqty!!.toInt()
                val totalqtyst = totalqty.toString()
                vqty.text = "$totalqtyst $vcunit"

            }


        }

        var displayInteger = view.findViewById<TextView>(R.id.txtQtyNo)
        var vname = view.findViewById<TextView>(R.id.txtName)
        var vprice = view.findViewById<TextView>(R.id.txtPrice)
        var vqty = view.findViewById<TextView>(R.id.txtQty)
        var vimage = view.findViewById<ImageView>(R.id.imgProduct)
        var buttoncart = view.findViewById<ImageView>(R.id.btnaddtocart)
        var buttonok = view.findViewById<ImageView>(R.id.btnok)


        fun bind(id: Int, name: String, price: String, qty: String, unit: String, image: String, type: String, vardtype:String) {
            vname.text = name.toString()
            vprice.text = price + ".0 ₹"
            vqty.text = "$qty $unit"
            vcid = id.toString()
            vcprice = price
            vcqty = qty
            vcunit = unit


            if(vardtype=="normal"){
                cartDataSource.getItemInCart(id.toString(),userid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object: SingleObserver<CartItem> {
                        override fun onSubscribe(d: Disposable) {
                        }
                        override fun onError(e: Throwable) {
                            buttoncart.visibility = View.VISIBLE
                            buttonok.visibility = View.GONE
                        }
                        override fun onSuccess(t: CartItem) {
                            buttoncart.visibility = View.GONE
                            buttonok.visibility = View.VISIBLE
                        }
                    })
            }
            else  if(vardtype=="train")
            {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(userid()).collection("traincarttemp").document(id.toString())
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



    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                searchableList.clear()
                if (constraint.isNullOrBlank())
                {
                    searchableList.addAll(list)
                }
                else
                {
                    val filterPattern = constraint.toString().trim { it <= ' ' }
                    for (item in 0..list.size) {
                        if (list[item].name!!.contains(filterPattern)) {
                            searchableList.add(list[item])
                            Toast.makeText(c,filterPattern+"found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                return filterResults.also {
                    it.values = searchableList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
               if (searchableList.isNullOrEmpty())
               {
                   Toast.makeText(c,"null $constraint", Toast.LENGTH_SHORT).show()
               }

                else{
                   Toast.makeText(c,"found $constraint", Toast.LENGTH_SHORT).show()
               }
                //onNothingFound?.invoke()
                notifyDataSetChanged()
            }

          }
        }

}