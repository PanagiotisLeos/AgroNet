package com.example.agronet
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CartActivity: AppCompatActivity() {



        private lateinit var cart: Cart

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.cart)

            cart = Cart(this)

            // Fetch products from database and add to cart
            cart.fetchProductsFromDatabase()

            val cartDetailsTextView: TextView = findViewById(R.id.cartDetails)
            cartDetailsTextView.text = cart.toString()

            val totalPriceTextView: TextView = findViewById(R.id.totalPrice)
            totalPriceTextView.text = "Total Price: ${cart.getTotalPrice()}€"
        }
    }


}