package com.example.agronet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    loadFragment(FarmersFragment())
                    true
                }
                else -> false
            }
        }

        // Set the initial fragment
        bottomNavigationView.selectedItemId = R.id.navigation_farmers
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
