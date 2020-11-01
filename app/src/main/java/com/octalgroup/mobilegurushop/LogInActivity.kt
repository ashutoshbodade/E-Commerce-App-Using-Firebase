package com.octalgroup.mobilegurushop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_log_in.*
import java.util.concurrent.TimeUnit

class LogInActivity : AppCompatActivity() {

    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    lateinit var verificationId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        mAuth = FirebaseAuth.getInstance()

        veriBtn.setOnClickListener {
               progress.visibility = View.VISIBLE
            verify ()
            veriBtnText.text="VERIFYING"
            veriBtn.isEnabled=false
            veriBtn.isClickable = false
            println("authclient 1")
        }

        authBtn.setOnClickListener {
            progress.visibility = View.VISIBLE
            authenticate()
            println("authclient 2")
        }
    }


    private fun verificationCallbacks () {
        mCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progress.visibility = View.INVISIBLE
                signIn(credential)
                println("authclient 3")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@LogInActivity,"Error", Toast.LENGTH_SHORT).show()
                println("authclient 4"+p0)
            }


            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0.toString()
                progress.visibility = View.INVISIBLE
                phnNoTxt.visibility=View.GONE
                phnNoTxtview.text=phnNoTxt.text
                phnNoTxtview.visibility=View.VISIBLE
                verifiTxt.visibility=View.VISIBLE
                veriBtn.visibility=View.GONE
                authBtn.visibility=View.VISIBLE
                println("authclient 5")
            }
        }
    }

    private fun verify () {

        verificationCallbacks()
        val ph = "+91"+phnNoTxt.text.toString()
     //   val phnNo = phnNoTxt.text.toString()


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            ph,
            30,
            TimeUnit.SECONDS,
            this,
            mCallbacks
        )

        println("authclient 6")
    }

    private fun signIn (credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                    task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Toasty.success(applicationContext, "Logged in Successfully :)!", Toast.LENGTH_SHORT, true).show();
                    startActivity(Intent(this, UserDetailsActivity::class.java))
                    finish()
                }
            }
        println("authclient 7")
    }

    private fun authenticate () {

        val verifiNo = verifiTxt.text.toString()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verifiNo)
        signIn(credential)
        println("authclient 8")
    }



}