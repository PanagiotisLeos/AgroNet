package com.example.agronet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //session
        val sessionManager = SessionManager(
            applicationContext
        )
        if (!sessionManager.isLoggedIn) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_products -> {
                    loadFragment(ProductsFragment())
                    true
                }
                R.id.navigation_farmers -> {
                    loadFragment(FarmersFragment())
                    true
                }
                R.id.navigation_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
