package com.example.agronet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityFarmerui : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.`activity_main_farmerui.xml`)

        //session
        val sessionManager = SessionManager(applicationContext)
        if (!sessionManager.isLoggedIn) {
            val intent = Intent(this@MainActivityFarmerui, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_products -> {
                    loadFragment(ProductPageFragment())
                    true
                }
                R.id.navigation_farmers -> {
                    loadFragment(FarmersFragment())
                    true
                }
                R.id.navigation_My_orders -> {
                    loadFragment(MyOrdersFragment())
                    true
                }
                else -> false
            }
        }
        //Default fragment
        loadFragment(ProductPageFragment())
        bottomNavigationView.selectedItemId = R.id.navigation_products
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
