package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Model.OrderModel
import com.octalgroup.mobilegurushop.OrderViewActivity
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(var c: Context, var list:ArrayList<OrderModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    override fun onClick(view: View) {
        Toast.makeText(c,"hello", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_orders, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(list[position].orderid,list[position].orderstatus.toString(),list[position].deliverytype.toString(),list[position].deliveryaddress.toString(),
            list[position].cancleddate.toString(),list[position].cancledtime.toString(),list[position].cancledreason.toString(),list[position].cancledby.toString(),
            list[position].orderdate.toString(),list[position].ordertime.toString(),list[position].confirmdate.toString(),list[position].confirmtime.toString(),
            list[position].intransitdate.toString(),list[position].intransittime.toString(),list[position].deliverydate.toString(),list[position].deliverytime.toString(),
            list[position].paymentmode.toString(),list[position].paymentstatus.toString(),list[position].totalprice.toString(),list[position].docid.toString(),
            list[position].trainno.toString(), list[position].trainname.toString())

       // val documentId: String = getSnapshots().getSnapshot(position).getId()
       /* var expandedPosition = -1

        holder.vtdropdown.setOnClickListener {
            if (expandedPosition == 1){
                holder.v_rvordersproducts.visibility=View.VISIBLE
                expandedPosition = -1
                holder.vtdropdownup.visibility=View.GONE
                holder.vtdropdowndown.visibility=View.VISIBLE
            }
            else
            {
                holder.v_rvordersproducts.visibility=View.GONE
                expandedPosition = 1
                holder.vtdropdownup.visibility=View.VISIBLE
                holder.vtdropdowndown.visibility=View.GONE

            }
        }*/


        holder.view.setOnClickListener {
            val intent= Intent(c,OrderViewActivity::class.java)
            intent.putExtra("orderid",list[position].orderid.toString())
            intent.putExtra("orderstatus",list[position].orderstatus.toString())

            intent.putExtra("deliverytype",list[position].deliverytype.toString())
            intent.putExtra("deliveryaddress",list[position].deliveryaddress.toString())

            intent.putExtra("cancleddate",list[position].cancleddate.toString())
            intent.putExtra("cancledtime",list[position].cancledtime.toString())
            intent.putExtra("cancledreason",list[position].cancledreason.toString())
            intent.putExtra("cancledby",list[position].cancledby.toString())

            intent.putExtra("orderdate",list[position].orderdate.toString())
            intent.putExtra("ordertime",list[position].ordertime.toString())

            intent.putExtra("confirmdate",list[position].confirmdate.toString())
            intent.putExtra("confirmtime",list[position].confirmtime.toString())

            intent.putExtra("intransitdate",list[position].intransitdate.toString())
            intent.putExtra("intransittime",list[position].intransittime.toString())

            intent.putExtra("deliverydate",list[position].deliverydate.toString())
            intent.putExtra("deliverytime",list[position].deliverytime.toString())

            intent.putExtra("paymentmode",list[position].paymentmode.toString())
            intent.putExtra("paymentstatus",list[position].paymentstatus.toString())

            intent.putExtra("totalprice",list[position].totalprice.toString())

            intent.putExtra("docid",list[position].docid.toString())


            intent.putExtra("userphone",list[position].userphone.toString())
            intent.putExtra("supportnumber",list[position].supportnumber.toString())

            intent.putExtra("did",list[position].did.toString())
            intent.putExtra("dname",list[position].dname.toString())

            intent.putExtra("dimage",list[position].dimage.toString())
            intent.putExtra("dnumber",list[position].dnumber.toString())

            intent.putExtra("instructions",list[position].instructions.toString())

            intent.putExtra("trainno",list[position].trainno.toString())
            intent.putExtra("trainname",list[position].trainname.toString())



            c.startActivity(intent)
        }

    }



    inner class MyProjects(var view: View): RecyclerView.ViewHolder(view){



        var v_orderstatusnext=view.findViewById<TextView>(R.id.txtOrderStatusNext)//change

        var v_orderid=view.findViewById<TextView>(R.id.txtOrderId)
        var v_orderstatus=view.findViewById<TextView>(R.id.txtOrderStatus)

        var v_deliveryaddress=view.findViewById<TextView>(R.id.txtDeliveryAddress)
        var v_deliverytype=view.findViewById<TextView>(R.id.txtDeliveryType)

        var v_paymentmode=view.findViewById<TextView>(R.id.txtPaymentMode)
        var v_paymentstatus=view.findViewById<TextView>(R.id.txtPaymentStatus)
        var v_totalprice=view.findViewById<TextView>(R.id.txtTotalPrice)

        var v_orderdate=view.findViewById<TextView>(R.id.txtOrderDate)//ordertime
        var v_confirmdate=view.findViewById<TextView>(R.id.txtConfirmedDate)//confirmtime
        var v_intransitdate=view.findViewById<TextView>(R.id.txtInTransitDate)//intransittime
        var v_deliverydate=view.findViewById<TextView>(R.id.txtDeliveredDate)//deliverytime

        var v_statusbooked=view.findViewById<TextView>(R.id.txtStatusBooked)
        var v_statusconfirmed=view.findViewById<TextView>(R.id.txtStatusConfirmed)
        var v_statusintransit=view.findViewById<TextView>(R.id.txtStatusInTransit)
        var v_statusdelivered=view.findViewById<TextView>(R.id.txtStatusDelivered)

        var v_orderstatusother=view.findViewById<TextView>(R.id.txtOrderStatusOther)

        var v_progressbar=view.findViewById<ProgressBar>(R.id.progressBar)

        var v_viewStatus=view.findViewById<LinearLayout>(R.id.viewStatus)


       // var v_rvordersproducts=view.findViewById<RecyclerView>(R.id.rvOrdersProducts)
       // var v_rvorderproductsprogressbar=view.findViewById<ProgressBar>(R.id.rvOrderProductsProgressBar)

        var vtdropdown=view.findViewById<TableLayout>(R.id.dropdown)
        //var vtdropdownup=view.findViewById<ImageView>(R.id.dropdownup)
        //var vtdropdowndown=view.findViewById<ImageView>(R.id.dropdowndown)



        fun bind(varorderid: Int, varorderstatus:String, vardeliverytype:String, vardeliveryaddress:String,
                 varcancledate:String, varcancletime:String, varcanclereason:String, varcancleby:String,
                 varorderdate:String, varordertime:String, varconfirmdate:String, varconfirmtime:String,
                 varintransitdate:String, varintransittime:String, vardeliverydate:String, vardeliverytime:String,
                 varpaymentmode:String, varpaymentstatus:String, vartotalprice:String,vardocid:String,
                 vartrainno:String,vartrainname:String)
        {
            /*val db = FirebaseFirestore.getInstance()
            db.collection("orders")
                .document(vardocid)
                .collection("orderitems")
                .get()
                .addOnSuccessListener { documents ->
                    val productsList = ArrayList<OrderProductModel>()
                    for (document in documents) {
                        if (document != null) {
                            val products = document.toObject(OrderProductModel::class.java)
                            productsList.add(products)
                        }
                    }
                    val adp = OrderProductAdapter(c, productsList)
                    v_rvordersproducts.layoutManager = LinearLayoutManager(c)
                    v_rvordersproducts.adapter = adp
                    v_rvorderproductsprogressbar.visibility=View.GONE

                }*/


            v_orderid.text="ORDER #"+varorderid.toString()


            if (vardeliverytype=="Train"){
                v_deliverytype.text=vardeliverytype
                v_deliveryaddress.text="$vartrainno $vartrainname @Akola Station"
            }
            else
            {
                v_deliverytype.text=vardeliverytype
                v_deliveryaddress.text=vardeliveryaddress
            }


            v_paymentmode.text=varpaymentmode
            v_paymentstatus.text=varpaymentstatus
            v_totalprice.text="TOTAL AMOUNT: $vartotalprice.0 ₹"

            /*if(varpaymentstatus=="Unpaid"){
                v_paymentmode.setTextColor(Color.parseColor("#c0392b"))
                v_paymentstatus.setTextColor(Color.parseColor("#c0392b"))
                v_totalprice.setTextColor(Color.parseColor("#c0392b"))
            }*/


            if(varorderstatus=="Booked"){

                v_progressbar.progress=15
                v_progressbar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#f1c40f"), android.graphics.PorterDuff.Mode.SRC_IN)

                //v_orderid.setTextColor(Color.parseColor("#f1c40f"))

                v_orderstatus.text="Booked"
                v_orderstatus.setTextColor(Color.parseColor("#f1c40f"))
                v_orderstatusnext.text="Waiting For Confirmation"
                v_orderstatusnext.setTextColor(Color.parseColor("#f1c40f"))
                v_statusbooked.setTextColor(Color.parseColor("#f1c40f"))
                v_orderdate.setTextColor(Color.parseColor("#f1c40f"))
                v_orderdate.visibility=View.VISIBLE
                v_orderdate.text="$varorderdate\n$varordertime"
            }
            else if(varorderstatus=="Confirmed")
            {
                v_progressbar.progress=35
                v_progressbar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#f39c12"), android.graphics.PorterDuff.Mode.SRC_IN)
               // v_orderid.setTextColor(Color.parseColor("#f39c12"))
                v_orderstatus.text="Confirmed"
                v_orderstatus.setTextColor(Color.parseColor("#f39c12"))

                v_orderstatusnext.text="Preparing Your Food"

                v_orderstatusnext.setTextColor(Color.parseColor("#f39c12"))
                v_statusconfirmed.setTextColor(Color.parseColor("#f39c12"))
                v_orderdate.visibility=View.VISIBLE
                v_orderdate.text="$varorderdate\n$varordertime"
                v_confirmdate.setTextColor(Color.parseColor("#f39c12"))
                v_confirmdate.visibility=View.VISIBLE
                v_confirmdate.text="$varconfirmdate\n$varconfirmtime"

            }
            else if(varorderstatus=="InTransit")
            {
                v_progressbar.progress=65
                v_progressbar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#2ecc71"), android.graphics.PorterDuff.Mode.SRC_IN)
               // v_orderid.setTextColor(Color.parseColor("#2ecc71"))
                v_orderstatus.text="Out For Delivery"
                v_orderstatus.setTextColor(Color.parseColor("#2ecc71"))
                if (vardeliverytype=="Pickup"){
                    v_orderstatusnext.text="Waiting for you to pickup"
                    v_deliverytype.text="Pickup from"
                }
                else
                {
                    v_orderstatusnext.text="On the way to delivery location"
                }
                v_orderstatusnext.setTextColor(Color.parseColor("#2ecc71"))
                v_statusintransit.setTextColor(Color.parseColor("#2ecc71"))

                v_orderdate.visibility=View.VISIBLE
                v_orderdate.text="$varorderdate\n$varordertime"
                v_confirmdate.visibility=View.VISIBLE
                v_confirmdate.text="$varconfirmdate\n$varconfirmtime"
                v_intransitdate.setTextColor(Color.parseColor("#2ecc71"))
                v_intransitdate.visibility=View.VISIBLE
                v_intransitdate.text="$varintransitdate\n$varintransittime"

            }
            else if(varorderstatus=="Delivered")
            {
                v_progressbar.progress=100
                v_progressbar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#16a085"), android.graphics.PorterDuff.Mode.SRC_IN)
               // v_orderid.setTextColor(Color.parseColor("#16a085"))
                v_orderstatusother.visibility=View.VISIBLE
                v_orderstatusother.text="Order Received on $vardeliverydate"
                v_orderstatusother.setTextColor(Color.parseColor("#16a085"))
                v_orderstatus.visibility=View.GONE
                v_orderstatusnext.visibility=View.GONE
                v_statusdelivered.setTextColor(Color.parseColor("#16a085"))
                v_orderdate.visibility=View.VISIBLE
                v_orderdate.text="$varorderdate\n$varordertime"
                v_confirmdate.visibility=View.VISIBLE
                v_confirmdate.text="$varconfirmdate\n$varconfirmtime"
                v_intransitdate.visibility=View.VISIBLE
                v_intransitdate.text="$varintransitdate\n$varintransittime"
                v_deliverydate.visibility=View.VISIBLE
                v_deliverydate.text="$vardeliverydate\n$vardeliverytime"
                v_deliverydate.setTextColor(Color.parseColor("#16a085"))
            }
            else if(varorderstatus=="Cancled")
            {
               // v_orderid.setTextColor(Color.parseColor("#c0392b"))
                v_orderstatusnext.visibility=View.GONE
                v_orderstatus.visibility=View.GONE
                v_orderstatusother.visibility=View.VISIBLE
                v_orderstatusother.setTextColor(Color.parseColor("#c0392b"))
                v_orderstatusother.text="Cancled by $varcancleby on $varcancledate at $varcancletime due to $varcanclereason"
                v_paymentmode.visibility=View.GONE
                v_paymentstatus.visibility=View.GONE
                v_totalprice.visibility=View.GONE
                v_viewStatus.visibility=View.GONE
                v_progressbar.visibility=View.GONE
                vtdropdown.visibility=View.GONE
            }

        }

    }

}