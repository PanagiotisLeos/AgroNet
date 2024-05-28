package com.example.agronet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException

class EditProfileFragment : Fragment() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var buttonSave: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        editTextFirstName = view.findViewById(R.id.editTextFirstName)
        editTextLastName = view.findViewById(R.id.editTextLastName)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        buttonSave = view.findViewById(R.id.buttonSave)
        sessionManager = SessionManager(requireContext())

        loadUserProfile()

        buttonSave.setOnClickListener {
            saveUserProfile()
        }

        return view
    }

    private fun loadUserProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val customer = DatabaseManager.fetchCustomerInfo(sessionManager.userId)

            editTextFirstName.setText(customer.fname)
            editTextLastName.setText(customer.lname)
            editTextPhone.setText(customer.email)
        }
    }

    private fun saveUserProfile() {
        val firstName = editTextFirstName.text.toString()
        val lastName = editTextLastName.text.toString()
        val email = editTextPhone.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val customer = DatabaseManager.fetchCustomerInfo(sessionManager.userId)
        customer?.let {
            it.fname = firstName
            it.lname = lastName
            it.email = email

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    DatabaseManager.updateCustomerInfo(it)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed() // Navigate back to the previous fragment
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
