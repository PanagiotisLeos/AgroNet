package com.example.agronet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.farmer_profile_from_user, container, false)

        // Use the farmerId to make database calls
        val farmerId = arguments?.getInt(ARG_FARMER_ID) ?: return view

        fetchFarmerProfile(farmerId);

        return view
    }

    private fun fetchFarmerProfile(farmerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()

                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM farmer where id = $farmerId")
                if (resultSet.next()) {
                    val fname = resultSet.getString("first_name")
                    val lname = resultSet.getString("last_name")
                }
            }
            catch (e: SQLException) {
                Log.e("LoginActivity", "SQL Exception: ${e.message}", e)
                Pair(false, null)
            }
        }
    }
}
