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
            Order(
                orderId = 1213,
                customerId = 1415,
                orderDate = "24-3-2024",
                customerName = "Panagiotis Leos",
                shippingAddress = "Aratou 26",
                quantity = 2.0,
                totalPrice = 19.99,
                productImageResId = R.drawable.bananas,
                items = listOf(
                    OrderItem(
                        itemId = 1,
                        productId = 101,
                        productName = "Bananas",
                        quantity = 2,
                        pricePerUnit = 9.99,
                        totalPrice = 19.98,
                        productImageResId = R.drawable.bananas
                    )
                )
            ),
            Order(
                orderId = 1214,
                customerId = 1416,
                orderDate = "25-3-2024",
                customerName = "Apostolaras Xrist",
                shippingAddress = "Fillelhnwn 7A",
                quantity = 1.0,
                totalPrice = 9.99,
                productImageResId = R.drawable.bananas,
                items = listOf(
                    OrderItem(
                        itemId = 2,
                        productId = 102,
                        productName = "Bananas",
                        quantity = 1,
                        pricePerUnit = 9.99,
                        totalPrice = 9.99,
                        productImageResId = R.drawable.bananas
                    )
                )
            )
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

