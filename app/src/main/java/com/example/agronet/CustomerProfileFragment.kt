package com.example.agronet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private lateinit var logout: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.customer_profile, container, false)
        sessionManager = SessionManager(requireContext())
        name = view.findViewById(R.id.consumerName)
        logout = view.findViewById(R.id.logout)
        val editProfBtn = view.findViewById<Button>(R.id.editProfile)


        loadUserProfile()

        editProfBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        logout.setOnClickListener {
            logoutUser()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val customer = DatabaseManager.fetchCustomerInfo(sessionManager.userId)
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

    private fun logoutUser() {
        sessionManager.logoutUser()

        // Redirect to the login activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}

