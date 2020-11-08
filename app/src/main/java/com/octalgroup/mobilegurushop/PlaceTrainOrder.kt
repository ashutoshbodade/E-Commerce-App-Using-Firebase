package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.CartTrainAdapter
import com.octalgroup.mobilegurushop.Model.TrainCartModel
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_place_train_order.*
import kotlinx.android.synthetic.main.activity_place_train_order.txtInstructions
import org.json.JSONObject

class PlaceTrainOrder : AppCompatActivity() , PaymentResultListener {

    val cartitems = ArrayList<Int>()
    val productsList = ArrayList<TrainCartModel>()
    val adp = CartTrainAdapter(this, productsList)
    var totalprice:Int=0
    var trainno:String?=null
    var trainname:String?=null

    private var supportnumber:String?=null

    private var userid:String=userid()
    private var userphone:String=userno()
    private var username:String= com.octalgroup.mobilegurushop.username()

    private var orderstatus:String="Booked"

    private var finaltotalprice:Int=0

    var minusprice:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_train_order)
        check()


        val bundle: Bundle?= intent.extras
        val deliveryaddress =  bundle!!.getString("deliveryaddress")

        btnCOD.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure want to place order.")
            builder.setMessage("Payment method COD")

            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton("Yes") { dialog, which ->
                rvTypesProgressBar.visibility = View.VISIBLE
                rvTProducts.visibility = View.GONE
                layout.visibility=View.GONE
                placeorder("COD", "Unnpaid","")
            }

            builder.setNegativeButton("No") { dialog, which ->

            }

            builder.show()


        }

        btnOnlinePay.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure want to place order.")
            builder.setMessage("Payment method Online")

            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton("Yes") { dialog, which ->


                rvTypesProgressBar.visibility = View.VISIBLE
                rvTProducts.visibility = View.GONE
                layout.visibility=View.GONE
                startPayment(finaltotalprice)
            }

            builder.setNegativeButton("No") { dialog, which ->

            }

            builder.show()

        }


        txtAddress.text = deliveryaddress

    }

    private fun placeorder(paymentmode:String, paymentstatus:String, paymentid:String){
        var orderid:Int=0
        val sfDocRef = db.collection("numbers").document("orderid")
        db.runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)
            orderid = snapshot.getDouble("orderid")!!.toInt() + 1
            transaction.update(sfDocRef, "orderid", orderid)
        }
         .addOnSuccessListener {

            val docorderID = hashMapOf(
                "orderid" to orderid.toInt(),
                "userid" to userid.toString(),
                "userphone" to userphone.toString(),
                "deliveryaddress" to  txtAddress.text,
                "supportnumber" to supportnumber.toString(),
                "orderstatus" to orderstatus.toString(),
                "paymentmode" to paymentmode.toString(),
                "paymentstatus" to paymentstatus.toString(),
                "paymentid" to paymentid.toString(),
                "orderdate" to todayDate().toString(),
                "ordertime" to Time().toString(),
                "totalprice" to finaltotalprice.toString(),
                "productstotal" to totalprice.toString(),
                "deliverycharges" to "",
                "username" to username.toString(),
                "date" to FieldValue.serverTimestamp(),
                "instructions" to txtInstructions.text.toString(),
                "confirmdate" to "",
                "confirmtime" to "",
                "intransitdate" to "",
                "intransittime" to "",
                "deliverydate" to "",
                "deliverytime" to "",
                "cancleddate" to "",
                "cancledtime" to "",
                "cancledreason" to "",
                "cancledby" to "",
                "did" to "",
                "dname" to "",
                "dimage" to "",
                "dnumber" to ""
            )

            db.collection("orders").document("order"+orderid.toString())
                .set(docorderID as Map<String, Any>)
                .addOnSuccessListener { documentReference ->
                    bookedsave()
                    saveorderitems(orderid)
                }

        }

    }

    private fun saveorderitems(oid:Int){

        for (x in 0 until productsList.size) {
            val productprice: Int = productsList[x].productprice!!.toInt()
            val productquantity: Int = productsList[x].productquantity.toInt()
            val producttotal: Int = productprice * productquantity

            val docData = hashMapOf(
                "orderid" to oid,
                "uid" to userid.toString(),
                "userphone" to userphone.toString(),
                "productid" to productsList[x].productid.toInt(),
                "productname" to productsList[x].productname.toString(),
                "productimage" to productsList[x].productimage.toString(),
                "productprice" to productsList[x].productprice.toString(),
                "productquantity" to productsList[x].productquantity.toString(),
                "productcategory" to productsList[x].productcategory.toString(),
                "productsubcategory" to productsList[x].productsubcategory.toString(),
                "producttotal" to producttotal.toString()
            )

            db.collection("orders")
                .document("order"+oid.toString())
                .collection("orderitems")
                .add(docData as Map<String, Any>)
                .addOnSuccessListener { documentReference ->
                    productplacedcount(productsList[x].productid.toString(), productsList[x].productquantity.toInt())

                }
                .addOnFailureListener { e ->
                }
        }

        resetcartcount()
        startActivity(Intent(this, OrdersViewActivity::class.java))
        finish()
        Toasty.success(this,"Order Placed Sucessfully", Toast.LENGTH_LONG, true).show()

    }

    private fun deletefromcart(cartitem:String){
        db.collection("users").document(userid()).collection("carttemp").document(cartitem)
            .delete()
            .addOnSuccessListener {
            }
    }

    private fun bookedsave(){
        val bookedadd = db.collection("numbers").document("stats")
        db.runTransaction { transaction ->
            val snapshot = transaction.get(bookedadd)
            val newOrderid = snapshot.getDouble("booked")!! + 1
            transaction.update(bookedadd, "booked", newOrderid)
        }


    }

    private fun productplacedcount(productid:String, qty:Int){
        val saleref = db.collection("products").document(productid.toInt().toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(saleref)
            val newsale = snapshot.getDouble("sale")!! + qty
            transaction.update(saleref, "sale", newsale)
        }.addOnSuccessListener {
            deletefromcart(productid.toString())
        }
    }

    private fun check(){
                    loaddata()
                    db.collection("numbers").document("data")
                        .get()
                        .addOnSuccessListener { document ->
                            supportnumber = document["supportno"] as String

                            val cod: Boolean = document["cod"] as Boolean

                            if(cod==true)
                            {
                                btnCOD.visibility=View.VISIBLE
                            }
                            else
                            {
                                btnCOD.visibility=View.GONE
                            }

                        }
    }

    private fun loaddata(){

        layout.visibility=View.VISIBLE
        close.visibility=View.GONE


        db.collection("users").document(userid()).collection("carttemp")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e)
                    // Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                productsList.clear()
                cartitems.clear()
                totalprice=0


                rvTypesProgressBar.visibility = View.VISIBLE
                for (document in value!!) {
                    if (document != null) {
                        val products = document.toObject(TrainCartModel::class.java)
                        productsList.add(products)
                        val price:String=document["productprice"] as String
                        val qty:Number=document["productquantity"] as Number
                        cartitems.add(price.toInt()*qty.toInt())
                    }
                }
                rvTProducts.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false )
                rvTProducts.adapter = adp
                rvTProducts.visibility = View.VISIBLE
                rvTypesProgressBar.visibility = View.GONE

                for (items in cartitems){
                    totalprice = totalprice + items
                }
                pricecalculate()

            }
    }

    private fun pricecalculate(){
        finaltotalprice  = totalprice-minusprice.toInt()
        txtTotal.text="To Pay: ₹ "+finaltotalprice.toString()
    }



    private fun resetcartcount(){
        val saleref = db.collection("users").document(userid)
        db.runTransaction { transaction ->
            transaction.update(saleref, "cart", 0)
        }


    }





    override fun onPaymentError(errorCode: Int, response: String?) {
        rvTypesProgressBar.visibility = View.VISIBLE
        rvTProducts.visibility = View.GONE
        layout.visibility=View.GONE
        try{

            Toasty.error(this,"Payment failed $errorCode \n $response",Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toasty.error(this,"Payment error $e ",Toast.LENGTH_LONG).show()

        }

    }

    override fun onPaymentSuccess(tranid: String?) {
        try {
            placeorder("RazorPay", "Paid",tranid!!)
        }
        catch (e: Exception){
            Toasty.error(this,"Payment error $e ",Toast.LENGTH_LONG).show()
        }

    }

    private fun startPayment(amount:Int) {

        val activity: Activity = this
        val co = Checkout()

        try {

            val options = JSONObject()
            options.put("name","Mobile Guru Shop")
            options.put("description", "Pay")
            //You can omit the image option to fetch the image from dashboard
          //  options.put("image","https://firebasestorage.googleapis.com/v0/b/mobile-guru-shop.appspot.com/o/Banners%2Fmobileguru.PNG?alt=media&token=b4e8f61f-ceb6-499f-b7cf-2a4c40177dbf")
            options.put("currency","INR")
            options.put("amount",amount*100)

            val prefill = JSONObject()
          //  prefill.put("email","test@razorpay.com")
            prefill.put("contact", userno().toString())
            options.put("prefill",prefill)
            co.open(activity,options)
        }
        catch (e: Exception){
            Toasty.error(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

}
