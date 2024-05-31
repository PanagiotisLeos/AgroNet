package com.example.agronet

data class CartItem(
    val productId: Int,
    val farmerId: Int,
    val productImage: ByteArray?,
    val productName: String,
    val quantity: Double,
    val price: Double, // Price per unit
    val totalPrice: Double
)
