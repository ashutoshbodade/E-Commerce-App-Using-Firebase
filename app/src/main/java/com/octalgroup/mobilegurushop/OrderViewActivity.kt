package com.octalgroup.mobilegurushop

import com.octalgroup.mobilegurushop.Adapter.OrderProductAdapter
import com.octalgroup.mobilegurushop.Model.OrderProductModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_order_view.*

class OrderViewActivity : AppCompatActivity() {

    var orderstatus:String?=null
    var docid:String?=null
    var orderdate:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_view)
        val bundle: Bundle?= intent.extras



        val deliveryaddress =bundle!!.getString("deliveryaddress")
        val deliverytype =bundle.getString("deliverytype")

        orderdate =bundle.getString("orderdate")
        orderstatus =bundle.getString("orderstatus")

        val ordertime =bundle.getString("ordertime")
        val paymentstatus=bundle.getString("paymentstatus")

        val totalprice=bundle.getString("totalprice")
        val confirmdate=bundle.getString("confirmdate")

        val confirmtime=bundle.getString("confirmtime")
        val intransitdate=bundle.getString("intransitdate")

        val intransittime=bundle.getString("intransittime")
        val deliverydate=bundle.getString("deliverydate")

        val deliverytime=bundle.getString("deliverytime")
        val cancleddate=bundle.getString("cancleddate")

        val cancledtime=bundle.getString("cancledtime")
        val cancledreason=bundle.getString("cancledreason")

        val cancledby=bundle.getString("cancledby")
        docid=bundle.getString("docid")

        val paymentmode=bundle.getString("paymentmode")
        val orderid = bundle.getString("orderid")

        this.setTitle("ORDER #"+orderid)

        val userphone=bundle.getString("userphone")

        val supportnumber=bundle.getString("supportnumber")
        txtSupport.text=supportnumber.toString()

        val did=bundle.getString("did")
        val dname=bundle.getString("dname")
        val dimage=bundle.getString("dimage")
        val dnumber=bundle.getString("dnumber")

        val instructions=bundle.getString("instructions")

        val trainno=bundle.getString("trainno")
        val trainname=bundle.getString("trainname")


        txtInstructions.text="Address : "+instructions

        callaction.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", supportnumber, null));
            startActivity(intent);
        }

        val v_orderstatusnext=findViewById<TextView>(R.id.txtOrderStatusNext)//change

        val v_orderstatus=findViewById<TextView>(R.id.txtOrderStatus)

        val v_deliveryaddress=findViewById<TextView>(R.id.txtDeliveryAddress)
        val v_deliverytype=findViewById<TextView>(R.id.txtDeliveryType)

        val v_paymentmode=findViewById<TextView>(R.id.txtPaymentMode)
        val v_paymentstatus=findViewById<TextView>(R.id.txtPaymentStatus)
        val v_totalprice=findViewById<TextView>(R.id.txtTotalPrice)

        val v_orderdate=findViewById<TextView>(R.id.txtOrderDate)//ordertime
        val v_confirmdate=findViewById<TextView>(R.id.txtConfirmedDate)//confirmtime
        val v_intransitdate=findViewById<TextView>(R.id.txtInTransitDate)//intransittime
        val v_deliverydate=findViewById<TextView>(R.id.txtDeliveredDate)//deliverytime

        val v_statusbooked=findViewById<TextView>(R.id.txtStatusBooked)
        val v_statusconfirmed=findViewById<TextView>(R.id.txtStatusConfirmed)
        val v_statusintransit=findViewById<TextView>(R.id.txtStatusInTransit)
        val v_statusdelivered=findViewById<TextView>(R.id.txtStatusDelivered)

        val v_orderstatusother=findViewById<TextView>(R.id.txtOrderStatusOther)

        val v_progressbar=findViewById<ProgressBar>(R.id.progressBar)

        val v_viewStatus=findViewById<LinearLayout>(R.id.viewStatus)

        val v_rvordersproducts=findViewById<RecyclerView>(R.id.rvOrdersProducts)

        db.collection("orders")
            .document(docid.toString())
            .collection("orderitems")
            .get()
            .addOnSuccessListener { documents ->
                val productsList = ArrayList<OrderProductModel>()
                for (document in documents) {
                    if (document != null)
                    {
                        val products = document.toObject(OrderProductModel::class.java)
                        productsList.add(products)
                    }
                }
                val adp = OrderProductAdapter(this, productsList)
                v_rvordersproducts.layoutManager = LinearLayoutManager(this)
                v_rvordersproducts.adapter = adp
                view.visibility=View.VISIBLE
                load.visibility=View.GONE
            }

            v_deliverytype.text=deliverytype
            v_deliveryaddress.text=deliveryaddress


        v_paymentmode.text=paymentmode
        v_paymentstatus.text=paymentstatus
        v_totalprice.text="$totalprice.0 ₹"

        dView.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", dnumber, null));
            startActivity(intent);
        }

        if(orderstatus=="Booked"){

            v_progressbar.progress=15
            v_progressbar.getProgressDrawable().setColorFilter(
                Color.parseColor("#f1c40f"), android.graphics.PorterDuff.Mode.SRC_IN)

            v_orderstatus.text="Booked"
            v_orderstatus.setTextColor(Color.parseColor("#f1c40f"))
            v_orderstatusnext.text="Waiting For Confirmation"
            v_orderstatusnext.setTextColor(Color.parseColor("#f1c40f"))
            v_statusbooked.setTextColor(Color.parseColor("#f1c40f"))
            v_orderdate.setTextColor(Color.parseColor("#f1c40f"))
            v_orderdate.visibility= View.VISIBLE
            v_orderdate.text="$orderdate\n$ordertime"
        }
        else if(orderstatus=="Confirmed")
        {
            v_progressbar.progress=35
            v_progressbar.getProgressDrawable().setColorFilter(
                Color.parseColor("#f39c12"), android.graphics.PorterDuff.Mode.SRC_IN)
            // v_orderid.setTextColor(Color.parseColor("#f39c12"))
            v_orderstatus.text="Confirmed"
            v_orderstatus.setTextColor(Color.parseColor("#f39c12"))
            v_orderstatusnext.text="Packaging your product"
            v_orderstatusnext.setTextColor(Color.parseColor("#f39c12"))
            v_statusconfirmed.setTextColor(Color.parseColor("#f39c12"))
            v_orderdate.visibility= View.VISIBLE
            v_orderdate.text="$orderdate\n$ordertime"
            v_confirmdate.setTextColor(Color.parseColor("#f39c12"))
            v_confirmdate.visibility= View.VISIBLE
            v_confirmdate.text="$confirmdate\n$confirmtime"

        }
        else if(orderstatus=="InTransit")
        {
            v_progressbar.progress=65
            v_progressbar.getProgressDrawable().setColorFilter(
                Color.parseColor("#2ecc71"), android.graphics.PorterDuff.Mode.SRC_IN)


            v_orderstatus.text="In Transit"
            v_orderstatus.setTextColor(Color.parseColor("#2ecc71"))

                dView.visibility=View.VISIBLE
                txtDname.text=dname
                txtDnumber.text="Call ($dnumber)"
                Glide.with(this).load(dimage).apply(RequestOptions.circleCropTransform()).into(imgd)
                v_orderstatusnext.text="On the way"

            v_orderstatusnext.setTextColor(Color.parseColor("#2ecc71"))
            v_statusintransit.setTextColor(Color.parseColor("#2ecc71"))

            v_orderdate.visibility= View.VISIBLE
            v_orderdate.text="$orderdate\n$ordertime"
            v_confirmdate.visibility= View.VISIBLE
            v_confirmdate.text="$confirmdate\n$confirmtime"
            v_intransitdate.setTextColor(Color.parseColor("#2ecc71"))
            v_intransitdate.visibility= View.VISIBLE
            v_intransitdate.text="$intransitdate\n$intransittime"

        }
        else if(orderstatus=="Delivered")
        {
            v_progressbar.progress=100
            v_progressbar.getProgressDrawable().setColorFilter(
                Color.parseColor("#16a085"), android.graphics.PorterDuff.Mode.SRC_IN)
            // v_orderid.setTextColor(Color.parseColor("#16a085"))
            v_orderstatusother.visibility= View.VISIBLE
            v_orderstatusother.text="Order Received on $deliverydate"
            v_orderstatusother.setTextColor(Color.parseColor("#16a085"))
            v_orderstatus.visibility= View.GONE
            v_orderstatusnext.visibility= View.GONE
            v_statusdelivered.setTextColor(Color.parseColor("#16a085"))
            v_orderdate.visibility= View.VISIBLE
            v_orderdate.text="$orderdate\n$ordertime"
            v_confirmdate.visibility= View.VISIBLE
            v_confirmdate.text="$confirmdate\n$confirmtime"
            v_intransitdate.visibility= View.VISIBLE
            v_intransitdate.text="$intransitdate\n$intransittime"
            v_deliverydate.visibility= View.VISIBLE
            v_deliverydate.text="$deliverydate\n$deliverytime"
            v_deliverydate.setTextColor(Color.parseColor("#16a085"))
        }
        else if(orderstatus=="Cancled")
        {
            v_orderstatusnext.visibility= View.GONE
            v_orderstatus.visibility= View.GONE
            v_orderstatusother.visibility= View.VISIBLE
            v_orderstatusother.setTextColor(Color.parseColor("#c0392b"))
            v_orderstatusother.text="Cancled by $cancledby on $cancleddate at $cancledtime due to $cancledreason"
            v_paymentmode.visibility= View.GONE
            v_paymentstatus.visibility= View.GONE
            v_totalprice.visibility= View.GONE
            v_viewStatus.visibility= View.GONE
            v_progressbar.visibility= View.GONE
        }

    }

    @SuppressLint("ResourceType")
    private fun showAlertWithTextInputLayout(context: Context) {
        val textInputLayout = TextInputLayout(context)
        textInputLayout.setPadding( resources.getDimensionPixelOffset(R.dimen._5sdp), // if you look at android alert_dialog.xml, you will see the message textview have margin 14dp and padding 5dp. This is the reason why I use 19 here
            0,
            resources.getDimensionPixelOffset(R.dimen._5sdp),
            0)

        val input = EditText(context)
        textInputLayout.hint = ""
        textInputLayout.addView(input)

        val alert = AlertDialog.Builder(context)
            .setTitle("Cancle order ")
            .setView(textInputLayout)
            .setMessage("Please enter valid reason")
            .setPositiveButton("Submit") { dialog, _ ->
                // do some thing with input.text

                if (input.text.toString()==""||input.text.toString()==" "||input.text.toString()=="  "){
                    Toast.makeText(context,"Please Enter Valid Reason", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    if(orderstatus=="Booked"){
                        val bookedadd = db.collection("stats").document("stats"+ orderdate)
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(bookedadd)

                            val newOrderid = snapshot.getDouble("booked")!! - 1
                            transaction.update(bookedadd, "booked", newOrderid)

                            val newconfirmedcount = snapshot.getDouble("cancled")!! + 1
                            transaction.update(bookedadd, "cancled", newconfirmedcount)

                        }
                        val sfDocRef = db.collection("orders").document(docid.toString())
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(sfDocRef)
                            transaction.update(sfDocRef, "orderstatus", "Cancled")
                            transaction.update(sfDocRef, "cancledreason", input.text.toString())
                            transaction.update(sfDocRef, "cancleddate", todayDate().toString())
                            transaction.update(sfDocRef, "cancledtime", Time().toString())
                            transaction.update(sfDocRef, "cancledby","USER")
                        }

                    }
                    else
                    {
                        Toast.makeText(context,"Sorry you can't cancle now",Toast.LENGTH_SHORT).show()
                    }

                }

                dialog.cancel()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }
}
