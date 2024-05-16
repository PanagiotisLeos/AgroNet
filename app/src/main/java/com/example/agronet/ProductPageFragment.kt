package com.example.agronet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductPageFragment : Fragment() {

    private lateinit var topSellingRecyclerView: RecyclerView
    private lateinit var imperfectProductsRecyclerView: RecyclerView
    private lateinit var fastShippedRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.product_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerViews
        topSellingRecyclerView = view.findViewById(R.id.top_selling_recycler_view)
        imperfectProductsRecyclerView = view.findViewById(R.id.imperfect_products_recycler_view)
        fastShippedRecyclerView = view.findViewById(R.id.fast_shipped_recycler_view)

        // Setup layout managers
        topSellingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        imperfectProductsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fastShippedRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Mock data
        val products = listOf(
            Product("Bananas Voiviotas", "0.99€ / per kg", R.drawable.bananas, R.drawable.farmer_photo),
            Product("Apples", "1.50€ / per kg", R.drawable.bananas, R.drawable.farmer_photo),
            Product("Oranges", "1.20€ / per kg", R.drawable.bananas, R.drawable.farmer_photo)
        )

        // Set adapters
        topSellingRecyclerView.adapter = ProductPageAdapter(requireContext(), products)
        imperfectProductsRecyclerView.adapter = ProductPageAdapter(requireContext(), products)
        fastShippedRecyclerView.adapter = ProductPageAdapter(requireContext(), products)
    }
}
