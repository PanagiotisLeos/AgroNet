package com.example.agronet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement

class CheckoutActivity : AppCompatActivity() {

    private lateinit var cartItemsLayout: LinearLayout
    private lateinit var redeemPointsInput: EditText
    private lateinit var applyPointsButton: AppCompatButton
    private lateinit var totalPriceTextView: TextView
    private lateinit var confirmOrderButton: AppCompatButton

    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout)

        cartItemsLayout = findViewById(R.id.cartItemsLayout)
        redeemPointsInput = findViewById(R.id.redeemPointsInput)
        applyPointsButton = findViewById(R.id.applyPointsButton)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        confirmOrderButton = findViewById(R.id.confirmOrderButton)

        displayCartItems()
        setupApplyPointsButton()
        setupConfirmOrderButton()
    }

    private fun displayCartItems() {
        val cartItems = CartManager.getItems()
        totalAmount = 0.0

        for (item in cartItems) {
            val itemView = layoutInflater.inflate(R.layout.cart_item, cartItemsLayout, false) as CardView

            val productImageView = itemView.findViewById<ImageView>(R.id.productImage)
            val productNameTextView = itemView.findViewById<TextView>(R.id.productName)
            val productQuantityTextView = itemView.findViewById<TextView>(R.id.productQuantity)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.productPrice)

            productImageView.setImageBitmap(byteArrayToBitmap(item.productImage))
            productNameTextView.text = item.productName
            productQuantityTextView.text = "Quantity: ${item.quantity} Kg"
            productPriceTextView.text = String.format("Total: €%.2f", item.totalPrice)

            totalAmount += item.totalPrice

            cartItemsLayout.addView(itemView)
        }

        updateTotalPrice()
    }

    private fun setupApplyPointsButton() {
        applyPointsButton.setOnClickListener {
            val points = redeemPointsInput.text.toString().toIntOrNull() ?: 0
            if (points > 0) {
                val discount = points * 0.1 // assuming each point gives €0.1 discount
                totalAmount -= discount
                Toast.makeText(this, "Applied €${discount} discount", Toast.LENGTH_SHORT).show()
                updateTotalPrice()
            } else {
                Toast.makeText(this, "Invalid points", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotalPrice() {
        totalPriceTextView.text = String.format("Total Price: €%.2f", totalAmount)
    }

    private fun setupConfirmOrderButton() {
        confirmOrderButton.setOnClickListener {
            confirmOrder()
        }
    }

    private fun confirmOrder() {
        val cartItems = CartManager.getItems()
        val customerId = SessionManager(this).userId.toInt() // Assuming you have a SessionManager to get the user ID
        val orderDate = System.currentTimeMillis()

        // Group items by farmer
        val groupedItems = cartItems.groupBy { it.farmerId }

        GlobalScope.launch(Dispatchers.IO) {
            var allOrdersSuccessful = true
            for ((farmerId, items) in groupedItems) {
                val totalAmountForFarmer = items.sumOf { it.totalPrice }

                val order = Order(
                    customerId = customerId,
                    farmerId = farmerId,
                    totalAmount = totalAmountForFarmer,
                    items = items,
                    orderDate = orderDate
                )

                Log.d("CheckoutActivity", "Processing order for farmerId: $farmerId, totalAmount: €$totalAmountForFarmer")

                // Upload the order to the database
                val success = uploadOrderToDatabase(order)
                if (!success) {
                    Log.e("CheckoutActivity", "Failed to upload order for farmerId: $farmerId")
                    allOrdersSuccessful = false
                } else {
                    Log.d("CheckoutActivity", "Successfully uploaded order for farmerId: $farmerId")
                }
            }

            withContext(Dispatchers.Main) {
                if (allOrdersSuccessful) {
                    Log.d("CheckoutActivity", "All orders confirmed successfully")
                    Toast.makeText(this@CheckoutActivity, "Order Confirmed!", Toast.LENGTH_SHORT).show()
                    CartManager.clearCart()
                    finish() // Close the checkout activity
                } else {
                    Log.e("CheckoutActivity", "Failed to confirm some orders")
                    Toast.makeText(this@CheckoutActivity, "Failed to confirm some orders", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadOrderToDatabase(order: Order): Boolean {
        var connection: Connection? = null
        var orderPreparedStatement: PreparedStatement? = null
        var itemPreparedStatement: PreparedStatement? = null
        return try {
            connection = DatabaseManager.getConnection()

            val orderQuery = "INSERT INTO orders (customer_id, farmer_id, total_amount, order_date) VALUES (?, ?, ?, ?)"
            orderPreparedStatement = connection.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS)
            orderPreparedStatement.setInt(1, order.customerId)
            orderPreparedStatement.setInt(2, order.farmerId)
            orderPreparedStatement.setDouble(3, order.totalAmount)
            orderPreparedStatement.setLong(4, order.orderDate)
            val orderRowsAffected = orderPreparedStatement.executeUpdate()

            if (orderRowsAffected > 0) {
                val generatedKeys = orderPreparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    val orderId = generatedKeys.getInt(1)

                    val itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)"
                    itemPreparedStatement = connection.prepareStatement(itemQuery)

                    for (item in order.items) {
                        itemPreparedStatement.setInt(1, orderId)
                        itemPreparedStatement.setInt(2, item.productId)
                        itemPreparedStatement.setDouble(3, item.quantity)
                        itemPreparedStatement.setDouble(4, item.price)
                        itemPreparedStatement.addBatch()
                    }
                    itemPreparedStatement.executeBatch()
                }
            }

            Log.d("CheckoutActivity", "Order upload successful for farmerId: ${order.farmerId}, orderId: ${orderPreparedStatement.generatedKeys.getInt(1)}")
            orderRowsAffected > 0
        } catch (e: Exception) {
            Log.e("CheckoutActivity", "Error uploading order to database", e)
            false
        } finally {
            orderPreparedStatement?.close()
            itemPreparedStatement?.close()
            connection?.close()
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
    }
}

