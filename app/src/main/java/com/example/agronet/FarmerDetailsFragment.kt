package com.example.agronet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.SQLException

class FarmerDetailsFragment : Fragment() {

    companion object {
        private const val ARG_FARMER_ID = "farmerId"

        fun newInstance(farmerId: Int): FarmerDetailsFragment {
            val fragment = FarmerDetailsFragment()
            val args = Bundle()
            args.putInt(ARG_FARMER_ID, farmerId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var location: TextView
    private lateinit var starButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.farmer_details, container, false)

        name = view.findViewById(R.id.farmerName)
        description = view.findViewById(R.id.farmerDescription)
        location = view.findViewById(R.id.farmerLocation)

        val farmerId = arguments?.getInt(ARG_FARMER_ID)
        Log.d("FarmerDetailsFragment", "Farmer ID: $farmerId")
        if (farmerId == null) return view
        fetchFarmerProfile(farmerId)


        return view
    }

    @SuppressLint("SetTextI18n")
    private fun fetchFarmerProfile(farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                val query = "SELECT * FROM farmer WHERE id = ?"
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, farmerId)

                val resultSet =
                    preparedStatement.executeQuery("SELECT * FROM farmer where id = $farmerId")
                if (resultSet.next()) {
                    val fname = resultSet.getString("first_name")
                    val lname = resultSet.getString("last_name")
                    val desc = resultSet.getString("description")
                    val loc = resultSet.getString("location")
                    launch(Dispatchers.Main) {
                        name.text = "$fname $lname"
                        description.text = desc
                        location.text = loc
                    }
                }
                preparedStatement.close()
            } catch (e: SQLException) {
                Log.e("FarmerDetailsFragment", "SQL Exception: ${e.message}", e)
                launch(Dispatchers.Main) {
                    name.text = "Error loading data"
                }
            } finally {
                connection?.close()
            }
        }

    }


    private fun addstar(userId: Int, farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                val query = "INSERT INTO stars (customer_id, farmer_id, created_at) VALUES (?, ?, NOW())"
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, userId)
                preparedStatement.setInt(2, farmerId)
                preparedStatement.executeUpdate()
                preparedStatement.close()

                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Added to stars", Toast.LENGTH_SHORT).show()
                    starButton.setImageResource(R.drawable.fb) // Change to filled star icon
                }
            } catch (e: SQLException) {
                Log.e("FarmerDetailsFragment", "SQL Exception: ${e.message}", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                }
            } finally {
                connection?.close()
            }
        }
    }

}
