package com.example.agronet

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var sessionManager: SessionManager
    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var location: TextView
    private lateinit var profileImg: ImageView
    private lateinit var starButton: Button
    private lateinit var productRecyclerView: RecyclerView
    private var isStarred = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.farmer_details, container, false)

        name = view.findViewById(R.id.farmerName)
        description = view.findViewById(R.id.farmerDescription)
        location = view.findViewById(R.id.farmerLocation)
        profileImg = view.findViewById(R.id.farmerProfImage)
        starButton = view.findViewById(R.id.starButton)
        productRecyclerView = view.findViewById(R.id.productRecyclerView)

        sessionManager = SessionManager(requireContext())

        val farmerId = arguments?.getInt(ARG_FARMER_ID)
        Log.d("FarmerDetailsFragment", "Farmer ID: $farmerId")
        if (farmerId == null) return view
        fetchFarmerProfileAndProducts(farmerId)



        starButton.setOnClickListener {
            if (isStarred) {
                removeStar(sessionManager.userId.toInt(), farmerId)
                isStarred = false
                fetchFarmerProfileAndProducts(farmerId)
            } else {
                addStar(sessionManager.userId.toInt(), farmerId)
                isStarred = true
                fetchFarmerProfileAndProducts(farmerId)
            }
        }

        return view
    }

    private fun fetchFarmerProfileAndProducts(farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                val query = """
                SELECT 
                    farmer.*,
                    COALESCE(total_stars.total_stars, 0) AS total_stars,
                    product.id AS product_id, 
                    product.name AS product_name, 
                    product.price AS product_price, 
                    product.prod_image AS product_image,
                    CASE 
                        WHEN customer_star.star_id IS NOT NULL THEN 1 
                        ELSE 0 
                    END AS customer_has_star
                FROM 
                    farmer 
                LEFT OUTER JOIN 
                    product ON farmer.id = product.farmer_id 
                LEFT JOIN (
                    SELECT 
                        farmer_id, 
                        COUNT(*) AS total_stars
                    FROM 
                        star 
                    GROUP BY 
                        farmer_id
                ) AS total_stars ON farmer.id = total_stars.farmer_id
                LEFT JOIN (
                    SELECT 
                        farmer_id, 
                        id AS star_id
                    FROM 
                        star
                    WHERE 
                        customer_id = ${sessionManager.userId}
                ) AS customer_star ON farmer.id = customer_star.farmer_id
                WHERE 
                    farmer.id = ?;
                
                
                """
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, farmerId)
                val resultSet = preparedStatement.executeQuery()

                var farmerDetailsLoaded = false
                val products = mutableListOf<Product>()
                while (resultSet.next()) {
                    if (!farmerDetailsLoaded) {
                        val fname = resultSet.getString("first_name")
                        val lname = resultSet.getString("last_name")
                        val desc = resultSet.getString("description")
                        val loc = resultSet.getString("location")
                        val profImg = resultSet.getBytes("prof_image")
                        val star = resultSet.getInt("customer_has_star")
                        val totalStars = resultSet.getInt("total_stars")

                        launch(Dispatchers.Main) {
                            name.text = "$fname $lname"
                            description.text = desc
                            location.text = loc
                            starButton.text = totalStars.toString()

                            val bitmap = BitmapFactory.decodeByteArray(profImg, 0, profImg.size)
                            profileImg.setImageBitmap(bitmap)

                            if (star != 0) {
                                isStarred = true
                                starButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_gold, 0, 0)
                            } else {
                                isStarred = false
                            }
                        }

                        farmerDetailsLoaded = true
                    }

                    val productId = resultSet.getInt("product_id")
                    if (productId != 0) {
                        val productName = resultSet.getString("product_name")
                        val productPrice = resultSet.getString("product_price")
                        val prodImage = resultSet.getBytes("product_image")
                        val imageBitmap = BitmapFactory.decodeByteArray(prodImage, 0, prodImage.size)

                        val product = Product(productId, productName, "$productPrice /per kg", farmerId, imageBitmap, imageBitmap)
                        products.add(product)
                    }
                }
                preparedStatement.close()
                launch(Dispatchers.Main) {
                    setupRecyclerView(products)
                }
            } catch (e: SQLException) {
                Log.e("FarmerDetailsFragment", "SQL Exception: ${e.message}", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            } finally {
                connection?.close()
            }
        }
    }

    private fun setupRecyclerView(products: List<Product>) {
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productRecyclerView.adapter = ProductAdapter(requireContext(), products)
    }

    private fun addStar(userId: Int, farmerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = DatabaseManager.getConnection()
                val star = Star(userId, farmerId, System.currentTimeMillis()) // Create a Star object

                val query = "INSERT INTO star (customer_id, farmer_id) VALUES (?, ?)"
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, star.customerId)
                preparedStatement.setInt(2, star.farmerId)
                preparedStatement.executeUpdate()
                preparedStatement.close()

                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Added to stars", Toast.LENGTH_SHORT).show()
                    starButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_gold, 0, 0)
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
