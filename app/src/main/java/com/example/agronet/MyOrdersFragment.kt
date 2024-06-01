package com.example.agronet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyOrdersFragment : Fragment(), OrderAdapter.OnOrderClickListener {

    private lateinit var orderAdapter: OrderAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var orders: List<Order>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_orders, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_orders)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize your orders list here
        orders = listOf(
            // Add your orders here
            Order(1213, 1415, "24-3-2024", "John Doe", "123 Street", 2.0, 19.99, R.drawable.bananas),
            Order(1213, 1415, "24-3-2024", "John Doe", "123 Street", 2.0, 19.99, R.drawable.bananas)
        )

        orderAdapter = OrderAdapter(orders, this)
        recyclerView.adapter = orderAdapter

        setupButtons(view)

        return view
    }

    private fun setupButtons(view: View) {
        val btnRequested: TextView = view.findViewById(R.id.btn_requested)
        val btnCompleted: TextView = view.findViewById(R.id.btn_completed)
        val btnCanceled: TextView = view.findViewById(R.id.btn_canceled)

        btnRequested.setOnClickListener {
            // Change button colors and load requested orders
            btnRequested.setBackgroundResource(R.drawable.button_background_selected)
            btnCompleted.setBackgroundResource(R.drawable.button_background_unselected)
            btnCanceled.setBackgroundResource(R.drawable.button_background_unselected)
            // Load requested orders
        }

        btnCompleted.setOnClickListener {
            // Change button colors and load completed orders
            btnRequested.setBackgroundResource(R.drawable.button_background_unselected)
            btnCompleted.setBackgroundResource(R.drawable.button_background_selected)
            btnCanceled.setBackgroundResource(R.drawable.button_background_unselected)
            // Load completed orders
        }

        btnCanceled.setOnClickListener {
            // Change button colors and load canceled orders
            btnRequested.setBackgroundResource(R.drawable.button_background_unselected)
            btnCompleted.setBackgroundResource(R.drawable.button_background_unselected)
            btnCanceled.setBackgroundResource(R.drawable.button_background_selected)
            // Load canceled orders
        }
    }

    override fun onDeleteOrderClick(order: Order) {
        // Handle delete order click
    }
}
