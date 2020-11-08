package com.octalgroup.mobilegurushop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        btnEnter.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}
