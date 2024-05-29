package com.example.agronet

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.SQLException
import java.time.Instant.now


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

    private lateinit var sessionManager: SessionManager

    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var location: TextView
    private lateinit var starButton: Button
    private var isStarred = false




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.farmer_details, container, false)

        name = view.findViewById(R.id.farmerName)
        description = view.findViewById(R.id.farmerDescription)
        location = view.findViewById(R.id.farmerLocation)
        starButton = view.findViewById(R.id.starButton)

        sessionManager = SessionManager(requireContext())




        val farmerId = arguments?.getInt(ARG_FARMER_ID)
        Log.d("FarmerDetailsFragment", "Farmer ID: $farmerId")
        if (farmerId == null) return view
        fetchFarmerProfile(farmerId)

        starButton.setOnClickListener{
            if(isStarred) {
            removeStar(sessionManager.userId.toInt(), farmerId)
            isStarred = false }
            else
            addStar(sessionManager.userId.toInt(), farmerId)
            isStarred = true
        }

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
                    preparedStatement.executeQuery("SELECT * FROM farmer inner join star on farmer.id = star.farmer_id WHERE farmer.id = $farmerId AND star.customer_id = ${sessionManager.userId}");
                if (resultSet.next()) {
                    val fname = resultSet.getString("first_name")
                    val lname = resultSet.getString("last_name")
                    val desc = resultSet.getString("description")
                    val loc = resultSet.getString("location")
                    val star = resultSet.getInt("star.id")
                    launch(Dispatchers.Main) {
                        name.text = "$fname $lname"
                        description.text = desc
                        location.text = loc

                        if (star != null){
                            isStarred = true
                            starButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_star_gold,0,0)
                        }
                        else {
                            isStarred = false
                        }

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



    private fun addStar(userId: Int, farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                val star = Star(userId, farmerId,System.currentTimeMillis()) // Create a Star object

                val query = "INSERT INTO star (customer_id, farmer_id) VALUES (?, ?)"
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, star.customerId)
                preparedStatement.setInt(2, star.farmerId)
                preparedStatement.executeUpdate()
                preparedStatement.close()

                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Added to stars", Toast.LENGTH_SHORT).show()
                    starButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_star_gold,0,0)
                    isStarred = true
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

    private fun removeStar(userId: Int, farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                val query = "DELETE FROM star WHERE customer_id = ? AND farmer_id = ?"
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, userId)
                preparedStatement.setInt(2, farmerId)
                val rowsAffected = preparedStatement.executeUpdate()
                preparedStatement.close()

                launch(Dispatchers.Main) {
                    if (rowsAffected > 0) {
                        Toast.makeText(requireContext(), "Removed from stars", Toast.LENGTH_SHORT).show()
                        starButton.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_star),
                            null,
                            null
                        )
                        isStarred = false
                    } else {
                        Toast.makeText(requireContext(), "Star not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SQLException) {
                Log.e("FarmerDetailsFragment", "SQL Exception: ${e.message}", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to remove from favorites", Toast.LENGTH_SHORT).show()
                }
            } finally {
                connection?.close()
            }
        }
    }

}
