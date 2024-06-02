package com.example.agronet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

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

        // Fetch orders from the database
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("MyOrdersFragment", "Starting to fetch orders from the database")
            orders = fetchOrdersFromDatabase()
            withContext(Dispatchers.Main) {
                Log.d("MyOrdersFragment", "Fetched ${orders.size} orders from the database")
                orderAdapter = OrderAdapter(orders, this@MyOrdersFragment)
                recyclerView.adapter = orderAdapter
            }
        }

        setupButtons(view)

        // Add profile button click listener
        val profileBtn = view.findViewById<ImageView>(R.id.profile_icon)
        profileBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FarmerProfileFragment())
                .addToBackStack(null)
                .commit()
        }

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
            Log.d("MyOrdersFragment", "Filtering orders by status: pending")
            filterOrdersByStatus("pending")
        }

        btnCompleted.setOnClickListener {
            // Change button colors and load completed orders
            btnRequested.setBackgroundResource(R.drawable.button_background_unselected)
            btnCompleted.setBackgroundResource(R.drawable.button_background_selected)
            btnCanceled.setBackgroundResource(R.drawable.button_background_unselected)
            // Load completed orders
            Log.d("MyOrdersFragment", "Filtering orders by status: completed")
            filterOrdersByStatus("completed")
        }

        btnCanceled.setOnClickListener {
            // Change button colors and load canceled orders
            btnRequested.setBackgroundResource(R.drawable.button_background_unselected)
            btnCompleted.setBackgroundResource(R.drawable.button_background_unselected)
            btnCanceled.setBackgroundResource(R.drawable.button_background_selected)
            // Load canceled orders
            Log.d("MyOrdersFragment", "Filtering orders by status: canceled")
            filterOrdersByStatus("canceled")
        }
    }

    private fun filterOrdersByStatus(status: String) {
        val filteredOrders = orders.filter { it.status == status }
        Log.d("MyOrdersFragment", "Filtered orders count: ${filteredOrders.size}")
        orderAdapter.updateOrders(filteredOrders)
    }

    override fun onDeleteOrderClick(order: Order) {
        // Handle delete order click
        Log.d("MyOrdersFragment", "Delete order clicked for order ID: ${order.orderId}")
    }

    private suspend fun fetchOrdersFromDatabase(): List<Order> {
        val orders = mutableListOf<Order>()
        var connection: Connection? = null
        var orderPreparedStatement: PreparedStatement? = null
        var itemPreparedStatement: PreparedStatement? = null
        var orderResultSet: ResultSet? = null
        var itemResultSet: ResultSet? = null

        try {
            connection = DatabaseManager.getConnection()
            Log.d("MyOrdersFragment", "Database connection established: $connection")

            val orderQuery = """
                SELECT * FROM orders 
                INNER JOIN order_items ON order_items.order_id = orders.order_id 
                INNER JOIN product ON product.id = order_items.product_id 
                WHERE orders.farmer_id = ?
            """
            orderPreparedStatement = connection.prepareStatement(orderQuery)
            val farmerId = SessionManager(requireContext()).userId.toInt()
            orderPreparedStatement.setInt(1, farmerId)
            Log.d("MyOrdersFragment", "Executing query: $orderQuery with farmerId: $farmerId")
            orderResultSet = orderPreparedStatement.executeQuery()
            Log.d("MyOrdersFragment", "Executed order query")

            while (orderResultSet.next()) {
                Log.d("MyOrdersFragment", "Order found: ID ${orderResultSet.getInt("order_id")}")

                val orderId = orderResultSet.getInt("order_id")
                val customerId = orderResultSet.getInt("customer_id")
                val farmerId = orderResultSet.getInt("farmer_id")
                val orderDate = orderResultSet.getString("created_at")
                val customerName = orderResultSet.getString("customer_name")
                val shippingAddress = orderResultSet.getString("shipping_address")
                val quantity = orderResultSet.getDouble("quantity")
                val totalPrice = orderResultSet.getDouble("total_price")
                val status = orderResultSet.getString("status")
                val productImageResId = orderResultSet.getBytes("prod_image")

                val items = mutableListOf<OrderItem>()
                val itemQuery = "SELECT * FROM order_items inner join product on order_items.product_id = product.id WHERE order_id = ?"
                itemPreparedStatement = connection.prepareStatement(itemQuery)
                itemPreparedStatement.setInt(1, orderId)
                itemResultSet = itemPreparedStatement.executeQuery()
                Log.d("MyOrdersFragment", "Executed item query for order ID: $orderId")

                while (itemResultSet.next()) {
                    val itemId = itemResultSet.getInt("order_item_id")
                    val productId = itemResultSet.getInt("product_id")
                    val productName = itemResultSet.getString("name")
                    val quantity = itemResultSet.getInt("quantity")
                    val pricePerUnit = itemResultSet.getDouble("price")
                    val totalPrice = itemResultSet.getDouble("total_price")
                    val productImageResId = itemResultSet.getBytes("prod_image") // Default image resource ID

                    val orderItem = OrderItem(itemId, productId, productName, quantity, pricePerUnit, totalPrice, productImageResId)
                    items.add(orderItem)
                }

                val order = Order(orderId, customerId, farmerId, status, orderDate, customerName, shippingAddress, quantity, totalPrice, productImageResId, items)
                orders.add(order)
                Log.d("MyOrdersFragment", "Added order ID: $orderId with ${items.size} items")
            }

        } catch (e: Exception) {
            Log.e("MyOrdersFragment", "Error fetching orders from database", e)
        } finally {
            orderResultSet?.close()
            itemResultSet?.close()
            orderPreparedStatement?.close()
            itemPreparedStatement?.close()
            connection?.close()
            Log.d("MyOrdersFragment", "Database connection closed")
        }

        return orders
    }
}
