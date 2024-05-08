package com.example.agronet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
    }

    fun farmersClicked(view: View?) {
        val i = Intent(this, FarmersActivity::class.java)
        startActivity(i)
    }
}