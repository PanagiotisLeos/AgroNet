package com.example.agronet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val regButton = findViewById<Button>(R.id.register_button)
        val loginButton = findViewById<Button>(R.id.login_button)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)

        emailInput.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) emailInput.setHint(
                ""
            ) else emailInput.setHint("Email")
        }

        passwordInput.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) passwordInput.setHint(
                ""
            ) else passwordInput.setHint("Password")
        }

        regButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {


            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            val (isValidUser, userType) = withContext(Dispatchers.IO) {
                validateUser(email, password)
            }
            if (isValidUser) {
                when (userType) {
                    0 -> {
                        SessionManager.createLoginSession("","customer")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java) // Replace with CustomerActivity if needed
                        startActivity(intent)
                        finish()
                    }
                    1 -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUser(email: String, password: String): Pair<Boolean, Int?> {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        return try {
            connection = DatabaseManager.getConnection()

            Log.d("LoginActivity", "Database connection established")

            // Prepare SQL statement to query the database for the user
            val sql = "SELECT * FROM user WHERE email = ? AND password = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, email)
            preparedStatement.setString(2, password)

            Log.d("LoginActivity", "SQL query prepared: $sql")

            // Execute the query
            resultSet = preparedStatement.executeQuery()

            Log.d("LoginActivity", "SQL query executed")

            // Check if a user with the given credentials exists
            if (resultSet.next()) {
                val userId = resultSet.getInt("id")
                val userType = resultSet.getInt("user_type")
                SessionManager.createLoginSession("$userId","$userType")

                Log.d("LoginActivity", "User exists: true, userType: $userType")
                Pair(true, userType)
            } else {
                Log.d("LoginActivity", "User exists: false")
                Pair(false, null)
            }
        } catch (e: SQLException) {
            Log.e("LoginActivity", "SQL Exception: ${e.message}", e)
            Pair(false, null)
        } catch (e: ClassNotFoundException) {
            Log.e("LoginActivity", "Class Not Found Exception: ${e.message}", e)
            Pair(false, null)
        } finally {
            try {
                // Close database resources
                resultSet?.close()
                preparedStatement?.close()
                connection?.close()
                Log.d("LoginActivity", "Database resources closed")
            } catch (e: SQLException) {
                Log.e("LoginActivity", "SQL Exception on close: ${e.message}", e)
            }
        }
    }
}
