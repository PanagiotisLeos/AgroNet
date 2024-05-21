package com.example.agronet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.SQLException

class CustomerProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var name: TextView
    private lateinit var txtEmail: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.customer_profile, container, false)
        // Inflate the layout for this fragment
        sessionManager = SessionManager(requireContext())
        name = view.findViewById(R.id.consumerName)

        loadUserProfile()
        return view;

    }

    @SuppressLint("SetTextI18n")
    private fun loadUserProfile() {
        val customer = DatabaseManager.fetchUserDetails(sessionManager.userId,sessionManager.useType)
        if (customer == null) {
            Log.e("CustomerProfileFragment", "No Customer Data Found")
            name.text = "No data available"
            txtEmail.text = "No data available"
        } else {
            name.text = customer.name
            txtEmail.text = customer.email
        }

    }
}