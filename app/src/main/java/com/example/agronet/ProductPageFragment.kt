package com.example.agronet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import android.widget.TextView

class ProductPageFragment : Fragment() {
    private var popupWindow: PopupWindow? = null
    private lateinit var topSellingRecyclerView: RecyclerView
    private lateinit var imperfectProductsRecyclerView: RecyclerView
    private lateinit var fastShippedRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.product_page, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerViews
        topSellingRecyclerView = view.findViewById(R.id.top_selling_recycler_view)
        imperfectProductsRecyclerView = view.findViewById(R.id.imperfect_products_recycler_view)
        fastShippedRecyclerView = view.findViewById(R.id.fast_shipped_recycler_view)

        // Setup layout managers and adapters for RecyclerViews
        setupRecyclerViews()

        // Setup filter icon click listener to show categories popup
        val filterIcon: ImageView = view.findViewById(R.id.filter_icon)
        filterIcon.setOnClickListener {
            showCategoriesPopup(it)
        }
    }

    private fun setupRecyclerViews() {
        val products = listOf(
            Product(
                "Bananas Voiviotas",
                "0.99€ / per kg",
                R.drawable.bananas,
                R.drawable.farmer_photo
            ),
            Product("Apples", "1.50€ / per kg", R.drawable.bananas, R.drawable.farmer_photo),
            Product("Oranges", "1.20€ / per kg", R.drawable.bananas, R.drawable.farmer_photo)
        )

        topSellingRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        imperfectProductsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fastShippedRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        topSellingRecyclerView.adapter = ProductPageAdapter(requireContext(), products)
        imperfectProductsRecyclerView.adapter = ProductPageAdapter(requireContext(), products)
        fastShippedRecyclerView.adapter = ProductPageAdapter(requireContext(), products)
    }

    private fun showCategoriesPopup(anchorView: View) {
        // Inflate the popup layout
        val context = context ?: return // return early if context is null

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
            e.printStackTrace()  // This will print the full stack trace, including the specific exception
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