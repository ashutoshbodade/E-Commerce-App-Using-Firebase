package com.octalgroup.mobilegurushop

import android.annotation.SuppressLint
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


val db = FirebaseFirestore.getInstance()

public fun userid() : String {

    val user = FirebaseAuth.getInstance().currentUser

    if(user != null){
        val uid:String = user!!.uid.toString()
        return uid
    }
   else{
        return "nouser"
    }

}

public fun userno(): String {
    val user = FirebaseAuth.getInstance().currentUser

    if(user != null){
        val uno:String = user!!.phoneNumber.toString()
        return uno
    }
    else{
        return "nouser"
    }

}





public fun username(): String {
        val user = FirebaseAuth.getInstance().currentUser


    if(user != null){
        val uname:String = user!!.displayName.toString()
        return uname
    }
    else{
        return "nouser"
    }
    }


/*fun sendRegistrationToServer(token: String){

    val dbRef = db.collection("usertokens").document(userid().toString())

    val docorderID = hashMapOf(
        "usertoken" to token
    )

    dbRef.set(docorderID as Map<String, Any>)
        .addOnSuccessListener { documentReference ->
            println("Saved token: $token")
        }.addOnFailureListener {
            println("Failed token: $token")
        }.addOnCompleteListener {
            println("Complete token: $token")
        }
}*/


@SuppressLint("SimpleDateFormat")
public  fun todayDate():String{
    val bookingDate:String
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        bookingDate =  current.format(formatter)
    }
    else
    {
        val date = todayDate();
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        bookingDate = formatter.format(date)
    }
    return bookingDate
}

@SuppressLint("SimpleDateFormat")
public  fun Time():String{
    val bookingDate:String
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        bookingDate =  current.format(formatter)
    }
    else
    {
        val date = todayDate();
        val formatter = SimpleDateFormat("HH:mm:ss")
       // val formatter = SimpleDateFormat("HH:mm:ss.SSS")
        bookingDate = formatter.format(date)
    }
    return bookingDate
}