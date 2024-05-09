package com.example.agronet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class FarmersActivity : AppCompatActivity() {

    private lateinit var farmerAdapter: FarmerAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmers)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_farmers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        farmerAdapter = FarmerAdapter(emptyList())
        recyclerView.adapter = farmerAdapter

        fetchDataFromDatabase()
    }

    private fun fetchDataFromDatabase() {
        Thread {
            var connection: Connection? = null
            try {
                // Establish a connection to your MariaDB database
                Class.forName("org.mariadb.jdbc.Driver")
                connection = DriverManager.getConnection(
                    "jdbc:mariadb://172.20.10.10:3306/agronetdb", // Replace with your IP
                    "root", // Replace with your username
                    "@gRTen#" // Replace with your password
                )

                Log.d("FarmersActivity", "Database connection established")

                // Execute a query to fetch farmer data
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM farmers")

                // Parse the retrieved data into Farmer objects
                val farmersList = mutableListOf<Farmer>()
                while (resultSet.next()) {
                    val name = resultSet.getString("name")
                    val location = resultSet.getString("location")
                    val profileImageResourceId = R.drawable.gmail // Default image resource ID
                    val type = resultSet.getString("type")
                    val farmer = Farmer(name, location, profileImageResourceId, type)
                    farmersList.add(farmer)
                }

                Log.d("FarmersActivity", "Fetched ${farmersList.size} farmers from the database")

                // Update the UI with the retrieved data
                runOnUiThread {
                    farmerAdapter.setData(farmersList)
                }

            } catch (e: ClassNotFoundException) {
                Log.e("FarmersActivity", "ClassNotFoundException: ${e.message}", e)
            } catch (e: SQLException) {
                Log.e("FarmersActivity", "SQLException: ${e.message}", e)
            } finally {
                try {
                    connection?.close()
                    Log.d("FarmersActivity", "Database connection closed")
                } catch (e: SQLException) {
                    Log.e("FarmersActivity", "SQLException on close: ${e.message}", e)
                }
            }
        }.start()
    }
}
