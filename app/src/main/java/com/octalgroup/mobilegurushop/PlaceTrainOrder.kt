package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.CartTrainAdapter
import com.octalgroup.mobilegurushop.Model.TrainCartModel
import android.app.Activity
import android.content.Intent
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

    private var spicelevel:String=""
    private var finaltotalprice:Int=0

    private var points: Number=0
    private var pointsused: Number=0
    var inrupee:Double=0.0
    var minusprice:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_train_order)
        val bundle:Bundle?=intent.extras
        trainno =bundle!!.getString("trainno")
        trainname =bundle.getString("name")

        check()

        btnCOD.setOnClickListener {
            rvTypesProgressBar.visibility = View.VISIBLE
            rvTProducts.visibility = View.GONE
            layout.visibility=View.GONE
            placeorder("COD", "Unnpaid","")
        }

        btnOnlinePay.setOnClickListener {
            rvTypesProgressBar.visibility = View.VISIBLE
            rvTProducts.visibility = View.GONE
            layout.visibility=View.GONE
            startPayment(finaltotalprice)
        }

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
                "deliveryaddress" to "",
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
                "deliverytype" to "Train",
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
                "dnumber" to "",
                "trainno" to trainno.toString(),
                "trainname" to trainname.toString(),
                "spicelevel" to spicelevel,
                "pointsused" to pointsused.toInt(),
                "pointsprice" to minusprice.toInt(),
                "pointsearned" to 0
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
                "productsize" to productsList[x].productsize.toString(),
                "producttype" to productsList[x].producttype.toString(),
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
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
        Toasty.success(this,"Order Placed Sucessfully", Toast.LENGTH_LONG, true).show()

    }

    private fun deletefromcart(cartitem:String){
        db.collection("users").document(userid()).collection("traincarttemp").document(cartitem)
            .delete()
            .addOnSuccessListener {

            }

    }

    private fun bookedsave(){
        val bookedadd = db.collection("stats").document("stats"+ todayDate())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(bookedadd)
            val newOrderid = snapshot.getDouble("booked")!! + 1
            transaction.update(bookedadd, "booked", newOrderid)
        }

        if(redeempoints.isChecked){
            val userupdate = db.collection("users").document(userid())
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userupdate)
                //val newOrderid = snapshot.getDouble("booked")!! + 1
                transaction.update(userupdate, "points",0)
            }
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

        val docRef = db.collection("numbers").document("statusontrainfood")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val isopen:Boolean=snapshot["isopen"] as Boolean
                if (isopen==true){
                    loaddata()
                    db.collection("numbers").document("data")
                        .get()
                        .addOnSuccessListener { document ->
                            supportnumber = document["supportno"] as String
                            val traincod: Boolean = document["traincod"] as Boolean

                            if(traincod==true)
                            {
                                btnCOD.visibility=View.VISIBLE
                            }
                            else
                            {
                                btnCOD.visibility=View.GONE
                            }

                        }
                }
                else
                {
                    rvTypesProgressBar.visibility = View.GONE
                    rvTProducts.visibility = View.GONE
                    rvTypesProgressBar.visibility=View.GONE
                    layout.visibility=View.GONE
                    close.visibility=View.VISIBLE
                }
            }
        }


    }

    private fun loaddata(){
        txtTrainName.text=trainname
        txtTrainNumber.text=trainno
        layout.visibility=View.VISIBLE
        close.visibility=View.GONE

        db.collection("users").document(userid()).collection("traincarttemp")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e)
                    // Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                productsList.clear()
                cartitems.clear()
                totalprice=0
                loadpoints()

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

    private fun loadpoints(){
        db.collection("numbers").document("points")
            .get()
            .addOnSuccessListener { document ->

                val pointsneeded: Number = document["pointredeemlimit"] as Number
                val ppp: Number = document["ppp"] as Number

                db.collection("users").document(userid())
                    .get()
                    .addOnSuccessListener { doc ->

                        points = doc["points"] as Number

                        if(points.toDouble() > pointsneeded.toDouble()){

                            redeempoints.visibility=View.VISIBLE
                            inrupee= ppp.toDouble() * points.toDouble()
                            redeempoints.text="Redeem Your Food Points $points \nand get discount of ${inrupee.toInt()} ₹"

                        }
                    }
            }
    }

    private fun resetcartcount(){

        val saleref = db.collection("users").document(userid)
        db.runTransaction { transaction ->
            transaction.update(saleref, "traincart", 0)
        }
    }

    fun redeempoints(view: View) {
        if(redeempoints.isChecked){
            pointsused= points
            minusprice= inrupee
            pricecalculate()
        }
        else
        {
            pointsused=0
            minusprice=0.0
            pricecalculate()
        }
    }

    fun rbclickspicy(view: View) {

        val radio2: RadioButton = findViewById(rgSpiceLevel.checkedRadioButtonId)

        if(radio2.text=="Low")
        {
            spicelevel="Low"
            Toast.makeText(this,spicelevel, Toast.LENGTH_SHORT).show()
        }
        else if(radio2.text=="Medium")
        {
            spicelevel="Medium"
            Toast.makeText(this,spicelevel, Toast.LENGTH_SHORT).show()
        }
        else if(radio2.text=="Spicy")
        {
            spicelevel="Spicy"
            Toast.makeText(this,spicelevel, Toast.LENGTH_SHORT).show()
        }
        else if(radio2.text=="Extra Spicy")
        {
            spicelevel="Extra Spicy"
            Toast.makeText(this,spicelevel, Toast.LENGTH_SHORT).show()
        }
        else
        {
            spicelevel="Not Selected"
            Toast.makeText(this,spicelevel, Toast.LENGTH_SHORT).show()
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
            options.put("name","Veera Da Dhaba")
            options.put("description", com.octalgroup.mobilegurushop.userid().toString())
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency","INR")
            options.put("amount",amount*100)

            val prefill = JSONObject()
            //prefill.put("email","test@razorpay.com")
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
