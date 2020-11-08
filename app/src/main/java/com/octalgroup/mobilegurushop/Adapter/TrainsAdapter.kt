package com.octalgroup.mobilegurushop.Adapter
/*
import com.octalgroup.mobilegurushop.Model.TrainsModel
import com.octalgroup.mobilegurushop.PlaceTrainOrder
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TrainsAdapter(var c: Context, var list:ArrayList<TrainsModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {


    override fun onClick(view: View) {
        val holder = view.tag as RecyclerView.ViewHolder
        Toast.makeText(c,"hello", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.list_trains, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(list[position].name.toString(),list[position].number.toString())

        val intent= Intent(c, PlaceTrainOrder::class.java)
        intent.putExtra("trainno",list[position].number)
        intent.putExtra("name",list[position].name)
        c.startActivity(intent)
        holder.view.setOnClickListener {

         /*   if(list[position].available){

            }
            else
            {
                Toast.makeText(c,"Sorry Delivery to ${list[position].number.toString()} ${list[position].name} not available now",Toast.LENGTH_SHORT).show()
            }*/

        }


    }


    inner class MyProjects(var view: View): RecyclerView.ViewHolder(view){

        var vtavailable=view.findViewById<TextView>(R.id.txtTrainAvailability)
        var vtname=view.findViewById<TextView>(R.id.txtTrainName)
        var vtnumber=view.findViewById<TextView>(R.id.txtTrainNumber)


        fun bind( varname:String, varnumber:String)
        {
            vtname.text=varname.toString()
            vtnumber.text=varnumber.toString()

          /*  if(varavailable==true){
                vtavailable.text="Delivery Available"
                vtavailable.setTextColor(Color.parseColor("#16a085"))
            }
            else
            {
                vtavailable.text="Delivery Not Available"
                vtavailable.setTextColor(Color.parseColor("#c0392b"))
            }*/
        }

    }

}*/