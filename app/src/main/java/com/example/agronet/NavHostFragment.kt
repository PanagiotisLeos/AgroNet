package com.example.agronet

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavHost
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController

abstract class NavHostFragment : Fragment(), NavHost {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Assuming this is within a Fragment, use requireActivity() instead of activity
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment?.findNavController()

        val bottomNav = requireView().findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav?.setupWithNavController(navController!!)
    }
}
