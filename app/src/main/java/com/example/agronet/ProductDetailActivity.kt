package com.example.agronet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    private lateinit var farmerName: TextView
    private lateinit var quantityInput: EditText
    private lateinit var totalPriceLabel: TextView
    private lateinit var addToCartButton: AppCompatButton

    private var productPricePerKg: Double = 0.0
    private var isEditing = false
    private var cartItem: CartItem? = null
    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productNameTextView = findViewById(R.id.productName)
        productImageView = findViewById(R.id.productImage)
        productPriceTextView = findViewById(R.id.productPricePerKg)
        farmerImageView = findViewById(R.id.farmerImage)
        farmerName = findViewById(R.id.postedByLabel)
        quantityInput = findViewById(R.id.quantityInput)
        totalPriceLabel = findViewById(R.id.totalPriceLabel)
        addToCartButton = findViewById(R.id.addToCartButton)

        val productName = intent.getStringExtra("PRODUCT_NAME")
        productId = intent.getIntExtra("PRODUCT_ID", 0)
        val productImageByteArray = intent.getByteArrayExtra("PRODUCT_IMAGE")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val farmerImageByteArray = intent.getByteArrayExtra("POSTED_BY_IMAGE")

        productNameTextView.text = productName

        if (productImageByteArray != null) {
            productImageView.setImageBitmap(byteArrayToBitmap(productImageByteArray))
        } else {
            productImageView.setImageResource(R.drawable.bananas) // Use your default image resource
        }

        productPriceTextView.text = productPrice

        if (farmerImageByteArray != null) {
            farmerImageView.setImageBitmap(byteArrayToBitmap(farmerImageByteArray))
        } else {
            farmerImageView.setImageResource(R.drawable.bananas) // Use your default image resource
        }

        // Extract price per kg from the productPrice string
        if (productPrice != null) {
            productPricePerKg = productPrice.replace("€", "").toDouble()
        }

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
            cartItem = CartItem(productId, productImageByteArray, productName ?: "", quantity, productPricePerKg, quantity * productPricePerKg)
        }

        addToCartButton.setOnClickListener {
            addToCart()
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
    }

    private fun calculateTotalPrice() {
        val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0
        val totalPrice = quantity * productPricePerKg
        totalPriceLabel.text = String.format("Total Price: €%.2f", totalPrice)
    }

    private fun addToCart() {
        val productName = productNameTextView.text.toString()
        val productImage = intent.getByteArrayExtra("PRODUCT_IMAGE")
        val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0
        val totalPrice = quantity * productPricePerKg

        if (quantity > 0) {
            if (isEditing && cartItem != null) {
                CartManager.removeItem(cartItem!!)
            }
            val cartItem = CartItem(productId, productImage, productName, quantity, productPricePerKg, totalPrice)
            CartManager.addItem(cartItem)
            Toast.makeText(this, "$productName added to cart", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
        }
    }
}
