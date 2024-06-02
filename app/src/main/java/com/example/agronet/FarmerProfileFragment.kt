package com.example.agronet

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class FarmerProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var location: TextView
    private lateinit var profileImg: ImageView
    private lateinit var starButton: Button
    private lateinit var productRecyclerView: RecyclerView

    companion object {
        private const val EDIT_PRODUCT_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FarmerProfileFragment", "onCreateView called")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_farmer_profile, container, false)

        // Initialize UI elements
        name = view.findViewById(R.id.farmerName)
        description = view.findViewById(R.id.farmerDescription)
        location = view.findViewById(R.id.farmerLocation)
        profileImg = view.findViewById(R.id.farmerImage)
        starButton = view.findViewById(R.id.starButton)
        productRecyclerView = view.findViewById(R.id.productRecyclerView)

        sessionManager = SessionManager(requireContext())

        // Fetch farmer profile details
        val farmerId = sessionManager.userId.toInt()
        Log.d("FarmerProfileFragment", "Fetching profile for farmer ID: $farmerId")
        fetchFarmerProfileAndProducts(farmerId)

        // Find the button and set click listener
        val uploadProductBtn = view.findViewById<Button>(R.id.addProduct)
        uploadProductBtn.setOnClickListener {
            Log.d("FarmerProfileFragment", "Add Product button clicked")
            val intent = Intent(activity, AddProductActivity::class.java)
            startActivityForResult(intent, EDIT_PRODUCT_REQUEST_CODE)
        }

        return view
    }

    private fun fetchFarmerProfileAndProducts(farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("FarmerProfileFragment", "Starting fetchFarmerProfileAndProducts for ID: $farmerId")
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                Log.d("FarmerProfileFragment", "Database connection established")
                val query = """
                SELECT 
                    farmer.*,
                    COALESCE(total_stars.total_stars, 0) AS total_stars
                FROM 
                    farmer 
                LEFT JOIN (
                    SELECT 
                        farmer_id, 
                        COUNT(*) AS total_stars
                    FROM 
                        star 
                    GROUP BY 
                        farmer_id
                ) AS total_stars ON farmer.id = total_stars.farmer_id
                WHERE 
                    farmer.id = ?;
                """
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, farmerId)
                val resultSet = preparedStatement.executeQuery()

                if (resultSet.next()) {
                    val fname = resultSet.getString("first_name")
                    val lname = resultSet.getString("last_name")
                    val desc = resultSet.getString("description")
                    val loc = resultSet.getString("location")
                    val profImg = resultSet.getBytes("prof_image")
                    val totalStars = resultSet.getInt("total_stars")

                    withContext(Dispatchers.Main) {
                        name.text = "$fname $lname"
                        description.text = desc
                        location.text = loc
                        starButton.text = totalStars.toString()

                        if (profImg == null || profImg.isEmpty()) {
                            profileImg.setImageResource(R.drawable.farmer_photo)
                        } else {
                            val bitmap = BitmapFactory.decodeByteArray(profImg, 0, profImg.size)
                            profileImg.setImageBitmap(bitmap)
                        }
                    }
                }
                preparedStatement.close()
                // Fetch and display products
                fetchAndDisplayProducts(farmerId)
            } catch (e: SQLException) {
                Log.e("FarmerProfileFragment", "SQL Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load farmer profile", Toast.LENGTH_SHORT).show()
                }
            } finally {
                connection?.close()
                Log.d("FarmerProfileFragment", "Database resources closed")
            }
        }
    }

    private fun fetchAndDisplayProducts(farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                val query = """
                SELECT 
                    product.id AS product_id, 
                    product.name AS product_name, 
                    product.price AS product_price, 
                    product.prod_image AS product_image
                FROM 
                    product 
                WHERE 
                    product.farmer_id = ?;
                """
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, farmerId)
                val resultSet = preparedStatement.executeQuery()

                val products = mutableListOf<Product>()
                while (resultSet.next()) {
                    val productId = resultSet.getInt("product_id")
                    val productName = resultSet.getString("product_name")
                    val productPrice = resultSet.getString("product_price")
                    val prodImage = resultSet.getBytes("product_image")
                    val imageBitmap = BitmapFactory.decodeByteArray(prodImage, 0, prodImage.size)

                    val product = Product(productId, productName, "$productPrice /per kg", farmerId, imageBitmap, imageBitmap)
                    products.add(product)
                }
                preparedStatement.close()
                withContext(Dispatchers.Main) {
                    setupRecyclerView(products)
                }
            } catch (e: SQLException) {
                Log.e("FarmerProfileFragment", "SQL Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            } finally {
                connection?.close()
            }
        }
    }

    private fun setupRecyclerView(products: List<Product>) {
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = ProductAdapter(requireContext(), products)
        productRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { product ->
            val intent = Intent(activity, AddProductActivity::class.java).apply {
                putExtra("product_id", product.id)
                putExtra("product_name", product.name)
                putExtra("product_price", product.price)
                putExtra("product_image", product.imageResId)
            }
            startActivityForResult(intent, EDIT_PRODUCT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PRODUCT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Refresh the profile
            val farmerId = sessionManager.userId.toInt()
            fetchFarmerProfileAndProducts(farmerId)
        }
    }
}
