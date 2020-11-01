package com.octalgroup.mobilegurushop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_user_details.*


class UserDetailsActivity : AppCompatActivity() {


   // val PERMISSION_ID = 42
   // lateinit var mFusedLocationClient: FusedLocationProviderClient

    //var lat:String? = null
   // var long:String? = null

    var points:Number= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //getLastLocation()

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val uphone: String? = user!!.phoneNumber
        val uid: String? = user.uid

        edttxtPhone.text=uphone.toString()



        db.collection("users")
            .whereEqualTo("uphone", uphone )
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document != null){

                        val uname:String=document["uname"] as String
                        edttxtName.setText(uname)
                        val uaddress:String=document["uaddress"] as String
                        edttxtAddress.setText(uaddress)
                        val uemail:String=document["uemail"] as String
                        edttxtEmail.setText(uemail)

                       points = document["points"] as Number

                    }

                }
            }
            .addOnFailureListener {

            }
            .addOnCompleteListener {
                loadFirst.visibility=View.GONE
                viewDetails.visibility=View.VISIBLE
            }



        btnContinue.setOnClickListener {

            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        println("getInstanceId failed"+ task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                   // sendRegistrationToServer(token!!)
                    //  println("getInstanceId Okay "+ token)
                    // Log and toast
                    // val msg = getString(R.string.msg_token_fmt, token)
                    // Log.d(TAG, msg)
                    // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                    val uname=edttxtName.text.toString()
                    val uaddress=edttxtAddress.text.toString()
                    val uemail=edttxtEmail.text.toString()


                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(uname)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,"updated",Toast.LENGTH_SHORT).show()
                            }
                        }

                    if(edttxtName.text.trim().length>0 && edttxtAddress.text.trim().length>0){
                        btnContinueTxt.text="SAVING DATA PLEASE WAIT ..."
                        btnContinue.isEnabled = false
                        btnContinue.isClickable = false


                        val userdata = hashMapOf(
                            "uid" to uid.toString(),
                            "uname" to uname,
                            "uaddress" to uaddress,
                            "uemail" to uemail,
                            "uphone" to uphone,
                            "points" to points,
                            "usertoken" to token,
                            "traincart" to 0
                        )

                        db.collection("users").document(uid.toString())

                            .set(userdata as Map<String, Any>)
                            .addOnSuccessListener {
                                Toasty.success(applicationContext, "Welcome", Toast.LENGTH_SHORT, true).show();
                                startActivity(Intent(this, MainActivity::class.java))

                            }


                    }
                    else{
                        btnContinue.isEnabled = true
                        btnContinue.isClickable = true
                        btnContinueTxt.text="CONTINUE TO GUJARATI SWEETS"
                        Toasty.error(applicationContext, "Please Fill All Details ", Toast.LENGTH_SHORT, true).show();
                    }
                })


        }
    }
/*
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    }
                    else {
                        findViewById<TextView>(R.id.txtLat).text = location.latitude.toString()
                        findViewById<TextView>(R.id.txtLog).text = location.longitude.toString()
                        lat= location.latitude.toString()
                        long= location.latitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation

            txtLat.text = mLastLocation.latitude.toString()
            txtLat.text = mLastLocation.longitude.toString()

            lat= mLastLocation.latitude.toString()
            long= mLastLocation.latitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun getaAddress(): String? {

        val geocoder = Geocoder(this@UserDetailsActivity, Locale.getDefault())
        var address: String? = ""
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(lat!!.toDouble(), long!!.toDouble(), 1)
            val obj: Address = addresses[0]
            var add: String = obj.getAddressLine(0)
            //optional
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();Log.e("Location", "Address$add")
            address = add
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return address
    }

    fun getAddress() : String? {

        var address: String? = ""
        val geocoder = Geocoder(this@UserDetailsActivity, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(lat!!.toDouble(), long!!.toDouble(), 1)
            val obj = addresses[0]
            var add = obj.getAddressLine(0)
            val currentAddress = (obj.subAdminArea + ","
                    + obj.adminArea)
            val latitude = obj.latitude
            val longitude = obj.longitude
            val currentCity = obj.subAdminArea
            val currentState = obj.adminArea
            add = add + "\n" + obj.countryName
            add = add + "\n" + obj.countryCode
            add = add + "\n" + obj.adminArea
            add = add + "\n" + obj.postalCode
            add = add + "\n" + obj.subAdminArea
            add = add + "\n" + obj.locality
            add = add + "\n" + obj.subThoroughfare
            println("obj.getCountryName()" + obj.countryName)
            println("obj.getCountryCode()" + obj.countryCode)
            println("obj.getAdminArea()" + obj.adminArea)
            println("obj.getPostalCode()" + obj.postalCode)
            println("obj.getSubAdminArea()" + obj.subAdminArea)
            println("obj.getLocality()" + obj.locality)
            println("obj.getSubThoroughfare()" + obj.subThoroughfare)
            Log.v("IGA", "Address$add")

            address = add.toString()
            // Toast.makeText(this, "Address=>" + add,
// Toast.LENGTH_SHORT).show();
// TennisAppActivity.showDialog(add);
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return  address
    }*/
}
