package com.octalgroup.veerasadadhaba

/*
class MyFirebaseInstanceIdService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        println("Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
             fun sendRegistrationToServer(token: String){

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
             }

}*/