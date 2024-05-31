package com.example.agronet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class CheckoutFragment : Fragment() {

    private lateinit var cartItemsLayout: LinearLayout
    private lateinit var redeemPointsInput: EditText
    private lateinit var applyPointsButton: AppCompatButton
    private lateinit var totalPriceTextView: TextView
    private lateinit var confirmOrderButton: AppCompatButton
    private lateinit var tipSeekBar: SeekBar
    private lateinit var tipAmount: TextView

    private var totalAmount: Double = 0.0
    private var tipAmountValue: Double = 0.5 // initial tip amount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.checkout, container, false)

        cartItemsLayout = view.findViewById(R.id.cartItemsLayout)
        redeemPointsInput = view.findViewById(R.id.redeemPointsInput)
        applyPointsButton = view.findViewById(R.id.applyPointsButton)
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView)
        confirmOrderButton = view.findViewById(R.id.confirmOrderButton)
        tipSeekBar = view.findViewById(R.id.tipSeekBar)
        tipAmount = view.findViewById(R.id.tipAmount)

        displayCartItems()
        setupTipSeekBar()
        setupApplyPointsButton()
        setupConfirmOrderButton()

        return view
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

    private fun removeItemFromCart(item: CartItem) {
        CartManager.removeItem(item)
        cartItemsLayout.removeAllViews()
        displayCartItems()
    }

    private fun editCartItem(item: CartItem) {
        val intent = Intent(context, ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", item.productId)
        intent.putExtra("PRODUCT_NAME", item.productName)
        intent.putExtra("PRODUCT_IMAGE", item.productImage)
        intent.putExtra("PRODUCT_PRICE", item.price)
        intent.putExtra("IS_EDITING", true)
        intent.putExtra("QUANTITY", item.quantity)
        startActivity(intent)
    }

    private fun setupTipSeekBar() {
        tipSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tipAmountValue = progress * 0.5
                tipAmount.text = String.format("€%.2f", tipAmountValue)
                updateTotalPrice()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupApplyPointsButton() {
        applyPointsButton.setOnClickListener {
            val points = redeemPointsInput.text.toString().toIntOrNull() ?: 0
            if (points > 0) {
                val discount = points * 0.1 // assuming each point gives €0.1 discount
                totalAmount -= discount
                Toast.makeText(context, "Applied €${discount} discount", Toast.LENGTH_SHORT).show()
                updateTotalPrice()
            } else {
                Toast.makeText(context, "Invalid points", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotalPrice() {
        val totalPrice = totalAmount + tipAmountValue
        totalPriceTextView.text = String.format("Total Price: €%.2f", totalPrice)
    }

    private fun setupConfirmOrderButton() {
        confirmOrderButton.setOnClickListener {
            confirmOrder()
        }
    }

    private fun confirmOrder() {
        val cartItems = CartManager.getItems()
        val customerId = SessionManager(requireContext()).userId.toInt() // Assuming you have a SessionManager to get the user ID
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

                Log.d("CheckoutFragment", "Processing order for farmerId: $farmerId, totalAmount: €$totalAmountForFarmer")

                // Upload the order to the database
                val success = uploadOrderToDatabase(order)
                if (!success) {
                    Log.e("CheckoutFragment", "Failed to upload order for farmerId: $farmerId")
                    allOrdersSuccessful = false
                } else {
                    Log.d("CheckoutFragment", "Successfully uploaded order for farmerId: $farmerId")
                }
            }

            withContext(Dispatchers.Main) {
                if (allOrdersSuccessful) {
                    Log.d("CheckoutFragment", "All orders confirmed successfully")
                    Toast.makeText(context, "Order Confirmed!", Toast.LENGTH_SHORT).show()
                    CartManager.clearCart()
                    requireActivity().supportFragmentManager.popBackStack() // Close the checkout fragment
                } else {
                    Log.e("CheckoutFragment", "Failed to confirm some orders")
                    Toast.makeText(context, "Failed to confirm some orders", Toast.LENGTH_SHORT).show()
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

            // Insert order into orders table
            val orderQuery = """
            INSERT INTO orders (customer_id, farmer_id, status, total_price, created_at)
            VALUES (?, ?,'pending', ?, now())
        """.trimIndent()
            orderPreparedStatement = connection.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS)
            orderPreparedStatement.setInt(1, order.customerId)
            orderPreparedStatement.setInt(2, order.farmerId)
            orderPreparedStatement.setDouble(3, order.totalAmount)
            val orderRowsAffected = orderPreparedStatement.executeUpdate()

            if (orderRowsAffected > 0) {
                val generatedKeys = orderPreparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    val orderId = generatedKeys.getInt(1)
                    Log.d("CheckoutFragment", "Generated Order ID: $orderId")

                    // Insert items into order_items table
                    val itemQuery = """
                    INSERT INTO order_items (order_id, product_id, quantity, price, total)
                    VALUES (?, ?, ?, ?, ?)
                """.trimIndent()
                    itemPreparedStatement = connection.prepareStatement(itemQuery)

                    for (item in order.items) {
                        itemPreparedStatement.setInt(1, orderId)
                        itemPreparedStatement.setInt(2, item.productId)
                        itemPreparedStatement.setDouble(3, item.quantity)
                        itemPreparedStatement.setDouble(4, item.price)
                        itemPreparedStatement.setDouble(5, item.totalPrice)
                        itemPreparedStatement.addBatch()
                    }
                    itemPreparedStatement.executeBatch()
                } else {
                    Log.e("CheckoutFragment", "No keys generated for order")
                    return false
                }
            }

            Log.d("CheckoutFragment", "Order upload successful for farmerId: ${order.farmerId}")
            orderRowsAffected > 0
        } catch (e: Exception) {
            Log.e("CheckoutFragment", "Error uploading order to database", e)
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
