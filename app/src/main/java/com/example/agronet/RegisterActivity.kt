package com.example.agronet

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class RegisterActivity : AppCompatActivity() {

    private lateinit var profileSpinner: Spinner
    private lateinit var buttonSignUp: Button
    private lateinit var uploadImageButton: Button

    private lateinit var customerFields: LinearLayout
    private lateinit var farmerFields: LinearLayout

    // Customer fields
    private lateinit var customerEmailEditText: EditText
    private lateinit var customerPasswordEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var telephoneEditText: EditText

    // Farmer fields
    private lateinit var farmerEmailEditText: EditText
    private lateinit var farmerPasswordEditText: EditText
    private lateinit var firstNameFarmerEditText: EditText
    private lateinit var lastNameFarmerEditText: EditText
    private lateinit var locationEditText: EditText

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        profileSpinner = findViewById(R.id.profileSpinner)
        buttonSignUp = findViewById(R.id.signUpButton)
        uploadImageButton = findViewById(R.id.uploadImageButton)

        customerFields = findViewById(R.id.customerFields)
        farmerFields = findViewById(R.id.farmerFields)

        // Initialize customer fields
        customerEmailEditText = findViewById(R.id.customerEmailEditText)
        customerPasswordEditText = findViewById(R.id.customerPasswordEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        telephoneEditText = findViewById(R.id.telephoneEditText)

        // Initialize farmer fields
        farmerEmailEditText = findViewById(R.id.farmerEmailEditText)
        farmerPasswordEditText = findViewById(R.id.farmerPasswordEditText)
        firstNameFarmerEditText = findViewById(R.id.firstNameFarmerEditText)
        lastNameFarmerEditText = findViewById(R.id.lastNameFarmerEditText)
        locationEditText = findViewById(R.id.locationEditText)

        setupSpinner()

        buttonSignUp.setOnClickListener {
            registerUser()
        }

        uploadImageButton.setOnClickListener {
            openFilePicker()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Toast.makeText(this, "Image selected: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        imagePickerLauncher.launch("image/*")
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
                when (position) {
                    1 -> { // Customer selected
                        customerFields.visibility = View.VISIBLE
                        farmerFields.visibility = View.GONE
                    }
                    2 -> { // Farmer selected
                        customerFields.visibility = View.GONE
                        farmerFields.visibility = View.VISIBLE
                    }
                    else -> {
                        customerFields.visibility = View.GONE
                        farmerFields.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed here
            }
        }
    }

    private fun registerUser() {
        val selectedProfile = profileSpinner.selectedItem.toString()

        Log.d("RegisterActivity", "registerUser called with profile: $selectedProfile")

        if (selectedProfile == "Choose a profile...") {
            Toast.makeText(this, "Please select a profile", Toast.LENGTH_SHORT).show()
            return
        }

        when (selectedProfile) {
            "Customer" -> {
                val email = customerEmailEditText.text.toString().trim()
                val password = customerPasswordEditText.text.toString().trim()
                val firstName = firstNameEditText.text.toString().trim()
                val lastName = lastNameEditText.text.toString().trim()
                val telephone = telephoneEditText.text.toString().trim()

                if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || telephone.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return
                }

                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("RegisterActivity", "Inserting user as Customer")
                    insertUserAndCustomer(email, password, firstName, lastName, telephone)
                }
            }
            "Farmer" -> {
                val email = farmerEmailEditText.text.toString().trim()
                val password = farmerPasswordEditText.text.toString().trim()
                val firstName = firstNameFarmerEditText.text.toString().trim()
                val lastName = lastNameFarmerEditText.text.toString().trim()
                val location = locationEditText.text.toString().trim()

                if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || location.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return
                }

                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("RegisterActivity", "Inserting user as Farmer")
                    insertUserAndFarmer(email, password, firstName, lastName, location)
                }
            }
        }
    }

    private suspend fun insertUserAndCustomer(email: String, password: String, firstName: String, lastName: String, telephone: String) {
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
                customerStmt.setString(2, firstName)
                customerStmt.setString(3, lastName)
                customerStmt.setString(4, telephone)
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

    private suspend fun insertUserAndFarmer(email: String, password: String, firstName: String, lastName: String, location: String) {
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
                farmerStmt.setString(2, firstName)
                farmerStmt.setString(3, lastName)
                farmerStmt.setString(4, location)
                farmerStmt.setString(5, "Individual") // Placeholder for type
                farmerStmt.setNull(6, java.sql.Types.BLOB) // Placeholder for prof_image
                farmerStmt.setString(7, "Description") // Placeholder for description
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
