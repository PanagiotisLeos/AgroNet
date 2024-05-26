package com.example.agronet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productImage = intent.getIntExtra("PRODUCT_IMAGE", 0)
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val postedByImage = intent.getIntExtra("POSTED_BY_IMAGE", 0)

        val productNameTextView: TextView = findViewById(R.id.productName)
        val productImageView: ImageView = findViewById(R.id.productImage)
        val productPriceTextView: TextView = findViewById(R.id.productPricePerKg)
        val postedByImageView: ImageView = findViewById(R.id.farmerImage)

        productNameTextView.text = productName
        productImageView.setImageResource(productImage)
        productPriceTextView.text = productPrice
        postedByImageView.setImageResource(postedByImage)
    }
}
