package com.example.agronet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException

class ProductPageFragment : Fragment() {
    private var popupWindow: PopupWindow? = null
    private lateinit var topSellingRecyclerView: RecyclerView
    private lateinit var imperfectProductsRecyclerView: RecyclerView
    private lateinit var fastShippedRecyclerView: RecyclerView

    private val databaseManager = DatabaseManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_page, container, false)

        val profileBtn = view.findViewById<ImageView>(R.id.profile_icon)
        profileBtn.setOnClickListener {
            val sessionManager = SessionManager(requireContext())
            val userType = sessionManager.getUserType()

            val fragment = if (userType == "1") { // Assuming "1" represents a farmer
                FarmerProfileFragment()
            } else {
                CustomerProfileFragment()
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerViews
        topSellingRecyclerView = view.findViewById(R.id.top_selling_recycler_view)
        imperfectProductsRecyclerView = view.findViewById(R.id.imperfect_products_recycler_view)
        fastShippedRecyclerView = view.findViewById(R.id.fast_shipped_recycler_view)

        // Set layout managers
        topSellingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        imperfectProductsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fastShippedRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Fetch data and set adapters
        fetchAndDisplayProducts()

        // Setup filter icon click listener to show categories popup
        val filterIcon: ImageView = view.findViewById(R.id.filter_icon)
        filterIcon.setOnClickListener {
            showCategoriesPopup(it)
        }
    }

    private fun fetchAndDisplayProducts() {
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("ProductPageFragment", "Fetching products")
            try {
                val products = databaseManager.getAllProducts()
                Log.d("ProductPageFragment", "Products fetched: ${products.size}")
                withContext(Dispatchers.Main) {
                    if (products.isNotEmpty()) {
                        Log.d("ProductPageFragment", "Setting adapter with products")
                        val adapter = ProductPageAdapter(requireContext(), products)
                        topSellingRecyclerView.adapter = adapter
                        imperfectProductsRecyclerView.adapter = adapter
                        fastShippedRecyclerView.adapter = adapter
                        Log.d("ProductPageFragment", "Adapter set")
                    } else {
                        Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show()
                        Log.d("ProductPageFragment", "No products found")
                    }
                }
            } catch (e: SQLException) {
                Log.e("ProductPageFragment", "SQL Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showCategoriesPopup(anchorView: View) {
        val context = context ?: return

        val layoutInflater = LayoutInflater.from(context)
        val popupView = layoutInflater.inflate(R.layout.popup_categories, null, false)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        setupCategoryClickListeners(popupView, popupWindow)

        try {
            popupWindow.showAsDropDown(anchorView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupCategoryClickListeners(view: View, popupWindow: PopupWindow) {
        view.findViewById<TextView>(R.id.top_selling_products).setOnClickListener {
            Toast.makeText(context, "Top Selling Products clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
        view.findViewById<TextView>(R.id.imperfect_products).setOnClickListener {
            Toast.makeText(context, "Imperfect Products clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.fastshipped).setOnClickListener {
            Toast.makeText(context, "fastshipped clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.fruits).setOnClickListener {
            Toast.makeText(context, "fruits clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.vegetables).setOnClickListener {
            Toast.makeText(context, "vegetables clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
        view.findViewById<TextView>(R.id.OilsandSeedoils).setOnClickListener {
            Toast.makeText(context, "OilsandSeedoils clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.nuts).setOnClickListener {
            Toast.makeText(context, "nuts clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.spices).setOnClickListener {
            Toast.makeText(context, "spices clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.diary).setOnClickListener {
            Toast.makeText(context, "diary clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.legumes).setOnClickListener {
            Toast.makeText(context, "legumes clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        view.findViewById<TextView>(R.id.beverages).setOnClickListener {
            Toast.makeText(context, "beverages clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        popupWindow?.dismiss()  // Dismiss the popup to prevent leakage
    }
}
