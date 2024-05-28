package com.example.agronet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productNameTextView: TextView
    private lateinit var productImageView: ImageView
    private lateinit var productPriceTextView: TextView
    private lateinit var farmerImageView: ImageView
    private lateinit var quantityInput: EditText
    private lateinit var totalPriceLabel: TextView
    private lateinit var addToCartButton: AppCompatButton

    private var productPricePerKg: Double = 0.0
    private var isEditing = false
    private var cartItem: CartItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productNameTextView = findViewById(R.id.productName)
        productImageView = findViewById(R.id.productImage)
        productPriceTextView = findViewById(R.id.productPricePerKg)
        farmerImageView = findViewById(R.id.farmerImage)
        quantityInput = findViewById(R.id.quantityInput)
        totalPriceLabel = findViewById(R.id.totalPriceLabel)
        addToCartButton = findViewById(R.id.addToCartButton)

        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productImage = intent.getIntExtra("PRODUCT_IMAGE", 0)
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val farmerImage = intent.getIntExtra("POSTED_BY_IMAGE", 0)

        productNameTextView.text = productName
        productImageView.setImageResource(productImage)
        productPriceTextView.text = productPrice
        farmerImageView.setImageResource(farmerImage)

        // Extract price per kg from the productPrice string
        productPricePerKg = productPrice?.replace("[^\\d.]".toRegex(), "")?.toDoubleOrNull() ?: 0.0

        quantityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateTotalPrice()
            }
        })

        isEditing = intent.getBooleanExtra("IS_EDITING", false)
        if (isEditing) {
            val quantity = intent.getDoubleExtra("QUANTITY", 0.0)
            quantityInput.setText(quantity.toString())
            cartItem = CartItem(productName ?: "", productImage, quantity, quantity * productPricePerKg)
        }

        addToCartButton.setOnClickListener {
            addToCart()
        }
    }

    private fun calculateTotalPrice() {
        val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0
        val totalPrice = quantity * productPricePerKg
        totalPriceLabel.text = String.format("Total Price: €%.2f", totalPrice)
    }

    private fun addToCart() {
        val productName = productNameTextView.text.toString()
        val productImage = intent.getIntExtra("PRODUCT_IMAGE", 0)
        val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0
        val totalPrice = quantity * productPricePerKg

        if (quantity > 0) {
            if (isEditing && cartItem != null) {
                CartManager.removeItem(cartItem!!)
            }
            val cartItem = CartItem(productName, productImage, quantity, totalPrice)
            CartManager.addItem(cartItem)
            Toast.makeText(this, "$productName added to cart", Toast.LENGTH_SHORT).show()
            // Επιστροφή στην `MainActivity` και `ProductPageFragment`
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
        }
    }
}
