package com.example.agronet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class FarmersFragment : Fragment(),OnFarmerClickListener {

    private lateinit var farmerAdapter: FarmerAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_farmers, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_farmers)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val userProfileImageView: ImageView = view.findViewById(R.id.go_to_profile)
        userProfileImageView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CustomerProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        sessionManager = SessionManager(requireContext())
        farmerAdapter = FarmerAdapter(listOf(), this)
        recyclerView.adapter = farmerAdapter

        fetchDataFromDatabase()

        return view
    }


    override fun onFarmerClick(farmerId: Int) {
        val profileFragment = FarmerDetailsFragment.newInstance(farmerId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, profileFragment)
            .addToBackStack(null) // Add to back stack for navigational purposes
            .commit()
    }

    private fun fetchDataFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()

                val sql = "SELECT farmer.*, COUNT(star.id) AS total_stars, " +
                        "CASE WHEN EXISTS " +
                        "(SELECT 1 FROM star WHERE star.farmer_id = farmer.id AND star.customer_id = ${sessionManager.userId}) " +
                        "THEN 1 ELSE 0 END AS customer_has_starred FROM farmer " +
                        "LEFT JOIN star ON farmer.id = star.farmer_id " +
                        "GROUP BY farmer.id"
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery(sql)

                val farmersList = mutableListOf<Farmer>()
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val fname = resultSet.getString("first_name")
                    val lname = resultSet.getString("last_name")
                    val location = resultSet.getString("location")
                    val profImg = resultSet.getBytes("prof_image")
                    val type = resultSet.getString("type")
                    val description = resultSet.getString("description")
                    val totalStars = resultSet.getInt("total_stars")
                    val customerHasStarred = resultSet.getInt("customer_has_starred") == 1
                    val farmer = Farmer(id, fname, lname, location, profImg, type, description,totalStars,customerHasStarred)
                    farmersList.add(farmer)
                }

                activity?.runOnUiThread {
                    farmerAdapter.setData(farmersList)
                }

            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                try {
                    connection?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
