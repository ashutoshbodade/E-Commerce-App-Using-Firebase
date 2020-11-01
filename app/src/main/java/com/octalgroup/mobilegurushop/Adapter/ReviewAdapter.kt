package com.octalgroup.mobilegurushop.Adapter

import com.octalgroup.mobilegurushop.Model.ReviewModel
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal
import java.math.RoundingMode

class ReviewAdapter(var c: Context, var list:ArrayList<ReviewModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {


    override fun onClick(view: View) {
        val holder = view.tag as RecyclerView.ViewHolder
        Toast.makeText(c,"hello", Toast.LENGTH_LONG).show()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_review, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(
            list[position].id,
            list[position].productid,
            list[position].rating!!,
            list[position].review!!,
            list[position].username.toString()
            )






    }



    inner class MyProjects(var view: View): RecyclerView.ViewHolder(view){

        var vtusertextimage=view.findViewById<TextView>(R.id.userTextImage)

        var vtusername=view.findViewById<TextView>(R.id.userName)

        var vtuserratingbar=view.findViewById<RatingBar>(R.id.userRatingBar)

        var vtuserrating=view.findViewById<TextView>(R.id.userRating)

        var vtuserreview=view.findViewById<TextView>(R.id.userReview)




        fun bind(tid: Int, tproductid:Int, trating:Double, treview:String, tusername:String)
        {
            val decimal = BigDecimal(trating).setScale(2, RoundingMode.HALF_EVEN)
            vtuserratingbar.rating=trating.toFloat()
            vtuserrating.text= decimal.toString()
            vtusername.text=tusername.toString()
            vtuserreview.text=treview.toString()

            val ashu:String = tusername.toString()

            if (ashu.length!=0){
                vtusertextimage.text=ashu.substring(0,1).toString()
            }

          //  val ashu1:String= "<html><body><p align=\"justify\">$treview</p></body></html>"


           // vtuserreview.loadData(ashu1, "text/html", "utf-8")




        }

    }

}