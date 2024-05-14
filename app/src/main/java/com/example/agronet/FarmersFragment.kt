package com.example.agronet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class FarmersFragment : Fragment() {

    private lateinit var farmerAdapter: FarmerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_farmers, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_farmers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        farmerAdapter = FarmerAdapter(emptyList())
        recyclerView.adapter = farmerAdapter

        val userProfileImageView: ImageView = view.findViewById(R.id.go_to_profile)
        userProfileImageView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UserProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        fetchDataFromDatabase()

        return view
    }

    private fun fetchDataFromDatabase() {
        Thread {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()

                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM farmer")

                // Parse the retrieved data into Farmer objects
                val farmersList = mutableListOf<Farmer>()
                while (resultSet.next()) {
                    val fname = resultSet.getString("first_name")
                    val lname = resultSet.getString("last_name")
                    val location = resultSet.getString("location")
                    val profImg = resultSet.getBytes("prof_image");
                    val type = resultSet.getString("type");
                    val description = resultSet.getString("description")
                    val farmer = Farmer(fname, lname, location, profImg, description,type)
                    farmersList.add(farmer)
                }

                // Update the UI with the retrieved data
                activity?.runOnUiThread {
                    farmerAdapter.setData(farmersList)
                }

            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                try {
                    connection?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}
