package com.example.agronet

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class CartActivity : AppCompatActivity() {

    private lateinit var cartItemsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart)

        cartItemsLayout = findViewById(R.id.cartItemsLayout)

        displayCartItems()
    }

    private fun displayCartItems() {
        val cartItems = CartManager.getItems()

        for (item in cartItems) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.cart_item, cartItemsLayout, false) as CardView

            val productNameTextView = itemView.findViewById<TextView>(R.id.productName)
            val productQuantityTextView = itemView.findViewById<TextView>(R.id.productQuantity)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.productPrice)

            productNameTextView.text = item.productName
            productQuantityTextView.text = "Quantity: ${item.quantity} Kg"
            productPriceTextView.text = String.format("Total: €%.2f", item.totalPrice)

            cartItemsLayout.addView(itemView)
        }
    }
}
