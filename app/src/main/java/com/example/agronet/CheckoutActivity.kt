package com.example.agronet

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView

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

            productImageView.setImageResource(item.productImage)
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
            // Implement order confirmation logic here
            Toast.makeText(this, "Order Confirmed!", Toast.LENGTH_SHORT).show()
            CartManager.clearCart()
            finish() // Close the checkout activity
        }
    }
}
