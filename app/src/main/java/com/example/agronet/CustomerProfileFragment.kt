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
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class CustomerProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var name: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.customer_profile, container, false)
        sessionManager = SessionManager(requireContext())
        name = view.findViewById(R.id.consumerName)

        loadUserProfile()
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val customer = DatabaseManager.fetchCustomerDetails(sessionManager.userId)
            withContext(Dispatchers.Main) {
                if (customer == null) {
                    Log.e("CustomerProfileFragment", "No Customer Data Found")
                    name.text = "No Customer Data Found"
                } else {
                    Log.d("CustomerProfileFragment", "Customer Data Found: ${customer.fname}")
                    name.text = customer.fname + customer.lname
                }
            }
        }
    }
}

