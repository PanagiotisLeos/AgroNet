package com.example.agronet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val regButton = findViewById<Button>(R.id.register_button)
        val loginButton = findViewById<Button>(R.id.login_button)

        regButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val emailInput = findViewById<EditText>(R.id.email_input)
            val passwordInput = findViewById<EditText>(R.id.password_input)

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
            val isValidUser = withContext(Dispatchers.IO) {
                validateUser(email, password)
            }

            if (isValidUser) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUser(email: String, password: String): Boolean {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null

        return try {
            // Connect to the MariaDB database
            Class.forName("org.mariadb.jdbc.Driver")
            connection = DriverManager.getConnection(
                "jdbc:mariadb://172.20.10.10:3306/agronetdb",
                "root",
                "@gRTen#"
            )

            Log.d("LoginActivity", "Database connection established")

            // Prepare SQL statement to query the database for the user
            val sql = "SELECT * FROM user WHERE email = ? AND password = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, email)
            preparedStatement.setString(2, password)

            Log.d("LoginActivity", "SQL query prepared: $sql")

            // Execute the query
            val resultSet = preparedStatement.executeQuery()

            Log.d("LoginActivity", "SQL query executed")

            // Check if a user with the given credentials exists
            val userExists = resultSet.next()
            Log.d("LoginActivity", "User exists: $userExists")

            userExists
        } catch (e: SQLException) {
            Log.e("LoginActivity", "SQL Exception: ${e.message}", e)
            false
        } catch (e: ClassNotFoundException) {
            Log.e("LoginActivity", "Class Not Found Exception: ${e.message}", e)
            false
        } finally {
            try {
                // Close database resources
                preparedStatement?.close()
                connection?.close()
                Log.d("LoginActivity", "Database resources closed")
            } catch (e: SQLException) {
                Log.e("LoginActivity", "SQL Exception on close: ${e.message}", e)
            }
        }
    }
}
