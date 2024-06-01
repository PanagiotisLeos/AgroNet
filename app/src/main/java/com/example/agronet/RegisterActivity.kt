package com.example.agronet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var profileSpinner: Spinner
    private lateinit var buttonSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        profileSpinner = findViewById(R.id.profileSpinner)
        buttonSignUp = findViewById(R.id.signUpButton)

        setupSpinner()

        buttonSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun setupSpinner() {
        val profiles = listOf("Choose a profile...", "Customer", "Farmer")
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, profiles) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        profileSpinner.adapter = adapter

        profileSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                (view as? TextView)?.setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed here
            }
        }
    }

    private fun registerUser() {
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val selectedProfile = profileSpinner.selectedItem.toString()

        Log.d("RegisterActivity", "registerUser called with username: $username, email: $email, password: $password, profile: $selectedProfile")

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || selectedProfile == "Choose a profile...") {
            Toast.makeText(this, "Please fill all fields and select a profile", Toast.LENGTH_SHORT).show()
            return
        }

        when (selectedProfile) {
            "Customer" -> CoroutineScope(Dispatchers.IO).launch {
                Log.d("RegisterActivity", "Inserting user as Customer")
                insertUserAndCustomer(username, email, password)
            }
            "Farmer" -> CoroutineScope(Dispatchers.IO).launch {
                Log.d("RegisterActivity", "Inserting user as Farmer")
                insertUserAndFarmer(username, email, password)
            }
        }
    }

    private suspend fun insertUserAndCustomer(username: String, email: String, password: String) {
        var connection: Connection? = null
        try {
            Log.d("RegisterActivity", "Getting database connection for Customer")
            connection = DatabaseManager.getConnection()
            connection.autoCommit = false

            val userId = insertUser(connection, email, password, 0)
            Log.d("RegisterActivity", "User inserted with ID: $userId")
            if (userId != -1) {
                val customerQuery = "INSERT INTO customer (customer_id, first_name, last_name, telephone) VALUES (?, ?, ?, ?)"
                val customerStmt: PreparedStatement = connection.prepareStatement(customerQuery)
                customerStmt.setInt(1, userId)
                customerStmt.setString(2, username)
                customerStmt.setString(3, "LastName") // Placeholder
                customerStmt.setString(4, "1234567890") // Placeholder
                customerStmt.executeUpdate()
                connection.commit()

                Log.d("RegisterActivity", "Customer inserted successfully")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Customer registered successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.e("RegisterActivity", "Error inserting customer: ${e.message}")
            connection?.rollback()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        } finally {
            connection?.close()
            Log.d("RegisterActivity", "Database connection closed for Customer")
        }
    }

    private suspend fun insertUserAndFarmer(username: String, email: String, password: String) {
        var connection: Connection? = null
        try {
            Log.d("RegisterActivity", "Getting database connection for Farmer")
            connection = DatabaseManager.getConnection()
            connection.autoCommit = false

            val userId = insertUser(connection, email, password, 1)
            Log.d("RegisterActivity", "User inserted with ID: $userId")
            if (userId != -1) {
                val farmerQuery = "INSERT INTO farmer (id, first_name, last_name, location, type, prof_image, description) VALUES (?, ?, ?, ?, ?, ?, ?)"
                val farmerStmt: PreparedStatement = connection.prepareStatement(farmerQuery)
                farmerStmt.setInt(1, userId)
                farmerStmt.setString(2, username)
                farmerStmt.setString(3, "LastName") // Placeholder
                farmerStmt.setString(4, "Location") // Placeholder
                farmerStmt.setString(5, "Individual") // Placeholder
                farmerStmt.setNull(6, java.sql.Types.BLOB) // Placeholder for prof_image
                farmerStmt.setString(7, "Description") // Placeholder
                farmerStmt.executeUpdate()
                connection.commit()

                Log.d("RegisterActivity", "Farmer inserted successfully")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Farmer registered successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.e("RegisterActivity", "Error inserting farmer: ${e.message}")
            connection?.rollback()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        } finally {
            connection?.close()
            Log.d("RegisterActivity", "Database connection closed for Farmer")
        }
    }

    private suspend fun insertUser(connection: Connection, email: String, password: String, userType: Int): Int {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("RegisterActivity", "Inserting user with email: $email, password: $password, userType: $userType")
                val userQuery = "INSERT INTO user (email, password, user_type) VALUES (?, ?, ?)"
                val userStmt: PreparedStatement = connection.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS)
                userStmt.setString(1, email)
                userStmt.setString(2, password)
                userStmt.setInt(3, userType)
                userStmt.executeUpdate()

                val rs = userStmt.generatedKeys
                if (rs.next()) {
                    val userId = rs.getInt(1)
                    Log.d("RegisterActivity", "User inserted with ID: $userId")
                    userId
                } else {
                    Log.e("RegisterActivity", "Failed to retrieve user ID")
                    -1
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("RegisterActivity", "Error inserting user: ${e.message}")
                -1
            }
        }
    }
}
