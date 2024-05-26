package com.example.agronet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productNameTextView: TextView
    private lateinit var productImageView: ImageView
    private lateinit var productPriceTextView: TextView
    private lateinit var farmerImageView: ImageView
    private lateinit var quantityInput: EditText
    private lateinit var totalPriceLabel: TextView

    private var productPricePerKg: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productNameTextView = findViewById(R.id.productName)
        productImageView = findViewById(R.id.productImage)
        productPriceTextView = findViewById(R.id.productPricePerKg)
        farmerImageView = findViewById(R.id.farmerImage)
        quantityInput = findViewById(R.id.quantityInput)
        totalPriceLabel = findViewById(R.id.totalPriceLabel)

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
    }

    private fun calculateTotalPrice() {
        val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0
        val totalPrice = quantity * productPricePerKg
        totalPriceLabel.text = String.format("Total Price: €%.2f", totalPrice)
    }
}
