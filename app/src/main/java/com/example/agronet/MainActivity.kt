package com.example.agronet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to activity_main
        setContentView(R.layout.activity_main)

        // Load the ProductPageFragment
        if (savedInstanceState == null) {
            loadFragment(ProductPageFragment())
        }

        // Additional initialization logic if needed
        enableEdgeToEdge()
    }

    private fun loadFragment(fragment: Fragment) {
        // Create fragment transaction
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        // Replace the container with the new fragment
        transaction.replace(R.id.fragment_container, fragment)
        // Commit the transaction
        transaction.commit()
    }

    private fun enableEdgeToEdge() {
        // Your existing method implementation
    }
}
